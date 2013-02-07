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
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.TreeMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.rheapocadapter.RHEAConstants;

/**
 *
 */
public class ConnectionHandler {

	public static SSLSocketFactory sslFactory;

	public static String username = getGloabalPropertyByName(RHEAConstants.SERVER_USER_NAME);

	public static String password = getGloabalPropertyByName(RHEAConstants.SERVER_PASSWORD);

	public static String hostname = getGloabalPropertyByName(RHEAConstants.SERVER_HOSTNAME);

	private String implementationId;

	public static int timeOut = Integer
			.parseInt(getGloabalPropertyByName(RHEAConstants.CONNECTION_TIME_OUT)) * 1000;

	private static Log log = LogFactory.getLog(ConnectionHandler.class);

	/**
	 * @return the implementationId
	 */
	private String getImplementationId() {

		return (implementationId != null) ? implementationId : "rwanda999";
	}

	/**
	 * @param string
	 * @param implementationId
	 *            the implementationId to set
	 */
	private void setImplementationId(String string) {
		try {
			this.implementationId = Context.getAdministrationService()
					.getImplementationId().getImplementationId();
		} catch (NullPointerException e) {
			log.error("can't get implementationId; value not set "
					+ e.getMessage());
		}
	}

	/**
	 * @param string
	 * @param implementationId
	 *            the implementationId to set
	 */
	private void setImplementationId() {
		try {
			this.implementationId = Context.getAdministrationService()
					.getImplementationId().getImplementationId();
		} catch (NullPointerException e) {
			log.error("can't get implementationId; valur not set "
					+ e.getMessage());
		}
	}

	public ConnectionHandler() throws KeyStoreException,
			NoSuchAlgorithmException, CertificateException, IOException,
			KeyManagementException {

		InputStream keyStoreStream = getClass().getResourceAsStream(
				"/web/module/resources/truststore.jks");

		// Load the keyStore
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		keyStore.load(keyStoreStream, "Jembi#123".toCharArray());
		keyStoreStream.close();

		TrustManagerFactory tmf = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(keyStore);

		SSLContext ctx = SSLContext.getInstance("TLS");
		ctx.init(null, tmf.getTrustManagers(), null);

		// set SSL Factory to be used for all HTTPS connections
		sslFactory = ctx.getSocketFactory();
		setImplementationId();
	}

	private static void addHTTPBasicAuthProperty(HttpsURLConnection conn) {
		String userpass = username + ":" + password;
		String basicAuth = "Basic "
				+ new String(DatatypeConverter.printBase64Binary(userpass
						.getBytes()));
		conn.setRequestProperty("Authorization", basicAuth);
	}

	public String[] callGet(String stringUrl) {
		try {

			// Setup connection
			URL url = new URL(stringUrl);

			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

			// This is important to get the connection to use our trusted
			// certificate
			conn.setSSLSocketFactory(sslFactory);

			addHTTPBasicAuthProperty(conn);
			//conn.setConnectTimeout(timeOut);
			// bug fixing for SSL error, this is a temporary fix, need to find a
			// long term one
			conn.setHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
			// printHttpsCert(conn);
			conn.connect();
			int code = conn.getResponseCode();
			
			if (code >= 200 && code < 300) {
				String result = IOUtils.toString(conn.getInputStream());
				conn.disconnect();
				return new String[] { code + "", result };
			} else {
				conn.disconnect();
				return new String[] { code + "", "Server returned " + code + " response code" };
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.error("MalformedURLException while callGet " + e.getMessage());
			return new String[] { 400 + "", e.getMessage() };
		} catch (IOException e) {
			e.printStackTrace();
			log.error("IOException while callGet " + e.getMessage());
			return new String[] { 600 + "", e.getMessage() };
		}
	}

	public String[] callPostAndPut(String stringUrl, String body, String method) {
		try {
			// Setup connection
			URL url = new URL(stringUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod(method.toUpperCase());
			conn.setDoInput(true);
			// This is important to get the connection to use our trusted
			// certificate
			conn.setSSLSocketFactory(sslFactory);

			addHTTPBasicAuthProperty(conn);
			conn.setConnectTimeout(timeOut);
			// bug fixing for SSL error, this is a temporary fix, need to find a
			// long term one
			conn.setHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
			
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			log.error("body" + body);
			out.write(body);
			out.close();
			conn.connect();
			String result = "";
			int code = conn.getResponseCode();
			
			if (code == 201) {
				result = "Saved succefully";
			} else {
				result = "Not Saved";
			}
			conn.disconnect();

			return new String[] { code + "", result };
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.error("MalformedURLException while callPostAndPut " + e.getMessage());
			return new String[] { 400 + "", e.getMessage() };
		} catch (IOException e) {
			e.printStackTrace();
			log.error("IOException while callPostAndPut " + e.getMessage());
			return new String[] { 600 + "", e.getMessage() };
		}
	}

	private static String getGloabalPropertyByName(String name) {
		return Context.getAdministrationService().getGlobalProperty(name);
	}

	public String createUrl(String methodType, TreeMap<String, String> parameter) {
		// URL encode all parameters
		for (String key : parameter.keySet()) {
			try {
				parameter.put(key, URLEncoder.encode(parameter.get(key), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				log.error("Unsupported encoding used during parameter URL encoding", e);
			}
		}
		
		String url = hostname;
		if ((hostname == null) || (hostname == "")) {
			return null;
		}
		if (methodType.equalsIgnoreCase("GetClinicalData")) {
			setImplementationId(getImplementationId().toLowerCase());
			String fosaid = getImplementationId().substring(
					getImplementationId().indexOf("rwanda") + 6);
			String createdSince = parameter.get("createdInSince");
			if (createdSince != null && createdSince != ""
					&& createdSince != "null") {
				return hostname
						+ getGloabalPropertyByName(RHEAConstants.PATIENT_SINGLE_PATIENT_URL_NAME)
						+ "/" + parameter.get("patientId") + "/encounters/?"
						+ "encounter_start_date=" + createdSince + "&ELID="
						+ fosaid;
			} else {

				return hostname
						+ getGloabalPropertyByName(RHEAConstants.PATIENT_SINGLE_PATIENT_URL_NAME)
						+ "/" + parameter.get("patientId") + "/encounters/?"
						+ "&ELID=" + fosaid;
			}
		} else if (methodType.equalsIgnoreCase("UpdatePatientData")) {
			return hostname
					+ getGloabalPropertyByName(RHEAConstants.PATIENT_SINGLE_PATIENT_URL_NAME)
					+ "/" + parameter.get("patientId");
		} else if (methodType.equalsIgnoreCase("RegisterNew")) {
			return hostname
					+ getGloabalPropertyByName(RHEAConstants.PATIENT_MULTIPLE_PATIENTS_URL_NAME)
					+ "/";
		} else if (methodType.equalsIgnoreCase("GetClients")) {
			String parameters = "";
			for (String param : parameter.keySet()) {
				if (parameters != "")
					parameters += "&";
				if ((param == "") || (param.equalsIgnoreCase(""))
						|| (param.equalsIgnoreCase(null)))
					continue;
				parameters += param + "=" + parameter.get(param);
			}
			if (parameters != "" && parameters != null) {
				url = hostname
						+ getGloabalPropertyByName(RHEAConstants.PATIENT_MULTIPLE_PATIENTS_URL_NAME)
						+ "/?" + parameters;
			} else {
				return null;
			}
			return url;
		} else if (methodType.equalsIgnoreCase("GetClient")) {
			return hostname
					+ getGloabalPropertyByName(RHEAConstants.PATIENT_SINGLE_PATIENT_URL_NAME)
					+ "/" + parameter.get("patientId");
		} else if (methodType.equalsIgnoreCase("SavePatientEncounter")) {
			return hostname
					+ getGloabalPropertyByName(RHEAConstants.PATIENT_SINGLE_PATIENT_URL_NAME)
					+ "/" + parameter.get("patientId") + "/encounters";
		}
		return null;
	}

	// private void printHttpsCert(HttpsURLConnection con) {
	//
	// if (con != null) {
	//
	// try {
	//
	// log.info("Response Code : " + con.getResponseCode());
	// log.info("Cipher Suite : " + con.getCipherSuite());
	// // ("\n");
	//
	// Certificate[] certs = con.getServerCertificates();
	// for (Certificate cert : certs) {
	// log.info("Cert Type : " + cert.getType());
	// log.info("Cert Hash Code : " + cert.hashCode());
	// log.info("Cert Public Key Algorithm : "
	// + cert.getPublicKey().getAlgorithm());
	// log.info("Cert Public Key Format : "
	// + cert.getPublicKey().getFormat());
	//
	// }
	//
	// } catch (SSLPeerUnverifiedException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// }

	// }

}
