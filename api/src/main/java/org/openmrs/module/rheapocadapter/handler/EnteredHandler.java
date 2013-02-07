package org.openmrs.module.rheapocadapter.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.module.rheapocadapter.RHEAConstants;
import org.openmrs.module.rheapocadapter.service.TransactionService;
import org.openmrs.module.rheapocadapter.transaction.ArchiveTransaction;
import org.openmrs.module.rheapocadapter.transaction.ErrorTransaction;
import org.openmrs.module.rheapocadapter.transaction.ProcessingTransaction;
import org.openmrs.module.rheapocadapter.transaction.Transaction;

public class EnteredHandler {
	private TransactionService queueService;

	Log log = LogFactory.getLog(this.getClass());

	/**
	 * @return the queueService
	 */
	public TransactionService getQueueService() {
		setQueueService();
		return queueService;
	}

	/**
	 * @param queueService
	 *            the queueService to set
	 */
	public void setQueueService(TransactionService queueService) {
		this.queueService = ServiceContext.getInstance().getService(
				TransactionService.class);
	}

	public EnteredHandler() {

	}

	public void sendBackEntered() {
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
		List<Encounter> encounters = getEncounterNotSent();
		for (Encounter e : encounters) {
			String clientId = "";
			Patient patient = e.getPatient();
			if (patient.getPatientIdentifier("NID") != null) {
				clientId = "NID-" + patient.getPatientIdentifier("NID");
			} else if (patient.getPatientIdentifier("Mutuelle") != null) {
				clientId = "Mutuelle-"
						+ patient.getPatientIdentifier("Mutuelle");
			} else if (patient.getPatientIdentifier("RAMA") != null) {
				clientId = "RAMA-" + patient.getPatientIdentifier("RAMA");
			} else {
				if (patient.getPatientIdentifier().getIdentifierType()
						.getName().equals("Primary Care ID Type")) {
					implementationId = implementationId.toLowerCase();
					String fosaid = implementationId.substring(implementationId
							.indexOf("rwanda") + 6);

					clientId = "OMRS" + fosaid + "-"
							+ patient.getPatientIdentifier().getIdentifier();
				} else {
					clientId = patient.getPatientIdentifier()
							.getIdentifierType().getName()
							+ "-" + patient.getPatientIdentifier();
				}
			}
			log.info("ClientId from BackEnteredHandler =" + clientId);
			sharedHealthRecordService.savePatientEncounter(e, clientId);

		}
	}

	public Map<Patient, List<Encounter>> groupEncounterByPatient(
			List<Encounter> encounters) {
		setQueueService();
		Map<Patient, List<Encounter>> group = new TreeMap<Patient, List<Encounter>>();
		while (!encounters.isEmpty()) {

			Patient p = encounters.get(0).getPatient();
			List<Encounter> patientEncounter = getEncounterForPatient(
					encounters, p);
			group.put(p, patientEncounter);
			encounters = removeEncounters(patientEncounter, encounters);

		}

		return null;

	}

	private List<Encounter> removeEncounters(List<Encounter> patientEncounter,
			List<Encounter> encounters) {
		for (Encounter e : patientEncounter) {
			encounters.remove(encounters.indexOf(e));
		}
		return encounters;
	}

	private List<Encounter> getEncounterForPatient(List<Encounter> encounters,
			Patient p) {
		List<Encounter> patientEncounter = new ArrayList<Encounter>();
		for (Encounter e : encounters) {
			if (e.getPatientId() == p.getPatientId()) {
				patientEncounter.add(e);
			}
		}
		return patientEncounter;
	}

	/**
	 * fetch from DB all processing queues, and delete them from databases, if
	 * the send fails, it's saved gain.
	 * 
	 * @return
	 */
	public List<? extends Transaction> getProcessingQueue() {
		log.info("ProcessingQueueHandler started");
		return getQueueService().getAllQueue(
				new ProcessingTransaction().getClass());
	}

	public List<? extends Transaction> getArchiveQueue() {
		log.info("ArchiveTransaction started");
		return getQueueService().getAllQueue(
				new ArchiveTransaction().getClass());

	}

	public List<? extends Transaction> getErrorQueue() {
		log.info("ErrorTransaction started");
		return getQueueService().getAllQueue(new ErrorTransaction().getClass());

	}

	@SuppressWarnings("unchecked")
	public List<Encounter> getEncounterNotSent() {

		List<Encounter> encounters = new ArrayList<Encounter>();
		List<Encounter> encounterToSend = new ArrayList<Encounter>();

		List<EncounterType> encTypes = new ArrayList<EncounterType>();
		String types = Context.getAdministrationService().getGlobalProperty(
				"rheapocadapter.encounterType");
		String[] encounterTypes = types.split(",");

		for (String s : encounterTypes) {
			encTypes.add(Context.getEncounterService().getEncounterType(
					s.trim()));
		}

		encounters = Context.getEncounterService().getEncounters(null, null,
				null, null, null, (Collection) encTypes, null, false);
		// Context.getEncounterService().getEncounters(arg0, arg1, arg2,
		// arg3,arg4, arg5, arg6, arg7)
		// for (Patient patient : Context.getPatientService().getAllPatients())
		// {
		// encounters.addAll(Context.getEncounterService()
		// .getEncountersByPatient(patient));
		// }
		List<ProcessingTransaction> processingTransaction = (List<ProcessingTransaction>) getProcessingQueue();
		List<ArchiveTransaction> archiveTransaction = (List<ArchiveTransaction>) getArchiveQueue();
		List<ErrorTransaction> errorTransaction = (List<ErrorTransaction>) getErrorQueue();
		// encounters = Context.getEncounterService().getEncounters;
		List<Encounter> encNotInError = new ArrayList<Encounter>();
		List<Encounter> encNotInProcessing = new ArrayList<Encounter>();
		List<Encounter> encNotInArchive = new ArrayList<Encounter>();

		for (Encounter encounter : encounters) {
			boolean exist = false;
			for (Transaction trans : processingTransaction) {
				String idInMessage = trans.getMessage().trim();
				idInMessage.replaceAll("\\ ", "");
				// log.info(idInMessage.substring(idInMessage
				// .indexOf("EncounterId") + "EncounterId".length()+3)
				// + " Encounter Id in ProcessingMessage ");
				if (idInMessage.startsWith("EncounterId")) {
					// log.info(idInMessage + " Encounter Id in Message ");
					idInMessage = idInMessage.split("=")[1];
					idInMessage=idInMessage.trim();
					// log.info(idInMessage + " Encounter Id in Message ");

					if (Integer.parseInt(idInMessage) == encounter.getId()) {
						// log.info("From Trans" + Integer.parseInt(idInMessage)
						// + " and from Enc " + encounter.getId());
						exist = true;

					}
				}
			}
			if (!exist) {
				encNotInProcessing.add(encounter);
			}
		}
		// log.info("get encNotInProcessing started " +
		// encNotInProcessing.size());
		for (Encounter encounter : encNotInProcessing) {
			boolean exist = false;
			for (Transaction trans : archiveTransaction) {
				String idInMessage = trans.getMessage().trim();
				idInMessage.replaceAll("\\ ", "");
				if (idInMessage.startsWith("EncounterId")) {
					// log.info(idInMessage + " Encounter Id in Message ");
					idInMessage = idInMessage.split("=")[1];
					idInMessage=idInMessage.trim();
					// log.info(idInMessage + " Encounter Id in Message ");

					if (Integer.parseInt(idInMessage) == encounter.getId()) {
						// log.info("From Trans" + Integer.parseInt(idInMessage)
						// + " and from Enc " + encounter.getId());
						exist = true;
					}

				}
			}
			if (!exist) {
				encNotInArchive.add(encounter);
			}
		}
		for (Encounter encounter : encNotInArchive) {
			boolean exist = false;
			for (Transaction trans : errorTransaction) {
				String idInMessage = trans.getMessage().trim();
				idInMessage.replaceAll("\\ ", "");
				if (idInMessage.startsWith("EncounterId")) {
					// log.info(idInMessage + " Encounter Id in Message ");
					idInMessage = idInMessage.split("=")[1];
					idInMessage=idInMessage.trim();
					// log.info(idInMessage + " Encounter Id in Message ");

					if (Integer.parseInt(idInMessage) == encounter.getId()) {
						// log.info("From Trans" + Integer.parseInt(idInMessage)
						// + " and from Enc " + encounter.getId());
						exist = true;
					}

				}
			}
			if (!exist) {
				encNotInError.add(encounter);
			}
		}

		// log.info("get encNotInArchive started " + encNotInArchive.size());
		String[] allowedEncounterType = Context.getAdministrationService()
				.getGlobalProperty(RHEAConstants.ENCOUNTER_TYPE).split(",");
		for (Encounter encounter : encNotInError) {
			for (int i = 0; i < allowedEncounterType.length; i++) {
				if (allowedEncounterType[i].equalsIgnoreCase(encounter
						.getEncounterType().getName())) {
					encounterToSend.add(encounter);
				}
			}
		}
		// encounterToSend.
		return encounterToSend;
	}

	public List<Encounter> getPatientsEncounterNotSent(Patient pat) {

		List<Encounter> encounters = new ArrayList<Encounter>();
		List<Encounter> encounterToSend = new ArrayList<Encounter>();
		// for (Patient patient : Context.getPatientService().getAllPatients())
		// {
		// encounters.addAll(Context.getEncounterService()
		// .getEncountersByPatient(patient));
		// }

		encounters.addAll(Context.getEncounterService().getEncountersByPatient(
				pat));

		List<ProcessingTransaction> processingTransaction = (List<ProcessingTransaction>) getProcessingQueue();
		List<ArchiveTransaction> archiveTransaction = (List<ArchiveTransaction>) getArchiveQueue();
		List<Encounter> encNotInError = new ArrayList<Encounter>();
		List<Encounter> encNotInProcessing = new ArrayList<Encounter>();
		List<Encounter> encNotInArchive = new ArrayList<Encounter>();

		for (Encounter encounter : encounters) {
			boolean exist = false;
			for (Transaction trans : processingTransaction) {
				try {
					if (trans.getMessage().equals(null)
							|| trans.getMessage() == null) {
						trans.setMessage(" ");
					}
				} catch (NullPointerException e) {
					trans.setMessage(" ");
				}
				String idInMessage = trans.getMessage().trim();
				idInMessage.replaceAll(" ", "");
				// log.info(idInMessage.substring(idInMessage
				// .indexOf("EncounterId") + "EncounterId".length()+3)
				// + " Encounter Id in ProcessingMessage ");
				if (idInMessage.startsWith("EncounterId")) {
					// log.info(idInMessage + " Encounter Id in Message ");
					idInMessage = idInMessage.substring(idInMessage
							.indexOf("EncounterId")
							+ "EncounterId".length()
							+ 3);
					// log.info(idInMessage + " Encounter Id in Message ");

					if (Integer.parseInt(idInMessage) == encounter.getId()) {
						// log.info("From Trans" + Integer.parseInt(idInMessage)
						// + " and from Enc " + encounter.getId());
						exist = true;

					}
				}
			}
			if (!exist) {
				encNotInProcessing.add(encounter);
			}
		}
		// log.info("get encNotInProcessing started " +
		// encNotInProcessing.size());
		for (Encounter encounter : encNotInProcessing) {
			boolean exist = false;
			for (Transaction trans : archiveTransaction) {
				String idInMessage = trans.getMessage().trim();
				idInMessage.replaceAll(" ", "");
				if (idInMessage.startsWith("EncounterId")) {
					// log.info(idInMessage + " Encounter Id in Message ");
					idInMessage = idInMessage.substring(idInMessage
							.indexOf("EncounterId")
							+ "EncounterId".length()
							+ 3);
					// log.info(idInMessage + " Encounter Id in Message ");

					if (Integer.parseInt(idInMessage) == encounter.getId()) {
						// log.info("From Trans" + Integer.parseInt(idInMessage)
						// + " and from Enc " + encounter.getId());
						exist = true;
					}

				}
			}
			if (!exist) {
				encNotInArchive.add(encounter);
			}
		}
		// log.info("get encNotInArchive started " + encNotInArchive.size());
		String[] allowedEncounterType = Context.getAdministrationService()
				.getGlobalProperty(RHEAConstants.ENCOUNTER_TYPE).split(",");
		for (Encounter encounter : encNotInArchive) {
			for (int i = 0; i < allowedEncounterType.length; i++) {
				if (allowedEncounterType[i].equalsIgnoreCase(encounter
						.getEncounterType().getName())) {
					encounterToSend.add(encounter);
				}
			}
		}
		for (Encounter encounter : encNotInProcessing) {
			for (int i = 0; i < allowedEncounterType.length; i++) {
				if (allowedEncounterType[i].equalsIgnoreCase(encounter
						.getEncounterType().getName())) {
					encounterToSend.add(encounter);
				}
			}
		}
		for (Encounter encounter : encNotInError) {
			for (int i = 0; i < allowedEncounterType.length; i++) {
				if (allowedEncounterType[i].equalsIgnoreCase(encounter
						.getEncounterType().getName())) {
					encounterToSend.add(encounter);
				}
			}
		}
		// encounterToSend.
		return encounterToSend;
	}

	public List<Encounter> getEncounterSent(Patient patient) {
		List<Encounter> encounterNotSent = getPatientsEncounterNotSent(patient);
		List<Encounter> patientEncounters = Context.getEncounterService()
				.getEncountersByPatient(patient);
		List<Encounter> encs = new ArrayList<Encounter>();
		for (Encounter e : patientEncounters) {
			if (!encounterNotSent.contains(e)
					&& (e.getPatient().equals(patient))) {
				encs.add(e);
			}
		}

		return encs;
	}

	public void setQueueService() {
		this.queueService = ServiceContext.getInstance().getService(
				TransactionService.class);
	}

}
