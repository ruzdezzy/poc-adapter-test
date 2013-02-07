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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.TreeMap;

import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.rheapocadapter.TransactionUtil;
import org.openmrs.module.rheapocadapter.transaction.Transaction;

/**
 *
 */
public class RequestHandler {

	HttpURLConnection connection = null;

	Log log = LogFactory.getLog(this.getClass());

	/**
	 * gets method, message body and parameters and call the connection, send
	 * and call the response handler
	 * 
	 * @param method
	 *            Method to use for sending, the first element is either get or
	 *            post, the second is used while creating the URL to know what
	 *            action to be done. e.g: GetClinicalData is different to
	 *            GetClients, both are gets but has different URLs.
	 * @param body
	 *            message to be sent, it can be null.
	 * @param parameter
	 *            used to create URL
	 */
	public Transaction sendRequest(String[] method, String body,
			TreeMap<String, String> parameter) {
		ResponseHandler response = new ResponseHandler();
		String url = null;
		User creator = Context.getUserService().getUserByUsername(
				Context.getAuthenticatedUser().getUsername());
		int sender = creator.getUserId();
		try {
			ConnectionHandler conn = new ConnectionHandler();
			// create url according to method to be used and transaction
			// performed
			url = conn.createUrl(method[1], parameter);
			if ((url == null) || (url == "")) {
				throw new SocketTimeoutException();
			}
			log.info("URL= " + url);
			// if the method is GET or POST, send accordingly.
			if (method[0].equalsIgnoreCase("GET")) {
				Date sendDateTime = new Date();
				String[] result = conn.callGet(url);
				Date receiveDateTime = new Date();
				log.info("After callGet " + result[0] + " = " + result[1]);
				Transaction transaction = generateTransaction(sendDateTime,
						result[1], url, sender);
				Transaction item = response
						.generateMessage(transaction,
								Integer.parseInt(result[0]), method[0],
								receiveDateTime);
				return item;

			} else if (method[0].equalsIgnoreCase("POST")
					|| method[0].equalsIgnoreCase("PUT")) {

				Date sendDateTime = new Date();
				String[] result = conn.callPostAndPut(url, body, method[0]);
				Date receiveDateTime = new Date();

				Transaction transaction = generateTransaction(sendDateTime,
						result[1], url, sender);
				Transaction item = response
						.generateMessage(transaction,
								Integer.parseInt(result[0]), method[0],
								receiveDateTime);
				return item;

			}
		} catch (KeyManagementException e) {
			Date sendDateTime = new Date();
			Date receiveDateTime = new Date();
			Transaction transaction = generateTransaction(sendDateTime,
					e.getMessage(), url, sender);
			Transaction item = response.generateMessage(transaction, 400,
					method[0], receiveDateTime);
			log.error("KeyManagementException generated" + e.getMessage());
			return item;
		} catch (KeyStoreException e) {
			Date sendDateTime = new Date();
			Date receiveDateTime = new Date();
			Transaction transaction = generateTransaction(sendDateTime,
					e.getMessage(), url, sender);
			Transaction item = response.generateMessage(transaction, 400,
					method[0], receiveDateTime);
			log.error("KeyStoreException generated" + e.getMessage());
			return item;
		} catch (NoSuchAlgorithmException e) {
			Date sendDateTime = new Date();
			Date receiveDateTime = new Date();
			Transaction transaction = generateTransaction(sendDateTime,
					e.getMessage(), url, sender);
			Transaction item = response.generateMessage(transaction, 400,
					method[0], receiveDateTime);
			log.error("NoSuchAlgorithmException generated" + e.getMessage());
			return item;
		} catch (CertificateException e) {
			Date sendDateTime = new Date();
			Date receiveDateTime = new Date();
			Transaction transaction = generateTransaction(sendDateTime,
					e.getMessage(), url, sender);
			Transaction item = response.generateMessage(transaction, 400,
					method[0], receiveDateTime);
			log.error("CertificateException generated" + e.getMessage());
			return item;
		} catch (TransformerFactoryConfigurationError e) {
			Date sendDateTime = new Date();
			Date receiveDateTime = new Date();
			Transaction transaction = generateTransaction(sendDateTime,
					e.getMessage(), url, sender);
			Transaction item = response.generateMessage(transaction, 400,
					method[0], receiveDateTime);
			log.error("TransformerFactoryConfigurationError generated"
					+ e.getMessage());
			return item;
		} catch (SocketTimeoutException e) {
			Date sendDateTime = new Date();
			Date receiveDateTime = new Date();
			Transaction transaction = generateTransaction(sendDateTime,
					e.getMessage(), url, sender);
			Transaction item = response.generateMessage(transaction, 600,
					method[0], receiveDateTime);
			log.error("SocketTimeoutException generated " + e.getMessage());
			return item;
		} catch (IOException e) {
			Date sendDateTime = new Date();
			Date receiveDateTime = new Date();
			Transaction transaction = generateTransaction(sendDateTime,
					e.getMessage(), url, sender);
			Transaction item = response.generateMessage(transaction, 600,
					method[0], receiveDateTime);
			log.error("IOException generated " + e.getMessage());
			e.printStackTrace();
			return item;
		}
		log.info("Gonna return null");
		return null;
	}

	/**
	 * this works with the sheduled task
	 * 
	 * @param method
	 *            to use while sending the body
	 * @param body
	 *            Message to send
	 * @param url
	 *            URL to send to
	 */

	public Transaction sendRequest(String method, String body, String url) {

		ResponseHandler response = new ResponseHandler();
		User creator = Context.getUserService().getUserByUsername(
				TransactionUtil.getCreator().getUsername());
		int sender = creator.getUserId();
		try {
			ConnectionHandler conn = new ConnectionHandler();

			log.info("url to use: " + url);

			if (method.equalsIgnoreCase("GET")) {
				Date sendDateTime = new Date();
				String[] result = conn.callGet(url);
				Date receiveDateTime = new Date();
				Transaction transaction = generateTransaction(sendDateTime,
						result[1], url, sender);
				Transaction item = response.generateMessage(transaction,
						Integer.parseInt(result[0]), method, receiveDateTime);
				return item;

			} else if (method.equalsIgnoreCase("POST")
					|| method.equalsIgnoreCase("PUT")) {

				Date sendDateTime = new Date();
				String[] result = conn.callPostAndPut(url, body, method);
				Date receiveDateTime = new Date();
				Transaction transaction = generateTransaction(sendDateTime,
						result[1], url, sender);
				Transaction item = response.generateMessage(transaction,
						Integer.parseInt(result[0]), method, receiveDateTime);
				return item;

			}
		} catch (KeyManagementException e) {
			Date sendDateTime = new Date();
			Date receiveDateTime = new Date();
			Transaction transaction = generateTransaction(sendDateTime,
					e.getMessage(), url, sender);
			Transaction item = response.generateMessage(transaction, 400,
					method, receiveDateTime);
			log.error("KeyManagementException generated" + e.getMessage());
			return item;
		} catch (KeyStoreException e) {
			Date sendDateTime = new Date();
			Date receiveDateTime = new Date();
			Transaction transaction = generateTransaction(sendDateTime,
					e.getMessage(), url, sender);
			Transaction item = response.generateMessage(transaction, 400,
					method, receiveDateTime);
			log.error("KeyStoreException generated" + e.getMessage());
			return item;
		} catch (NoSuchAlgorithmException e) {
			Date sendDateTime = new Date();
			Date receiveDateTime = new Date();
			Transaction transaction = generateTransaction(sendDateTime,
					e.getMessage(), url, sender);
			Transaction item = response.generateMessage(transaction, 400,
					method, receiveDateTime);
			log.error("NoSuchAlgorithmException generated" + e.getMessage());
			return item;
		} catch (CertificateException e) {
			Date sendDateTime = new Date();
			Date receiveDateTime = new Date();
			Transaction transaction = generateTransaction(sendDateTime,
					e.getMessage(), url, sender);
			Transaction item = response.generateMessage(transaction, 400,
					method, receiveDateTime);
			log.error("CertificateException generated" + e.getMessage());
			return item;
		} catch (TransformerFactoryConfigurationError e) {
			Date sendDateTime = new Date();
			Date receiveDateTime = new Date();
			Transaction transaction = generateTransaction(sendDateTime,
					e.getMessage(), url, sender);
			Transaction item = response.generateMessage(transaction, 400,
					method, receiveDateTime);
			log.error("TransformerFactoryConfigurationError generated"
					+ e.getMessage());
			return item;
		} catch (SocketTimeoutException e) {
			Date sendDateTime = new Date();
			Date receiveDateTime = new Date();
			Transaction transaction = generateTransaction(sendDateTime,
					e.getMessage(), url, sender);
			Transaction item = response.generateMessage(transaction, 600,
					method, receiveDateTime);
			log.error("SocketTimeoutException generated " + e.getMessage());
			return item;
		} catch (IOException e) {
			Date sendDateTime = new Date();
			Date receiveDateTime = new Date();
			Transaction transaction = generateTransaction(sendDateTime,
					e.getMessage(), url, sender);
			Transaction item = response.generateMessage(transaction, 600,
					method, receiveDateTime);
			log.error("IOException generated " + e.getMessage());
			e.printStackTrace();
			return item;
		} catch (Exception e) {
			Date sendDateTime = new Date();
			Date receiveDateTime = new Date();
			Transaction transaction = generateTransaction(sendDateTime,
					e.getMessage(), url, sender);
			Transaction item = response.generateMessage(transaction, 400,
					method, receiveDateTime);
			log.error("IOException generated " + e.getMessage());
			e.printStackTrace();
			return item;
		}
		return null;

	}

	private Transaction generateTransaction(Date timeRequestSent,
			String message, String url, int sender) {

		return new Transaction(timeRequestSent, message, url, sender);
	}

}
