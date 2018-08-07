package com.geu.aem.web.core.service.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourceNotFoundException;
import com.day.commons.datasource.poolservice.DataSourcePool;
import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.models.bean.AdmissionFields;
import com.geu.aem.web.core.service.GEHULoginAdmissionFormService;
import com.geu.aem.web.core.service.GEHUSendEmailService;
import com.geu.aem.web.core.service.SendEmailService;
import com.geu.aem.web.service.SMSApplicationService;

@Component(service = GEHULoginAdmissionFormService.class, immediate = true)
public class GEHULoginAdmissionFormServiceImpl implements GEHULoginAdmissionFormService {

	private final Logger LOGGER = LoggerFactory
			.getLogger(GEHULoginAdmissionFormServiceImpl.class);

	@Reference
	private DataSourcePool source;
	
	@Reference
	SMSApplicationService smsAppService;
	
	@Reference
	GEHUSendEmailService gehuEmailService;

	/**
	 * Returns connection using the configured DataSourcePool
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

	public void insertGEHULoginAdmissionData(Map<String, String> admissionMap,
			String phoneNumber) {
		Connection connection=null;
		try {
			connection = getConnection();
			String formIdGenerated = generateFormID();
			
			admissionMap.put("formId", formIdGenerated);
			String timeStamp = new SimpleDateFormat("ddMMYYYY_HHmmss").format(Calendar.getInstance().getTime());
			admissionMap.put(ResourceConstants.LAST_MODIFIED, timeStamp);
			String insertQuery = generateInsertQuery(admissionMap);
			LOGGER.info("insertQuery :::"+insertQuery);

			if (StringUtils.isNotBlank(insertQuery)) {
				PreparedStatement admissionFormPreparedStmt = connection
						.prepareStatement(insertQuery);
				int columnIndex = 1;
				for (String admissionField : admissionMap.keySet()) {
					if(((String)admissionField).equalsIgnoreCase(ResourceConstants.LAST_MODIFIED)){
						java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
						admissionFormPreparedStmt.setTimestamp(columnIndex, date);
					}else{
						if (((String) admissionMap.get(admissionField)).isEmpty()) {
							admissionFormPreparedStmt.setNull(columnIndex,
									java.sql.Types.VARCHAR);
						} else {
							admissionFormPreparedStmt.setObject(columnIndex, admissionMap
									.get(admissionField).toString());
						}
					}					
					columnIndex++;
				}

				int insertStatus = admissionFormPreparedStmt.executeUpdate();
				LOGGER.info("insertStatus ::::" + insertStatus);
				if(insertStatus==1){
					LOGGER.info("Inserted successfully the BASIC DATA!!!"+formIdGenerated);
					smsAppService.sendMessage(formIdGenerated, phoneNumber, true);
					String email = admissionMap.get(ResourceConstants.EMAIL);
					gehuEmailService.sendEmailToUser(email, formIdGenerated);					
				}
			}
		} catch (SQLException e) {
			LOGGER.error("SQLException occurred in insertGEHULoginAdmissionData method "
					+ e.getMessage());
		}catch (Exception e) {
			LOGGER.error("Exception occurred in insertGEHULoginAdmissionData method "
					+ e.getMessage());
		} finally{
			try {
				if(connection!=null){
					connection.close();
				}
			} catch (SQLException e) {
				LOGGER.error("SQLException occurred in insertGEHULoginAdmissionData method "
						+ e.getMessage());
			}
		}
	}

	/**
	 * This method is used to generate insert query for admission table
	 * @param admissionMap
	 * @return query
	 */
	private String generateInsertQuery(Map<String, String> admissionMap) {

		StringBuilder query = new StringBuilder("INSERT INTO ").append(
				ResourceConstants.GEHU_ADMISSION_TABLE_NAME).append(" (");
		StringBuilder placeholders = new StringBuilder();

		for (Iterator<String> iter = admissionMap.keySet().iterator(); iter
				.hasNext();) {
			query.append(iter.next());
			placeholders.append("?");

			if (iter.hasNext()) {
				query.append(",");
				placeholders.append(",");
			}
		}
		query.append(") VALUES (").append(placeholders).append(")").append(";");
		return query.toString();
	}

	/**
	 * This method generates the form ID for the admission form.
	 * @param admissionMap
	 * @return formID
	 */
	private String generateFormID()
			throws SQLException {
		String formID = null;
		boolean formIDCheck = false;

		int length = ResourceConstants.FORM_ID_LENGTH;
		String alpha = ResourceConstants.FORM_CHARACTERS;
		Random random = new Random();
		StringBuilder strbuilder = new StringBuilder(length);		
		strbuilder.append(ResourceConstants.FORM_ID_GEHU);
		 
		int lastTwoDigits = Calendar.getInstance().get(Calendar.YEAR) % 100;
		strbuilder.append(lastTwoDigits);

		for (int i = 0; i < length; i++) {
			strbuilder.append(alpha.charAt(random.nextInt(alpha.length())));
		}
		formID = strbuilder.toString();
		formIDCheck = checkFormIDAlreadyExistsInGEHU(formID);
		if (formIDCheck) {
			generateFormID();
		} else {
			return formID;
		}
		return formID;
	}

	public boolean checkFormIDAlreadyExistsInGEHU(String formID) {
		boolean formIDCheck = false;
		Connection connection = null;
		try {
			String checkQuery = "SELECT firstName FROM gehu_admission WHERE formId = '"
					+ formID + "'";
			LOGGER.info("checkQuery::::" + checkQuery);
			connection = getConnection();
			PreparedStatement checkPreparedStmt = connection
					.prepareStatement(checkQuery);
			ResultSet result = checkPreparedStmt.executeQuery(checkQuery);

			if (result.next()) {
				formIDCheck = true;
			} else {
				formIDCheck = false;
			}
		} catch (SQLException e) {
			LOGGER.error("SQLException occurred in checkFormIDAlreadyExistsInGEHU method:"
					+ e.getMessage());
		} finally {
			try {
				if(connection!=null){
					connection.close();
				}
			} catch (SQLException e) {
				LOGGER.error("SQLException occurred in checkFormIDAlreadyExistsInGEHU method "
						+ e.getMessage());
			}
		}

		return formIDCheck;
	}
	
	public String getGEHUMobileNumberFormID(String formID){
		String mobileNumber = null;
		Connection connection = null;
		try{
			String phNumberQuery = "SELECT contactNum FROM gehu_admission WHERE formId = '"
					+ formID + "'";
			connection = getConnection();
			PreparedStatement checkPreparedStmt = connection
					.prepareStatement(phNumberQuery);
			ResultSet result = checkPreparedStmt.executeQuery(phNumberQuery);
			if(result.next()){
				mobileNumber = result.getString("contactNum");
			}
		}catch (SQLException e){
			LOGGER.error("SQLException occurred in getGEHUMobileNumberFormID : "+e.getMessage());
		} finally {
			try {
				if(connection!=null){
					connection.close();
				}
			} catch (SQLException e) {
				LOGGER.error("SQLException occurred in getGEHUMobileNumberFormID method in finally "
						+ e.getMessage());
			}
		}
		
		return mobileNumber;
	}

	public boolean checkUserAlreadyExistsInGEHU(Map<String, String> admissionMap) {
		boolean userCheck = false;	
		Connection connection = null;
		try {
			String firstName = admissionMap.get(AdmissionFields.firstName
					.toString());
			String lastName = admissionMap.get(AdmissionFields.lastName
					.toString());
			String contactNum = admissionMap.get(AdmissionFields.contactNum
					.toString());

			String selectQuery = "SELECT * FROM gehu_admission WHERE firstName = '"
					+ firstName + "' AND lastName = '" + lastName
					+ "' AND contactNum = '" + contactNum + "'";
			LOGGER.info("select query ::" + selectQuery);
			connection = getConnection();
			PreparedStatement checkPreparedStmt = connection
					.prepareStatement(selectQuery);
			ResultSet result = checkPreparedStmt.executeQuery(selectQuery);

			if (result.next()) {
				userCheck = true;
			} else {
				userCheck = false;
			}
		} catch (SQLException e) {
			LOGGER.error("SQLException occurred in checkUserAlreadyExistsInGEHU method : "
					+ e.getMessage());
		} finally {
			try {
				if(connection!=null){
					connection.close();	
				}
			} catch (SQLException e) {
				LOGGER.error("SQLException occurred in checkUserAlreadyExistsInGEHU method in finally "
						+ e.getMessage());
			}
		}

		return userCheck;
	}

	public JSONObject getGEHULoginAdmissionData(Map<String, String> admissionMap) {

		JSONObject jsonObject = new JSONObject();
		Connection connection = null;
		Map<String,String> admissionjsonMap=new LinkedHashMap<String, String>();
		try {
			String getDataQuery = "SELECT "
					+ AdmissionFields.firstName.toString() + "," + " "
					+ AdmissionFields.lastName.toString() + ", "
					+ AdmissionFields.email.toString() + "," + " "
					+ AdmissionFields.formId.toString() + ", "
					+ AdmissionFields.contactNum.toString() + ", "
					+ AdmissionFields.interest.toString() + " "
					+ " FROM gehu_admission WHERE " + "  "
					+ AdmissionFields.firstName.toString() + "='"
					+ admissionMap.get(AdmissionFields.firstName.toString())
					+ "' " + " AND " + AdmissionFields.lastName.toString()
					+ "='"
					+ admissionMap.get(AdmissionFields.lastName.toString())
					+ "' " + " AND " + AdmissionFields.contactNum.toString()
					+ "='"
					+ admissionMap.get(AdmissionFields.contactNum.toString())
					+ "'";

			connection = getConnection();
			PreparedStatement getPreparedStmt = connection
					.prepareStatement(getDataQuery);
			ResultSet result = getPreparedStmt.executeQuery(getDataQuery);
			ResultSetMetaData addmissionData = result.getMetaData();
			while(result.next()){
				for (int i = 1; i <= addmissionData.getColumnCount(); i++) {
					String admissionHeader = addmissionData.getColumnName(i);
					Object admissionValue = result.getObject(admissionHeader);
					if(admissionValue!=null){
						admissionjsonMap.put(admissionHeader, admissionValue.toString());
					}
				}
			}
			jsonObject.putAll(admissionjsonMap);

		} catch (SQLException e) {
			LOGGER.error("SQLException occurred in getGEHULoginAdmissionData method :"
					+ e.getMessage());
		} finally {
			try {
				if(connection!=null){
					connection.close();	
				}
			} catch (SQLException e) {
				LOGGER.error("SQLException occurred in getGEHULoginAdmissionData method in finally "
						+ e.getMessage());
			}
		}

		return jsonObject;

	}
	
	public JSONObject getGEHUAdmissionDataFromFormID(String formID) throws SQLException{
		JSONObject jsonObject = new JSONObject();
		Connection connection = null;
		Map<String,String> admissionFormIDjsonMap=new LinkedHashMap<String, String>();
		try{			
			connection = getConnection();			
			CallableStatement cStmt = connection.prepareCall("{call GEHUGetAdmissionDetails (?) }");
			cStmt.setString(1, formID);
			cStmt.execute();
			ResultSet result = cStmt.getResultSet();		
			ResultSetMetaData addmissionBasicData = result.getMetaData();			
			LOGGER.info("Admision query :::"+cStmt.toString());
			while(result.next()){
				for (int i = 1; i <= addmissionBasicData.getColumnCount(); i++) {
					String admissionHeader = addmissionBasicData.getColumnName(i);
					Object admissionValue = result.getObject(admissionHeader);
					if(admissionValue!=null){
						if(admissionHeader.equalsIgnoreCase(ResourceConstants.DATE_OF_BIRTH)){
							SimpleDateFormat sdf1=new SimpleDateFormat(ResourceConstants.DATE_FORMAT_YYYY_MM_DD);
							SimpleDateFormat sdf2=new SimpleDateFormat(ResourceConstants.DATE_FORMAT_MM_DD_YYYY);
							Date date1=sdf1.parse(admissionValue.toString());
							String datevalue = sdf2.format(date1);
							admissionFormIDjsonMap.put(admissionHeader, datevalue);
						}else{
							admissionFormIDjsonMap.put(admissionHeader, admissionValue.toString());
						}
					}
				}
			}
			
			jsonObject.putAll(admissionFormIDjsonMap);
			
		} catch (ParseException e) {
			LOGGER.error("ParseException occurred in getAdmissionDataFromFormID :"+e.getMessage());
		} finally {
			try {
				if(connection!=null){
					connection.close();	
				}
			} catch (SQLException e) {
				LOGGER.error("SQLException occurred in getAdmissionDataFromFormID method in finally "
						+ e.getMessage());
			}
		}
		
		return jsonObject;		
	}

	public boolean checkPaymentStatus(String formID) {

		boolean formIDCheck = false;
		Connection connection = null;
		try {
			String checkQuery = "SELECT paymentStatus FROM gehu_admission WHERE formId = '"
					+ formID + "'";
			LOGGER.info("checkQueryForPayment::::" + checkQuery);
			connection = getConnection();
			PreparedStatement checkPreparedStmt = connection
					.prepareStatement(checkQuery);
			ResultSet result = checkPreparedStmt.executeQuery(checkQuery);

			if (result.next()) {
				String status = result.getString("paymentStatus");
				if(StringUtils.isNotBlank(status)){
					if(status.equalsIgnoreCase(ResourceConstants.SUCCESSFUL)){
						formIDCheck = false;
					}else{
						formIDCheck = true;
					}
				}else{
					formIDCheck = true;
				}				
			}
		} catch (SQLException e) {
			LOGGER.error("SQLException occurred in checkPaymentStatus method:"
					+ e.getMessage());
		} finally {
			try {
				if(connection!=null){
					connection.close();
				}
			} catch (SQLException e) {
				LOGGER.error("SQLException occurred in checkPaymentStatus method "
						+ e.getMessage());
			}
		}

		return formIDCheck;	
	}

}
