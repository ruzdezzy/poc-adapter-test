package org.openmrs.module.rheapocadapter.advice;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.rheapocadapter.RHEAConstants;
import org.openmrs.module.rheapocadapter.util.GetPatientUtil;

/**
 * AOP class used to get Patient previous encounter when the ObsService methods
 * saved is add patient to service
 */

public class ObsServiceAdvice implements MethodInterceptor {

	protected static final Log log = LogFactory.getLog(ObsServiceAdvice.class);
	
	// List for Hack
	private static List<PatientServiceAdvice.AOPEvent> processedObsList = new LinkedList<PatientServiceAdvice.AOPEvent>();

	/**
	 * @see org.springframework.aop.AfterReturningAdvice#afterReturning(Object,
	 *      Method, Object[], Object)
	 */
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		
		Object o = invocation.proceed();
		
		// log.info(invocation.getMethod().getName());
		GetPatientUtil getPatientUtil = new GetPatientUtil();
		if (invocation.getMethod().getName().equals("saveObs")) {
			
			// HACK: Idempotency check (message uniqueness)
			Obs returnedObs = (Obs) o;
			Integer id = returnedObs.getId();
			synchronized(processedObsList) {
				if (id != null && PatientServiceAdvice.isEventWithinDiffPeriod(processedObsList, id)) {
						return o;
				}
			}
			// /HACK
			
			// log.info("GetPatientUtil 2");
			// Setting the parameter from the ObsService.saveObs method
			Obs obs = (Obs) invocation.getArguments()[0];

			// Checking if the obs is the "NEXT SCHEDULED VISIT"
			if (obs.getConcept().getConceptId().intValue() == getNextScheduledVisitConceptId()) {

				// Querry SHR for patient encounters
				Patient patient = Context.getPatientService().getPatient(
						obs.getPerson().getPersonId().intValue());
				log.info("find patient " + patient.getPatientId()
						+ " encounters from SHR");
				getPatientUtil.getPatientData(patient);

			}
		}
		
		return o;
	}

	public int getNextScheduledVisitConceptId() throws Exception {
		GlobalProperty gp = Context.getAdministrationService()
				.getGlobalPropertyObject(
						RHEAConstants.NEXT_SCHEDULED_VISIT_CONCEPT_ID);
		return (gp != null) ? Integer.parseInt(gp.getPropertyValue()) : null;
	}
}
