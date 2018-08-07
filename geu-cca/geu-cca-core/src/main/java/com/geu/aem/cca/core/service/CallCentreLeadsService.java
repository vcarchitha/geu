package com.geu.aem.cca.core.service;

import java.util.Map;
import org.json.simple.JSONObject;

public interface CallCentreLeadsService {

	//Adds a New CCALead Record in the table
	public int insertLeadData(Map<String,String> map,String sqlAction,int leadId);

	//Retrieves CCALead Data from the table
	public JSONObject getLeadData(String firstName,String lastName,String contactNum);
	
	//Get Follow up Leads Data from the table
	public JSONObject getFollowUpLeadData(String userId);

}