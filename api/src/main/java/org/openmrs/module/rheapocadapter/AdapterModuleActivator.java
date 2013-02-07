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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.Activator;

/**
 * This class contains the logic that is run every time this module is either
 * started or shutdown
 */
public class AdapterModuleActivator implements Activator {

	private Log log = LogFactory.getLog(this.getClass());

	/**
	 * @see org.openmrs.module.Activator#startup()
	 */
	public void startup() {

		// Context.addAdvisor(PatientServiceAdvisor.PatientServiceAdvice.class,
		// new PatientServiceAdvisor());
		log.info("Starting Adapter Module");
	}

	public void addMetadata() {
		{
			String[] allowedEncounterType = Context.getAdministrationService()
					.getGlobalProperty(RHEAConstants.ENCOUNTER_TYPE).split(",");
			for (int i = 0; i < allowedEncounterType.length; i++) {
				// find encounter type and create it if not exists
				EncounterType et = Context.getEncounterService()
						.getEncounterType(allowedEncounterType[i]);

				if (et == null) {
					et = new EncounterType(allowedEncounterType[i],
							allowedEncounterType[i]);
					Context.getEncounterService().saveEncounterType(et);
					log.info("Created new encounter type: " + et);
				}
			}
		}

	}

	/**
	 * @see org.openmrs.module.Activator#shutdown()
	 */
	public void shutdown() {

		// Context.removeAdvisor(PatientServiceAdvisor.PatientServiceAdvice.class,
		// new PatientServiceAdvisor());
		log.info("Shutting down Adapter Module");
	}
}
