package com.geu.aem.web.core.servlets;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.service.GEHUBasicAdmissionFormService;
import com.geu.aem.web.service.PaymentFormService;

/**
 * This servlet is used for inserting, updating and fetching the data from Basic Details Tab of admission form
 * fields. 
 * @author smrithi.g
 */
@Component(service = Servlet.class, property = {
	"service.description=" + "Basic Admission Form Servlet",
	"sling.servlet.methods=" + HttpConstants.METHOD_POST,
	"sling.servlet.paths=" + "/bin/gehu/basicAdmissionForm" })
public class GEHUBasicAdmissionFormServlet extends SlingAllMethodsServlet {
	
	@Reference
	GEHUBasicAdmissionFormService gehuAdmissionService;
	
	@Reference
	PaymentFormService paymentFormService;
	
	private static final long serialVersionUID = 1L;
	private final Logger LOGGER = LoggerFactory
			.getLogger(GEHUBasicAdmissionFormServlet.class);
	
	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {
		try{
			RequestParameterMap requestMap = request.getRequestParameterMap();
			Map<String, Object> basicAdmissionMap = new LinkedHashMap<String, Object>();
			Map<String, InputStream> uploadBasicFileStreamMap = new LinkedHashMap<String, InputStream>();
			Map<String, String> uploadBasicFileNameMap = new LinkedHashMap<String , String>();
			String[] cancelColumns = {ResourceConstants.FORM_ID};

			if(requestMap!=null){
				for (String basicAdmissionField : requestMap.keySet()) {
					if(basicAdmissionField.equalsIgnoreCase(ResourceConstants.FORM_ID)){
						String formId = requestMap.getValue(basicAdmissionField).toString();
						String type = ResourceConstants.GEHU;
						response = paymentFormService.processRegistrationFormId(request, response, formId, type);
					}
					boolean foundColumn = false;
					for(String cancelColumn : cancelColumns) {
	            		if(cancelColumn.equalsIgnoreCase(basicAdmissionField.toString())){
	            			foundColumn = true;
	            		}
	            	}
					if(!foundColumn){
						String value = requestMap.getValue(basicAdmissionField).toString();					
						if (value != null) {
							if(basicAdmissionField.equalsIgnoreCase(ResourceConstants.REGISTRATION_MARKSHEET)){
								Map<String, RequestParameter[]> params = request.getRequestParameterMap();
								for (Map.Entry<String, RequestParameter[]> pairs : params.entrySet()) {
									String paramKeys = pairs.getKey();
									if(paramKeys.equalsIgnoreCase(ResourceConstants.REGISTRATION_MARKSHEET)){
										RequestParameter[] registrationValue = pairs.getValue();
										if(registrationValue != null) {
											RequestParameter reqParam = registrationValue[0];
											InputStream stream = reqParam.getInputStream();
											basicAdmissionMap.put(paramKeys, stream.toString());
											uploadBasicFileStreamMap.put(paramKeys, stream);
											uploadBasicFileNameMap.put(paramKeys, reqParam.getFileName());
										}
									}
								}
							}else{
								basicAdmissionMap.put(basicAdmissionField,
										requestMap.getValue(basicAdmissionField).toString());
							}
						} else {
							basicAdmissionMap.put(basicAdmissionField,
									ResourceConstants.EMPTY_STRING);
						}	
					}									
				}
				gehuAdmissionService.updateBasicAdmissionData(basicAdmissionMap, request, uploadBasicFileStreamMap, 
						uploadBasicFileNameMap);
			}
		}catch(Exception e){
			LOGGER.error("Exception occurred in doPost :"+e.getMessage());
		}
	}
}
