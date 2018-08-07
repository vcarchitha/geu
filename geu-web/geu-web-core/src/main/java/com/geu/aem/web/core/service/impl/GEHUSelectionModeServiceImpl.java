package com.geu.aem.web.core.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourceNotFoundException;
import com.day.commons.datasource.poolservice.DataSourcePool;
import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.service.GEHUSelectionModeService;

@Component(service = GEHUSelectionModeService.class, immediate = true)
public class GEHUSelectionModeServiceImpl implements GEHUSelectionModeService{

	private final Logger LOGGER = LoggerFactory
			.getLogger(GEHUSelectionModeServiceImpl.class);
	
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
	
	public void updateGEHUSelectionMode(String selectionMode, String formId){
		Connection connection = null;
		try{
			connection = getConnection();
			String updateSelectionQuery = "UPDATE gehu_admission SET selectionMode = '"+selectionMode+"'"
					+ "WHERE formId = '"+formId+"'";
			
			PreparedStatement checkPreparedStmt = connection
					.prepareStatement(updateSelectionQuery);
			int updateStatus = checkPreparedStmt.executeUpdate();
			LOGGER.info("update Status::: "+updateStatus);
		}catch(SQLException e){
			LOGGER.error("SQLException occurred in updateGEHUSelectionMode method :"+e.getMessage());
		}catch(Exception e){
			LOGGER.error("Exception occurred in updateGEHUSelectionMode method :"+e.getMessage());
		}finally{
			try {
				if(connection!=null){
					connection.close();
				}
			} catch (SQLException e) {
				LOGGER.error("SQLException occurred in updateGEHUSelectionMode method "
						+ e.getMessage());
			}
		}
	}

}
