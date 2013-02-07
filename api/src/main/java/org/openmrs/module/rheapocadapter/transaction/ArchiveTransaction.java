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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class ArchiveTransaction extends Transaction {

	/**
	 * @param id
	 * @param timeRequestSent
	 * @param message
	 * @param url
	 * @param sender
	 */

	private Date timeResponseReceived;
	private String relatedEncounter;

	/**
	 * @param timeResponseReceived
	 *            the timeResponseReceived to set
	 */
	public void setTimeResponseReceived(Date timeResponseReceived) {
		this.timeResponseReceived = timeResponseReceived;
	}

	/**
	 * @return the timeResponseReceived
	 */
	public Date getTimeResponseReceived() {
		return timeResponseReceived;
	}

	Log log = LogFactory.getLog(this.getClass());

	public ArchiveTransaction(Date timeRequestSent, String message, String url,
			int sender, Date timeResponseReceived) {
		super(timeRequestSent, message, url, sender);
		setTimeResponseReceived(timeResponseReceived);
		log.info("Archive queue created");
	}

	/**
     * 
     */
	public ArchiveTransaction() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
     * 
     */
	public ArchiveTransaction(Transaction transaction, Date timeResponseReceived) {
		super(transaction.getTimeRequestSent(), transaction.getMessage(),
				transaction.getUrl(), transaction.getSender());
		setTimeResponseReceived(timeResponseReceived);
		log.info("Archive transaction created");
	}

	/**
     * 
     */
	public ArchiveTransaction(Transaction transaction,
			Date timeResponseReceived, String relatedEncounter) {
		super(transaction.getTimeRequestSent(), transaction.getMessage(),
				transaction.getUrl(), transaction.getSender());
		setTimeResponseReceived(timeResponseReceived);
		setRelatedEncounter(relatedEncounter);
		log.info("Archive transaction created");
	}

	/**
	 * @return the relatedEncounters
	 */
	public String getRelatedEncounter() {
		return relatedEncounter;
	}

	/**
	 * @param relatedEncounters
	 *            the relatedEncounters to set
	 */
	public void setRelatedEncounter(String relatedEncounters) {
		this.relatedEncounter = relatedEncounters;
	}

}
