package com.geu.aem.web.core.servlets;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Servlet;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.service.UploadAdmissionFormService;

/**
 * This servlet is used for updating and fetching the data from Upload Documents
 * Tab of admission form
 * 
 * @author smrithi.g
 * 
 */
@Component(service = Servlet.class, property = {
		"service.description=" + "Upload Documents Admission Form Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST,
		"sling.servlet.paths=" + "/bin/geu/uploadAdmissionForm" })
public class UploadDocumentsAdmissionServlet extends SlingAllMethodsServlet {

	@Reference
	UploadAdmissionFormService uploadService;

	private static final long serialVersionUID = 1L;
	private final Logger LOGGER = LoggerFactory
			.getLogger(UploadDocumentsAdmissionServlet.class);

	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {
		Map<String, Object> uploadAdmissionMap = new LinkedHashMap<String, Object>();
		Map<String, InputStream> uploadFileStreamMap = new LinkedHashMap<String, InputStream>();
		Map<String, String> uploadFileNameMap = new LinkedHashMap<String , String>();
		try {
			final boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			if(isMultipart){
				Map<String, RequestParameter[]> params = request.getRequestParameterMap();
				String[] cancelColumns = { "passport"};
				String[] uploadFileColumn = {ResourceConstants.PHOTO, ResourceConstants.MARKSHEET_TEN,
						ResourceConstants.MARKSHEET_TWELVE, ResourceConstants.MARKSHEET_UG};

	            for (Map.Entry<String, RequestParameter[]> pairs : params.entrySet()) {
		            String k = pairs.getKey();
		            boolean foundColumn = false;
		            boolean isFile = false;
					for (String cancelColumn : cancelColumns) {
						if (cancelColumn.equalsIgnoreCase(k.toString())) {
							foundColumn = true;
							break;
						}
					}
					for (String fileColumn : uploadFileColumn) {
						if (fileColumn.equalsIgnoreCase(k.toString())) {
							isFile = true;
							break;
						}
					}
					if (!foundColumn) {
						RequestParameter[] value = pairs.getValue();
						if (value != null) {
							RequestParameter param = value[0];
							if(!isFile){
								uploadAdmissionMap.put(k,param);
							}else{								
								InputStream stream = param.getInputStream();								
								uploadAdmissionMap.put(k, stream.toString());
								uploadFileStreamMap.put(k, stream);
								uploadFileNameMap.put(k, param.getFileName());
							}
						} else {
							uploadAdmissionMap.put(k,ResourceConstants.EMPTY_STRING);
						}
					}		      
				}
	            uploadService.updateUploadAdmissionData(uploadAdmissionMap,
	            		uploadFileStreamMap, uploadFileNameMap);
			}	
			
		} catch (Exception e) {
			LOGGER.error("Exception occurred in doPost :" + e.getMessage());
		}

	}

}
