package com.geu.aem.web.core.servlets;

import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.service.GEHUSelectionModeService;

/**
 * This servlet is used for inserting, updating the selection mode value from Selection Mode Tab of admission form
 * fields. 
 * @author smrithi.g
 */
@Component(service = Servlet.class, property = {
	"service.description=" + "GEHU Selection Mode Servlet",
	"sling.servlet.methods=" + HttpConstants.METHOD_POST,
	"sling.servlet.paths=" + "/bin/gehu/selectionModeServlet"})
public class GEHUSelectionModeServlet extends SlingAllMethodsServlet {
	
	@Reference
	GEHUSelectionModeService modeService;
	
	private static final long serialVersionUID = 1L;
	private final Logger LOGGER = LoggerFactory
			.getLogger(GEHUSelectionModeServlet.class);
	
	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {
		try{
			String selectionMode = request.getParameter("selectionMode");
			if(StringUtils.isNotBlank(selectionMode)){
				String formId = request.getParameter("formId");
				if(StringUtils.isNotBlank(formId)){
					modeService.updateGEHUSelectionMode(selectionMode, formId);
				}			
			}else{
				String selectionModeErrorMsg = ResourceConstants.SELECTION_MODE_ERROR_MSG;
				response.getWriter().write(selectionModeErrorMsg);
			}
		}catch(Exception e){
			LOGGER.error("Exception occurred in doPost :"+e.getMessage());
		}
	}

}
