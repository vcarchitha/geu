package com.geu.aem.web.core.servlets;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.simple.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.service.EnquiryFormService;
import com.geu.aem.web.service.AWSEmailService;
import com.geu.aem.web.util.GEUWebUtils;

/**
 * @author nitin.jangir
 *
 */
@Component(service = { Servlet.class },
property = { "sling.servlet.paths=" + "/bin/geu/enquiryformgehu",
		"sling.servlet.methods=" + "POST"})
public class EnquiryFormGehuServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;
	final static Logger LOGGER = LoggerFactory.getLogger(EnquiryFormGehuServlet.class);
	
	@Reference
	EnquiryFormService enquiryFormService;
	
	@Reference
	ResourceResolverFactory resolverFactory;
	
	@Reference
	AWSEmailService awsEmailService;
	
	@Override
	protected void doPost(SlingHttpServletRequest  request,
			SlingHttpServletResponse response) throws ServletException, IOException {
		RequestParameterMap requestMap = request.getRequestParameterMap();
		Map<String,String> enquiryFormMap = new LinkedHashMap<String, String>();
		String requestPath = request.getRequestURI();
		if(requestMap != null)
		{
			for(String enquiryForm : requestMap.keySet())
			{
				Object value = requestMap.get(enquiryForm);
				if(value != null) {
					if (enquiryForm.equalsIgnoreCase(ResourceConstants.STATE_PARAM)) {
						enquiryFormMap.put(enquiryForm, requestMap.getValue(enquiryForm).toString().replaceAll(ResourceConstants.HYPHEN, ResourceConstants.SPACE));
					} else {
						enquiryFormMap.put(enquiryForm, requestMap.getValue(enquiryForm).toString());
					}
				} else {
					enquiryFormMap.put(enquiryForm, ResourceConstants.EMPTY_STRING);
				}
			}
		}
		DateFormat dateFormat = new SimpleDateFormat(ResourceConstants.YYYY_MM_DD_HH_MM_SS);
		Date date = new Date();
		enquiryFormMap.put(ResourceConstants.CREATED_DATE_TIME, dateFormat.format(date));
		
		String toAddress = enquiryFormMap.get(ResourceConstants.EMAIL);
		enquiryFormMap.remove(ResourceConstants.RECAPTCHA_RESPONSE);
		boolean insertStatus = enquiryFormService.insertData(enquiryFormMap, requestPath);
		if(insertStatus == false) {
			LOGGER.info("Inserted Successfully in enquiryform table");
			response.getWriter().write(ResourceConstants.SUCCESS);
		} else {
			LOGGER.error("Error while inserting in enquiryform table");
		}
		sendEnquiryMail(toAddress);		
	}
	
	private void sendEnquiryMail(String toAddress) {
		StringBuilder stringBuilder=new StringBuilder();
		try {
        String jsonPath=ResourceConstants.EMAIL_CONFIG+"gehu-enquiry-form.json";
        JSONObject emailObject=	GEUWebUtils.getJSONData(resolverFactory, jsonPath);

		String subject=emailObject.get("subject").toString();
		String body=String.join(
				System.getProperty("line.separator"),
				emailObject.get("body").toString(),
				stringBuilder.toString()
				);

		Multipart multipart = new MimeMultipart();
		
		BodyPart bodyPart = new MimeBodyPart();
		bodyPart.setContent(body, "text/html");
		multipart.addBodyPart(bodyPart);
		awsEmailService.sendEmail(subject, multipart, toAddress);
		}catch (MessagingException e) {
			LOGGER.error("Messaging Exception in  EnquiryFormGehuServlet" + e.getMessage());
		}catch (NullPointerException e) {
			 LOGGER.error("NullPointerException Exception in  EnquiryFormGehuServlet" + e.getMessage()); 
		}
		
	}
}
