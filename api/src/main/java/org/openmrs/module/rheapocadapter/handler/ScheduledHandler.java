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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.module.rheapocadapter.service.TransactionService;
import org.openmrs.module.rheapocadapter.transaction.ProcessingTransaction;
import org.openmrs.module.rheapocadapter.transaction.Transaction;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 *
 */
public class ScheduledHandler extends AbstractTask {

	private TransactionService queueService = ServiceContext.getInstance()
			.getService(TransactionService.class);

	Log log = LogFactory.getLog(this.getClass());

	/**
	 * @see org.openmrs.scheduler.tasks.AbstractTask#execute()
	 */

	@SuppressWarnings("unchecked")
	@Override
	public void execute() {
		Context.openSession();
		if (!Context.isAuthenticated()) {
			log.info("Authenticating ...");
			authenticate();
		}
		log.info("Scheduled Task Started");
		List<ProcessingTransaction> queues = (List<ProcessingTransaction>) getProcessingQueue();

		// get saved patient and save
		List<Patient> patientToSave = getSavePatientFromTransaction(queues);
		sendBackEnteredPatients(patientToSave);

		// get update patient and save
		List<Patient> patientToUpdate = getUpdatePatientFromTransaction(queues);
		updateBackEnteredPatients(patientToUpdate);

		// getEncounters and save
		List<Encounter> encounterToSend = getEncounterFromTransaction(queues);
		sendBackEnteredEncounter(encounterToSend);

		// close session
		Context.closeSession();
	}

	/**
	 * fetch from DB all processing queues, and delete them from databases, if
	 * the send fails, it's saved gain.
	 * 
	 * @return
	 */
	private List<? extends Transaction> getProcessingQueue() {
		log.info("ProcessingQueueHandler started");
		return queueService.getAllQueue(new ProcessingTransaction().getClass());

	}

	private List<Encounter> getEncounterFromTransaction(
			List<? extends Transaction> transaction) {
		List<Encounter> encs = new ArrayList<Encounter>();
		EncounterService encService = Context.getEncounterService();
		for (Transaction trans : transaction) {
			String idInMessage = trans.getMessage().trim();
			if (idInMessage.contains("SavePatientId=")
					|| (idInMessage.contains("UpdatePatientId"))) {
				continue;
			}
			if (idInMessage.contains("EncounterId=")) {
				log.info(idInMessage + " ");
				idInMessage = idInMessage.split("=")[1];
				idInMessage = idInMessage.trim();
				queueService.removeQueue(trans);
				encs.add(encService.getEncounter(Integer.parseInt(idInMessage)));

			}
		}
		return encs;
	}

	private List<Patient> getSavePatientFromTransaction(
			List<? extends Transaction> transaction) {
		List<Patient> patients = new ArrayList<Patient>();
		PatientService patService = Context.getPatientService();
		for (Transaction trans : transaction) {
			String idInMessage = trans.getMessage().trim();
			if (idInMessage.contains("EncounterId=")
					|| (idInMessage.contains("UpdatePatientId"))) {
				continue;
			}
			if (idInMessage.contains("SavePatientId=")) {
				log.info(idInMessage + " ");
				idInMessage = idInMessage.split("=")[1];
				idInMessage = idInMessage.trim();
				queueService.removeQueue(trans);
				patients.add(patService.getPatient(Integer
						.parseInt(idInMessage)));

			}

		}
		return patients;
	}

	private List<Patient> getUpdatePatientFromTransaction(
			List<? extends Transaction> transaction) {
		List<Patient> patients = new ArrayList<Patient>();
		PatientService patService = Context.getPatientService();
		for (Transaction trans : transaction) {
			String idInMessage = trans.getMessage().trim();
			if (idInMessage.contains("EncounterId=")
					|| (idInMessage.contains("SavePatientId"))) {
				continue;
			}
			if (idInMessage.contains("UpdatePatientId=")) {
				log.info(idInMessage + " ");
				idInMessage = idInMessage.split("=")[1];
				idInMessage = idInMessage.trim();
				queueService.removeQueue(trans);
				patients.add(patService.getPatient(Integer
						.parseInt(idInMessage)));

			}

		}
		return patients;
	}

	private void sendBackEnteredEncounter(List<Encounter> encounters) {
		SharedHealthRecordService sharedHealthRecordService = new SharedHealthRecordService();
		log.info("Sending BackEnteredData");
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
		for (Encounter e : encounters) {
			Set<PatientIdentifier> identifiers = e.getPatient()
					.getIdentifiers();
			String clientId = "";
			PatientIdentifierType nid = Context.getPatientService()
					.getPatientIdentifierTypeByName("NID");
			PatientIdentifierType mutuelle = Context.getPatientService()
					.getPatientIdentifierTypeByName("Mutelle");
			PatientIdentifierType rama = Context.getPatientService()
					.getPatientIdentifierTypeByName("RAMA");
			PatientIdentifierType primaryCare = Context.getPatientService()
					.getPatientIdentifierTypeByName("Primary Care ID Type");

			for (PatientIdentifier id : identifiers) {
				if (id.getIdentifierType().equals(nid)) {
					clientId = nid + "-" + id.getIdentifier();
				} else if (id.getIdentifierType().equals(mutuelle)) {
					clientId = mutuelle + "-" + id.getIdentifier();
				} else if (id.getIdentifierType().equals(rama)) {
					clientId = rama + "-" + id.getIdentifier();
				} else if (id.getIdentifierType().equals(primaryCare)) {
					implementationId = implementationId.toLowerCase();
					String fosaid = implementationId.substring(implementationId
							.indexOf("rwanda") + 6);

					clientId = "OMRS" + fosaid + "-" + id.getIdentifier();
				}

			}
			if (clientId == "") {

				clientId = e.getPatient().getPatientIdentifier()
						.getIdentifierType()
						+ "-"
						+ e.getPatient().getPatientIdentifier().getIdentifier();
			}
			log.info("ClientId from ScheduledHandler =" + clientId);
			sharedHealthRecordService.savePatientEncounter(e, clientId);

		}
	}

	private void sendBackEnteredPatients(List<Patient> patients) {
		for (Patient patient : patients) {
			ClientRegistryService clService = new ClientRegistryService();
			String result = clService.registerNewPatient(patient);
			log.info("back entered patient saving Result: \n" + result);
		}

	}

	private void updateBackEnteredPatients(List<Patient> patients) {
		for (Patient patient : patients) {
			ClientRegistryService clService = new ClientRegistryService();
			String result = clService.updateClientDetails(patient);
			log.info("back entered patient update Result: \n" + result);
		}

	}
}
