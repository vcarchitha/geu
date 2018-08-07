package com.geu.aem.web.core.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourceNotFoundException;
import com.day.commons.datasource.poolservice.DataSourcePool;
import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.service.SendEmailService;
import com.geu.aem.web.core.service.UploadAdmissionFormService;
import com.geu.aem.web.service.SMSApplicationService;

@Component(service = UploadAdmissionFormService.class, immediate = true)
public class UploadAdmissionFormServiceImpl implements UploadAdmissionFormService{
	
	private final Logger LOGGER = LoggerFactory
			.getLogger(UploadAdmissionFormServiceImpl.class);
	
	@Reference
	private DataSourcePool source;
	
	@Reference
	SMSApplicationService smsService;
	
	@Reference
	SendEmailService emailService;

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
	
	public void updateUploadAdmissionData(Map<String, Object> uploadAdmissionMap,
			Map<String, InputStream> uploadFileStreamMap, Map<String, String> uploadFileNameMap){
		Connection connection = null;
		try{
			connection = getConnection();
			String timeStamp = new SimpleDateFormat("ddMMYYYY_HHmmss").format(Calendar.getInstance().getTime());
			uploadAdmissionMap.put(ResourceConstants.LAST_MODIFIED, timeStamp);
			uploadAdmissionMap.put(ResourceConstants.SUBMIT_STATUS, ResourceConstants.SUBMITTED);
			String[] uploadFileColumn = {ResourceConstants.PHOTO_NAME, ResourceConstants.MARKSHEET_TEN_FILENAME,
					ResourceConstants.MARKSHEET_TWELVE_FILENAME, ResourceConstants.MARKSHEET_UG_FILENAME};
			List<String> columnNamesToBeAdded = new ArrayList<String>();

			for (String fileColumn : uploadFileColumn) {				
				for(String data: uploadAdmissionMap.keySet()){
					if(fileColumn.equalsIgnoreCase(data+"_name")){
						columnNamesToBeAdded.add(fileColumn);
					}
				}
			}
			
			for(String columnName:columnNamesToBeAdded){
				uploadAdmissionMap.put(columnName, null);
			}			

			String updateUploadQuery = generateUpdateQuery(uploadAdmissionMap);
			
			if (StringUtils.isNotBlank(updateUploadQuery)) {
				PreparedStatement admissionFormPreparedStmt = connection
						.prepareStatement(updateUploadQuery);
				int columnIndex= 1;
				for(String uploadData:uploadAdmissionMap.keySet()){
					if(StringUtils.isNotBlank(uploadData)){
						if(uploadData.equalsIgnoreCase(ResourceConstants.PHOTO) || uploadData.equalsIgnoreCase(ResourceConstants.MARKSHEET_TEN)
								|| uploadData.equalsIgnoreCase(ResourceConstants.MARKSHEET_TWELVE) || uploadData.equalsIgnoreCase(ResourceConstants.MARKSHEET_UG)){
							if(uploadAdmissionMap.get(uploadData).toString().isEmpty()){
								admissionFormPreparedStmt.setNull(columnIndex, java.sql.Types.BLOB);
							}else{							
								for(String uploadFileData:uploadFileNameMap.keySet()){
									if(uploadData.equalsIgnoreCase(uploadFileData)){
										InputStream is = uploadFileStreamMap.get(uploadFileData);
										byte[] bytes = IOUtils.toByteArray(is);
										admissionFormPreparedStmt.setBytes(columnIndex, bytes);	
									}
								}								
							}
							
						}else if(uploadData.equalsIgnoreCase(ResourceConstants.LAST_MODIFIED)){
							java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
							admissionFormPreparedStmt.setTimestamp(columnIndex, date);
						}else{
							if(uploadAdmissionMap.get(uploadData)==null){
								if(uploadData.equalsIgnoreCase(ResourceConstants.PHOTO_NAME) || uploadData.equalsIgnoreCase(ResourceConstants.MARKSHEET_TEN_FILENAME)
										|| uploadData.equalsIgnoreCase(ResourceConstants.MARKSHEET_TWELVE_FILENAME) || uploadData.equalsIgnoreCase(ResourceConstants.MARKSHEET_UG_FILENAME)){
									for(String uploadFileData:uploadFileNameMap.keySet()){
										if(uploadData.equalsIgnoreCase(uploadFileData+"_name")){
											String fileName = uploadFileNameMap.get(uploadFileData);
											admissionFormPreparedStmt.setObject(columnIndex, fileName);
										}
									}
								}else{
									admissionFormPreparedStmt.setNull(columnIndex, java.sql.Types.VARCHAR);
								}
							}else{
								admissionFormPreparedStmt.setObject(columnIndex, uploadAdmissionMap.get(uploadData).toString());
							}
						}																	
						columnIndex++;
					}					
				}
				if(uploadAdmissionMap.get(ResourceConstants.FORM_ID)!=null){
					int updateStatus = admissionFormPreparedStmt.executeUpdate();
					LOGGER.info("updateStatus :"+updateStatus);
					if(updateStatus==1){
						String formId = uploadAdmissionMap.get(ResourceConstants.FORM_ID).toString();
						Map<String, String> contactEamilMap = new HashMap<String, String>();
						Map<String, String> admissionMap = new HashMap<String, String>();
						Map<String, InputStream> admissionFormIDMap = new HashMap<String, InputStream>();
						Map<String, String> fileNameMap = new HashMap<String, String>();

						contactEamilMap = getMobileEmailFromFormID(formId);
						LOGGER.info("DATA UPLOADED SUCCESSFULLY !!"+formId);
						String contactNum = contactEamilMap.get("mobile");
						smsService.sendMessage(null, contactNum, false);
						String email = contactEamilMap.get("email");
						emailService.sendSubmissionEmailToUser(email);
						admissionMap = getAdmissionDataFromFormID(formId);
						admissionFormIDMap = getBlobFromAdmission(formId);
						fileNameMap = getFileNames(formId);
						emailService.sendAdmissionDataToAdmin(formId, admissionMap, admissionFormIDMap, fileNameMap);

					}else{
						LOGGER.debug("The upload data is not updated in the database");
					}
				}				
			}
		} catch (SQLException e){
			LOGGER.error("SQLException occurred in updateUploadAdmissionData :"+e.getMessage());
		} catch (IOException e) {
			LOGGER.error("IOException occurred in updateUploadAdmissionData :"+e.getMessage());
		} catch (NullPointerException e){
			LOGGER.error("NullPointerException occurred in updateUploadAdmissionData :"+e.getMessage());
		}
		finally{
			try {
				if(connection!=null){
					connection.close();	
				}
			} catch (SQLException e) {
				LOGGER.error("SQLException occurred in updateUploadAdmissionData method in finally block"
						+ e.getMessage());
			}
		}
	}
	
	/**
	 * This method is used to generate update query for admission table
	 * @param admissionMap
	 * @return query
	 */
	private String generateUpdateQuery(Map<String, Object> admissionMap) {

		StringBuilder query = new StringBuilder("UPDATE ").append(
				ResourceConstants.ADMISSION_TABLE_NAME).append(" SET ");	
		
		for (Iterator<String> iter = admissionMap.keySet().iterator(); iter
				.hasNext();) {
			query.append(iter.next()+"= ?");

			if (iter.hasNext()) {
				query.append(",");
			}
		}
		if(admissionMap.get(ResourceConstants.FORM_ID)!=null){
			String formID = admissionMap.get(ResourceConstants.FORM_ID).toString();			
			query.append(" WHERE "+ResourceConstants.FORM_ID+"='"+formID+"'").append(";");
		}
		LOGGER.info("update query:: "+query.toString());
		return query.toString();
	}
	
	private Map<String, String> getMobileEmailFromFormID(String formID){
		Map<String, String> mobileEmailMap = new HashMap<String, String>();
		Connection connection = null;
		try{
			String phNumberQuery = "SELECT contactNum, email FROM admission WHERE formId = '"
					+ formID + "'";
			connection = getConnection();
			PreparedStatement checkPreparedStmt = connection
					.prepareStatement(phNumberQuery);
			ResultSet result = checkPreparedStmt.executeQuery(phNumberQuery);
			if(result.next()){
				String mobileNumber = result.getString("contactNum");
				String email = result.getString("email");
				mobileEmailMap.put("mobile", mobileNumber);
				mobileEmailMap.put("email", email);
			}
		} catch (SQLException e){
			LOGGER.error("SQLException occurred in getMobileNumberFormID : "+e.getMessage());
		}  catch (NullPointerException e){
			LOGGER.error("NullPointerException occurred in getMobileNumberFormID : "+e.getMessage());
		}finally{
			try {
				if(connection!=null){
					connection.close();	
				}
			} catch (SQLException e) {
				LOGGER.error("SQLException occurred in getMobileNumberFormID method in finally "
						+ e.getMessage());
			}
		}
		
		return mobileEmailMap;
	}
	
	
	private Map<String, String> getAdmissionDataFromFormID(String formID){
		Connection connection = null;
		Map<String,String> admissionFormIDMap = new LinkedHashMap<String, String>();
		try{
			String getDetailsQuery = "SELECT * FROM admission WHERE formId = '"
					+ formID + "'";
			LOGGER.info("checkQuery::::" + getDetailsQuery);
			connection = getConnection();
			PreparedStatement checkPreparedStmt = connection
					.prepareStatement(getDetailsQuery);
			ResultSet result = checkPreparedStmt.executeQuery(getDetailsQuery);
			ResultSetMetaData admissionBasicData = result.getMetaData();
			
			String[] cancelColumns = {ResourceConstants.LAST_MODIFIED, ResourceConstants.PHOTO,
					ResourceConstants.PHOTO_NAME, ResourceConstants.MARKSHEET_TEN, ResourceConstants.MARKSHEET_TEN_FILENAME,
					ResourceConstants.MARKSHEET_TWELVE, ResourceConstants.MARKSHEET_TWELVE_FILENAME, 
					ResourceConstants.MARKSHEET_UG, ResourceConstants.MARKSHEET_UG_FILENAME, ResourceConstants.SUBMIT_STATUS,
					ResourceConstants.PAYMENT_STATUS, ResourceConstants.PAYMENT_MODE, ResourceConstants.FEE_AMOUNT,
					ResourceConstants.FEE_PAYMENT_TYPE, ResourceConstants.TRANSACTION_ID};
			while(result.next()){
				for (int i = 1; i <= admissionBasicData.getColumnCount(); i++) {
					String admissionHeader = admissionBasicData.getColumnName(i);
					Object admissionValue = result.getObject(admissionHeader);
					
					boolean foundColumn = false;				
					for(String cancelColumn : cancelColumns) {
	            		if(cancelColumn.equalsIgnoreCase(admissionHeader.toString())){
	            			foundColumn = true;
	            		}
	            	}
					if(!foundColumn){
						if(admissionValue!=null){
							if(admissionHeader.equalsIgnoreCase(ResourceConstants.DATE_OF_BIRTH)){
								SimpleDateFormat sdf1=new SimpleDateFormat(ResourceConstants.DATE_FORMAT_YYYY_MM_DD);
								SimpleDateFormat sdf2=new SimpleDateFormat(ResourceConstants.DATE_FORMAT_MM_DD_YYYY);
								Date date1=sdf1.parse(admissionValue.toString());
								String datevalue = sdf2.format(date1);
								admissionFormIDMap.put(admissionHeader, datevalue);
							}else{
								admissionFormIDMap.put(admissionHeader, admissionValue.toString());
							}
						}else{							
							admissionFormIDMap.put(admissionHeader, ResourceConstants.EMPTY_STRING);							
						}
					}
				}
			}

		} catch(SQLException e){
			LOGGER.error("SQLException occurred in getAdmissionDataFromFormID :"+e.getMessage());
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
		
		return admissionFormIDMap;
		
	}
	
	private Map<String, InputStream> getBlobFromAdmission(String formID){
		Connection connection = null;
		Map<String, InputStream> imageInpustStreamMap = new HashMap<String, InputStream>();
		try{
			String getDetailsQuery = "SELECT photo,  marksheet_10th, "
					+ " marksheet_12th,  marksheet_ug "
					+ " FROM admission WHERE formId = '"+formID + "'";
			LOGGER.info("getDetailsQuery::::" + getDetailsQuery);
			connection = getConnection();
			PreparedStatement checkPreparedStmt = connection
					.prepareStatement(getDetailsQuery);
			ResultSet result = checkPreparedStmt.executeQuery(getDetailsQuery);
			ResultSetMetaData admissionBasicData = result.getMetaData();
			while(result.next()){
				for (int i = 1; i <= admissionBasicData.getColumnCount(); i++) {
					String admissionHeader = admissionBasicData.getColumnName(i);					
						InputStream binaryStream = result.getBinaryStream(admissionHeader);
						imageInpustStreamMap.put(admissionHeader, binaryStream);										
				}
			}
			
		} catch(Exception e){
			LOGGER.error("Exception occurred in getBlobFromAdmission :"+e.getMessage());
		} finally {
			try {
				if(connection!=null){
					connection.close();	
				}
			} catch (SQLException e) {
				LOGGER.error("SQLException occurred in getBlobFromAdmission method in finally "
						+ e.getMessage());
			}
		}
		return imageInpustStreamMap;
	}
	
	private Map<String, String> getFileNames(String formID){
		Connection connection = null;
		Map<String, String> fileNameMap = new HashMap<String, String>();
		try{
			String getDetailsQuery = "SELECT photo_name,  marksheet_10th_name,"
					+ " marksheet_12th_name,  marksheet_ug_name"
					+ " FROM admission WHERE formId = '"
					+ formID + "'";
			LOGGER.info("getDetailsQuery::::" + getDetailsQuery);
			connection = getConnection();
			PreparedStatement checkPreparedStmt = connection
					.prepareStatement(getDetailsQuery);
			ResultSet result = checkPreparedStmt.executeQuery(getDetailsQuery);
			ResultSetMetaData admissionBasicData = result.getMetaData();
			while(result.next()){
				for (int i = 1; i <= admissionBasicData.getColumnCount(); i++) {
					String admissionHeader = admissionBasicData.getColumnName(i);					
						fileNameMap.put(admissionHeader,result.getObject(admissionHeader).toString());									
				}
			}
			
		} catch(Exception e){
			LOGGER.error("Exception occurred in getFileNames :"+e.getMessage());
		} finally {
			try {
				if(connection!=null){
					connection.close();	
				}
			} catch (SQLException e) {
				LOGGER.error("SQLException occurred in getFileNames method in finally "
						+ e.getMessage());
			}
		}
		return fileNameMap;
	}
	
}
