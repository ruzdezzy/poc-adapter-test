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
package org.openmrs.module.rheapocadapter.util;



import org.openmrs.Patient;



/**
 *
 */
public class AttributeList {
	Patient patient;
	String  mothersName;
	String  fathersName;
	String nid;
	/**
     * 
     */
    public AttributeList() {
	   
    }
	/**
     * @param pat
     * @param mothersName
     * @param fathersName
     */
    public AttributeList(Patient patient, String mothersName, String fathersName,String nid) {
	    super();
	    this.patient = patient;
	    this.mothersName = mothersName;
	    this.fathersName = fathersName;
	    this.nid=nid;
    }
	
    public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	public String getNid() {
		return nid;
	}
	public void setNid(String nid) {
		this.nid = nid;
	}
	/**
     * @return the pat
     */
    
	
    /**
     * @return the mothersName
     */
    public String getMothersName() {
    	return mothersName;
    }
	
    /**
     * @param mothersName the mothersName to set
     */
    public void setMothersName(String mothersName) {
    	this.mothersName = mothersName;
    }
	
    /**
     * @return the fathersName
     */
    public String getFathersName() {
    	return fathersName;
    }
	
    /**
     * @param fathersName the fathersName to set
     */
    public void setFathersName(String fathersName) {
    	this.fathersName = fathersName;
    }
	
    
}
