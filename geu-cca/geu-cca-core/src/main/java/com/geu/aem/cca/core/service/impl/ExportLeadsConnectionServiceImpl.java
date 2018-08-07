package com.geu.aem.cca.core.service.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourceNotFoundException;
import com.day.commons.datasource.poolservice.DataSourcePool;
import com.day.cq.dam.api.AssetManager;
import com.geu.aem.cca.beans.ExcelFields;
import com.geu.aem.cca.beans.LeadBean;
import com.geu.aem.cca.constants.ResourceConstants;
import com.geu.aem.cca.core.constants.CCAConstants;
import com.geu.aem.cca.core.service.ExportLeadsConnectionService;
import com.geu.aem.cca.util.CreateExcelUtil;

/**
 * @author aimen.sania
 * 
 */
@Component(service = ExportLeadsConnectionService.class, immediate = true)
public class ExportLeadsConnectionServiceImpl implements
		ExportLeadsConnectionService {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Reference
	private DataSourcePool source;

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	private Connection getConnection() {

		DataSource dataSource = null;
		Connection connection = null;
		try {
			dataSource = (DataSource) source
					.getDataSource(CCAConstants.DATASOURCE_NAME);
			connection = dataSource.getConnection();
			LOGGER.info("connection:: " + connection);

		} catch (SQLException e) {
			LOGGER.error("SQLException occurred in getConnection method of "
					+ "MySQLConnectionServiceImpl class " + e.getMessage());
		} catch (DataSourceNotFoundException e) {
			LOGGER.error("DataSourceNotFoundException occurred in getConnection method of "
					+ "MySQLConnectionServiceImpl class " + e.getMessage());
		}
		return connection;
	}

	/**
	 * Returns customer data from mySQL table by calling store procedure
	 * 
	 * @return
	 */
	public void processExportLeads() {
		LOGGER.info("Inside processExportLeads method!!!!");
		Connection connection = null;
		try {
			connection = getConnection();
			CallableStatement cStmt = connection
					.prepareCall("{call GetDetailToCreateFlatFileGeneration}");
			cStmt.execute();
			ResultSet resultSet = cStmt.getResultSet();
			exportLeads(resultSet);
			createExcelInDAM();
		} catch (Exception e) {
			LOGGER.info("SQLException occurred in readFile method of "
					+ "MySQLConnectionServiceImpl " + e.getMessage());
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				LOGGER.error("SQLException occurred in finally of readFile :"
						+ e.getMessage());
			}
		}
	}

	/**
	 * This methods creates excel file.
	 */
	public ArrayList<LeadBean> exportLeads(ResultSet resultset) {
		
		ArrayList<LeadBean> list = new ArrayList<LeadBean>();
		try {
			while (resultset.next()) {
				LeadBean exportLead = new LeadBean();
				exportLead.setFormId(resultset.getString(ExcelFields.formId
						.toString()));
				exportLead.setAdmissionId(resultset
						.getString(ExcelFields.admission_id.toString()));
				exportLead.setFirstName(resultset
						.getString(ExcelFields.firstName.toString()));
				exportLead.setLastName(resultset.getString(ExcelFields.lastName
						.toString()));
				exportLead.setContactNum(resultset
						.getString(ExcelFields.contactnum.toString()));
				exportLead.setEmail(resultset.getString(ExcelFields.email
						.toString()));
				exportLead.setInterest(resultset.getString(ExcelFields.interest
						.toString()));
				exportLead.setLevel(resultset.getString(ExcelFields.level
						.toString()));
				exportLead
						.setLeadGenerationDate(resultset
								.getString(ExcelFields.lead_generation_date
										.toString()));
				exportLead.setCampusPreference1(resultset
						.getString(ExcelFields.campus_preference_1.toString()));
				exportLead.setCoursePreference1(resultset
						.getString(ExcelFields.course_preference_1.toString()));
				exportLead.setCampusPreference2(resultset
						.getString(ExcelFields.campus_preference_2.toString()));
				exportLead.setCoursePreference2(resultset
						.getString(ExcelFields.course_preference_2.toString()));
				exportLead.setGender(resultset.getString(ExcelFields.gender
						.toString()));
				exportLead.setCategory(resultset.getString(ExcelFields.category
						.toString()));
				exportLead.setCorrespondenceAddress(resultset
						.getString(ExcelFields.correspondence_address
								.toString()));
				exportLead.setPermanentAddress(resultset
						.getString(ExcelFields.permanent_address.toString()));
				exportLead.setCity(resultset.getString(ExcelFields.city
						.toString()));
				exportLead.setState(resultset.getString(ExcelFields.state
						.toString()));
				exportLead.setCountry(resultset.getString(ExcelFields.country
						.toString()));
				exportLead.setProvisions(resultset
						.getString(ExcelFields.provisions.toString()));
				exportLead.setFatherEmail(resultset
						.getString(ExcelFields.father_email.toString()));
				exportLead.setMotherName(resultset
						.getString(ExcelFields.mother_name.toString()));
				exportLead.setMotherContact(resultset
						.getString(ExcelFields.mother_contact.toString()));
				exportLead.setMotherEmail(resultset
						.getString(ExcelFields.mother_email.toString()));
				exportLead.setSchoolName10th(resultset
						.getString(ExcelFields.school_name_10th.toString()));
				exportLead
						.setAggregateMarks10th(resultset
								.getString(ExcelFields.aggregate_marks_10th
										.toString()));
				exportLead.setBoard10th(resultset
						.getString(ExcelFields.board_10th.toString()));
				exportLead
						.setYearOfPassing10th(resultset
								.getString(ExcelFields.year_of_passing_10th
										.toString()));
				exportLead.setIntermediateSenior(resultset
						.getString(ExcelFields.intermediate_senior.toString()));
				exportLead
						.setProjectedMarks12th(resultset
								.getString(ExcelFields.projected_marks_12th
										.toString()));
				exportLead.setPreboardYear(resultset
						.getString(ExcelFields.preboard_year.toString()));
				exportLead.setMarks12th(resultset
						.getString(ExcelFields.marks_12th.toString()));
				exportLead.setBoard12th(resultset
						.getString(ExcelFields.board_12th.toString()));
				exportLead
						.setYearOfPassing12th(resultset
								.getString(ExcelFields.year_of_passing_12th
										.toString()));
				exportLead.setHigherEducationInstitute(resultset
						.getString(ExcelFields.higher_education_institute
								.toString()));
				exportLead.setCgpaPercentage(resultset
						.getString(ExcelFields.cgpa_percentage.toString()));
				exportLead.setYearOfGraduation(resultset
						.getString(ExcelFields.year_of_graduation.toString()));
				exportLead.setDateOfBirth(resultset
						.getString(ExcelFields.date_of_birth.toString()));
				exportLead.setAge(resultset.getString(ExcelFields.age
						.toString()));
				exportLead.setPriorAssociation(resultset
						.getString(ExcelFields.prior_association.toString()));
				exportLead.setDateOfAdmission(resultset
						.getString(ExcelFields.date_of_admission.toString()));
				exportLead.setPaymentMode(resultset
						.getString(ExcelFields.paymentMode.toString()));
				exportLead.setLeadStatus(resultset
						.getString(ExcelFields.leadStatus.toString()));
				exportLead.setLastComment(resultset
						.getString(ExcelFields.previous_comment.toString()));
				exportLead.setFollowUpDate(resultset
						.getString(ExcelFields.follow_up_date.toString()));
				exportLead.setAdmissionId(resultset
						.getString(ExcelFields.admission_id.toString()));
				exportLead.setSource(resultset.getString(ExcelFields.source
						.toString()));
				exportLead
						.setAdmissionPaymentType(resultset
								.getString(ExcelFields.admissionPaymentType
										.toString()));
				exportLead.setAdmissionFeeAmount(resultset
						.getString(ExcelFields.admissionFeeAmount.toString()));
				exportLead.setAdmissionPaymentStatus(resultset
						.getString(ExcelFields.admissionPaymentStatus
								.toString()));
				exportLead.setAdmissionTransactionId(resultset
						.getString(ExcelFields.admissionTransactionId
								.toString()));
				exportLead.setRegistrationPaymentMode(resultset
						.getString(ExcelFields.registrationPaymentMode
								.toString()));
				exportLead
						.setRegistrationFeeAmount(resultset
								.getString(ExcelFields.registrationFeeAmount
										.toString()));
				exportLead.setRegistrationPaymentStatus(resultset
						.getString(ExcelFields.registrationPaymentStatus
								.toString()));
				exportLead.setRegistrationTransactionId(resultset
						.getString(ExcelFields.registrationTransactionId
								.toString()));
				exportLead.setLeadAllocatedTo(resultset
						.getString(ExcelFields.leadAllocatedTo
								.toString()));
				exportLead.setLeadAllocated(resultset
						.getString(ExcelFields.leadAllocated
								.toString()));
					
				list.add(exportLead);
	
			}
			String directory = ResourceConstants.EXPORT_EXCEL_FILEPATH + "/"
					+ ResourceConstants.EXPORT_EXCEL_FILENAME
					+ ResourceConstants.EXCEL_FILEFORMAT;
			CreateExcelUtil.createExcel(list, directory);
		} catch (SQLException e) {
			LOGGER.info("SQLException occurred in exportLeads "
					+ "method of ExportLeadsServiceImpl class" + e.getMessage());
		}
		return list;
	
	}

	/**
	 * This methods creates excel file in DAM.
	 */
	private void createExcelInDAM() {
		String outputFilePath = ResourceConstants.EXPORT_EXCEL_FILEPATH + "/"
				+ ResourceConstants.EXPORT_EXCEL_FILENAME + ResourceConstants.EXCEL_FILEFORMAT;
		Map<String, Object> sessionParam = new HashMap<String, Object>();
		sessionParam.put(ResourceResolverFactory.SUBSERVICE,
				"getMySQLServiceResource");
		if (resourceResolverFactory != null) {
			ResourceResolver resResolver;
			try {
				resResolver = resourceResolverFactory
						.getServiceResourceResolver(sessionParam);
				AssetManager assetMgr = resResolver.adaptTo(AssetManager.class);
				InputStream is;
				is = new FileInputStream(outputFilePath);
				
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date date = new Date();
				String todaysDate=dateFormat.format(date);
				LOGGER.info("Today's Date:"+todaysDate);
				
				String exportPath = ResourceConstants.DAM_LOCATION + "/"
						+ ResourceConstants.EXPORT_EXCEL_FILENAME + "_"
						+ todaysDate + ResourceConstants.EXCEL_FILEFORMAT;
				
				assetMgr.createAsset(exportPath, is,
						ResourceConstants.DAM_EXCEL_FORMAT, true);
				LOGGER.info("asset created");
			} catch (FileNotFoundException e) {
				LOGGER.error("File Not Found in doPost method" + e.getMessage());
			} catch (LoginException e) {
				LOGGER.error("LoginException occurred in doPost method "
						+ e.getMessage());
			}
		}
	}
}
