package com.geu.aem.web.core.service;

import java.io.InputStream;
import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;

public interface GEHUBasicAdmissionFormService {

	public void updateBasicAdmissionData (Map<String, Object> basicAdmissionMap, SlingHttpServletRequest request,
			Map<String, InputStream> uploadBasicFileStreamMap, Map<String, String> uploadBasicFileNameMap);
}
