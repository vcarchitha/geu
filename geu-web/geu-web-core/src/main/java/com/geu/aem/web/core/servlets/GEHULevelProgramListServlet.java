package com.geu.aem.web.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.service.GEHULevelProgramListService;

/**
 * This servlet returns the list of levels and programs in Basic Details page.
 * 
 * @author smrithi.g
 * 
 */
@Component(service = Servlet.class, property = {
		"service.description=" + "Level Program List Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST,
		"sling.servlet.paths=" + "/bin/gehu/levelProgramList" })
public class GEHULevelProgramListServlet extends SlingAllMethodsServlet {

	@Reference
	GEHULevelProgramListService gehuLevelPrgm;

	private static final long serialVersionUID = 1L;
	private final Logger LOGGER = LoggerFactory
			.getLogger(GEHULevelProgramListServlet.class);
	private static final String CLASS_NAME = GEHULevelProgramListServlet.class
			.getSimpleName();

	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {

		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		try {
			String type = request.getParameter(ResourceConstants.TYPE);
			if (type.equalsIgnoreCase(ResourceConstants.LEVEL)) {
				String campus = request.getParameter(ResourceConstants.CAMPUS);
				JSONObject jsonLevelObj = new JSONObject();
				jsonLevelObj = gehuLevelPrgm.getAdmissionLevel(request, campus);
				String jsonLevelData = jsonLevelObj.toJSONString();
				response.getWriter().write(jsonLevelData);
			} else if (type.equalsIgnoreCase(ResourceConstants.FEE)) {
				String programSelected = request
						.getParameter(ResourceConstants.PROGRAM_SELECTED);
				String pcm = request.getParameter(ResourceConstants.PCM);
				float pcmInteger = Float.valueOf(pcm);
				String jee =request.getParameter(ResourceConstants.JEE);
				float jeeInteger = Float.valueOf(jee);
				String gender = request.getParameter(ResourceConstants.GENDER);
				String sslcPercentage = request.getParameter(ResourceConstants.SSLCPERCENTAGE);
				float tenthPercentage = Float.valueOf(sslcPercentage);
				String hscPercentage = request.getParameter(ResourceConstants.HSCPERCENTAGE);
				float twelfthPercentage = Float.valueOf(hscPercentage);
				
				LOGGER.info("twelfthPercentage :::"+twelfthPercentage);

				String ugPercentage = request.getParameter(ResourceConstants.UGPERCENTAGE);
				float graduationPercentage = Float.valueOf(ugPercentage);
				
				LOGGER.info("graduationPercentage :::"+graduationPercentage);

				String hscResultsDeclared = request.getParameter(ResourceConstants.HSCRESULTSDECLARED);
				LOGGER.info("hscResultsDeclared :::"+hscResultsDeclared);

				String ugResultsDeclared = request.getParameter(ResourceConstants.UGRESULTSDECLARED);	
				LOGGER.info("ugResultsDeclared :::"+ugResultsDeclared);
				
				String sslcResultsDeclared = request.getParameter(ResourceConstants.SSLCRESULTSDECLARED);
				LOGGER.info("sslcResultsDeclared :::"+sslcResultsDeclared);
				
				String isPcmOrJeeMandatory = request.getParameter(ResourceConstants.ISPCMORJEEMANDATORY);
				LOGGER.info("isPcmOrJeeMandatory :::"+isPcmOrJeeMandatory);

				int isPcmOrJeeMandatoryInt = Integer.parseInt(isPcmOrJeeMandatory);
				String levelSelected = request
						.getParameter(ResourceConstants.LEVEL_SELECTED); 
				LOGGER.info("levelSelected :::"+levelSelected);

				String campus = request.getParameter(ResourceConstants.CAMPUS);
				LOGGER.info("CAMPUS :::"+campus);
				String state = request.getParameter(ResourceConstants.STATE);
				LOGGER.info("state :::"+state);
				
				JSONArray jsonArray = new JSONArray();
				jsonArray = gehuLevelPrgm.getProgramFeeDetails(request,
						programSelected, pcmInteger, jeeInteger, gender, tenthPercentage, 
						twelfthPercentage, graduationPercentage, 
						hscResultsDeclared,	ugResultsDeclared, isPcmOrJeeMandatoryInt, levelSelected, campus,
						state, sslcResultsDeclared);
				String jsonPrgmData = jsonArray.toJSONString();
				response.getWriter().write(jsonPrgmData);
			}else if(type.equalsIgnoreCase(ResourceConstants.PROGRAM)){
				JSONArray jsonProgramArray = new JSONArray();
				String levelSelected = request
						.getParameter(ResourceConstants.LEVEL_SELECTED); 
				String campus = request.getParameter(ResourceConstants.CAMPUS);
				jsonProgramArray = gehuLevelPrgm.getAdmissionProgram(request, levelSelected, campus);
				String jsonProgramData = jsonProgramArray.toJSONString();
				response.getWriter().write(jsonProgramData);
			}
		} catch (IOException e) {
			LOGGER.error("Connection Exception in " + METHOD_NAME + " "
					+ CLASS_NAME + " " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Exception in " + METHOD_NAME + " " + CLASS_NAME + " "
					+ e.getMessage());
		}
	}
}
