package com.geu.aem.cca.core.servlets;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
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
	Constants.SERVICE_DESCRIPTION + CCAConstants.SERVLET_NAME,
	"sling.servlet.methods=" + HttpConstants.METHOD_POST,
	"sling.servlet.paths="+ "/bin/ccaLeadCaptureServlet"
})
public class CCALeadCaptureServlet extends SlingAllMethodsServlet{

	private static final long serialVersionUID = 1L;
	final static Logger LOGGER = LoggerFactory.getLogger(CCALeadCaptureServlet.class);
	private static final String CLASS_NAME = CCALeadCaptureServlet.class.getSimpleName();

	@Reference
	private CallCentreLeadsService callCentreLeadsService;

	@Override
	protected void doPost(SlingHttpServletRequest  request,
			SlingHttpServletResponse response) throws ServletException, IOException {

		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		LOGGER.info(CCAConstants.ENTERING + CCAConstants.SPACE + CLASS_NAME
				+ CCAConstants.SPACE+METHOD_NAME);

		RequestParameterMap requestmap=request.getRequestParameterMap();
		Map<String,String> ccaLeadsMap=new LinkedHashMap<String, String>();
		
		if(null!=requestmap)
		{
			for(String ccaLead:requestmap.keySet())
			{
				Object value = requestmap.get(ccaLead);
				if(null==value)
				{
					ccaLeadsMap.put(ccaLead, CCAConstants.EMPTY);
				}
				else
				{
					ccaLeadsMap.put(ccaLead, requestmap.getValue(ccaLead).toString());
				}
			}
		}
		
		String firstName=request.getParameter(CCAConstants.FIRSTNAME);
		String lastName=request.getParameter(CCAConstants.LASTNAME);
		String contactNum=request.getParameter(CCAConstants.CONTACTNUM);
		String formAction=request.getParameter(CCAConstants.FORMACTION);
		
		if(StringUtils.isNotEmpty(formAction) && StringUtils.isNotEmpty(firstName) && StringUtils.isNotEmpty(lastName) && StringUtils.isNotEmpty(contactNum))
		{
			if(formAction.equals(CCAConstants.FORMACTION_GET))
			{
				JSONObject jsonObject=callCentreLeadsService.getLeadData(firstName, lastName, contactNum);
				String jsonData=jsonObject.toJSONString();
				response.getWriter().write(jsonData);
			}
			else if(formAction.equals(CCAConstants.FORMACTION_INSERT))
			{
				JSONObject jsonObject=callCentreLeadsService.getLeadData(firstName, lastName, contactNum);
				int leadId=0;
				String interest=request.getParameter(CCAConstants.INTEREST);
				String status="";
				if(StringUtils.isNotEmpty(interest))
				{
					if(null!=jsonObject.get(CCAConstants.LEAD_ID))
					{
						leadId=Integer.parseInt(jsonObject.get(CCAConstants.LEAD_ID).toString());
						if(null!=ccaLeadsMap.get(CCAConstants.COMMENT))
						{
							String comment=ccaLeadsMap.get(CCAConstants.COMMENT);
							if(!comment.isEmpty())
							{
								if(null==jsonObject.get(CCAConstants.PREVIOUS_COMMENT))
								{
									ccaLeadsMap.put(CCAConstants.PREVIOUS_COMMENT, comment);
								}
								else
								{
									String previousComment=jsonObject.get(CCAConstants.PREVIOUS_COMMENT).toString();

									StringBuilder comments = new StringBuilder();
									if(previousComment.isEmpty())
									{
										ccaLeadsMap.put(CCAConstants.PREVIOUS_COMMENT, comment);
									}
									else
									{
										comments.append(previousComment).append(CCAConstants.DELIMITER).append(comment);
										previousComment=comments.toString();
										ccaLeadsMap.put(CCAConstants.PREVIOUS_COMMENT, previousComment);
									}
									
								}
							}
							else {
								if(null!=jsonObject.get(CCAConstants.PREVIOUS_COMMENT))
								{
									ccaLeadsMap.put(CCAConstants.PREVIOUS_COMMENT, jsonObject.get(CCAConstants.PREVIOUS_COMMENT).toString());
								}
							}
						}
						ccaLeadsMap.remove(CCAConstants.COMMENT);
						int updateStatus=callCentreLeadsService.insertLeadData(ccaLeadsMap,CCAConstants.FORMACTION_UPDATE,leadId);
						
						if(updateStatus!=0)
						{
							LOGGER.info("Updated Successfully in "+CCAConstants.TABLE_NAME);
						}
						else
						{
							LOGGER.error("Error while updating "+CCAConstants.TABLE_NAME);
						}
						status=String.valueOf(updateStatus);
					}
					else
					{
						ccaLeadsMap.put(CCAConstants.PREVIOUS_COMMENT, ccaLeadsMap.get(CCAConstants.COMMENT));
						ccaLeadsMap.remove(CCAConstants.COMMENT);
						int insertStatus=callCentreLeadsService.insertLeadData(ccaLeadsMap,CCAConstants.FORMACTION_INSERT,leadId);

						if(insertStatus!=0)
						{
							LOGGER.info("Inserted Successfully in "+CCAConstants.TABLE_NAME);
						}
						else {
							LOGGER.error("Error while inserting in"+CCAConstants.TABLE_NAME);
						}
						status=String.valueOf(insertStatus);
					}
			    }
				response.getWriter().write(status);
			}
		}
		else
		{
			LOGGER.error("Mandatory Fields Empty in "+CLASS_NAME+CCAConstants.SPACE+METHOD_NAME);
		}
		LOGGER.info(CCAConstants.EXITING + CCAConstants.SPACE + CLASS_NAME
				+ CCAConstants.SPACE+METHOD_NAME);
	}

}
