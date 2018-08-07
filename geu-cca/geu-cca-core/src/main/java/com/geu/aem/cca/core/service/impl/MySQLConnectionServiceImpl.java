package com.geu.aem.cca.core.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourceNotFoundException;
import com.day.commons.datasource.poolservice.DataSourcePool;
import com.day.cq.dam.api.AssetManager;
import com.geu.aem.cca.beans.LeadBean;
import com.geu.aem.cca.beans.LeadFields;
import com.geu.aem.cca.beans.LeadUniqueKeyBean;
import com.geu.aem.cca.constants.ResourceConstants;
import com.geu.aem.cca.core.constants.CCAConstants;
import com.geu.aem.cca.core.service.MySQLConnectionService;
import com.geu.aem.cca.util.CCAMapUtil;
import com.geu.aem.cca.util.DeleteTemporaryTableUtil;
import com.geu.aem.cca.util.UpdateMySQLTableUtil;

@Component(service = MySQLConnectionService.class,immediate = true)
@Designate(ocd = MySQLDatabaseConfiguration.class)
public class MySQLConnectionServiceImpl implements MySQLConnectionService{
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@Reference
	private DataSourcePool source;
	
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	
	private MySQLDatabaseConfiguration dataBaseConfiguration;
	
	@Activate
	@Modified
	public void activate(MySQLDatabaseConfiguration dataBaseConfiguration){
		this.dataBaseConfiguration = dataBaseConfiguration;		
	}
	
	/**
	 * Returns connection using the configured DataSourcePool
	 * @return
	 */
	private Connection getConnection(){
		
		DataSource dataSource = null;
		Connection connection = null;
		try{
			dataSource = (DataSource) source.getDataSource(CCAConstants.DATASOURCE_NAME);
			connection = dataSource.getConnection();
			LOGGER.info("connection:: "+connection);
			return connection;
			
		}catch(SQLException e){
			LOGGER.error("SQLException occurred in getConnection method of "
					+ "MySQLConnectionServiceImpl class "+e.getMessage());
		} catch (DataSourceNotFoundException e) {
			LOGGER.error("DataSourceNotFoundException occurred in getConnection method of "
					+ "MySQLConnectionServiceImpl class "+e.getMessage());
		} 
		return null;
	}
	
	/**
	 * Returns customer data from mySQL table
	 * @return
	 */
	public Map<LeadUniqueKeyBean, LeadBean> readFile() {
		// TODO Auto-generated method stub
		LOGGER.info("Inside readFile method!!!!");
		Map<LeadUniqueKeyBean, LeadBean> combinedMap = new HashMap<LeadUniqueKeyBean, LeadBean>();	
		Connection connection = null;
		
		try{
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = dateFormat.format(yesterday());			
			connection = getConnection();			
			CallableStatement cStmt = connection.prepareCall("{call GetDetailToCreateFlatFileGeneration}");
			cStmt.execute();
			ResultSet resultSet = cStmt.getResultSet();
			combinedMap = CCAMapUtil.createLeadMap(resultSet);			
		
		} catch (Exception e) {
			LOGGER.info("SQLException occurred in readFile method of "
					+ "MySQLConnectionServiceImpl "+e.getMessage());
		} finally {
			try{
				connection.close();
			} catch(SQLException e){
				LOGGER.error("SQLException occurred in finally of readFile :"+e.getMessage());
			}
		}
		return combinedMap;
	}
	
	private Date yesterday() {
	    final Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.DATE, -1);
	    cal.set(Calendar.HOUR_OF_DAY, 6);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    return cal.getTime();
	}
	
	/**
	 * Writes customer data to csv file
	 * @return
	 */
	public void writeCSV(){
		
		try{
			Map<String, Object> param = new HashMap<String, Object>();
	        param.put(ResourceResolverFactory.SUBSERVICE, "getServiceForMySQL"); 
	        if(resourceResolverFactory!=null){
	        	ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);
				AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);   
	        	Map<LeadUniqueKeyBean, LeadBean> combinedMap = readFile();
	        	LOGGER.info("combinedMap size::"+combinedMap.size());
				File csvFile = new File(ResourceConstants.SERVER_LOCATION+"/"
						+ResourceConstants.SFTP_FILE_NAME+ResourceConstants.CSV_FILEFORMAT);
				LOGGER.info("CSV file path :::"+csvFile.getPath());
				PrintWriter csvWriter = new PrintWriter(csvFile);				
				String header ="";
				for(LeadFields field: LeadFields.values()){
					header = header+field+"|";
				} 
				
				String headerToUpdate = header.substring(0, header.length() - 1);
				csvWriter.println(headerToUpdate);
				
				for (Map.Entry<LeadUniqueKeyBean, LeadBean> entry : combinedMap.entrySet()) {
					String eachRow = null;
					LeadUniqueKeyBean key = entry.getKey();
					LeadBean value = entry.getValue();						
					
					if(StringUtils.isNotBlank(value.getAdmissionId())){
						eachRow = value.getAdmissionId()+"|";
					}else{
						eachRow = "|";
					}
					if(StringUtils.isNotBlank(value.getFirstName())){
						eachRow = eachRow + value.getFirstName()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getLastName())){
						eachRow = eachRow + value.getLastName()+"|";
					}else{
						eachRow = eachRow+"|";
					}				
					if(StringUtils.isNotBlank(value.getContactNum())){
						eachRow = eachRow + value.getContactNum()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getEmail())){
						eachRow = eachRow + value.getEmail()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getInterest())){
						eachRow = eachRow + value.getInterest()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getLevel())){
						eachRow = eachRow + value.getLevel()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getCampusPreference1())){
						eachRow = eachRow + value.getCampusPreference1()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getCoursePreference1())){
						eachRow = eachRow + value.getCoursePreference1()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getCampusPreference2())){
						eachRow = eachRow + value.getCampusPreference2()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getCoursePreference2())){
						eachRow = eachRow + value.getCoursePreference2()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getGender())){
						eachRow = eachRow + value.getGender()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getCategory())){
						eachRow = eachRow + value.getCategory()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getCorrespondenceAddress())){
						eachRow = eachRow + value.getCorrespondenceAddress()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getPermanentAddress())){
						eachRow = eachRow + value.getPermanentAddress()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getCity())){
						eachRow = eachRow + value.getCity()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getState())){
						eachRow = eachRow + value.getState()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getCountry())){
						eachRow = eachRow + value.getCountry()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getProvisions())){
						eachRow = eachRow + value.getProvisions()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getFatherEmail())){
						eachRow = eachRow + value.getFatherEmail()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getMotherName())){
						eachRow = eachRow + value.getMotherName()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getMotherContact())){
						eachRow = eachRow + value.getMotherContact()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getMotherEmail())){
						eachRow = eachRow + value.getMotherEmail()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getSchoolName10th())){
						eachRow = eachRow + value.getSchoolName10th()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getAggregateMarks10th())){
						eachRow = eachRow + value.getAggregateMarks10th()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getBoard10th())){
						eachRow = eachRow + value.getBoard10th()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getYearOfPassing10th())){
						eachRow = eachRow + value.getYearOfPassing10th()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getIntermediateSenior())){
						eachRow = eachRow + value.getIntermediateSenior()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getProjectedMarks12th())){
						eachRow = eachRow + value.getProjectedMarks12th()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getPreboardYear())){
						eachRow = eachRow + value.getPreboardYear()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getMarks12th())){
						eachRow = eachRow + value.getMarks12th()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getBoard12th())){
						eachRow = eachRow + value.getBoard12th()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getYearOfPassing12th())){
						eachRow = eachRow + value.getYearOfPassing12th()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getHigherEducationInstitute())){
						eachRow = eachRow + value.getHigherEducationInstitute()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getCgpaPercentage())){
						eachRow = eachRow + value.getCgpaPercentage()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getYearOfGraduation())){
						eachRow = eachRow + value.getYearOfGraduation()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getDateOfBirth())){
						eachRow = eachRow + value.getDateOfBirth()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getLeadGenerationDate())){
						eachRow = eachRow + value.getLeadGenerationDate()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getAge())){
						eachRow = eachRow + value.getAge()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getPriorAssociation())){
						eachRow = eachRow + value.getPriorAssociation()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getDateOfAdmission())){
						eachRow = eachRow + value.getDateOfAdmission()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getPaymentMode())){
						eachRow = eachRow + value.getPaymentMode()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getLeadStatus())){
						eachRow = eachRow + value.getLeadStatus()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getLastComment())){
						eachRow = eachRow + value.getLastComment()+"|";
					}else{
						eachRow = eachRow+"|";
					}
					if(StringUtils.isNotBlank(value.getFollowUpDate())){
						eachRow = eachRow + value.getFollowUpDate();
					}else{
						eachRow = eachRow+"";
					}
					csvWriter.println(eachRow);			
				}
				csvWriter.close();
				String timeStamp = new SimpleDateFormat("ddMMYYYY_HHmmss").format(Calendar.getInstance().getTime());
				InputStream is = new FileInputStream(csvFile);	
				if(assetManager!=null){
					assetManager.createAsset(ResourceConstants.DAM_SFTP_FILE_LOCATION+"/"
							+ResourceConstants.SFTP_FILE_NAME+"_"+timeStamp
							+ResourceConstants.CSV_FILEFORMAT, is, "text/csv", true);
				}else{
					LOGGER.info("AssetManager is null "+assetManager);
				}
	        }
			
		} catch(Exception e){
			LOGGER.error("Exception occurred "+e.getMessage());
		}		
	}
	
	public void updateTableFromCSV(String exportPath){
		Connection connection = null;
		try{
			connection = getConnection();
			LOGGER.info("Inside updateTableFromCSV method!!! ");
			CallableStatement cStmt = connection.prepareCall("{call InsertOrUpdateProcessedLeadData}");
			cStmt.execute();
			UpdateMySQLTableUtil.updateTableUsingCSV(exportPath, connection);
			
			DeleteTemporaryTableUtil.deleteCSVLeadsTable(connection);
		}catch (Exception e){
			LOGGER.error("Exception occurred in updateTableFromCSV : "+e.getMessage());
		} finally{
			try{
				connection.close();
			}catch(SQLException e){
				LOGGER.error("SQLException occurred in updateTableFromCSV :"+e.getMessage());
			}
		}
	}

}
