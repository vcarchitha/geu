package com.geu.aem.cca.core.service.impl;

import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.json.simple.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourcePool;
import com.geu.aem.cca.core.constants.CCAConstants;
import com.geu.aem.cca.core.service.CallCentreLeadsService;


@Component(service= {CallCentreLeadsService.class},immediate=true)

public class CallCentreLeadsServiceImpl implements CallCentreLeadsService{

	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	private static final String CLASS_NAME = CallCentreLeadsServiceImpl.class.getSimpleName();

	@Reference
	private DataSourcePool source;

	/**
	 * This Method is used to fetch lead data from callCentreLeads table
	 * @param firstName
	 * @param lastName
	 * @param contactNum
	 */
	public JSONObject getLeadData(String firstName,String lastName,String contactNum) {

		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		LOGGER.info(CCAConstants.ENTERING + CCAConstants.SPACE + CLASS_NAME
				+ CCAConstants.SPACE+METHOD_NAME);

		Connection connection = null;
		String query = "";
		JSONObject jsonObject=new JSONObject();
		try {

			connection=getConnection();

			ResultSet resultSet = null;
			PreparedStatement preparedStatement = null;

			query="Select * FROM callCentreLeads where firstName='"+firstName+"' AND lastName='"+lastName+"' AND contactNum='"+contactNum+"';";

			preparedStatement = connection.prepareStatement(query); 
			resultSet = preparedStatement.executeQuery();
			ResultSetMetaData ccaLeadData = resultSet.getMetaData();
			
			Map<String,String> ccaLeadsjsonMap=new LinkedHashMap<String, String>();
			while(resultSet.next())
			{
				for (int i = 1; i <= ccaLeadData.getColumnCount(); i++) {
	                String ccaLeadHeader = ccaLeadData.getColumnName(i);
	                Object ccaLeadValue = resultSet.getObject(ccaLeadHeader);
	              
	                if(null!=ccaLeadValue)
					{
						if((ccaLeadHeader.equals(CCAConstants.LEAD_GENERATION_DATE))||(ccaLeadHeader.equals(CCAConstants.DATE_OF_BIRTH))||(ccaLeadHeader.equals(CCAConstants.FOLLOW_UP_DATE))||(ccaLeadHeader.equals(CCAConstants.DATE_OF_ADMISSION)))
						{
							String datestr=ccaLeadValue.toString();
							SimpleDateFormat sdf1=new SimpleDateFormat(CCAConstants.DATE_FORMAT_DD_MM_YYYY);
							SimpleDateFormat sdf2=new SimpleDateFormat(CCAConstants.DATE_FORMAT_YYYY_MM_DD);
							Date date1=sdf2.parse(datestr);
							String datevalue=sdf1.format(date1);
							ccaLeadsjsonMap.put(ccaLeadHeader,datevalue);
						}
						else if(ccaLeadHeader.equals(CCAConstants.AGGREGATE_MARKS_10TH)||ccaLeadHeader.equals(CCAConstants.PROJECTED_MARKS_12TH)||ccaLeadHeader.equals(CCAConstants.MARKS_12TH)||ccaLeadHeader.equals(CCAConstants.CGPA_PERCENTAGE))
						{
							DecimalFormat df = new DecimalFormat("##.##");
							df.setRoundingMode(RoundingMode.DOWN);
							ccaLeadsjsonMap.put(ccaLeadHeader,df.format(ccaLeadValue));
						}
						else
						{
							ccaLeadsjsonMap.put(ccaLeadHeader,ccaLeadValue.toString());
						}
					}
					else
					{
						ccaLeadsjsonMap.put(ccaLeadHeader, null);
					}
	                
	            }
			}
			if(ccaLeadsjsonMap.containsKey(CCAConstants.LAST_MODIFIED))
			{
				ccaLeadsjsonMap.remove(CCAConstants.LAST_MODIFIED);
			}
			if(ccaLeadsjsonMap.containsKey(CCAConstants.USER_ID))
			{
				ccaLeadsjsonMap.remove(CCAConstants.USER_ID);
			}
			jsonObject.putAll(ccaLeadsjsonMap);
			
		}
		catch(Exception e)
		{
			LOGGER.error("Error in"+CLASS_NAME+CCAConstants.SPACE+METHOD_NAME+CCAConstants.SPACE+e.getMessage());
		}
		finally {
			try
			{
				connection.close();             
			}

			catch (SQLException e) {
				LOGGER.error("Connection Error in"+CLASS_NAME+CCAConstants.SPACE+METHOD_NAME+CCAConstants.SPACE+e.getMessage());
			}
		}
		LOGGER.info(CCAConstants.EXITING + CCAConstants.SPACE + CLASS_NAME
				+ CCAConstants.SPACE+METHOD_NAME);

		return jsonObject;
	}
	
	/**
	 * This Method is used to connect to mySQL using JDBC Connection pool
	 */

	private Connection getConnection()
	{
		DataSource datasource=null;
		Connection connection=null;

		try
		{
			datasource=(DataSource)source.getDataSource(CCAConstants.DATASOURCE_NAME);
			connection=datasource.getConnection();
			return connection;
		}
		catch(Exception e)
		{
			LOGGER.error("Exception in Datasource Connection!"+e.getMessage());
		}
		return null;

	}
	
	/**
	 * This Method is used to insert or update lead data into callCentreLeads table
	 * @param ccaLeadsMap
	 * @param sqlAction
	 * @param leadId
	 */

	public int insertLeadData(Map<String, String> ccaLeadsMap,String sqlAction,int leadId) {

		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		LOGGER.info(CCAConstants.ENTERING + CCAConstants.SPACE + CLASS_NAME
				+ CCAConstants.SPACE+METHOD_NAME);
		Connection connection = null;
		String query=null;
		int insertStatus=0;

		try {

			connection=getConnection();

			if(null!=connection)
			{
				LOGGER.info("Connection established with MYSQL");
			}
			else
			{
				LOGGER.error("Failed to make connection!");
			}

			PreparedStatement preparedStatement = null; 
			
			if(ccaLeadsMap.containsKey(CCAConstants.FORMACTION))
			{
				ccaLeadsMap.remove(CCAConstants.FORMACTION);
			}
			ccaLeadsMap.put(CCAConstants.LAST_MODIFIED, "");
			
			if(sqlAction.equals(CCAConstants.FORMACTION_INSERT))
			{
				query=generateInsertQuery(ccaLeadsMap);
			}
			else
			{
				if(ccaLeadsMap.containsKey(CCAConstants.LEAD_ID))
				{
					ccaLeadsMap.remove(CCAConstants.LEAD_ID);
				}
				query=generateUpdateQuery(ccaLeadsMap);
			}
			
			
			preparedStatement = connection.prepareStatement(query);
			
			int i=1;
			
			for(String ccaLead:ccaLeadsMap.keySet())
			{
					if((ccaLead.equals(CCAConstants.LEAD_GENERATION_DATE))||(ccaLead.equals(CCAConstants.DATE_OF_BIRTH))||(ccaLead.equals(CCAConstants.FOLLOW_UP_DATE))||(ccaLead.equals(CCAConstants.DATE_OF_ADMISSION)))
					{
						String datestr=ccaLeadsMap.get(ccaLead);
						if(datestr.isEmpty())
						{
							preparedStatement.setNull(i, java.sql.Types.DATE);
						}
						else
						{
							SimpleDateFormat sdf1=new SimpleDateFormat(CCAConstants.DATE_FORMAT_DD_MM_YYYY);
							SimpleDateFormat sdf2=new SimpleDateFormat(CCAConstants.DATE_FORMAT_YYYY_MM_DD);
							Date date1=sdf1.parse(datestr);
							String datevalue=sdf2.format(date1);
							
							Date date2=sdf2.parse(datevalue);
							java.sql.Date sqlDate = new java.sql.Date(date2.getTime());
							preparedStatement.setDate(i, sqlDate);
						}
						
					}
					else if((ccaLead.equals(CCAConstants.AGE))||(ccaLead.equals(CCAConstants.YEAR_OF_PASSING_10TH))||(ccaLead.equals(CCAConstants.PREBOARD_YEAR))||(ccaLead.equals(CCAConstants.YEAR_OF_PASSING_12TH)) ||(ccaLead.equals(CCAConstants.YEAR_OF_GRADUATION)))
					{
						if(ccaLeadsMap.get(ccaLead).isEmpty())
						{
							preparedStatement.setNull(i, java.sql.Types.INTEGER);
						}
						else{
							int value=Integer.parseInt(ccaLeadsMap.get(ccaLead));
							preparedStatement.setInt(i, value);
						}
						
					}
					else if(ccaLead.equals(CCAConstants.AGGREGATE_MARKS_10TH)||ccaLead.equals(CCAConstants.PROJECTED_MARKS_12TH)||ccaLead.equals(CCAConstants.MARKS_12TH)||ccaLead.equals(CCAConstants.CGPA_PERCENTAGE))
					{
						if(ccaLeadsMap.get(ccaLead).isEmpty())
						{
							preparedStatement.setNull(i, java.sql.Types.FLOAT);
						}
						else
						{
							float f=Float.valueOf(ccaLeadsMap.get(ccaLead));
							preparedStatement.setFloat(i, f);
						}
					}
					else if(ccaLead.equals(CCAConstants.LAST_MODIFIED))
					{
						java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
						preparedStatement.setTimestamp(i, date);
					}
					else
					{
						if(ccaLeadsMap.get(ccaLead).isEmpty())
						{
							preparedStatement.setNull(i, java.sql.Types.VARCHAR);
						}
						else
						{
							preparedStatement.setObject(i, ccaLeadsMap.get(ccaLead).toString());
						}
					}
					
				
				i++;
			}
			
			if(sqlAction.equals(CCAConstants.FORMACTION_UPDATE))
			{
				preparedStatement.setInt(i, leadId);
			}
			
			insertStatus=preparedStatement.executeUpdate();
			
			LOGGER.info(CCAConstants.EXITING + CCAConstants.SPACE + CLASS_NAME
					+ CCAConstants.SPACE+METHOD_NAME);
			return insertStatus;
		}
		catch (Exception e) {
			LOGGER.error("Error in"+CLASS_NAME+CCAConstants.SPACE+METHOD_NAME+CCAConstants.SPACE+e.getMessage());
		}
		finally {
			try
			{
				connection.close();
			}

			catch (SQLException e) {
				LOGGER.error("Connection Error in"+CLASS_NAME+CCAConstants.SPACE+METHOD_NAME+CCAConstants.SPACE+e.getMessage());
			}
		}
		return insertStatus; 
	}
	
	/**
	 * This Method is used to generate insert query for ccaLeads
	 * @param ccaLeadsMap
	 */
	
	public String generateInsertQuery(Map<String, String> ccaLeadsMap) {
		
		StringBuilder query = new StringBuilder("INSERT INTO ").append(CCAConstants.TABLE_NAME).append(" (");
		StringBuilder placeholders = new StringBuilder();
		
		for (Iterator<String> iter = ccaLeadsMap.keySet().iterator(); iter.hasNext();) {
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
	 * This Method is used to generate update query for ccaLeads
	 * @param ccaLeadsMap
	 */
	public String generateUpdateQuery(Map<String, String> ccaLeadsMap) {
			
		StringBuilder query = new StringBuilder("UPDATE ").append("callCentreLeads ").append("SET ");
		
		for (Iterator<String> iter = ccaLeadsMap.keySet().iterator(); iter.hasNext();) {
			query.append(iter.next());
			query.append("= ").append("?");

		    if (iter.hasNext()) {
		    	query.append(",");
		    }
		}
		query.append(" WHERE leadId= ?").append(";");
			return query.toString();
		}

	public JSONObject getFollowUpLeadData(String userId) {
		
		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		LOGGER.info(CCAConstants.ENTERING + CCAConstants.SPACE + CLASS_NAME
				+ CCAConstants.SPACE+METHOD_NAME);
		
		Map<String,String> ccaLeadsjsonMap=new LinkedHashMap<String, String>();
		List<Map<String,String>> followUpList=new ArrayList<Map<String,String>>();
		
		Connection connection = null;
		String query = "";
		JSONObject jsonObject=new JSONObject();
		try {

			connection=getConnection();

			ResultSet resultSet = null;
			PreparedStatement preparedStatement = null;
			
			SimpleDateFormat sdf=new SimpleDateFormat(CCAConstants.DATE_FORMAT_YYYY_MM_DD);
			Date date=new Date();
			String todayDate=sdf.format(date);

			query="SELECT firstName,LastName,contactNum FROM callCentreLeads WHERE follow_up_date='"+todayDate+"' AND user_id='"+userId+"';";
			
			preparedStatement = connection.prepareStatement(query); 
			resultSet = preparedStatement.executeQuery();
			int i=1;
			
			while(resultSet.next())
			{
				ccaLeadsjsonMap=new LinkedHashMap<String, String>();
				ccaLeadsjsonMap.put("ccaLeadId", Integer.toString(i));
				ccaLeadsjsonMap.put("firstName",resultSet.getString(1));
				ccaLeadsjsonMap.put("lastName",resultSet.getString(2));
				ccaLeadsjsonMap.put("contactNum",resultSet.getString(3));
				i++;
				
				followUpList.add(ccaLeadsjsonMap);
				ccaLeadsjsonMap=null;
			}
			//Sort Follow-Up List by firstName and if firstName are same then sort by lastName
			
			 Collections.sort(followUpList, new Comparator<Map<String, String>>() {
			        public int compare(final Map<String, String> object1, final Map<String, String> object2) {
			        	String firstName1 = object1.get("firstName");
			            String firstName2 = object2.get("firstName");

			            int result = firstName1.compareToIgnoreCase(firstName2);
			            if (result != 0) {
			               return result;
			            } else {
			               String lastName1 = object1.get("lastName");
			               String lastName2 = object2.get("lastName");
			               return lastName1.compareToIgnoreCase(lastName2);
			            }
			        }
			    });
			 
			 //Re-arrange ccaLeadId based on the sorted list
			 for(int j=0;j<followUpList.size();j++)
			 {
				 ccaLeadsjsonMap=new LinkedHashMap<String, String>();
				 ccaLeadsjsonMap=followUpList.get(j);
				 ccaLeadsjsonMap.put("ccaLeadId",Integer.toString(j+1));
				 ccaLeadsjsonMap=null;
			 }
			
			jsonObject.put("followUpJson", followUpList);
		}
		catch(Exception e)
		{
			LOGGER.error("Error in"+CLASS_NAME+CCAConstants.SPACE+METHOD_NAME+CCAConstants.SPACE+e.getMessage());
		}
		finally {
			try
			{
				connection.close();             
			}

			catch (SQLException e) {
				LOGGER.error("Connection Error in"+CLASS_NAME+CCAConstants.SPACE+METHOD_NAME+CCAConstants.SPACE+e.getMessage());
			}
		}
		LOGGER.info(CCAConstants.EXITING + CCAConstants.SPACE + CLASS_NAME
				+ CCAConstants.SPACE+METHOD_NAME);
		
		return jsonObject;
	}
}

