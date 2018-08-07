package com.geu.aem.web.core.service;

import java.io.InputStream;
import java.util.Map;

public interface SendEmailService {

	/**
	 * This method sends email to the user, the form ID details once the form id is generated.
	 * @param emailID
	 * @param formID
	 */
	public void sendEmailToUser(String emailID, String formID);
	
	/**
	 * This method sends email to the user, the submission status once the admission form is successfully submitted. 
	 * @param emailID
	 */
	public void sendSubmissionEmailToUser(String emailID);
	
	/**
	 * This method sends email to admin once the Admission form is successfully submitted.
	 * @param formID
	 */
	public void sendAdmissionDataToAdmin(String formID, Map<String, String> admissionMap, 
			Map<String, InputStream> admissionFormIDMap, Map<String, String> fileNameMap);
}
