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
package org.openmrs.module.rheapocadapter.advice;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.rheapocadapter.RHEAConstants;
import org.openmrs.module.rheapocadapter.handler.SharedHealthRecordService;
import org.openmrs.module.rheapocadapter.util.GetPatientUtil;
import org.springframework.aop.AfterReturningAdvice;

/**
 *
 */
public class EncounterServiceAdvice implements AfterReturningAdvice {

	protected final Log log = LogFactory.getLog(EncounterServiceAdvice.class);
	
	// List for Hack
	private static List<PatientServiceAdvice.AOPEvent> processedEncounterList = new LinkedList<PatientServiceAdvice.AOPEvent>();

	/**
	 * @see org.springframework.aop.AfterReturningAdvice#afterReturning(java.lang.Object,
	 *      java.lang.reflect.Method, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public void afterReturning(Object returnVal, Method method, Object[] args,
			Object target) throws Throwable {
		// try {
		GetPatientUtil getPatientUtil = new GetPatientUtil();
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
		if (method.getName().equals("saveEncounter")
				|| method.getName().equals("updateEncounter")) {
			
			// ArrayList from global property that contains allowed
			// encounterType.
			String[] allowedEncounterType = Context.getAdministrationService()
					.getGlobalProperty(RHEAConstants.ENCOUNTER_TYPE).split(",");
			Encounter encounter = (Encounter) args[0];
			// For the encounter type "Registration" check the service
			// selected and if this is one of the service that are in
			// allowed encounter type, request patient data from SHR
			String encounterTypeName = encounter.getEncounterType().getName();
			log.info("encounterTypeName " + encounterTypeName);
			if ("Registration".equalsIgnoreCase(encounterTypeName)) {
				for (Obs obs : encounter.getAllObs()) {
					if(obs.getValueCoded() != null){
					String obsAnswer = obs.getValueCoded().getDisplayString();
					// for (int i = 0; i < allowedEncounterType.length; i++)
					// {
					if ("ANTENATAL CLINIC".equalsIgnoreCase(obsAnswer)) {
						
						// HACK: Idempotency check (message uniqueness)
						Encounter returnedEnc = (Encounter) returnVal;
						Integer id = returnedEnc.getId();
						synchronized(processedEncounterList) {
							if (id != null && PatientServiceAdvice.isEventWithinDiffPeriod(processedEncounterList, id)) {
								return;
							}
						}
						// /HACK
						
						// Querry SHR for patient encounters
						Patient patient = Context.getPatientService()
								.getPatient(
										obs.getPerson().getPersonId()
												.intValue());
						log.info("find patient " + patient.getPatientId()
								+ " encounters from SHR");
						getPatientUtil.getPatientData(patient);
						break;
						// }
					}
					}
					// log.info(obsAnswer);
				}

			}
			
			// Get all encounter and check if this is one of the allowed
			// encounter, then send this to SHR
			boolean correctEncounter = false;

			for (int i = 0; i < allowedEncounterType.length; i++) {
				allowedEncounterType[i] = allowedEncounterType[i].trim();
				if (allowedEncounterType[i].equalsIgnoreCase(encounterTypeName
						.trim())) {
					correctEncounter = true;
					break;
				}
			}
			if (correctEncounter) {
				
				// HACK: Idempotency check (message uniqueness)
				synchronized(this) {
					Encounter returnedEnc = (Encounter) returnVal;
					Integer id = returnedEnc.getId();
					synchronized(processedEncounterList) {
						if (id != null && PatientServiceAdvice.isEventWithinDiffPeriod(processedEncounterList, id)) {
							return;
						}
					}
				}
				// /HACK

				SharedHealthRecordService sharedHealthRecordService = new SharedHealthRecordService();
				// Set<PatientIdentifier>
				Patient patient = encounter.getPatient();
				String clientId = "";
				PatientIdentifierType nid = Context.getPatientService()
						.getPatientIdentifierTypeByName("NID");
				PatientIdentifierType mutuelle = Context.getPatientService()
						.getPatientIdentifierTypeByName("Mutuelle");
				PatientIdentifierType rama = Context.getPatientService()
						.getPatientIdentifierTypeByName("RAMA");
				PatientIdentifierType primaryCare = Context.getPatientService()
						.getPatientIdentifierTypeByName("Primary Care ID Type");

				if (getPatientIdentifierByIdentifierType(patient, nid) != null) {
					clientId = nid
							+ "-"
							+ getPatientIdentifierByIdentifierType(patient, nid);
				} else if (getPatientIdentifierByIdentifierType(patient, rama) != null) {
					clientId = rama
							+ "-"
							+ getPatientIdentifierByIdentifierType(patient,
									rama);
				} else if (getPatientIdentifierByIdentifierType(patient,
						mutuelle) != null) {
					clientId = mutuelle
							+ "-"
							+ getPatientIdentifierByIdentifierType(patient,
									mutuelle);
				} else if (getPatientIdentifierByIdentifierType(patient,
						primaryCare) != null) {
					implementationId = implementationId.toLowerCase();
					String fosaid = implementationId.substring(implementationId
							.indexOf("rwanda") + 6);
					clientId = "OMRS"
							+ fosaid
							+ "-"
							+ getPatientIdentifierByIdentifierType(patient,
									primaryCare);
				}
				if (clientId == "") {

					clientId = patient.getPatientIdentifier()
							.getIdentifierType()
							+ "-"
							+ patient.getPatientIdentifier().getIdentifier();
				}

				log.info("ClientId from Advice =" + clientId);
				sharedHealthRecordService.savePatientEncounter(encounter,
						clientId);

			} else
				log.info("correctEncounter " + correctEncounter);
		}
		// } catch (Exception e) {
		// log.info(e.getMessage());
		// e.printStackTrace();
		//
		// }
	}

	private PatientIdentifier getPatientIdentifierByIdentifierType(
			Patient patient, PatientIdentifierType idType) {
		return ((patient.getPatientIdentifier(idType) != null) && (patient
				.getPatientIdentifier(idType).getIdentifierType()
				.equals(idType))) ? (patient.getPatientIdentifier(idType))
				: null;

	}
}
