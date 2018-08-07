package com.geu.aem.web.core.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourceNotFoundException;
import com.day.commons.datasource.poolservice.DataSourcePool;
import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.service.GEHUBasicAdmissionFormService;

@Component(service = GEHUBasicAdmissionFormService.class, immediate = true)
public class GEHUBasicAdmissionFormServiceImpl implements GEHUBasicAdmissionFormService {

	private final Logger LOGGER = LoggerFactory
			.getLogger(GEHUBasicAdmissionFormServiceImpl.class);

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

	public void updateBasicAdmissionData(Map<String, Object> basicAdmissionMap,
			SlingHttpServletRequest request, Map<String, InputStream> uploadBasicFileStreamMap, 
			Map<String, String> uploadBasicFileNameMap) {
		Connection connection = null;
		try {
			String formID = request.getParameter(ResourceConstants.FORM_ID);
			connection = getConnection();		
			String timeStamp = new SimpleDateFormat(ResourceConstants.FORMAT_DDMMYY).format(Calendar.getInstance().getTime());
			basicAdmissionMap.put(ResourceConstants.LAST_MODIFIED, timeStamp);			
			String[] columnName = {ResourceConstants.REGISTRATION_MARKSHEET_NAME};
			List<String> columnNamesToBeAdded = new ArrayList<String>();
			
			for (String fileColumn : columnName) {				
				for(String data: basicAdmissionMap.keySet()){
					if(fileColumn.equalsIgnoreCase(data+"_name")){
						columnNamesToBeAdded.add(fileColumn);
					}
				}
			}
			
			for(String name:columnNamesToBeAdded){
				basicAdmissionMap.put(name, null);
			}
			
			/*for(String name: columnName){
				basicAdmissionMap.put(name, null);
			}
			*/
			String updateBasicQuery = generateUpdateQuery(basicAdmissionMap,
					formID);
			if (StringUtils.isNotBlank(updateBasicQuery)) {
				PreparedStatement admissionFormPreparedStmt = connection
						.prepareStatement(updateBasicQuery);
				int columnIndex=1;
				for(String basicData:basicAdmissionMap.keySet()){
					boolean foundIntColumn = false;
					boolean foundFloatColumn = false;
					String[] intColumns = {ResourceConstants.DURATION, ResourceConstants.YEAR_OF_PASSING_TEN,
							ResourceConstants.YEAR_OF_PASSING_TWELVE, ResourceConstants.PINCODE_TWELVE,
							ResourceConstants.YEAR_OF_GRADUATION, ResourceConstants.PINCODE_GRADUATION};
					for(String eachIntColumn : intColumns){
						if(eachIntColumn.equalsIgnoreCase(basicData)){
							foundIntColumn = true;
	            		}
					}
					String[] floatColumns = {ResourceConstants.FEES, ResourceConstants.DISCOUNT,
							ResourceConstants.AGGREGATE_MARKS_TEN, ResourceConstants.PROJECTED_MARKS_TWELVE,
							ResourceConstants.CGPA_PERCENTAGE};
					for(String eachFloatColumn : floatColumns){
						if(eachFloatColumn.equalsIgnoreCase(basicData)){
							foundFloatColumn = true;
	            		}
					}
					if(StringUtils.isNotBlank(basicData)){
						if(basicData.equals(ResourceConstants.DATE_OF_BIRTH)){
							String datestr=basicAdmissionMap.get(basicData).toString();
							if(datestr.isEmpty()){
								admissionFormPreparedStmt.setNull(columnIndex, java.sql.Types.DATE);
							}else{
								SimpleDateFormat sdf1=new SimpleDateFormat(ResourceConstants.DATE_FORMAT_MM_DD_YYYY);
								SimpleDateFormat sdf2=new SimpleDateFormat(ResourceConstants.DATE_FORMAT_YYYY_MM_DD);
								Date date1=sdf1.parse(datestr);
								String datevalue=sdf2.format(date1);
								
								Date date2=sdf2.parse(datevalue);
								java.sql.Date sqlDate = new java.sql.Date(date2.getTime());
								admissionFormPreparedStmt.setDate(columnIndex, sqlDate);
							}
						}else if(foundIntColumn){
							if(basicAdmissionMap.get(basicData).toString().isEmpty()){
								admissionFormPreparedStmt.setNull(columnIndex, java.sql.Types.INTEGER);
							}else{
								int value=Integer.parseInt(basicAdmissionMap.get(basicData).toString());
								admissionFormPreparedStmt.setInt(columnIndex, value);
							}
						}else if(basicData.equals(ResourceConstants.LAST_MODIFIED)){

							java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
							admissionFormPreparedStmt.setTimestamp(columnIndex, date);
						}else if(foundFloatColumn){
	
							if(basicAdmissionMap.get(basicData).toString().isEmpty()){
								admissionFormPreparedStmt.setNull(columnIndex, java.sql.Types.FLOAT);
							}else{
								float f=Float.valueOf(basicAdmissionMap.get(basicData).toString());
								admissionFormPreparedStmt.setFloat(columnIndex, f);
							}
						}else if(basicData.equals(ResourceConstants.REGISTRATION_MARKSHEET)){
							
							if(basicAdmissionMap.get(basicData).toString().isEmpty()){
								admissionFormPreparedStmt.setNull(columnIndex, java.sql.Types.BLOB);
							}else{
								for(String basicFileData:uploadBasicFileNameMap.keySet()){
									if(basicData.equalsIgnoreCase(basicFileData)){
										InputStream is = uploadBasicFileStreamMap.get(basicFileData);
										byte[] bytes = IOUtils.toByteArray(is);
										admissionFormPreparedStmt.setBytes(columnIndex, bytes);	
									}
								}
							}
						}else{
	
							if(basicAdmissionMap.get(basicData)==null){
								if(basicData.equals(ResourceConstants.REGISTRATION_MARKSHEET_NAME)){
									for(String basicFileData:uploadBasicFileNameMap.keySet()){
										if(basicData.equalsIgnoreCase(basicFileData+"_name")){
											String fileName = uploadBasicFileNameMap.get(basicFileData);
											admissionFormPreparedStmt.setObject(columnIndex, fileName);
										}
									}
								}else{
									admissionFormPreparedStmt.setNull(columnIndex, java.sql.Types.VARCHAR);
								}
							}else{
								admissionFormPreparedStmt.setObject(columnIndex, basicAdmissionMap.get(basicData).toString());
							}
						}
					}
					columnIndex++;
				}
				if(StringUtils.isNotBlank(formID)){
					int updateStatus = admissionFormPreparedStmt.executeUpdate();
					if(updateStatus!=1){
						LOGGER.error("Table is not properly updated!!");
					}
				}
			}

		} catch (SQLException e) {
			LOGGER.error("Exception occurred in updateBasicAdmissionData :"
					+ e.getMessage());
		} catch (ParseException e) {
			LOGGER.error("ParseException occurred in updateBasicAdmissionData :"
					+ e.getMessage());
		} catch (IOException e) {
			LOGGER.error("IOException occurred in updateBasicAdmissionData :"
					+ e.getMessage());
		} finally{
			try {
				if(connection!=null){
					connection.close();
				}
			} catch (SQLException e) {
				LOGGER.error("SQLException occurred in updateBasicAdmissionData method "
						+ e.getMessage());
			}
		}
	}

	/**
	 * This method is used to generate update query for admission table
	 * @param admissionMap
	 * @return query
	 */
	private String generateUpdateQuery(Map<String, Object> admissionMap,
			String formID) {

		StringBuilder query = new StringBuilder("UPDATE ").append(
				ResourceConstants.GEHU_ADMISSION_TABLE_NAME).append(" SET ");	
		
		for (Iterator<String> iter = admissionMap.keySet().iterator(); iter
				.hasNext();) {
			query.append(iter.next()+"= ?");

			if (iter.hasNext()) {
				query.append(",");
			}
		}
		if(StringUtils.isNotBlank(formID)){
			query.append(" WHERE "+ResourceConstants.FORM_ID+"='"+formID+"'").append(";");
		}
		LOGGER.info("update query:: "+query.toString());
		return query.toString();
	}

}
