package org.openmrs.module.rheapocadapter.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.openmrs.api.OpenmrsService;

public class AttributesUtil implements OpenmrsService {
	private SessionFactory sessionFactory;

	protected final Log log = LogFactory.getLog(getClass());

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	

	@Override
	public void onStartup() {

	}

	@Override
	public void onShutdown() {

	}
}
