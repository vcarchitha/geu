package com.geu.aem.web.core.service;

import java.io.InputStream;
import java.util.Map;

public interface GEHUUploadAdmissionFormService {

	/**
	 * This method updates the admission table with the uploaded documents from
	 * Upload Documents tab of Admission Form.
	 * 
	 * @param uploadAdmissionMap
	 * @param request
	 */
	public void updateUploadGEHUAdmissionData(
			Map<String, Object> uploadAdmissionMap,
			Map<String, InputStream> fileMap,
			Map<String, String> uploadDocsMaps);
}
