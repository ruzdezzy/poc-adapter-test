package org.openmrs.module.rheapocadapter;

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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.User;
import org.openmrs.module.rheapocadapter.transaction.ArchiveTransaction;
import org.openmrs.module.rheapocadapter.transaction.ErrorTransaction;
import org.openmrs.module.rheapocadapter.transaction.ProcessingTransaction;
import org.openmrs.module.rheapocadapter.transaction.Transaction;

/**
 *
 */
public class TransactionUtil {
	
	private static User creator;
	
	
	static Log log = LogFactory.getLog(TransactionUtil.class);
	/**
	 * 
	 * This Produce an implementation of the transaction that has to be saved in database
	 * 
	 * @param transaction common data for all transactions
	 * @param error error message if there's an error, this can be null
	 * @param responseTime the time the response has been received (if there's no connection error).
	 * @param method Method used to transmit the message, either POST or GET
	 * @param queueType the type of the queue in which to save the transaction
	 * @return a transaction implementation
	 */
	public static Transaction setQueueMessage(Transaction transaction, String error, Date responseTime, String method,
	                                          String queueType) {
		//		Transaction trans=null;
		if (queueType.equalsIgnoreCase("Archive")) {
			log.info("Archive queue");
			ArchiveTransaction trans = new ArchiveTransaction(transaction, responseTime);
			return trans;
		} else if (queueType.equalsIgnoreCase("Processing")) {
			log.info("Processing queue");
			ProcessingTransaction trans = new ProcessingTransaction(transaction, method);
			return trans;
		} else if (queueType.equalsIgnoreCase("Error")) {
			ErrorTransaction trans = new ErrorTransaction(transaction, error, responseTime);
			return trans;
		}
		log.info("null queue");
		return null;
	}
	/**
	 * @return the creator
	 */
	public static User getCreator() {
		return creator;
	}
	
	/**
	 * @param creator the creator to set
	 */
	public static void setCreator(User creator) {
		TransactionUtil.creator = creator;
	}
	
	
}
