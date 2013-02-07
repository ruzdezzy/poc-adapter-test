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
package org.openmrs.module.rheapocadapter.web.controller.util;

import java.util.Date;
import java.util.Set;

import org.openmrs.Encounter;
import org.openmrs.Obs;

/**
 *
 */
public class DeliveryReport {
	
	Date delDate;
	
	String mode, type, bLoss, mOutcome, feeding;
	
	/**
     * 
     */
	public DeliveryReport() {
		
	}
	
	/**
	 * @param delDate
	 * @param mode
	 * @param type
	 * @param bLoss
	 * @param mOutcome
	 * @param feeding
	 */
	public DeliveryReport(Encounter e) {
		
		Set<Obs> obsv = e.getAllObs();
		for (Obs o : obsv) {
			if (o.getConcept().isNamed("DATE OF CONFINEMENT")) {
				
				this.delDate = o.getValueDatetime();
			}
			if (o.getConcept().isNamed("METHOD OF DELIVERY")) {
				
				this.mode = o.getValueCoded().getDisplayString();
			}
			if (o.getConcept().isNamed("CHILD BORN STATUS")) {
				
				this.type = o.getValueCoded().getDisplayString();
			}
			if (o.getConcept().isNamed("Maternal Blood Loss")) {
				
				this.bLoss = o.getValueCoded().getDisplayString();
			}
			if (o.getConcept().isNamed("Maternal Outcome")) {
				
				this.mOutcome = o.getValueCoded().getDisplayString();
			}
			if (o.getConcept().isNamed("Feeding Option")) {
				
				this.feeding = o.getValueCoded().getDisplayString();
			}
		}
		
	}
	
	/**
	 * @return the delDate
	 */
	public Date getDelDate() {
		return delDate;
	}
	
	/**
	 * @param delDate the delDate to set
	 */
	public void setDelDate(Date delDate) {
		this.delDate = delDate;
	}
	
	/**
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}
	
	/**
	 * @param mode the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return the bLoss
	 */
	public String getbLoss() {
		return bLoss;
	}
	
	/**
	 * @param bLoss the bLoss to set
	 */
	public void setbLoss(String bLoss) {
		this.bLoss = bLoss;
	}
	
	/**
	 * @return the mOutcome
	 */
	public String getmOutcome() {
		return mOutcome;
	}
	
	/**
	 * @param mOutcome the mOutcome to set
	 */
	public void setmOutcome(String mOutcome) {
		this.mOutcome = mOutcome;
	}
	
	/**
	 * @return the feeding
	 */
	public String getFeeding() {
		return feeding;
	}
	
	/**
	 * @param feeding the feeding to set
	 */
	public void setFeeding(String feeding) {
		this.feeding = feeding;
	}
	
}
