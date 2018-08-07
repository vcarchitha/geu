package com.geu.aem.web.core.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourcePool;
import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.service.EnquiryFormService;

@Component(service= {EnquiryFormService.class},immediate=true)
public class EnquiryFormServiceImpl implements EnquiryFormService {
	
	protected final Logger LOGGER = LoggerFactory.getLogger(EnquiryFormService.class);
	
	@Reference
	private DataSourcePool source;

	@Override
	public boolean insertData(Map<String, String> enquiryFormMap, String requestPath) {
		Connection connection = null;
		boolean insertStatus = true;
		PreparedStatement preparedStatement = null;
		try {
			connection = getConnection();

			if(null!=connection)
			{
				LOGGER.info("Connection established with MYSQL for enquiryform table");
				String query = generateInsertQuery(enquiryFormMap, requestPath);
				try {
					preparedStatement = connection.prepareStatement(query);
					int i = 1;
					for(String enquiryForm : enquiryFormMap.keySet())
					{
						if(enquiryFormMap.get(enquiryForm).isEmpty())
						{
							preparedStatement.setNull(i, java.sql.Types.VARCHAR);
						}
						else
						{
							preparedStatement.setObject(i, enquiryFormMap.get(enquiryForm).toString());
						}
						i++;
					}
					insertStatus = preparedStatement.execute();
				} catch (SQLException e) {
					LOGGER.error("SQLException in insert data of enquiryform table" + e.getMessage());
				}
			}
			else
			{
				LOGGER.error("Failed to make connection with MYSQL for enquiryform table!");
			}
			return insertStatus;
		}
		catch (Exception e) {
			LOGGER.error("Exception in Insert data of enquiryform table" + e.getMessage());
		}
		finally {
			try
			{
				if (connection != null) {
					connection.close();
				}
			}

			catch (SQLException e) {
				LOGGER.error("Connection Error in insert data of enquiryform table" + e.getMessage());
			}
		}
		return insertStatus;
		
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
			datasource=(DataSource)source.getDataSource(ResourceConstants.DATASOURCE_NAME);
			connection=datasource.getConnection();
			return connection;
		}
		catch(Exception e)
		{
			LOGGER.error("Exception in Datasource Connection of enquiryform table" + e.getMessage());
		}
		return null;

	}
	
	/**
	 * This Method is used to generate insert query for enquiryform
	 * @param enquiryFormMap
	 * @param requestPath 
	 */
	
	public String generateInsertQuery(Map<String, String> enquiryFormMap, String requestPath) {
		StringBuilder query = null;
		if (requestPath.equalsIgnoreCase(ResourceConstants.ENQUIRYFORM_PATH)) {
			query = new StringBuilder("INSERT INTO ").append(ResourceConstants.ENQUIRYFORM_TABLENAME).append(" (");
		} else {
			query = new StringBuilder("INSERT INTO ").append(ResourceConstants.ENQUIRYFORM_GEHU_TABLENAME).append(" (");
		}
		
		StringBuilder placeholders = new StringBuilder();
		
		for (Iterator<String> iter = enquiryFormMap.keySet().iterator(); iter.hasNext();) {
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

}
