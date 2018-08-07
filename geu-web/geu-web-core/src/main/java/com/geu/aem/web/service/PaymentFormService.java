package com.geu.aem.web.service;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

public interface PaymentFormService {

	public String getContactNumber(String formNumber, String formType);
	
	public String getVersion();
	
	public String getMerchantAccessCode();
	
	public String getCommandType();
	
	public String getMerchantID();
	
	public String getReceiptReturnURL();
	
	public String getRegistrationReceiptReturnURL();
	
	public SlingHttpServletResponse processFormId(SlingHttpServletRequest request,SlingHttpServletResponse response,String formId);
	
	public SlingHttpServletResponse processRegistrationFormId(SlingHttpServletRequest request, SlingHttpServletResponse response,
			String formId, String type);
	
	public void savePaymentStatus(SlingHttpServletRequest request, String amount, String transactionReference, String transactionResponseMessage, String formType);
	
	public void saveRegistrationPaymentStatus(SlingHttpServletRequest request, String amount, String transactionReference, String transactionResponseMessage, String formType);

}
