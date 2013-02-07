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

import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.rheapocadapter.handler.RequestHandler;
import org.openmrs.module.rheapocadapter.handler.ResponseHandler;
import org.openmrs.module.rheapocadapter.transaction.ArchiveTransaction;
import org.openmrs.module.rheapocadapter.transaction.ErrorTransaction;
import org.openmrs.module.rheapocadapter.transaction.ProcessingTransaction;
import org.openmrs.module.rheapocadapter.transaction.Transaction;
import org.openmrs.scheduler.SchedulerConstants;

public class MessagePostingThread implements Runnable{
	
	private Log log = LogFactory.getLog(this.getClass());

	private RequestHandler requestHandler = new RequestHandler();
	private ResponseHandler response = new ResponseHandler();

	private String[] methd;
	private String message;
	private String result = "";
	
	
	public MessagePostingThread(String[] methd2, String message2,
			TreeMap<String, String> parameters2, Encounter encounter2) {
		this.methd = methd2;
		this.message = message2;
		this.parameters = parameters2;
		this.encounter = encounter2;
	}


	public Encounter getEncounter() {
		return encounter;
	}


	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}


	private Encounter encounter;
	public TreeMap<String, String> getParameters() {
		return parameters;
	}


	public void setParameters(TreeMap<String, String> parameters) {
		this.parameters = parameters;
	}


	private TreeMap<String, String> parameters = new TreeMap<String, String>();
	
	
    public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


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
		
		Transaction item = requestHandler.sendRequest(getMethd(), getMessage(),
				getParameters());
		if (item instanceof ArchiveTransaction) {
			item.setMessage("EncounterId=" + getEncounter().getEncounterId());
			String url = methd[0] + " " + item.getUrl();
			item.setUrl(url);
			result = "Save Patient Encounter succeded";
		} else if (item instanceof ProcessingTransaction) {
			item.setMessage("EncounterId=" + getEncounter().getEncounterId() + "");
			result = "Save Patient Encounter failed, try again later";
		} else if (item instanceof ErrorTransaction) {

			item.setMessage("EncounterId=" + getEncounter().getEncounterId() + "");
			result = "Save Patient Encounter failed, Contact Administrator";

		}
		response.handleResponse(item);	
		// close session
		Context.closeSession();
}
}
