package com.geu.aem.cca.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.simple.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.cca.core.constants.CCAConstants;
import com.geu.aem.cca.core.service.CallCentreLeadsService;

@Component(service=Servlet.class,
property={
	Constants.SERVICE_DESCRIPTION + CCAConstants.FOLLOW_UPSERVLET_NAME,
	"sling.servlet.methods=" + HttpConstants.METHOD_GET,
	"sling.servlet.paths="+ "/bin/ccaFollowUpServlet"
})
public class CCAFollowUpServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	final static Logger LOGGER = LoggerFactory.getLogger(CCALeadCaptureServlet.class);
	private static final String CLASS_NAME = CCALeadCaptureServlet.class.getSimpleName();
	
	@Reference
	private CallCentreLeadsService callCentreLeadsService;

	@Override
	protected void doGet(SlingHttpServletRequest  request,
			SlingHttpServletResponse response) throws ServletException, IOException {

		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		LOGGER.info(CCAConstants.ENTERING + CCAConstants.SPACE + CLASS_NAME
				+ CCAConstants.SPACE+METHOD_NAME);
		
		String userId=request.getParameter(CCAConstants.USER_ID);
		JSONObject jsonObject=callCentreLeadsService.getFollowUpLeadData(userId);
		
		String jsonData=jsonObject.toJSONString();
		response.getWriter().write(jsonData);
		
		LOGGER.info(CCAConstants.EXITING + CCAConstants.SPACE + CLASS_NAME
				+ CCAConstants.SPACE+METHOD_NAME);
	}

}
