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
package org.openmrs.module.rheapocadapter.transaction;

import java.util.Date;

/**
 *
 */
public class ProcessingTransaction extends Transaction {
	
	private String method;
	
	/**
	 * @param id
	 * @param timeRequestSent
	 * @param message
	 * @param url
	 * @param sender
	 */
	public ProcessingTransaction(Date timeRequestSent, String message, String url, int sender) {
		super(timeRequestSent, message, url, sender);
		
	}
	
	/**
     * 
     */
	public ProcessingTransaction() {
		super();
	}
	
	/**
	 * @param transaction
	 * @param method
	 */
	public ProcessingTransaction(Transaction transaction, String method) {
		super(transaction.getTimeRequestSent(), transaction.getMessage(), transaction.getUrl(), transaction.getSender());
		setMethod(method);
	}
	
	/**
	 * @return the retryTimes
	 */
	public String getMethod() {
		return method;
	}
	
	/**
	 * @param retryTimes the retryTimes to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	
}
