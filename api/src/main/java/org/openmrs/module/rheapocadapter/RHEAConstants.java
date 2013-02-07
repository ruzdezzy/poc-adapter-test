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
package org.openmrs.module.rheapocadapter;

/**
 *
 */
public class RHEAConstants {
	public static final String MODULE_ID = "rheapocadapter";

	public static final int HTTP_OK = 200;

	public static final int HTTP_ERROR = 400;

	public static final int HTTP_SERVER_ERROR = 500;

	public static final String SERVER_HOSTNAME = MODULE_ID + ".hostname";

	public static final String SERVER_USER_NAME = MODULE_ID + ".username";

	public static final String SERVER_PASSWORD = MODULE_ID + ".password";
	
	public static final String NEXT_SCHEDULED_VISIT_CONCEPT_ID="concept.nextScheduledVisit";

	public static final String PATIENT_MULTIPLE_PATIENTS_URL_NAME = MODULE_ID
			+ ".getpatients";

	public static final String PATIENT_SINGLE_PATIENT_URL_NAME = MODULE_ID
			+ ".getpatient";

	public static final String MOTHER_NAME_ATTRIBUTE_TYPE = "Mother's name";

	public static final String FATHER_NAME_ATTRIBUTE_TYPE = "Father's name";

	public static final String ENCOUNTER_TYPE = MODULE_ID + ".encounterType";

	public static final String CONNECTION_TIME_OUT = MODULE_ID
			+ ".connection_Time_out";

	public static final String HL7_LOCAL_RELATIONSHIP = "99REL";

	public static final String HL7_LOCAL_DRUG = "99RX";

	public static final String HL7_LOCAL_CONCEPT = "99DCT";

	public static final String HL7_LOCAL_CONCEPT_NAME = "99NAM";

	public static final String HL7_AUTHORITY_UUID = "UUID";

	public static final String HL7_AUTHORITY_LOCAL = "L";

	public static final Object HL7_ID_PERSON = "PN";

	public static final Object HL7_ID_PATIENT = "PI";
}
