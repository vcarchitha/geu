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
import com.geu.aem.web.core.service.LevelProgramListService;

/**
 * This servlet returns the list of levels and programs in Basic Details page.
 * 
 * @author smrithi.g
 * 
 */
@Component(service = Servlet.class, property = {
		"service.description=" + "Level Program List Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST,
		"sling.servlet.paths=" + "/bin/geu/levelProgramList" })
public class LevelProgramListServlet extends SlingAllMethodsServlet {

	@Reference
	LevelProgramListService levelPrgm;

	private static final long serialVersionUID = 1L;
	private final Logger LOGGER = LoggerFactory
			.getLogger(LevelProgramListServlet.class);
	private static final String CLASS_NAME = LevelProgramListServlet.class
			.getSimpleName();

	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {

		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		try {
			String type = request.getParameter(ResourceConstants.TYPE);
			if (type.equalsIgnoreCase(ResourceConstants.LEVEL)) {
				JSONObject jsonLevelObj = new JSONObject();
				jsonLevelObj = levelPrgm.getAdmissionLevel(request);
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
				String ugPercentage = request.getParameter(ResourceConstants.UGPERCENTAGE);
				float graduationPercentage = Float.valueOf(ugPercentage);
				String hscResultsDeclared = request.getParameter(ResourceConstants.HSCRESULTSDECLARED);
				String ugResultsDeclared = request.getParameter(ResourceConstants.UGRESULTSDECLARED);				
				String isPcmOrJeeMandatory = request.getParameter(ResourceConstants.ISPCMORJEEMANDATORY);
				int isPcmOrJeeMandatoryInt = Integer.parseInt(isPcmOrJeeMandatory);
				String levelSelected = request
						.getParameter(ResourceConstants.LEVEL_SELECTED); 

				JSONArray jsonArray = new JSONArray();
				jsonArray = levelPrgm.getProgramFeeDetails(request,
						programSelected, pcmInteger, jeeInteger, gender, tenthPercentage, 
						twelfthPercentage, graduationPercentage, 
						hscResultsDeclared,	ugResultsDeclared, isPcmOrJeeMandatoryInt, levelSelected);
				String jsonPrgmData = jsonArray.toJSONString();
				response.getWriter().write(jsonPrgmData);
			}else if(type.equalsIgnoreCase(ResourceConstants.PROGRAM)){
				JSONArray jsonProgramArray = new JSONArray();
				String levelSelected = request
						.getParameter(ResourceConstants.LEVEL_SELECTED); 
				jsonProgramArray = levelPrgm.getAdmissionProgram(request, levelSelected);
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
