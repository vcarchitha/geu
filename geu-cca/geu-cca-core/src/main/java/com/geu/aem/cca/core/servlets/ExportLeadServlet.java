package com.geu.aem.cca.core.servlets;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourceNotFoundException;
import com.day.commons.datasource.poolservice.DataSourcePool;
import com.day.cq.dam.api.AssetManager;
import com.geu.aem.cca.beans.LeadBean;
import com.geu.aem.cca.constants.ResourceConstants;
import com.geu.aem.cca.core.constants.CCAConstants;
import com.geu.aem.cca.core.service.ExportLeadsService;

@Component(service = Servlet.class, property = {
		"service.description=" + " Export Lead Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST,
		"sling.servlet.paths=" + "/bin/exportLead" })
public class ExportLeadServlet extends SlingAllMethodsServlet {

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	/**
	 * The servlet updates the acs_nurtured_leads table with the output csv file
	 * in the SFTP folder and creates an excel file and downloads it to client
	 * side.
	 */
	private static final long serialVersionUID = 1L;

	private final Logger LOGGER = LoggerFactory
			.getLogger(ExportLeadServlet.class);

	@Reference
	private DataSourcePool source;

	@Reference
	ExportLeadsService exportLeadsService;

	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		Connection connection = null;
		try {
			connection = getConnection();
			// updateTableUsingCSV(exportPath, connection);
			ArrayList<LeadBean> list = fetchDataForExcel(connection);
			String message="";
			if (list.size() < 1) {
				message=ResourceConstants.EXPORT_ERROR_MESSAGE;
			} else
			{
				message = ResourceConstants.DAM_EXCEL_EXPORTLEAD_LOCATION
						+ "/" + ResourceConstants.EXCEL_FILENAME;
			}
			response.getWriter().write(message);

		} catch (NullPointerException e) {
			LOGGER.error("NullPointerException occurred in doPost method "
					+ e.getMessage());
		} catch (IOException e) {
			LOGGER.error("IOException occurred in doPost method "
					+ e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Exception occurred in doPost method "
					+ e.getMessage());
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				LOGGER.error("SQLException occurred in doPost method :"
						+ e.getMessage());
			}
		}

	}

	/**
	 * This methods fetches data from acs_nurtured_leads and creates the excel
	 * file list for downloading
	 * 
	 * @param response
	 * @param connection
	 * @throws SQLException
	 * @throws IOException
	 * @throws LoginException
	 */
	private ArrayList<LeadBean> fetchDataForExcel(Connection connection) {
		ResultSet exportResultSet;
		ArrayList<LeadBean> list = new ArrayList<LeadBean>();
		DateFormat dateFormat = new SimpleDateFormat(
				ResourceConstants.DATE_FORMAT);
		String time = dateFormat.format(today());
		String exportQuery = "select * from acs_nurtured_leads WHERE lastModified >= '"
				+ time + "'";
		LOGGER.info("Export Query :::" + exportQuery);
		PreparedStatement preparedStatement;
		try {
			preparedStatement = connection.prepareStatement(exportQuery);

			exportResultSet = preparedStatement.executeQuery(exportQuery);
			list = exportLeadsService.exportLeads(exportResultSet);
			createExcelInDAM();
		} catch (SQLException e) {
			LOGGER.error("SQLException occurred in doPost method "
					+ e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Exception occurred in doPost method "
					+ e.getMessage());
		}
		return list;
	}

	private Date today() {
		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}

	/**
	 * Returns connection using the configured DataSourcePool
	 * 
	 * @return
	 */
	private Connection getConnection() {
		Connection connection = null;
		try {
			DataSource dataSource = (DataSource) source
					.getDataSource(CCAConstants.DATASOURCE_NAME);
			connection = dataSource.getConnection();

		} catch (SQLException e) {
			LOGGER.error("SQLException occurred in getConnection method  "
					+ e.getMessage());
		} catch (DataSourceNotFoundException e) {
			LOGGER.error("DataSourceNotFoundException occurred in getConnection method  "
					+ e.getMessage());
		}
		return connection;
	}

	/**
	 * This methods creates excel file in DAM.
	 */
	private void createExcelInDAM() {
		String outputFilePath = ResourceConstants.EXCEL_FILEPATH + "/"
				+ ResourceConstants.EXCEL_FILENAME;
		Map<String, Object> sessionParam = new HashMap<String, Object>();
		sessionParam.put(ResourceResolverFactory.SUBSERVICE, "getMySQLService");
		if (resourceResolverFactory != null) {
			ResourceResolver resResolver;
			try {
				resResolver = resourceResolverFactory
						.getServiceResourceResolver(sessionParam);

				AssetManager assetMgr = resResolver.adaptTo(AssetManager.class);
				InputStream is;

				is = new FileInputStream(outputFilePath);

				String exportPath = ResourceConstants.DAM_EXCEL_EXPORTLEAD_LOCATION
						+ "/" + ResourceConstants.EXCEL_FILENAME;
				assetMgr.createAsset(exportPath, is,
						ResourceConstants.DAM_EXCEL_FILE_FORMAT, true);
			} catch (FileNotFoundException e) {
				LOGGER.error("File Not Found in doPost method "
						+ e.getMessage());
			} catch (LoginException e) {
				LOGGER.error("LoginException occurred in doPost method "
						+ e.getMessage());

			}

		}

	}

}
