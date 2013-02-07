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
package org.openmrs.module.rheapocadapter.service;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v24.message.ADT_A01;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.model.v24.segment.PID;
import ca.uhn.hl7v2.parser.DefaultXMLParser;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * Example transmitting a message
 * 
 * @author <a href="mailto:jamesagnew@sourceforge.net">James Agnew</a>
 * @version $Revision: 1.4 $ updated on $Date: 2009-10-03 15:29:05 $ by $Author: jamesagnew $
 */
public class CreateAMessage {
	
	/**
	 * @param args
	 * @throws HL7Exception
	 */
	public static void main(String[] args) throws HL7Exception {
		
		ADT_A01 adt = new ADT_A01();
		
		// Populate the MSH Segment
		MSH mshSegment = adt.getMSH();
		mshSegment.getFieldSeparator().setValue("|");
		mshSegment.getEncodingCharacters().setValue("^~\\&");
		mshSegment.getDateTimeOfMessage().getTimeOfAnEvent().setValue("200701011539");
		mshSegment.getSendingApplication().getNamespaceID().setValue("TestSendingSystem");
		mshSegment.getSequenceNumber().setValue("123");
		mshSegment.getMessageType().getMessageType().setValue("ADT");
		mshSegment.getMessageType().getTriggerEvent().setValue("A01");
		mshSegment.getMessageType().getMessageStructure().setValue("ADT_A01");
		
		// Populate the PID Segment
		PID pid = adt.getPID();
		pid.getPatientName(0).getFamilyName().getSurname().setValue("Doe");
		pid.getPatientName(0).getGivenName().setValue("John");
		pid.getPatientIdentifierList(0).getID().setValue("123456");
		
		/*
		 * In a real situation, of course, many more segments and fields would be populated
		 */
		
		// Now, let's encode the message and look at the output
		Parser parser = new PipeParser();
		String encodedMessage = parser.encode(adt);
		System.out.println("Printing ER7 Encoded Message:");
		System.out.println(encodedMessage);
		
		/*
		 * Prints:
		 * 
		 * MSH|^~\&|TestSendingSystem||||200701011539||ADT^A01^ADT A01||||123
		 * PID|||123456||Doe^John
		 */
		
		// Next, let's use the XML parser to encode as XML          
		parser = new DefaultXMLParser();
		encodedMessage = parser.encode(adt);
		System.out.println("Printing XML Encoded Message:");
		System.out.println(encodedMessage);
		
	}
}
