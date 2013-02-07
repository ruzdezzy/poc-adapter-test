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
package org.openmrs.module.rheapocadapter.handler;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.module.rheapocadapter.TransactionUtil;
import org.openmrs.module.rheapocadapter.service.TransactionService;
import org.openmrs.module.rheapocadapter.transaction.Transaction;

/**
 *
 */
public class ResponseHandler {

	private TransactionService transactionService = ServiceContext
			.getInstance().getService(TransactionService.class);

	Log log = LogFactory.getLog(this.getClass());

	Transaction queue = null;

	/**
	 * @param transactionService
	 *            the queueService to set
	 */
	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	/**
	 * @return the transactionService
	 */
	public TransactionService getTransactionService() {
		return transactionService;
	}

	/**
     * 
     */
	public ResponseHandler() {
		// TODO Auto-generated constructor stub
	}

	public Transaction generateMessage(Transaction transaction, int code,
			String method, Date responseTime) {

		Transaction queueItem = null;
		log.info("Code " + code);
		if (code >= 200 && code < 300) {

			queueItem = TransactionUtil.setQueueMessage(transaction, "",
					responseTime, method, "Archive");

		} else if (code == 600) {

			queueItem = TransactionUtil.setQueueMessage(transaction, "",
					responseTime, method, "Processing");

		} else {
			queueItem = TransactionUtil.setQueueMessage(transaction, "",
					responseTime, method, "Error");

		}

		return queueItem;
	}

	public void handleResponse(Transaction item) {
		transactionService.saveQueue(item);

	}

}
