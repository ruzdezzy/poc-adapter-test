/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.rheapocadapter.util;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.rheapocadapter.handler.RequestHandler;
import org.openmrs.module.rheapocadapter.handler.ResponseHandler;
import org.openmrs.module.rheapocadapter.impl.HL7MessageTransformer;
import org.openmrs.module.rheapocadapter.transaction.ArchiveTransaction;
import org.openmrs.module.rheapocadapter.transaction.ErrorTransaction;
import org.openmrs.module.rheapocadapter.transaction.ProcessingTransaction;
import org.openmrs.module.rheapocadapter.transaction.Transaction;
import org.openmrs.scheduler.SchedulerConstants;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;


public class GetClinicalDataThread implements Runnable{
	
	private Log log = LogFactory.getLog(this.getClass());

	private RequestHandler requestHandler = new RequestHandler();
	private ResponseHandler response = new ResponseHandler();

	private String[] methd;
	private String result = "";
	private String clientId;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public GetClinicalDataThread(String[] methd2,
			TreeMap<String, String> parameters2, String clientId2) {
		this.methd = methd2;
		this.parameters = parameters2;
		this.clientId = clientId2;
	}
	
	public TreeMap<String, String> getParameters() {
		return parameters;
	}


	public void setParameters(TreeMap<String, String> parameters) {
		this.parameters = parameters;
	}


	private TreeMap<String, String> parameters = new TreeMap<String, String>();


	public String[] getMethd() {
		return methd;
	}


	public void setMethd(String[] methd) {
		this.methd = methd;
	}

	  /**
     * Authenticate the context so the task can call service layer.
     */
    protected void authenticate() {
            try {
                    AdministrationService adminService = Context.getAdministrationService();
                    Context.authenticate(adminService.getGlobalProperty(SchedulerConstants.SCHEDULER_USERNAME_PROPERTY),
                        adminService.getGlobalProperty(SchedulerConstants.SCHEDULER_PASSWORD_PROPERTY));
                    
            }
            catch (ContextAuthenticationException e) {
                    log.error("Error authenticating user");
            }
    }
    

	@Override
    public void run() {
		Context.openSession();
		if (!Context.isAuthenticated()) {
			log.info("Authenticating ...");
			authenticate();
		}
		
		EncounterService encService = Context.getEncounterService();
		String relatedEncounters = "";
		
		Transaction item = requestHandler.sendRequest(methd, result,
				parameters);
		
		if (item instanceof ArchiveTransaction) {
			String url = methd[0] + " " + item.getUrl();
			item.setUrl(url);
			result = item.getMessage();

			HL7MessageTransformer messageTransformer = new HL7MessageTransformer();
			Message message = (Message) messageTransformer
					.translateMessage(result);

			List<Encounter> encounterToSave = new ArrayList<Encounter>();
			try {
				encounterToSave = messageTransformer
						.messageToEncounter((ORU_R01) message);
			} catch (HL7Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			log.info(encounterToSave.size() + " encounter to save");
			for (Encounter e : encounterToSave) {
				EncounterType type = e.getEncounterType();
				List<EncounterType> encTypes = encService
						.getAllEncounterTypes();
				log.info(encService.getEncounterType(type.getName()
						+ " IMPORTED"));
				boolean encounterTypeExist = false;
				for (EncounterType ty : encTypes) {
					if (ty.getName().equalsIgnoreCase(
							type.getName() + " IMPORTED")) {
						encounterTypeExist = true;
					}
				}
				if (!encounterTypeExist) {
					EncounterType newType = new EncounterType();
					newType.setCreator(Context.getAuthenticatedUser());
					newType.setDescription(type.getName() + " IMPORTED");
					newType.setName(type.getName() + " IMPORTED");
					encService.saveEncounterType(newType);

				}
				e.setEncounterType(encService.getEncounterType(type
						.getName() + " IMPORTED"));
				Encounter saved = encService.saveEncounter(e);
				if (relatedEncounters.equals(""))
					relatedEncounters += saved.getId();
				else
					relatedEncounters += "," + saved.getId();

			}
			ArchiveTransaction transaction = (ArchiveTransaction) item;
			transaction.setRelatedEncounter(relatedEncounters);
			response.handleResponse(transaction);
			result = "Result after saving encounter "
					+ relatedEncounters + " "
					+ transaction.getMessage();
		} else if (item instanceof ProcessingTransaction) {
			result = "Get Clinical Data failed for " + clientId
					+ ", try again later";
			item.setMessage(result);
			response.handleResponse(item);
		} else if (item instanceof ErrorTransaction) {
			result = "Get Clinical Data failed for " + clientId
					+ ", Contact Administrator";
			item.setMessage(result);
			response.handleResponse(item);
		}
		
		// close session
		Context.closeSession();
}
}
