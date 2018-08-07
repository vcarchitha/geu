package com.geu.aem.web.core.service;

import java.util.Map;

public interface HDFCPaymentService {
	
	public String fetchAmountFromFormID(String formID);
	
	public Map<String, String> fetchHDFCConfigFromFormID(String formID, String selectionMode, String feeType, String partialAmount);
}
