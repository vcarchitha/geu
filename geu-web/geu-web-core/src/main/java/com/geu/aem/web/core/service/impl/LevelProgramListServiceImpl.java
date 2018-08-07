package com.geu.aem.web.core.service.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.sling.api.SlingHttpServletRequest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourceNotFoundException;
import com.day.commons.datasource.poolservice.DataSourcePool;
import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.service.LevelProgramListService;

@Component(service = LevelProgramListService.class, immediate = true)
public class LevelProgramListServiceImpl implements LevelProgramListService{

	private final Logger LOGGER = LoggerFactory
			.getLogger(LevelProgramListServiceImpl.class);
	
	@Reference
	private DataSourcePool source;
	/**
	 * Returns connection using the configured DataSourcePool
	 * 
	 * @return connection
	 */
	private Connection getConnection() {
		Connection connection = null;
		try {
			DataSource dataSource = (DataSource) source
					.getDataSource(ResourceConstants.DATASOURCE_NAME);
			connection = dataSource.getConnection();
			LOGGER.info("connection:: " + connection);

		} catch (SQLException e) {
			LOGGER.error("SQLException occurred in getConnection method "
					+ e.getMessage());
		} catch (DataSourceNotFoundException e) {
			LOGGER.error("DataSourceNotFoundException occurred in getConnection method "
					+ e.getMessage());
		}
		return connection;
	}
	
	public JSONObject getAdmissionLevel(SlingHttpServletRequest request){
		
		JSONObject jsonObj = new JSONObject();	
		Connection connection = null;
		try{
			connection = getConnection();
			String levelQuery = "SELECT DISTINCT levelName FROM geu_programfeedetails";
			LOGGER.info("levelQuery :::"+levelQuery);
			PreparedStatement levelPreparedStmt = connection
					.prepareStatement(levelQuery);
			ResultSet result = levelPreparedStmt.executeQuery(levelQuery);
			while(result.next()){
				String levelName = result.getString("levelName");
				String levelKey = levelName.toLowerCase().replaceAll("\\s","");
				jsonObj.put(levelKey, levelName);
			}
			
		}catch(SQLException e){
			LOGGER.error("SQLException occurred in getAdmissionLevel method:"+e.getMessage());
		}catch(Exception e){
			LOGGER.error("Exception occurred in getAdmissionLevel method:"+e.getMessage());
		} finally{
			try {
				if(connection!=null){
					connection.close();
				}
			} catch (SQLException e) {
				LOGGER.error("SQLException occurred in getAdmissionLevel method "
						+ e.getMessage());
			}
		}
		return jsonObj;		
	}
	
	public JSONArray getProgramFeeDetails(SlingHttpServletRequest request,
			String programSelected, float pcm, float jee, String gender,
			float tenthPercentage, float twelfthPercentage, float graduationPercentage,
			String hscResultsDeclared, String ugResultsDeclared, int isPcmOrJeeMandatoryInt, String levelSelected){
		
		JSONArray jsonArray = new JSONArray();	
		Connection connection = null;
		try{
			connection = getConnection();
			float aggregate = 0;		
			String resultDeclared = null;
			JSONObject jsonObj = new JSONObject();	
			if(levelSelected.equalsIgnoreCase(ResourceConstants.POST_GRADUATE)){
				aggregate = graduationPercentage;
				resultDeclared = ugResultsDeclared;
			}else if((levelSelected.equalsIgnoreCase(ResourceConstants.UNDER_GRADUATE) &&
					(isPcmOrJeeMandatoryInt==1))){
				aggregate = twelfthPercentage;
				resultDeclared = hscResultsDeclared;
			}else if((levelSelected.equalsIgnoreCase(ResourceConstants.UNDER_GRADUATE) &&
					(isPcmOrJeeMandatoryInt==0))){
				aggregate = twelfthPercentage;
				resultDeclared = hscResultsDeclared;
			}
				
				CallableStatement cStmt = connection.prepareCall("{call GEUGetAdmissionFeeDetails (?,?,?,?,?,?)}");
				cStmt.setFloat(1, pcm);
				cStmt.setFloat(2, jee);
				cStmt.setString(3, gender);
				cStmt.setFloat(4, aggregate);
				cStmt.setString(5, programSelected);
				cStmt.setString(6, resultDeclared);
				cStmt.execute();
				ResultSet resultSetForFees = cStmt.getResultSet();	
				LOGGER.info("cmt query:::"+cStmt.toString());
				while(resultSetForFees.next()){
					jsonObj = new JSONObject();
					jsonObj.put("eligibilityText", resultSetForFees.getString("eligibilityText"));
					jsonObj.put("totalFees", resultSetForFees.getString("totalFees"));
					jsonObj.put("discountRate", resultSetForFees.getString("discountRate"));
					jsonObj.put("discountedFees", resultSetForFees.getString("discountedFees"));
					jsonObj.put("duration", resultSetForFees.getString("duration"));
					jsonArray.add(jsonObj);
				}			
			
		}catch(Exception e){
			LOGGER.error("Exception occurred in getAdmissionProgram method :"+e.getMessage());
		} finally{
			try {
				if(connection!=null){
					connection.close();
				}
			} catch (SQLException e) {
				LOGGER.error("SQLException occurred in getAdmissionProgram method "
						+ e.getMessage());
			}
		}
		return jsonArray;	
	}


	public JSONArray getAdmissionProgram(SlingHttpServletRequest request, String levelSelected) {
		
		JSONArray jsonArray = new JSONArray();	
		Connection connection =null;
		try{
			JSONObject jsonObj = null;	
			connection = getConnection();
			String programQuery = "SELECT DISTINCT programTitle, isPcmOrJeeMandatory FROM geu_programfeedetails"
					+ " WHERE levelName='"+levelSelected+"'";
			LOGGER.info("programQuery :::"+programQuery);
			PreparedStatement pgmPreparedStmt = connection
					.prepareStatement(programQuery);
			ResultSet result = pgmPreparedStmt.executeQuery(programQuery);
			while(result.next()){
				jsonObj = new JSONObject();	
				String programTitle = result.getString("programTitle");
				String pgmKey = programTitle.toLowerCase().replaceAll("\\s","");
				int isPcmOrJeeMandatory = result.getInt("isPcmOrJeeMandatory");
				jsonObj.put("programTitle", programTitle);
				jsonObj.put("isPcmOrJeeMandatory", isPcmOrJeeMandatory);
				jsonObj.put("nodename", pgmKey);
				jsonArray.add(jsonObj);
			}			
		}catch(SQLException e){
			LOGGER.error("SQLException occurred in getAdmissionProgram method:"+e.getMessage());
		}catch(Exception e){
			LOGGER.error("Exception occurred in getAdmissionProgram method:"+e.getMessage());
		} finally{
			try {
				if(connection!=null){
					connection.close();
				}
			} catch (SQLException e) {
				LOGGER.error("SQLException occurred in getAdmissionProgram method "
						+ e.getMessage());
			}
		}
		return jsonArray;	
	}	
}
