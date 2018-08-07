package com.geu.aem.web.core.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.Servlet;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.simple.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourceNotFoundException;
import com.day.commons.datasource.poolservice.DataSourcePool;
import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.service.AWSEmailService;
import com.geu.aem.web.util.GEUWebUtils;

/**
 * @author aimen.sania
 * 
 */
@Component(service = Servlet.class, property = {
		"service.description=" + "TataTechServlet",
		"sling.servlet.methods=" + "POST",
		"sling.servlet.paths=" + "/bin/geu/TataTechServlet" })
public class TataTechServlet extends SlingAllMethodsServlet {

	@Reference
	private DataSourcePool source;

	@Reference
	ResourceResolverFactory resolverFactory;

	@Reference
	AWSEmailService awsEmailService;

	private static final long serialVersionUID = 1L;
	final static Logger LOGGER = LoggerFactory.getLogger(TataTechServlet.class);
	private static final String CLASS_NAME = TataTechServlet.class
			.getSimpleName();

	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {

		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		LOGGER.info(ResourceConstants.ENTERING + ResourceConstants.SPACE
				+ CLASS_NAME + ResourceConstants.SPACE + METHOD_NAME);
		final String email = request.getParameter(ResourceConstants.EMAIL);
		final RequestParameterMap requestmap = request.getRequestParameterMap();

		Map<String, String> tataTechMap = new LinkedHashMap<String, String>();

		if (null != requestmap) {
			for (String tataTech : requestmap.keySet()) {
				Object value = requestmap.get(tataTech);
				if (null == value) {
					tataTechMap.put(tataTech, ResourceConstants.EMPTY_STRING);
				} else {
					tataTechMap.put(tataTech, requestmap.getValue(tataTech)
							.toString());
				}
			}
		}

		int flag = insertData(tataTechMap);
		if (flag != 0) {

			LOGGER.info("Inserted Successfully in "
					+ ResourceConstants.TABLE_NAME);
			try {
				response.getWriter().write("success");
				ExecutorService executor = Executors.newFixedThreadPool(10);

				executor.execute(new Runnable() {
					public void run() {
						sendMailToAdmin(requestmap, email);
					}
				});

			} catch (IOException e) {
				LOGGER.error("Error while inserting in"
						+ ResourceConstants.TABLE_NAME + e.getMessage());
			}
		} else {
			LOGGER.error("Error while inserting in"
					+ ResourceConstants.TABLE_NAME);
		}

		LOGGER.info(ResourceConstants.EXITING + ResourceConstants.SPACE
				+ CLASS_NAME + ResourceConstants.SPACE + METHOD_NAME);
	}

	private int insertData(Map<String, String> tataTechMap) {

		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		LOGGER.info(ResourceConstants.ENTERING + ResourceConstants.SPACE
				+ CLASS_NAME + ResourceConstants.SPACE + METHOD_NAME);

		Connection connection = null;
		String query = null;
		int flag = 0;

		try {

			connection = getConnection();
			if (null != connection) {
				LOGGER.info("Connection established with MYSQL");
				PreparedStatement preparedStatement = null;
				query = generateInsertQuery(tataTechMap);

				preparedStatement = connection.prepareStatement(query);

				int i = 1;
				for (String tataTech : tataTechMap.keySet()) {
					if (tataTechMap.get(tataTech).isEmpty()) {
						preparedStatement.setNull(i, java.sql.Types.VARCHAR);
					} else {
						preparedStatement.setObject(i, tataTechMap
								.get(tataTech).toString());
					}
					i++;
				}
				flag = preparedStatement.executeUpdate();
			} else {
				LOGGER.error("Failed to make SQL connection!");
			}
		} catch (SQLException e) {
			LOGGER.error("SQL Exception in " + CLASS_NAME
					+ ResourceConstants.SPACE + METHOD_NAME
					+ ResourceConstants.SPACE + e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Error in" + CLASS_NAME + ResourceConstants.SPACE
					+ METHOD_NAME + ResourceConstants.SPACE + e.getMessage());
		} finally {
			try {
				connection.close();
			}

			catch (SQLException e) {
				LOGGER.error("Connection Error in" + CLASS_NAME
						+ ResourceConstants.SPACE + METHOD_NAME
						+ ResourceConstants.SPACE + e.getMessage());
			}
		}

		LOGGER.info(ResourceConstants.EXITING + ResourceConstants.SPACE
				+ CLASS_NAME + ResourceConstants.SPACE + METHOD_NAME);
		return flag;

	}

	private Connection getConnection() {
		DataSource datasource = null;
		Connection connection = null;

		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		try {
			datasource = (DataSource) source
					.getDataSource(ResourceConstants.DATASOURCE_NAME);

			connection = datasource.getConnection();
			return connection;
		} catch (DataSourceNotFoundException e) {
			LOGGER.error("Data Source Not Found in " + CLASS_NAME
					+ ResourceConstants.SPACE + METHOD_NAME
					+ ResourceConstants.SPACE + e.getMessage());
		} catch (SQLException e) {
			LOGGER.error("SQL Exception in " + CLASS_NAME
					+ ResourceConstants.SPACE + METHOD_NAME
					+ ResourceConstants.SPACE + e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Exception in Datasource Connection!" + CLASS_NAME
					+ ResourceConstants.SPACE + METHOD_NAME
					+ ResourceConstants.SPACE + e.getMessage());
		}
		return null;

	}

	public String generateInsertQuery(Map<String, String> ccaLeadsMap) {

		StringBuilder query = new StringBuilder("INSERT INTO ").append(
				ResourceConstants.TABLE_NAME).append(" (");
		StringBuilder placeholders = new StringBuilder();

		for (Iterator<String> iter = ccaLeadsMap.keySet().iterator(); iter
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

	private void sendMailToAdmin(RequestParameterMap requestmap, String email) {
		try {

			String jsonPath = ResourceConstants.EMAIL_CONFIG
					+ ResourceConstants.TATATEACH_QUERY_JSON;
			JSONObject emailObject = GEUWebUtils.getJSONData(resolverFactory,
					jsonPath);
			String body = null;
			String toAddress = null;
			String subject = null;
			if (emailObject.containsKey("body")) {
				body = emailObject.get("body").toString();
			}
			if (emailObject.containsKey("subject")) {
				subject = emailObject.get("subject").toString();
			}
			if (emailObject.containsKey("toAddress")) {
				toAddress = emailObject.get("toAddress").toString() + ","
						+ email;
			} else {
				toAddress = email;
			}
			if (StringUtils.isNotBlank(body)) {
				body = body
						.replace(
								"{name}",
								requestmap.getValue(
										ResourceConstants.ENQUIRY_FORM_NAME)
										.toString())
						.replace(
								"{phone}",
								requestmap
										.getValue(
												ResourceConstants.ENQUIRY_FORM_PHONENUMBER)
										.toString())
						.replace(
								"{email}",
								requestmap.getValue(ResourceConstants.EMAIL)
										.toString());

				if (StringUtils.isNotBlank(requestmap.getValue(
						ResourceConstants.ENQUIRY_FORM_CITY).toString())) {
					body = body.replace(
							"{city}",
							requestmap.getValue(
									ResourceConstants.ENQUIRY_FORM_CITY)
									.toString());

				} else {
					body = body.replace("City : {city}", "City : "
							+ ResourceConstants.NOT_MENTIONED);
				}

				if (requestmap.getValue(ResourceConstants.ENQUIRY_FORM_COURSE)
						.toString().equals(ResourceConstants.SELECT_COURSE)) {
					body = body.replace("Course: {course}", "Course: "
							+ ResourceConstants.NOT_MENTIONED);

				} else {
					body = body.replace(
							"{course}",
							requestmap.getValue(
									ResourceConstants.ENQUIRY_FORM_COURSE)
									.toString());

				}

				if (StringUtils.isNotBlank(requestmap.getValue(
						ResourceConstants.ENQUIRY_FORM_QUERY).toString())) {
					body = body.replace(
							"{query}",
							requestmap.getValue(
									ResourceConstants.ENQUIRY_FORM_QUERY)
									.toString());
				} else {
					body = body.replace("Query : {query}", "Query: "
							+ ResourceConstants.NOT_MENTIONED);
				}

				Multipart multipart = new MimeMultipart();
				BodyPart bodyPart = new MimeBodyPart();
				bodyPart.setContent(body, "text/html");
				multipart.addBodyPart(bodyPart);
				awsEmailService.sendEmail(subject, multipart, toAddress);
			}

		} catch (MessagingException e) {
			LOGGER.error("Messaging Exception in TataTech Servlet"
					+ e.getMessage());
		} catch (NullPointerException e) {
			LOGGER.error("NullPointerException Exception TataTech Servlet"
					+ e.getMessage());
		}

	}

}