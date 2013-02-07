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
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.module.rheapocadapter.handler.ClientRegistryService;
import org.openmrs.module.rheapocadapter.handler.SharedHealthRecordService;
import org.openmrs.module.rheapocadapter.impl.ADTMessageHandler;
import org.openmrs.module.rheapocadapter.impl.HL7MessageTransformer;
import org.openmrs.module.rheapocadapter.service.MessageTransformer;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.message.ADT_A05;

/**
 *
 */
public class GetPatientUtil {

	protected final Log log = LogFactory.getLog(getClass());

	public List<Patient> getPatientFromClientReg(
			TreeMap<String, String> parameters) {
		List<Patient> results = new ArrayList<Patient>();
		try {
			ClientRegistryService registryService = new ClientRegistryService();
			String result = registryService.getClients(parameters);
			String[] messages = loadADTFromString(result);
			for (int i = 0; i < messages.length; i++) {
				MessageTransformer messageTransformer = new HL7MessageTransformer();
				ADTMessageHandler messageHandler = new ADTMessageHandler();
				Message message = (Message) messageTransformer
						.translateMessage(messages[i]);
				Message adtMessage;
				adtMessage = messageHandler.processMessage(message);
				Patient pat = messageHandler.getPatient((ADT_A05) adtMessage);
				if (pat != null) {
					results.add(pat);

				}
			}

		} catch (HL7Exception e) {
			log.error("Error generated" + e.getMessage());
		}

		catch (ApplicationException e) {
			// TODO Auto-generated catch block
			log.error("Error generated", e);
		}
		return results;
	}

	public List<AttributeList> getPatientWithAttributeListFromClientReg(
			TreeMap<String, String> parameters) {
		List<AttributeList> results = new ArrayList<AttributeList>();
		try {
			ClientRegistryService registryService = new ClientRegistryService();
			String result = registryService.getClients(parameters);
			String[] messages = loadADTFromString(result);
			for (int i = 0; i < messages.length; i++) {
				MessageTransformer messageTransformer = new HL7MessageTransformer();
				ADTMessageHandler messageHandler = new ADTMessageHandler();
				Message message = (Message) messageTransformer
						.translateMessage(messages[i]);
				Message adtMessage;
				adtMessage = messageHandler.processMessage(message);
				AttributeList pat = messageHandler
						.getPatientWithAttribute((ADT_A05) adtMessage);
				if (pat != null) {
					results.add(pat);

				}
			}

		} catch (HL7Exception e) {
			log.error("Error generated" + e.getMessage());
		}

		catch (ApplicationException e) {
			// TODO Auto-generated catch block
			log.error("Error generated", e);
		}
		return results;
	}

	public List<AttributeList> getPatientWirhAttributeFromClientRegById(
			String id) {
		List<AttributeList> results = new ArrayList<AttributeList>();
		try {
			ClientRegistryService registryService = new ClientRegistryService();
			String result = registryService.getClient(id);
			String[] messages = loadADTFromString(result);
			for (int i = 0; i < messages.length; i++) {
				HL7MessageTransformer messageTransformer = new HL7MessageTransformer();
				ADTMessageHandler messageHandler = new ADTMessageHandler();
				Message message = (Message) messageTransformer
						.translateMessage(messages[i]);
				Message adtMessage;
				adtMessage = messageHandler.processMessage(message);
				AttributeList pat = messageHandler
						.getPatientWithAttribute((ADT_A05) adtMessage);
				if (pat != null) {
					results.add(pat);

				}
			}

		} catch (HL7Exception e) {
			log.error("Error generated", e);
		}

		catch (ApplicationException e) {
			// TODO Auto-generated catch block
			log.error("Error generated", e);
		}
		return results;
	}

	public List<Patient> getPatientFromClientRegById(String id) {
		List<Patient> results = new ArrayList<Patient>();
		try {
			ClientRegistryService registryService = new ClientRegistryService();
			String result = registryService.getClient(id);
			String[] messages = loadADTFromString(result);
			for (int i = 0; i < messages.length; i++) {
				HL7MessageTransformer messageTransformer = new HL7MessageTransformer();
				ADTMessageHandler messageHandler = new ADTMessageHandler();
				Message message = (Message) messageTransformer
						.translateMessage(messages[i]);
				Message adtMessage;
				adtMessage = messageHandler.processMessage(message);
				Patient pat = messageHandler.getPatient((ADT_A05) adtMessage);
				if (pat != null) {
					results.add(pat);

				}
			}

		} catch (HL7Exception e) {
			log.error("Error generated", e);
		}

		catch (ApplicationException e) {
			// TODO Auto-generated catch block
			log.error("Error generated", e);
		}
		return results;
	}

	public String[] getHL7FromPipeString(String theSource) {
		ArrayList<String> messages = new ArrayList<String>(20);
		Pattern startPattern = Pattern.compile("^MSH", Pattern.MULTILINE);
		Matcher startMatcher = startPattern.matcher(theSource);

		while (startMatcher.find()) {
			String messageExtent = getMessageExtent(
					theSource.substring(startMatcher.start()), startPattern);

			char fieldDelim = messageExtent.charAt(3);
			Pattern segmentPattern = Pattern.compile("^[A-Z\\d]{3}\\"
					+ fieldDelim + ".*$", Pattern.MULTILINE);
			Matcher segmentMatcher = segmentPattern.matcher(messageExtent);
			StringBuffer msg = new StringBuffer();
			while (segmentMatcher.find()) {
				msg.append(segmentMatcher.group().trim());
				msg.append('\r');
			}
			messages.add(msg.toString());
		}
		return messages.toArray(new String[0]);
	}

	/**
	 * Given a string that contains at least one HL7 message, returns the
	 * smallest string that contains the first of these messages.
	 */
	public String getMessageExtent(String theSource, Pattern theStartPattern) {
		Matcher startMatcher = theStartPattern.matcher(theSource);
		if (!startMatcher.find()) {
			throw new IllegalArgumentException(theSource
					+ "does not contain message start pattern"
					+ theStartPattern.toString());
		}

		int start = startMatcher.start();
		int end = theSource.length();
		if (startMatcher.find()) {
			end = startMatcher.start();
		}

		return theSource.substring(start, end).trim();
	}

	public AttributeList getAttibuteListFromClientReg(
			TreeMap<String, String> parameters) {
		try {
			ClientRegistryService registryService = new ClientRegistryService();
			String result = registryService.getClients(parameters);
			String[] messages = getHL7FromPipeString(result);
			for (int i = 0; i < messages.length; i++) {
				HL7MessageTransformer messageTransformer = new HL7MessageTransformer();
				ADTMessageHandler messageHandler = new ADTMessageHandler();
				Message message = (Message) messageTransformer
						.translateMessage(messages[i]);
				Message adtMessage;
				adtMessage = messageHandler.processMessage(message);
				messageHandler.getPatient((ADT_A05) adtMessage);

				return messageHandler
						.getPatientWithAttribute((ADT_A05) adtMessage);
			}

		} catch (HL7Exception e) {

		}

		catch (ApplicationException e) {
			// TODO Auto-generated catch block
			log.error("Error generated", e);
		}
		return null;
	}

	private String[] loadADTFromString(String xml) {

		int firstOc = xml.indexOf("<ADT_A05>");
		int lastOc = xml.indexOf("</ADT_A05>") + 10;
		String subString = xml;
		ArrayList<String> messages = new ArrayList<String>(20);
		String x = "";
		boolean messPresent = containsMessage(xml);
		while (messPresent) {
			x = subString.substring(firstOc, lastOc);
			messages.add(x);
			subString = subString.substring(subString.indexOf(x) + x.length());
			messPresent = containsMessage(subString);
			if (messPresent) {

				firstOc = subString.indexOf("<ADT_A05>");
				lastOc = subString.indexOf("</ADT_A05>") + 10;
			}

		}
		return messages.toArray(new String[0]);

	}

	private boolean containsMessage(String xml) {
		return xml.contains("<ADT_A05>");
	}

	public void getPatientData(Patient patient) {

		String result = new SharedHealthRecordService()
				.getPatientClinicalDataFromSHR(patient);
		log.info(result);

	}
}
