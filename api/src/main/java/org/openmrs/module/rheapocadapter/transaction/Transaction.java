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
public class Transaction {
	
	private Integer id;
	
	private Date timeRequestSent;
	
	private String message;
	
	private String url;
	
	private int senderId;
	
	private String queueType;
	
	/**
     * 
     */
	public Transaction(Date timeRequestSent, String message, String url, int sender) {
		setTimeRequestSent(timeRequestSent);
		setMessage(message);
		setUrl(url);
		setSender(sender);
	}
	
	/**
     * 
     */
	public Transaction() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	 * @param timeRequestSent the timeRequestSent to set
	 */
	public void setTimeRequestSent(Date timeRequestSent) {
		this.timeRequestSent = timeRequestSent;
	}
	
	/**
	 * @return the timeRequestSent
	 */
	public Date getTimeRequestSent() {
		return timeRequestSent;
	}
	
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * @return the sender
	 */
	public int getSender() {
		return senderId;
	}
	
	/**
	 * @param sender the sender to set
	 */
	public void setSender(int sender) {
		this.senderId = sender;
	}
	
	public String toString() {
		return this.queueType + ", " + this.senderId + ", " + this.url + ", " + this.id;
	}
	
}
