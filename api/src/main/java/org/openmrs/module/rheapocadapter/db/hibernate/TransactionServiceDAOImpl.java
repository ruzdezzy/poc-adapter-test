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
package org.openmrs.module.rheapocadapter.db.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.Person;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.rheapocadapter.db.TransactionServiceDAO;
import org.openmrs.module.rheapocadapter.transaction.Transaction;

/**
 *
 */
public class TransactionServiceDAOImpl implements TransactionServiceDAO {

	private SessionFactory sessionFactory;

	protected final Log log = LogFactory.getLog(getClass());

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @see org.openmrs.module.TransactionServiceDAO.db.QueueServiceDAO#getAllQueue()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Transaction> getAllQueue(
			Class<? extends Transaction> transaction) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(
				transaction);
		log.info(criteria.getClass() + " " + criteria.list().size());
		return criteria.list();

	}

	/**
	 * @see org.openmrs.module.TransactionServiceDAO.db.QueueServiceDAO#getQueue(java.lang.Integer)
	 */
	@Override
	public Transaction getQueue(Integer id, Transaction queue) {
		// TODO Auto-generated method stub
		return (Transaction) sessionFactory.getCurrentSession().get(
				queue.getClass(), id);

	}

	/**
	 * @see org.openmrs.module.TransactionServiceDAO.db.QueueServiceDAO#saveQueue(org.openmrs.module.Transaction.queue.Queue)
	 */
	@Override
	public void saveQueue(Transaction queue) {
		sessionFactory.getCurrentSession().saveOrUpdate(queue);

	}

	/**
	 * @see org.openmrs.module.rheapocadapter.db.TransactionServiceDAO#removeQueue(org.openmrs.module.rheapocadapter.transaction.Transaction)
	 */
	@Override
	public void removeQueue(Transaction queue) {
		sessionFactory.getCurrentSession().delete(queue);

	}

	@Override
	public Person getPersonByNID(String NID) {
		List<Person> candidates = new ArrayList<Person>();
		PersonAttributeType pat = Context.getPersonService()
				.getPersonAttributeTypeByName("NID");
		if (pat == null) {
			pat = new PersonAttributeType();
			pat.setName("NID");
			pat.setDescription("National ID");
			Context.getPersonService().savePersonAttributeType(pat);
		}

		Query query = sessionFactory
				.getCurrentSession()
				.createQuery(
						"select p.person from PersonAttribute p where value = :code and p.attributeType.personAttributeTypeId = :attr_id ");
		query.setParameter("code", NID);
		query.setParameter("attr_id", pat.getPersonAttributeTypeId());

		if (query.list() != null) {
			candidates = (List<Person>) query.list();
			if (candidates.size() != 0) {
				return candidates.get(0);
			}
		} else {
			return null;
		}
		return null;
	}

	@Override
	public String getPersonAttributesByPerson(Person p, String NID) {
		List<String> candidates = new ArrayList<String>();
		PersonAttributeType pat = Context.getPersonService()
				.getPersonAttributeTypeByName(NID);

		if (pat == null) {
			return null;
			// pat = new PersonAttributeType();
			// pat.setName("NID");
			// pat.setDescription("National ID");
			// Context.getPersonService().savePersonAttributeType(pat);
		}

		Query query = sessionFactory
				.getCurrentSession()
				.createQuery(
						"select p.value from PersonAttribute p where p.attributeType.personAttributeTypeId = :attr_id and p.person = :pers");
		query.setParameter("attr_id", pat.getPersonAttributeTypeId());
		query.setParameter("pers", p);

		if (query.list() != null) {

			candidates = (List<String>) query.list();
			log.info("candidates.size() " + candidates.size());
			if (candidates.size() != 0) {
				String x = candidates.get(0);
				log.info(x);
				return x;
			}
		} else {
			return null;
		}
		return null;
	}

}
