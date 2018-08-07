package com.geu.aem.web.core.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
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

import com.day.commons.datasource.poolservice.DataSourcePool;
import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.util.SQLConnectionUtil;

/**
 * @author nitin.jangir
 *
 */

@Component(service = Servlet.class, property = {
		"service.description=" + "Landing Page Level Course List Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET,
		"sling.servlet.paths=" + "/bin/gehu/levelCourseList" })
public class LandingPageLevelCourseServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOGGER = LoggerFactory
			.getLogger(LandingPageLevelCourseServlet.class);
	private static final String CLASS_NAME = LandingPageLevelCourseServlet.class
			.getSimpleName();
	
	@Reference
	private DataSourcePool source;

	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {

		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();
		
		String type = request.getParameter(ResourceConstants.TYPE);
		if (StringUtils.isNotBlank(type) && type.equalsIgnoreCase(ResourceConstants.LEVEL)) {
			JSONObject jsonLevelObj = new JSONObject();
			jsonLevelObj = getLevelData();
			String jsonLevelData = jsonLevelObj.toJSONString();
			try {
				response.getWriter().write(jsonLevelData);
			} catch (IOException e) {
				LOGGER.error("IOException in " + METHOD_NAME + " "
						+ CLASS_NAME + " " + e.getMessage());
			}
		} else if(StringUtils.isNotBlank(type) && type.equalsIgnoreCase(ResourceConstants.PROGRAM)){
			JSONObject jsonProgramObj = new JSONObject();
			String levelSelected = request
					.getParameter(ResourceConstants.LEVEL_SELECTED); 
			jsonProgramObj = getProgramData(levelSelected);
			String jsonProgramData = jsonProgramObj.toJSONString();
			try {
				response.getWriter().write(jsonProgramData);
			} catch (IOException e) {
				LOGGER.error("IOException in " + METHOD_NAME + " "
						+ CLASS_NAME + " " + e.getMessage());
			}
		}
	}

	private JSONObject getProgramData(String levelSelected) {
		JSONObject jsonObj = new JSONObject();	
		Connection connection =null;
		try{
			connection = SQLConnectionUtil.getConnection(source);
			String programQuery = "SELECT DISTINCT programTitle FROM gehu_programfeedetails"
					+ " WHERE levelName='"+levelSelected+"'";
			LOGGER.info("programQuery :::"+programQuery);
			PreparedStatement pgmPreparedStmt = connection
					.prepareStatement(programQuery);
			ResultSet result = pgmPreparedStmt.executeQuery(programQuery);
			while(result.next()){
				String programTitle = result.getString("programTitle");
				String programKey = programTitle.toLowerCase().replaceAll("\\s","");
				jsonObj.put(programKey, programTitle);
			}			
		}catch(SQLException e){
			LOGGER.error("SQLException occurred in LandingPageLevelCourseServlet"+e.getMessage());
		}catch(Exception e){
			LOGGER.error("Exception occurred in LandingPageLevelCourseServlet"+e.getMessage());
		} finally{
			try {
				if(connection!=null){
					connection.close();
				}
			} catch (SQLException e) {
				LOGGER.error("SQLException occurred in LandingPageLevelCourseServlet while closing connection"
						+ e.getMessage());
			}
		}
		return jsonObj;
	}

	private JSONObject getLevelData() {
		JSONObject jsonObj = new JSONObject();	
		Connection connection = null;
		try{
			connection = SQLConnectionUtil.getConnection(source);
			String levelQuery = "SELECT DISTINCT levelName FROM gehu_programfeedetails";
			LOGGER.info("levelQuery :::"+levelQuery);
			PreparedStatement levelPreparedStmt = connection
					.prepareStatement(levelQuery);
			ResultSet result = levelPreparedStmt.executeQuery(levelQuery);
			while(result.next()){
				String levelName = result.getString("levelName");
				String levelKey = levelName.toLowerCase().replaceAll("\\s","");
				jsonObj.put(levelKey, levelName);
			}
			
		} catch(SQLException e){
			LOGGER.error("SQLException occurred in LandingPageLevelCourseServlet"+e.getMessage());
		} finally{
			try {
				if(connection!=null){
					connection.close();
				}
			} catch (SQLException e) {
				LOGGER.error("SQLException occurred in LandingPageLevelCourseServlet while closing connection"
						+ e.getMessage());
			}
		}
		return jsonObj;
	}

}
