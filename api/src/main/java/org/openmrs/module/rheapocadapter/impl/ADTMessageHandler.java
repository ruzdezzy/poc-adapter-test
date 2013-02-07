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
package org.openmrs.module.rheapocadapter.impl;

/**
 *
 */

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.PatientIdentifierException;
import org.openmrs.api.context.Context;
import org.openmrs.module.rheapocadapter.util.AttributeList;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.validator.PatientIdentifierValidator;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.datatype.CX;
import ca.uhn.hl7v2.model.v25.datatype.ID;
import ca.uhn.hl7v2.model.v25.datatype.TS;
import ca.uhn.hl7v2.model.v25.datatype.XAD;
import ca.uhn.hl7v2.model.v25.datatype.XPN;
import ca.uhn.hl7v2.model.v25.message.ADT_A05;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import ca.uhn.hl7v2.model.v25.segment.NK1;
import ca.uhn.hl7v2.model.v25.segment.PID;

/* HL7 using HAPI to handle ADT A28 Messages
 * 
 * ADT/ACK - Add person or patient information (Event A28)
 * 
 * FYI:  The 3rd field of MSH contains the sending application.  
 * For example, the Rwanda lab system uses 'neal_lims'.  
 * If neal_lims exists as an OpenMRS username, then this handler
 * will use that user as the creator for patients it creates.
 * If the sending application isn't setup as an OpenMRS user,
 * the creator will default to the user running this task.
 *  
 * TODO: You may wonder why the createPatient, validate, getMSH, 
 * getPIH and tsToDate code is duplicated in this file (and the R01
 * message handler file? It would be more useful to have these in the 
 * HL7 Utility file.  It's a good question, and it will happen 
 * soon.
 * 
 * The HL7 v2.5 manual table 0354 (section 2.17.3) describes A28.
 *   
 * There are many cases in HL7 where events (like A05, A14, A28, and A31) 
 * share a common structure.  This table also represented in HL7APIs 
 * eventmap properties file (http://tinyurl.com/2almfx)  -- describes 
 * exactly which events share which structures.
 *
 * So the answer to the A28 event is to use the ADT_A05 message 
 * structure from within the v2.5 object hierarchy.  Without going 
 * to the table, you can see this relationship in the description 
 * of the A28 event message structure (3.3.28), which is labeled as 
 * ADT^A28^ADT_A05.  This represents the message type (ADT), 
 * event (A28), and message structure (ADT_A05).
 * 
 * TODO: This ADT A28 handler does NOT currently handle ALL possible segments.
 * 		 Some of the segments that are not handled include these:
 * 			
 * 			EVN (Event type) - required to be backwardly compatible
 * 			SFT (Software segment)
 * 			PD1 (Additional demographics) (*)
 * 			ROL (Role)
 * 			NK1 (Next of Kin / associated parties) (*)
 * 			PV1/2 (Patient visit - additional information) (*)
 * 			DB1 (Disability information)
 * 			OBX (Observation / result)  (***)
 * 			AL1 (Allergy information)
 * 			DG1 (Diagnosis information)
 * 			DRG (Diagnosis related group)
 * 			PR1	(Procedures)
 * 			GT1 (Guarantor)
 * 			IN1 (Insurance)
 * 			ACC (Accident information)
 * 			UB1/2 (Universal Bill Information)
 * 
 *  NOTE:  The ones with (*) could be useful in the near future.
 */

public class ADTMessageHandler {

	private Log log = LogFactory.getLog(ADTMessageHandler.class);

	/**
	 * Always returns true, assuming that the router calling this handler will
	 * only call this handler with ADT_A28 messages.
	 * 
	 * @return true
	 */
	public boolean canProcess(Message message) {
		if (!message.equals(null) && "ADT_A28".equals(message.getName())) {
			return true;
		} else if (!message.equals(null) && "ADT_A05".equals(message.getName())) {
			return true;
		} else if (!message.equals(null) && "ADT_A31".equals(message.getName())) {
			return true;
		} else
			return false;
	}

	/**
	 * Processes an ADT A28 event message
	 */
	public Message processMessage(Message message) throws ApplicationException {

		log.debug("Processing ADT_A28 message");

		if (!(message instanceof ADT_A05))
			throw new ApplicationException(
					"Invalid message sent to ADT_A28 handler");

		Message response;
		try {
			ADT_A05 adt = (ADT_A05) message;
			response = adt;
		} catch (ClassCastException e) {
			log.error("Error casting " + message.getClass().getName()
					+ " to ADT_A28", e);
			throw new ApplicationException("Invalid message type for handler");
		}

		log.debug("Finished processing ADT_A28 message");

		return response;
	}

	private Message processADT_A28(ADT_A05 adt) {
		try {
			// validate HL7 version
			validate(adt);

			// extract segments for convenient use below
			MSH msh = getMSH(adt);
			PID pid = getPID(adt);

			// Obtain message control id (unique ID for message from sending
			// application). Eventually avoid replaying the same message.
			String messageControlId = msh.getMessageControlID().getValue();
			log.debug("Found HL7 message in inbound queue with control id = "
					+ messageControlId);

			// Add creator of the patient to application
			String sendingApp = msh.getSendingApplication().getComponent(0)
					.toString();
			log.debug("SendingApplication = " + sendingApp);

			// Search for the patient
			Integer patientId = findPatientId(pid);

			// Create new patient if the patient id doesn't exist yet
			if (patientId != null) {
				log.info("Creating new patient in response to ADT_A28 "
						+ messageControlId);
				Patient patient = createPatient(pid, sendingApp);
				if (patient == null)
					throw new HL7Exception(
							"Couldn't create Patient object from PID");
				// Context.getPatientService().savePatient(patient);

			} else {
				// TODO: Add a global property that enables different behavior
				// here.
				log.info("Ignoring ADT_A28 message because patient ("
						+ patientId + ") already exists.");
			}

			// Assumption: all observations (OBX) messages will be in the R01

			return adt;
		} catch (HL7Exception e) {
			log.error("Error while processing ADT_A28 message" + e.getMessage());
			// e.printStackTrace();

			return null;
		}
	}

	// Look for patient using the patient id
	private Integer findPatientId(PID pid) throws HL7Exception {

		return resolvePatientIds(pid);
		// if (patientId == null) {
		// PatientService ps = Context.getPatientService();
		// for (Patient p : ps.getAllPatients()) {
		// List<PatientIdentifier> patientIds = p.getActiveIdentifiers();
		// if (patientIds.contains(patientId)) {
		// return patientId;
		// }
		// }
		// }
		// return null;
	}

	// Create a new patient when this patient doesn't exist in the database
	private Patient createPatient(PID pid, String creatorName)
			throws HL7Exception {

		Patient patient = new Patient();
		User creator = null;
		// Try to use the specified username as the creator
		if (creatorName != null && creatorName != "") {
			creator = Context.getUserService().getUserByUsername(creatorName);
		} else {
			creator = Context.getAuthenticatedUser();
		}
		if (creator != null) {
			patient.setCreator(creator);
		}

		// Create all patient identifiers specified in the message
		// Copied code from resolvePatientId() in HL7ServiceImpl.java
		CX[] idList = pid.getPatientIdentifierList();
		if (idList == null || idList.length < 1)
			throw new HL7Exception("Missing patient identifier in PID segment");

		List<PatientIdentifier> goodIdentifiers = new ArrayList<PatientIdentifier>();
		for (CX id : idList) {

			String assigningAuthority = id.getIdentifierTypeCode().getValue();
			String hl7PatientId = id.getIDNumber().getValue();

			log.info("identifier has id=" + hl7PatientId
					+ " assigningAuthority=" + assigningAuthority);

			if (assigningAuthority != null && assigningAuthority.length() > 0) {

				try {
					PatientIdentifierType pit = Context.getPatientService()
							.getPatientIdentifierTypeByName(assigningAuthority);
					if (pit == null) {
						log.warn("Can't find PatientIdentifierType named '"
								+ assigningAuthority + "'");
						continue; // skip identifiers with unknown type
					}
					PatientIdentifier pi = new PatientIdentifier();
					if (creator != null) {
						pi.setCreator(creator);
					}
					pi.setIdentifierType(pit);
					pi.setIdentifier(hl7PatientId);
					// Get default location
					Location location = Context.getLocationService()
							.getDefaultLocation();
					if (location == null) {
						throw new HL7Exception("Cannot find default location");
					}
					pi.setLocation(location);

					try {

						PatientIdentifierValidator.validateIdentifier(pi);
						goodIdentifiers.add(pi);
					} catch (PatientIdentifierException ex) {
						log.error(ex.getMessage());
						if (Context.getPatientService()
								.isIdentifierInUseByAnotherPatient(pi)) {
							log.info(
									"Patient identifier in PID already exist in local system : "
											+ pi, ex);
							List<Patient> pats = Context.getPatientService()
									.getPatients(null, pi.getIdentifier(),
											null, true);
							if (pats.size() > 0) {
								return pats.get(0);
							}
						} else
							log.info("Patient identifier in PID is invalid : "
									+ pi, ex);
					}

				} catch (Exception e) {
					log.error(
							"Uncaught error parsing/creating patient identifier '"
									+ hl7PatientId
									+ "' for assigning authority '"
									+ assigningAuthority + "'", e);
				}
			}

			else {
				log.debug("PID contains identifier with no assigning authority");
				continue;
			}
		}
		if (goodIdentifiers.size() == 0) {
			throw new HL7Exception(
					"PID segment has no recognizable patient identifiers.");
		}
		patient.addIdentifiers(goodIdentifiers);

		// Extract patient name from the message
		XPN patientNameX = pid.getPatientName(0);
		if (patientNameX == null)
			throw new HL7Exception("Missing patient name in the PID segment");

		// Patient name
		PersonName name = new PersonName();
		name.setFamilyName(patientNameX.getFamilyName().getSurname().getValue());
		name.setGivenName(patientNameX.getGivenName().getValue());
		name.setMiddleName(patientNameX
				.getSecondAndFurtherGivenNamesOrInitialsThereof().getValue());
		if (creator != null) {
			name.setCreator(creator);
		}
		patient.addName(name);

		// Gender (checks for null, but not for 'M' or 'F')
		String gender = pid.getAdministrativeSex().getValue();
		if (gender == null)
			throw new HL7Exception("Missing gender in the PID segment");
		gender = gender.toUpperCase();
		if (!OpenmrsConstants.GENDER().containsKey(gender))
			throw new HL7Exception("Unrecognized gender: " + gender);
		patient.setGender(gender);

		// Date of Birth
		TS dateOfBirth = pid.getDateTimeOfBirth();
		if (dateOfBirth == null || dateOfBirth.getTime() == null
				|| dateOfBirth.getTime().getValue() == null)
			throw new HL7Exception("Missing birth date in the PID segment");
		patient.setBirthdate(tsToDate(dateOfBirth));

		// Estimated birthdate?
		ID precisionTemp = dateOfBirth.getDegreeOfPrecision();
		if (precisionTemp != null && precisionTemp.getValue() != null) {
			String precision = precisionTemp.getValue().toUpperCase();
			log.debug("The birthdate is estimated: " + precision);

			if (precision.equals("Y") || precision.equals("L"))
				patient.setBirthdateEstimated(true);
		}
		// Address
		try {
			XAD add = pid.getPatientAddress(0);
			PersonAddress pAddress = new PersonAddress();
			pAddress.setCountry(add.getCountry().getValue());
			pAddress.setStateProvince(add.getStateOrProvince().getValue());
			pAddress.setCountyDistrict(add.getCity().getValue());
			pAddress.setAddress1(add.getCensusTract().getValue());
			pAddress.setNeighborhoodCell(add.getOtherGeographicDesignation()
					.getValue());
			pAddress.setCityVillage(add.getCountyParishCode().getValue());
			patient.addAddress(pAddress);
			log.info("Add size >>>>>>> " + patient.getAddresses().size());
		} catch (Exception e) {
			log.error("Error while creating person address : " + e.getMessage());
		}
		return patient;
	}

	private void validate(Message message) throws HL7Exception {
		if (!canProcess(message)) {
			throw new HL7Exception("Message not valid");
		}
	}

	private MSH getMSH(ADT_A05 adt) {
		return adt.getMSH();
	}

	private PID getPID(ADT_A05 adt) {
		try {
			PID pid = (PID) adt.get("PID");
			return pid;
		} catch (HL7Exception e) {

			log.error("Error generated in getPID " + e.getMessage());

		}
		return null;
	}

	// TODO: Debug (and use) methods in HL7Util instead
	private Date tsToDate(TS ts) throws HL7Exception {
		// need to handle timezone
		String dtm = ts.getTime().getValue();
		int year = Integer.parseInt(dtm.substring(0, 4));
		int month = (dtm.length() >= 6 ? Integer.parseInt(dtm.substring(4, 6)) - 1
				: 0);
		int day = (dtm.length() >= 8 ? Integer.parseInt(dtm.substring(6, 8))
				: 1);
		int hour = (dtm.length() >= 10 ? Integer.parseInt(dtm.substring(8, 10))
				: 0);
		int min = (dtm.length() >= 12 ? Integer.parseInt(dtm.substring(10, 12))
				: 0);
		int sec = (dtm.length() >= 14 ? Integer.parseInt(dtm.substring(12, 14))
				: 0);
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, hour, min, sec);

		return cal.getTime();
	}

	public Patient getPatient(ADT_A05 adt) throws HL7Exception {

		// validate HL7 version
		validate(adt);

		// extract segments for convenient use below
		MSH msh = getMSH(adt);
		PID pid = getPID(adt);

		// Obtain message control id (unique ID for message from sending
		// application). Eventually avoid replaying the same message.
		String messageControlId = msh.getMessageControlID().getValue();
		log.debug("Found HL7 message in inbound queue with control id = "
				+ messageControlId);

		// Add creator of the patient to application
		String sendingApp = msh.getSendingApplication().getComponent(0)
				.toString();
		log.debug("SendingApplication = " + sendingApp);

		// Search for the patient
		Integer patientId = findPatientId(pid);
		log.info("==> " + patientId);
		// Create new patient if the patient id doesn't exist yet
		if (patientId == null) {
			log.info("Creating new patient in response to ADT_A28 "
					+ messageControlId);
			Patient patient = createPatient(pid, sendingApp);
			if (patient == null)
				throw new HL7Exception(
						"Couldn't create Patient object from PID");
			return patient;

		} else {
			// TODO: Add a global property that enables different behavior here.
			log.info("Ignoring ADT_A28 message because patient (" + patientId
					+ ") already exists.");
			return null;
		}
	}

	public Integer resolvePatientIds(PID pid) throws HL7Exception {
		// TODO: Properly handle assigning authority. If specified it's
		// currently treated as PatientIdentifierType.name
		// TODO: Throw exceptions instead of returning null in some cases
		// TODO: Don't hydrate Patient objects unnecessarily
		// TODO: Determine how to handle assigning authority and openmrs
		// patient_id numbers

		Integer patientId = null;

		CX[] patientIdentifierList = pid.getPatientIdentifierList();
		if (patientIdentifierList.length < 1)
			throw new HL7Exception("Missing patient identifier in PID segment");

		// TODO other potential identifying characteristics in PID we could use
		// to identify the patient
		// XPN[] patientName = pid.getPersonName();
		// String gender = pid.getAdministrativeSex().getValue();
		// TS dateOfBirth = pid.getDateTimeOfBirth();

		// Take the first uniquely matching identifier
		for (CX identifier : patientIdentifierList) {
			String hl7PatientId = identifier.getIDNumber().getValue();
			// TODO if 1st component is blank, check 2nd and 3rd of assigning
			// authority
			String assigningAuthority = identifier.getAssigningAuthority()
					.getNamespaceID().getValue();

			if (assigningAuthority != null && assigningAuthority.length() > 0) {
				// Assigning authority defined
				try {
					PatientIdentifierType pit = Context.getPatientService()
							.getPatientIdentifierTypeByName(assigningAuthority);
					if (pit == null) {
						log.warn("Can't find PatientIdentifierType named '"
								+ assigningAuthority + "'");
						continue; // skip identifiers with unknown type
					}
					List<PatientIdentifier> matchingIds = Context
							.getPatientService().getPatientIdentifiers(
									hl7PatientId,
									Collections.singletonList(pit), null, null,
									null);
					if (matchingIds == null || matchingIds.size() < 1) {
						// no matches
						log.warn("NO matches found for " + hl7PatientId);
						continue; // try next identifier
					} else if (matchingIds.size() == 1) {
						// unique match -- we're done
						return matchingIds.get(0).getPatient().getPatientId();
					} else {
						// ambiguous identifier
						log.debug("Ambiguous identifier in PID. "
								+ matchingIds.size()
								+ " matches for identifier '" + hl7PatientId
								+ "' of type '" + pit + "'");
						continue; // try next identifier
					}
				} catch (Exception e) {
					log.error("Error resolving patient identifier '"
							+ hl7PatientId + "' for assigning authority '"
							+ assigningAuthority + "'", e);
					continue;
				}
			} else {
				try {
					log.debug("PID contains patient ID '"
							+ hl7PatientId
							+ "' without assigning authority -- assuming patient.patient_id");
					patientId = Integer.parseInt(hl7PatientId);
					return patientId;
				} catch (NumberFormatException e) {
					// throw new HL7Exception("Invalid patient ID '" +
					// hl7PatientId + "'");
					log.warn("Invalid patient ID '" + hl7PatientId + "'");
				}
			}
		}

		return null;
	}

	public AttributeList getPatientWithAttribute(ADT_A05 adt)
			throws HL7Exception {

		// validate HL7 version
		validate(adt);

		// extract segments for convenient use below
		MSH msh = getMSH(adt);
		PID pid = getPID(adt);
		NK1 nk1Mother = adt.getNK1(0);
		NK1 nk1Father = adt.getNK1(1);
		AttributeList attributeList = null;
		// Obtain message control id (unique ID for message from sending
		// application). Eventually avoid replaying the same message.
		String messageControlId = msh.getMessageControlID().getValue();
		log.debug("Found HL7 message in inbound queue with control id = "
				+ messageControlId);

		// Add creator of the patient to application
		String sendingApp = msh.getSendingApplication().getComponent(0)
				.toString();
		log.debug("SendingApplication = " + sendingApp);

		// Create new patient
		log.info("Creating new patient in response to ADT_A28 "
				+ messageControlId);
		Patient patient = createPatient(pid, sendingApp);
		if (patient == null)
			throw new HL7Exception(
					"Couldn't create Patient object from PID");
		String father = "";
		String mother = "";
		log.info(nk1Mother.getRelationship().getIdentifier().getValue()
				+ "=>"
				+ nk1Mother.getNKName(0).getGivenName()
				+ " "
				+ nk1Mother.getNKName(0).getFamilyName().getSurname().getValue());
		log.info(nk1Father.getRelationship().getIdentifier().getValue()
				+ "=>"
				+ nk1Father.getNKName(0).getGivenName()
				+ " "
				+ nk1Father.getNKName(0).getFamilyName().getSurname().getValue());
		mother = nk1Mother.getNKName(0).getFamilyName().getSurname().getValue();
		father = nk1Father.getNKName(0).getFamilyName().getSurname().getValue();
		attributeList = new AttributeList(patient, mother, father, "");

		return attributeList;

	}

}
