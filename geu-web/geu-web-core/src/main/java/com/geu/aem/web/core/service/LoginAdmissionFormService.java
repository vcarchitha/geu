package com.geu.aem.web.core.service;

import java.sql.SQLException;
import java.util.Map;

import org.json.simple.JSONObject;

public interface LoginAdmissionFormService {
	
	/**
	 * This method inserts the Admission form values into the Admission table
	 * @param admissionMap
	 */
	public void insertLoginAdmissionData(Map<String,String> admissionMap, String phoneNumber);	
	
	/**
	 * This method checks if the user is already registered before in Admission Table.
	 * @param admissionMap
	 * @return boolean
	 */
	public boolean checkUserAlreadyExists(Map<String,String> admissionMap);
	
	/**
	 * This method check if the formID is already in Admission Table.
	 * @param formID
	 * @return boolean
	 */
	public boolean checkFormIDAlreadyExists(String formID);
	
	/**
	 * This method returns the Admission Data to the Basic Details Tab from Admission table.
	 * @param admissionMap
	 * @return jsonObject
	 */
	public JSONObject getLoginAdmissionData(Map<String,String> admissionMap);
	
	/**
	 * This method returns the Admission data to the Basic Details Tab from Admission Table using FormID.
	 * @param formID
	 * @return jsonObject
	 */
	public JSONObject getAdmissionDataFromFormID(String formID) throws SQLException;
	
	/**
	 * This method returns the contactNum for each formID
	 * @param formID
	 * @return
	 */
	public String getMobileNumberFormID(String formID);

	/**
	 * This method check the payment status for each formID
	 * @param formID
	 * @return
	 */
	public boolean checkPaymentStatus(String formID);

}
