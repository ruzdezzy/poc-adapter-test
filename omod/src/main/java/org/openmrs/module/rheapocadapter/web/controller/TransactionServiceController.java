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
package org.openmrs.module.rheapocadapter.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.rheapocadapter.handler.EnteredHandler;
import org.openmrs.module.rheapocadapter.transaction.ArchiveTransaction;
import org.openmrs.module.rheapocadapter.transaction.ErrorTransaction;
import org.openmrs.module.rheapocadapter.transaction.ProcessingTransaction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ;
 */
@Controller
@SuppressWarnings("unchecked")
public class TransactionServiceController {

	protected static final Log log = LogFactory
			.getLog(TransactionServiceController.class);

	@RequestMapping("/module/rheapocadapter/manageQueue.form")
	public String listAllQueues(ModelMap map) {
		EnteredHandler enteredHandler = new EnteredHandler();
		List<ArchiveTransaction> archiveTransactions = new ArrayList<ArchiveTransaction>();
		archiveTransactions = (List<ArchiveTransaction>) enteredHandler
				.getArchiveQueue();
		Collections.reverse(archiveTransactions);
		map.addAttribute("archiveTransactions", archiveTransactions);
		log.info("Archive Transaction Size " + archiveTransactions.size());
		return "/module/rheapocadapter/manageQueue";
	}

	@RequestMapping("/module/rheapocadapter/processingQueue.form")
	public String showProcessingQueue(ModelMap map) {
		EnteredHandler enteredHandler = new EnteredHandler();
		List<ProcessingTransaction> processingTransactions = new ArrayList<ProcessingTransaction>();
		processingTransactions = (List<ProcessingTransaction>) enteredHandler
				.getProcessingQueue();
		Collections.reverse(processingTransactions);
		map.addAttribute("processingTransactions", processingTransactions);

		return "/module/rheapocadapter/processingQueue";

	}

	@RequestMapping("/module/rheapocadapter/errorQueue.form")
	public String showErrorQueue(ModelMap map) {
		EnteredHandler enteredHandler = new EnteredHandler();
		List<ErrorTransaction> errorTransactions = (List<ErrorTransaction>) enteredHandler
				.getErrorQueue();
		Collections.reverse(errorTransactions);
		map.addAttribute("errorTransactions", errorTransactions);

		return "/module/rheapocadapter/errorQueue";
	}

	@RequestMapping("/module/rheapocadapter/backEnteredQueue.form")
	public String showBackEnteredQueue(ModelMap map) {
		EnteredHandler enteredHandler = new EnteredHandler();
		List<Encounter> encounter = enteredHandler.getEncounterNotSent();
		Collections.reverse(encounter);
		map.addAttribute("encounterNotSent", encounter);

		return "/module/rheapocadapter/backEnteredQueue";
	}

	@RequestMapping("/module/rheapocadapter/sentBackEnteredData.form")
	public String sendBackEnteredQueue(ModelMap map) {
		EnteredHandler enteredHandler = new EnteredHandler();
		log.info("Task Started for sending BackEnteredData");
		enteredHandler.sendBackEntered();

		return "/module/rheapocadapter/backEnteredQueue";
	}
}
