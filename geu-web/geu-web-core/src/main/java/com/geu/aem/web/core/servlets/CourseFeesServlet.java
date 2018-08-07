package com.geu.aem.web.core.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourcePool;
import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.models.bean.CourseFeesBean;
import com.geu.aem.web.util.SQLConnectionUtil;
import com.google.gson.Gson;

/**
 * @author nitin.jangir
 *
 */
@Component(service = { Servlet.class },
property = { "sling.servlet.paths=" + "/bin/geu/courseFees",
		"sling.servlet.methods=" + "GET"})
public class CourseFeesServlet extends SlingAllMethodsServlet {
	
	private static final long serialVersionUID = 1L;
	
	protected final Logger LOGGER = LoggerFactory.getLogger(CourseFeesServlet.class);
	
	private static final String CLASS_NAME = CourseFeesServlet.class.getSimpleName();
	
	@Reference
	private DataSourcePool source;

	@Override
	protected void doGet(SlingHttpServletRequest  request,
			SlingHttpServletResponse response) {
		
		String formId = request.getParameter(ResourceConstants.FORM_ID);
		String formType = request.getParameter(ResourceConstants.FORM_TYPE);
		List<CourseFeesBean> courseFeeList = new ArrayList<CourseFeesBean>();
		if(StringUtils.isNotBlank(formId) && StringUtils.isNotBlank(formType)) {
			courseFeeList = getFees(formId, formType);
		}
		String json = new Gson().toJson(courseFeeList);
		response.setContentType("application/json");
		try {
			response.getWriter().write(json);
		} catch (IOException e) {
			LOGGER.error("IOException in " + CLASS_NAME+ResourceConstants.SPACE+ e.getMessage());
		}
	}
	
	private List<CourseFeesBean> getFees(String storedFormValue, String formType) {
		Connection connection = null;
		List<CourseFeesBean> resultsList = new ArrayList<CourseFeesBean>();
		CourseFeesBean courseFeesBean = new CourseFeesBean();
		String query = ResourceConstants.EMPTY_STRING;
		try {
		connection = SQLConnectionUtil.getConnection(source);
		ResultSet resultSet = null;

		if (formType.equalsIgnoreCase(ResourceConstants.GEU)) {
			query = "Select * FROM admission where FormId='" + storedFormValue + "';";
		} else if (formType.equalsIgnoreCase(ResourceConstants.GEHU)) {
			query = "Select * FROM gehu_admission where FormId='" + storedFormValue + "';";
		}
		
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		
		resultSet = preparedStatement.executeQuery();
		ResultSetMetaData paymentFormData = resultSet.getMetaData();
		
		double totalFees = 0;
		double discountedFees = 0;
		
		while(resultSet.next())
		{
			for (int i = 1; i <= paymentFormData.getColumnCount(); i++) {
                String paymentFormHeader = paymentFormData.getColumnName(i);
                Object paymentFormValue = resultSet.getObject(paymentFormHeader);
              
                if(null != paymentFormValue && paymentFormHeader.equals(ResourceConstants.TOATAL_FEES))
				{
                	totalFees = Double.parseDouble(paymentFormValue.toString());
                	courseFeesBean.setTotalFees(totalFees);
				}
                if(null != paymentFormValue && paymentFormHeader.equals(ResourceConstants.DISCOUNT_RATE))
				{
                	double discount = Double.parseDouble(paymentFormValue.toString());
                	if ((discount - (int)discount) != 0) {
                		courseFeesBean.setDiscount(String.valueOf(discount));
                	} else {
                		courseFeesBean.setDiscount(String.valueOf((int)discount));
                	}
				}
                if(null != paymentFormValue && paymentFormHeader.equals(ResourceConstants.DISCOUNTED_FEES))
				{
                	discountedFees = Double.parseDouble(paymentFormValue.toString());
                	courseFeesBean.setTotalPayment(discountedFees);
                	courseFeesBean.setTotalAmount(String.valueOf((int)(discountedFees)));
				}
			}
		}
		double discountAmount = totalFees - discountedFees;
		
		courseFeesBean.setDiscountAmount(discountAmount);
		
		resultsList.add(courseFeesBean);
		} catch (SQLException e) {
			LOGGER.error("SQLException in " + CLASS_NAME+ResourceConstants.SPACE+ e.getMessage());
		}
		finally {
			try
			{
				if (connection != null) {
					connection.close();
				}
			}

			catch (SQLException e) {
				LOGGER.error("SQLException in "+ CLASS_NAME+ResourceConstants.SPACE + e.getMessage());
			}
		}
		return resultsList;
	}

}
