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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.module.rheapocadapter.db.TransactionServiceDAO;
import org.openmrs.module.rheapocadapter.service.TransactionService;
import org.openmrs.module.rheapocadapter.transaction.Transaction;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 */
public class TransactionServiceImpl implements TransactionService {

	private TransactionServiceDAO queueServiceDAO;

	Log log = LogFactory.getLog(this.getClass());

	/**
	 * @see org.openmrs.module.TransactionService.queue.services.QueueService#getAllQueue()
	 */
	@Override
	public List<Transaction> getAllQueue(
			Class<? extends Transaction> transaction) {
		// TODO Auto-generated method stub
		return queueServiceDAO.getAllQueue(transaction);
	}

	/**
	 * @see org.openmrs.module.TransactionService.queue.services.QueueService#getQueue(java.lang.Integer)
	 */
	@Override
	public Transaction getQueue(Integer id, Transaction queue) {
		// TODO Auto-generated method stub
		return queueServiceDAO.getQueue(id, queue);
	}

	/**
	 * @see org.openmrs.module.TransactionService.queue.services.QueueService#saveQueue(org.openmrs.module.Transaction.queue.Queue)
	 */
	@Override
	public void saveQueue(Transaction msg) {
		queueServiceDAO.saveQueue(msg);

	}

	/**
	 * @return the queueServiceDao
	 */
	public TransactionServiceDAO getQueueServiceDAO() {
		return queueServiceDAO;
	}

	/**
	 * @param queueServiceDao
	 *            the queueServiceDao to set
	 */
	public void setQueueServiceDAO(TransactionServiceDAO archiveQueueServiceDao) {
		this.queueServiceDAO = archiveQueueServiceDao;
	}

	/**
	 * @see org.openmrs.module.rheapocadapter.service.TransactionService#removeQueue(org.openmrs.module.rheapocadapter.transaction.Transaction)
	 */
	@Override
	public void removeQueue(Transaction queue) {
		queueServiceDAO.removeQueue(queue);

	}

	/**
	 * @see org.openmrs.api.OpenmrsService#onStartup()
	 */
	@Override
	public void onStartup() {
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.openmrs.api.OpenmrsService#onShutdown()
	 */
	@Override
	public void onShutdown() {
		// TODO Auto-generated method stub

	}

	@Override
	@Transactional
	public String getPersonAttributesByPerson(Person p, String NID) {
		// TODO Auto-generated method stub
		return queueServiceDAO.getPersonAttributesByPerson(p, NID);
	}

	@Override
	@Transactional
	public Person getPersonByNID(String NID) {
		// TODO Auto-generated method stub
		return queueServiceDAO.getPersonByNID(NID);
	}

}
