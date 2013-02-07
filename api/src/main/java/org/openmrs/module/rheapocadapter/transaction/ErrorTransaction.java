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
public class ErrorTransaction extends Transaction {
	
	/**
	 * @param id
	 * @param timeRequestSent
	 * @param message
	 * @param url
	 * @param sender
	 */
	public ErrorTransaction(Date timeRequestSent, String message, String url, int sender, String Error,
	    Date responseTimeReceived) {
		super(timeRequestSent, message, url, sender);
		setError(Error);
		setResponseTimeReceived(responseTimeReceived);
	}
	
	/**
     * 
     */
	public ErrorTransaction() {
		super();
	}
	
	/**
	 * @param transaction
	 * @param error2
	 * @param responseTime
	 */
	public ErrorTransaction(Transaction transaction, String error, Date responseTime) {
		super(transaction.getTimeRequestSent(), transaction.getMessage(), transaction.getUrl(), transaction.getSender());
		setError(error);
		setResponseTimeReceived(responseTime);
	}
	
	private String error;
	
	private Date responseTimeReceived;
	
	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}
	
	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}
	
	/**
	 * @return the responseTimeReceived
	 */
	public Date getResponseTimeReceived() {
		return responseTimeReceived;
	}
	
	/**
	 * @param responseTimeReceived the responseTimeReceived to set
	 */
	public void setResponseTimeReceived(Date responseTimeReceived) {
		this.responseTimeReceived = responseTimeReceived;
	}
	
}
