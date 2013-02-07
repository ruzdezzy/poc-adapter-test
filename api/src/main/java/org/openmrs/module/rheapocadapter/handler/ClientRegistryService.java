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
package org.openmrs.module.rheapocadapter.handler;

import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.rheapocadapter.util.PatientMessageThread;
import org.openmrs.module.rheapocadapter.TransactionUtil;
import org.openmrs.module.rheapocadapter.impl.HL7MessageTransformer;
import org.openmrs.module.rheapocadapter.service.MessageTransformer;
import org.openmrs.module.rheapocadapter.transaction.ArchiveTransaction;
import org.openmrs.module.rheapocadapter.transaction.ErrorTransaction;
import org.openmrs.module.rheapocadapter.transaction.ProcessingTransaction;
import org.openmrs.module.rheapocadapter.transaction.Transaction;

/**
 *
 */
public class ClientRegistryService {

	private ResponseHandler response = new ResponseHandler();

	private RequestHandler requestHandler = new RequestHandler();

	private Log log = LogFactory.getLog(this.getClass());

	/**
	 * @see org.openmrs.module.rheapocadapter.service.ClientRegistryService#registerNewPatient(java.util.List)
	 */
	public String registerNewPatients(List<Patient> patients) {
		try {
			String result = "";
			MessageTransformer messageTransformer = new HL7MessageTransformer();
			String message = "";
			String patientIds = "";
			for (Patient patient : patients) {
				message += (String) messageTransformer.generateMessage(patient,
						"Create");
				if (patientIds != "") {
					patientIds += "," + patient.getPatientId();
				} else {
					patientIds += patient.getPatientId();
				}
			}
			if ("".equals(message) || message.equals(null)) {
				return "Register failed due to message creation, Contact Administrator";
			}
			log.error("Message to send " + message);
			TransactionUtil.setCreator(patients.get(0).getCreator());
			String[] methd = new String[] { "POST", "RegisterNew" };
			TreeMap<String, String> parameters = new TreeMap<String, String>();
			Transaction item = requestHandler.sendRequest(methd, message,
					parameters);
			if (item instanceof ArchiveTransaction) {
				item.setMessage("Saving patients with Ids" + patientIds
						+ " Succeded");
				result = "Register succeded";
			} else if (item instanceof ProcessingTransaction) {
				item.setMessage(patientIds);
				result = "Register failed, try again later";
			} else if (item instanceof ErrorTransaction) {
				result = "Register failed, Contact Administrator";

			}
			response.handleResponse(item);
			return result;
		} catch (NullPointerException e) {
			log.error(e.getMessage());
			return "";
		}
	}

	public String registerNewPatient(Patient patient) {
		try {
			String result = "";
			MessageTransformer messageTransformer = new HL7MessageTransformer();
			String message = "";
			message += (String) messageTransformer.generateMessage(patient,
					"Create");

			if ("".equals(message) || message.equals(null)) {
				return "Register failed due to message creation, Contact Administrator";
			}
			log.error("Message to send " + message);
			TransactionUtil.setCreator(patient.getCreator());
			String[] methd = new String[] { "POST", "RegisterNew" };
			TreeMap<String, String> parameters = new TreeMap<String, String>();

			Thread thread = new Thread(new PatientMessageThread(methd,message,parameters,patient,"Saving "));
		    thread.setDaemon(true);
		    thread.start();
		 	 
			return null;
		} catch (NullPointerException e) {
			log.error(e.getMessage());
			return "";
		}
	}

	/**
	 * @see org.openmrs.module.rheapocadapter.service.ClientRegistryService#getClients(java.util.TreeMap)
	 */
	public String getClients(TreeMap<String, String> parameters) {
		String result = "";
		String message = "";
		String[] methd = new String[] { "GET", "GetClients" };
		Transaction item = requestHandler.sendRequest(methd, message,
				parameters);
		if (item instanceof ArchiveTransaction) {
			result = item.getMessage();

		} else if (item instanceof ProcessingTransaction) {
			result = "Get Clients failed, the internet may be down";
			// we need these results immediately, we can't wait
			// send this to the error queue instead
			item = TransactionUtil.setQueueMessage(item, "", new Date(), "GET",
					"Error");
			item.setMessage(result);
		} else if (item instanceof ErrorTransaction) {
			result = "Get Clients failed, Contact Administrator";
		}
		response.handleResponse(item);
		return result;
	}

	/**
	 * @see org.openmrs.module.rheapocadapter.service.ClientRegistryService#getClient(java.lang.String)
	 */
	public String getClient(String clientId) {
		String result = "";
		String message = "";
		String[] methd = new String[] { "GET", "GetClient" };
		TreeMap<String, String> parameters = new TreeMap<String, String>();
		parameters.put("patientId", clientId);
		Transaction item = requestHandler.sendRequest(methd, message,
				parameters);

		if (item instanceof ArchiveTransaction) {
			result = item.getMessage();
		} else if (item instanceof ProcessingTransaction) {
			result = "Get Client failed, the internet may be down";
			// we need these results immediately, we can't wait
			// send this to the error queue instead
			item = TransactionUtil.setQueueMessage(item, "", new Date(), "GET",
					"Error");
			item.setMessage(result);
		} else if (item instanceof ErrorTransaction) {
			result = "Get Client failed, Contact Administrator";
		}
		response.handleResponse(item);

		return result;
	}

	/**
	 * @see org.openmrs.module.rheapocadapter.service.ClientRegistryService#updateClientDetails(java.util.TreeMap)
	 *      UpdatePatientData
	 */
	public String updateClientDetails(Patient patient) {
		try {
			String implementationId = "";
			try {
				implementationId = (Context.getAdministrationService()
						.getImplementationId().getImplementationId() != null) ? Context
						.getAdministrationService().getImplementationId()
						.getImplementationId()
						: "rwanda000";
			} catch (NullPointerException e) {
				log.error("No Implementation Id  set;");
				implementationId = "rwanda000";
			}
			String result = "";
			MessageTransformer messageTransformer = new HL7MessageTransformer();
			String message = (String) messageTransformer.generateMessage(
					patient, "Update");
			TransactionUtil.setCreator(patient.getCreator());
			String[] methd = new String[] { "PUT", "UpdatePatientData" };
			TreeMap<String, String> parameters = new TreeMap<String, String>();
			if (patient.getPatientIdentifier("NID") != null) {
				parameters.put("patientId",
						"NID-" + patient.getPatientIdentifier("NID"));
			} else if (patient.getPatientIdentifier("Mutuelle") != null) {
				parameters.put("patientId",
						"Mutuelle-" + patient.getPatientIdentifier("Mutuelle"));
			} else if (patient.getPatientIdentifier("RAMA") != null) {
				parameters.put("patientId",
						"RAMA-" + patient.getPatientIdentifier("RAMA"));
			} else {
				if (patient.getPatientIdentifier().getIdentifierType()
						.getName().equals("Primary Care ID Type")) {
					implementationId = implementationId.toLowerCase();
					String fosaid = implementationId.substring(implementationId
							.indexOf("rwanda") + 6);

					String clientId = "OMRS" + fosaid + "-"
							+ patient.getPatientIdentifier().getIdentifier();
					parameters.put("patientId", clientId);
				} else {
					parameters.put("patientId", patient.getPatientIdentifier()
							.getIdentifierType().getName()
							+ "-" + patient.getPatientIdentifier());
				}
			}
			
			Thread thread = new Thread(new PatientMessageThread(methd,message,parameters,patient,"Updating "));
		    thread.setDaemon(true);
		    thread.start();
		 	 
			return null;	
		} catch (NullPointerException e) {
			log.error(e.getMessage());
			return "";
		}
	}

}
