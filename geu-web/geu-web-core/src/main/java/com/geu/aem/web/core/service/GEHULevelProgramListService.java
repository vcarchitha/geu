package com.geu.aem.web.core.service;

import org.apache.sling.api.SlingHttpServletRequest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface GEHULevelProgramListService {

	/**
	 * This method returns the levels in Basic Details Tab of GEHU Admission Form.
	 * @param request
	 * @return jsonObject
	 */
	public JSONObject getAdmissionLevel(SlingHttpServletRequest request, String campus);
	
	/**
	 * This method returns the programs fees in the Basic Details Tab of GEHU Admission Form.
	 * @param levelSelected
	 * @param request
	 * @return jsonObject
	 */
	public JSONArray getProgramFeeDetails(SlingHttpServletRequest request, String programSelected, float pcmScore,
			float jeeScore, String gender, float tenthPercentage, float twelvethPercentage, float graduationPercentage,
			String hscResultsDeclared, String ugResultsDeclared, int isPcmOrJeeMandatoryInt, String levelSelected,
			String campus, String state, String sslcResultsDeclared);
	
	/**
	 * This method returns the program in Basic Details Tab of GEHU Admission Form.
	 * @param request
	 * @return
	 */
	public JSONArray getAdmissionProgram(SlingHttpServletRequest request, String levelSelected, String campus);
}
