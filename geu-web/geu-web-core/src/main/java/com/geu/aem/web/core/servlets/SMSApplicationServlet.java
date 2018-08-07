package com.geu.aem.web.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.service.SMSApplicationService;
import com.geu.aem.web.util.GEUWebUtils;

@Component(service=Servlet.class,
property={
	Constants.SERVICE_DESCRIPTION + ResourceConstants.OTP_SERVLET_NAME,
	"sling.servlet.methods=" + HttpConstants.METHOD_POST,
	"sling.servlet.paths="+ "/bin/smsOTPServlet"
})
public class SMSApplicationServlet extends SlingAllMethodsServlet{

	private static final long serialVersionUID = 1L;
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	private static final String CLASS_NAME = SMSApplicationServlet.class.getSimpleName();	

	@Reference
	private SMSApplicationService smsApplicationService;

	@Override
	protected void doPost(SlingHttpServletRequest  request,
			SlingHttpServletResponse response) throws ServletException, IOException {

		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		LOGGER.info(ResourceConstants.ENTERING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);

		String formAction=request.getParameter(ResourceConstants.FORM_ACTION);
		try {

			if(StringUtils.isNotBlank(formAction))
			{
				if(formAction.equals("sendotp"))
				{
					String otp=smsApplicationService.generateSecureOTP();
					String phoneNumber=request.getParameter("phoneNumber");
					
					String result=smsApplicationService.sendOTP(otp,phoneNumber);
					if(StringUtils.isNotBlank(result))
					{
						String status=GEUWebUtils.getJsonValue(result, "status");
						if(StringUtils.isNotBlank(status))
						{
							if(status.equalsIgnoreCase("success"))
						    {
						    	response.getWriter().write("success");
						    }
						    else
						    {
						    	response.getWriter().write("error");
						    }
						}
					}
					
					response=smsApplicationService.processSendOTP(request,response,otp);
				
				}
				else if(formAction.equals("verifyotp"))
				{
					boolean isOTPValid=smsApplicationService.verifyOTP(request); 
					if (!isOTPValid)
					{
						response.getWriter().write("invalid_otp");
					}
					else
					{
						response.getWriter().write("valid_otp");
					}
				}
			}
			else
			{
				LOGGER.error("Form Action Not Specified "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME);
			}
		}
		catch (Exception e) {
			LOGGER.error("Exception in "+CLASS_NAME + e.getMessage());
		}

		LOGGER.info(ResourceConstants.EXITING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);

	}
}
