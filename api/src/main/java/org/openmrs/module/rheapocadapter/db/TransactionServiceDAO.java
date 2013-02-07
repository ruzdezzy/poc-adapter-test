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
package org.openmrs.module.rheapocadapter.db;

import java.util.List;

import org.openmrs.Person;
import org.openmrs.module.rheapocadapter.transaction.Transaction;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 */
public interface TransactionServiceDAO {
	@Transactional(readOnly = true)
	public List<Transaction> getAllQueue(
			Class<? extends Transaction> transaction);

	@Transactional(readOnly = true)
	public Transaction getQueue(Integer id, Transaction queue);

	@Transactional
	public void saveQueue(Transaction queue);

	@Transactional
	public void removeQueue(Transaction queue);

	@Transactional
	public String getPersonAttributesByPerson(Person p, String NID);

	@Transactional
	public Person getPersonByNID(String NID);

}
