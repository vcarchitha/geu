package com.geu.aem.web.core.service;

import java.sql.SQLException;
import java.util.Map;

import org.json.simple.JSONObject;

public interface GEHULoginAdmissionFormService {

	/**
	 * This method inserts the Admission form values into the Admission table
	 * @param admissionMap
	 */
	public void insertGEHULoginAdmissionData(Map<String,String> admissionMap, String phoneNumber);	
	
	/**
	 * This method checks if the user is already registered before in Admission Table.
	 * @param admissionMap
	 * @return boolean
	 */
	public boolean checkUserAlreadyExistsInGEHU(Map<String,String> admissionMap);
	
	/**
	 * This method check if the formID is already in Admission Table.
	 * @param formID
	 * @return boolean
	 */
	public boolean checkFormIDAlreadyExistsInGEHU(String formID);
	
	/**
	 * This method returns the Admission Data to the Basic Details Tab from Admission table.
	 * @param admissionMap
	 * @return jsonObject
	 */
	public JSONObject getGEHULoginAdmissionData(Map<String,String> admissionMap);
	
	/**
	 * This method returns the Admission data to the Basic Details Tab from Admission Table using FormID.
	 * @param formID
	 * @return jsonObject
	 */
	public JSONObject getGEHUAdmissionDataFromFormID(String formID) throws SQLException;
	
	/**
	 * This method returns the contactNum for each formID
	 * @param formID
	 * @return
	 */
	public String getGEHUMobileNumberFormID(String formID);

	/**
	 * This method checks the payment status for each form ID
	 * @param formID
	 * @return
	 */
	public boolean checkPaymentStatus(String formID);
}
