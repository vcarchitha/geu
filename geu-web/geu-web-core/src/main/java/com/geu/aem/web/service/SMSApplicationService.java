package com.geu.aem.web.service;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

public interface SMSApplicationService {
	
	public String sendOTP(String otp,String phoneNumber);
	
	public String generateSecureOTP();
	
	public boolean verifyOTP(SlingHttpServletRequest request);
	
	public SlingHttpServletResponse processSendOTP(SlingHttpServletRequest request,SlingHttpServletResponse response,String otp);
	
	public String sendMessage(String formId,String phoneNumber,boolean hasVariable);

}
