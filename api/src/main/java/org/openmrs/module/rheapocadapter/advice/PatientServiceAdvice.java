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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.rheapocadapter.handler.ClientRegistryService;

/**
 *
 */

public class PatientServiceAdvice implements MethodInterceptor {

	protected final Log log = LogFactory.getLog(PatientServiceAdvice.class);

	// List for Hack
	private static List<AOPEvent> processedPatList = new LinkedList<AOPEvent>();

	public Object invoke(MethodInvocation invocation) throws Throwable {
		ClientRegistryService clientService = new ClientRegistryService();
		boolean update = false;
		Patient patient = null;
		PatientService ps = Context.getPatientService();
		if (invocation.getMethod().getName().equals("savePatient")) {
			patient = (Patient) invocation.getArguments()[0];
			ps = Context.getPatientService();
			if (ps.getPatientByExample(patient) != null) {
				update = true;

			}
		}
		Object o = invocation.proceed();

		if (invocation.getMethod().getName().equals("savePatient")) {

			// HACK: Idempotency check (message uniqueness)
			Patient returnedPatient = (Patient) o;
			Integer id = returnedPatient.getId();

			synchronized (processedPatList) {
				if (!update && id != null
						&& isEventWithinDiffPeriod(processedPatList, id)) {
					return o;
				}
			}
			// /HACK

			if (!update) {
				log.info("Save Patient to Client Registry");
				clientService.registerNewPatient(patient);
			} else {
				log.info("Update Patient to Client Registry");
				clientService.updateClientDetails(patient);

			}

		}
		return o;
	}

	// HACK-HACK: check that an AOP event happens within a certain amount of
	// time
	public static class AOPEvent {
		Integer eventId;
		long timestamp = System.currentTimeMillis();

		public boolean withinEventDiff(AOPEvent ev) {
			long diff = (Long.parseLong(Context.getAdministrationService()
					.getGlobalProperty("rheapocadapter.connection_Time_out")
					.trim()) * 2L + 20L) * 1000L;
			return Math.abs(ev.timestamp - timestamp) < diff;
		}
	}

	public static boolean isEventWithinDiffPeriod(List<AOPEvent> lst,
			Integer eventId) {
		boolean found = false;
		Iterator<AOPEvent> iter = lst.iterator();
		AOPEvent ev = new AOPEvent();
		ev.eventId = eventId;

		while (iter.hasNext()) {
			AOPEvent lev = iter.next();
			if (!lev.withinEventDiff(ev)) {
				iter.remove();
				continue;
			}

			if (lev.eventId.equals(ev.eventId)) {
				lev.timestamp = ev.timestamp;
				found = true;
			}
		}

		if (!found)
			lst.add(ev);

		return found;
	}
	// /HACK-HACK
}
