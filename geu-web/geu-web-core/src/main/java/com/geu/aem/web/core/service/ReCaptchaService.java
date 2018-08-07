package com.geu.aem.web.core.service;

import org.apache.sling.api.SlingHttpServletRequest;

public interface ReCaptchaService {
	
	public boolean verify(SlingHttpServletRequest request);
    public String getSiteKey();
    
}
