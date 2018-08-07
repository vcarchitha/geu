package com.geu.aem.cca.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.cca.beans.ExportFields;

public class UpdateMySQLTableUtil {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(UpdateMySQLTableUtil.class);

	/**
	 * This method updates the csv_leads with csv flat file from SFTP server.
	 * 
	 * @param exportPath
	 */
	public static void updateTableUsingCSV(String exportPath,
			Connection connection) {
		try {
			if (StringUtils.isNotBlank(exportPath)) {
				String loadQuery = "LOAD DATA LOCAL INFILE '" + exportPath
						+ "' INTO TABLE csv_leads "
						+ "FIELDS TERMINATED BY '|'"
						+ " LINES TERMINATED BY '\n' " + " IGNORE 1 LINES "
						+ "(" + ExportFields.firstName + ", "
						+ ExportFields.lastName + ", "
						+ ExportFields.email + ", " + ExportFields.contactNum
						+ ", " + ExportFields.leadSource + ", "
						+ ExportFields.leadScore + ")";
				LOGGER.info("load Query :::" + loadQuery);
				PreparedStatement preparedStatement = connection
						.prepareStatement(loadQuery);
				preparedStatement.executeQuery(loadQuery);
			}

		} catch (NullPointerException e) {
			LOGGER.error("NullPointerException occurred in updateTable  "
					+ e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Exception occurred in updateTable  " + e.getMessage());
		}
	}

	public static void insertTempTable(Connection connection){		
		
		try {
			String createTempTableQuery = "CREATE TEMPORARY TABLE tempacs "
					+ "("+ExportFields.admissionId+" VARCHAR(100), "+ExportFields.firstName+" VARCHAR(100), "
					+ExportFields.lastName+" VARCHAR(100), "+ExportFields.contactNum+" VARCHAR(100), "
					+ExportFields.email+" VARCHAR(100), "+ExportFields.interest+" VARCHAR(100), "
					+ExportFields.level+" VARCHAR(60), "+ExportFields.campus_preference_1+" VARCHAR(765), "
					+ExportFields.course_preference_1+" VARCHAR(765), "+ExportFields.campus_preference_2+" VARCHAR(765), "
					+ExportFields.course_preference_2+" VARCHAR(765), "+ExportFields.gender+" VARCHAR(30), "
					+ExportFields.category+" VARCHAR(60), "+ExportFields.correspondence_address+" VARCHAR(300), "
					+ExportFields.permanent_address+" VARCHAR(300), "+ExportFields.city+" VARCHAR(60), "
					+ExportFields.state+" VARCHAR(60), "+ExportFields.country+" VARCHAR(60), "
					+ExportFields.provisions+" VARCHAR(60),"+ExportFields.father_email+" VARCHAR(100), "
					+ExportFields.mother_name+" VARCHAR(90), "+ExportFields.mother_contact+" VARCHAR(100), "
					+ExportFields.mother_email+" VARCHAR(100), "+ExportFields.school_name_10th+" VARCHAR(120), "
					+ExportFields.aggregate_marks_10th+" FLOAT(10, 4) NULL,"+ExportFields.board_10th+" VARCHAR(120), "
					+ExportFields.year_of_passing_10th+" INT(4), "+ExportFields.intermediate_senior+" VARCHAR(120), "
					+ExportFields.projected_marks_12th+" FLOAT, "+ExportFields.preboard_year+" INT(4), "
					+ExportFields.marks_12th+" FLOAT, "+ExportFields.board_12th+" VARCHAR(120), "
					+ExportFields.year_of_passing_12th+" INT(4), "+ExportFields.higher_education_institute+" VARCHAR(120), "
					+ExportFields.cgpa_percentage+" FLOAT, "+ExportFields.year_of_graduation+" INT(4), "
					+ExportFields.date_of_birth+" DATE, "+ExportFields.lead_generation_date+" DATE, "
					+ExportFields.age+" INT(3), "+ExportFields.prior_association+" VARCHAR(90), "
					+ExportFields.date_of_admission+" DATE, "+ExportFields.paymentMode+" VARCHAR(90), "
					+ExportFields.leadSource+" VARCHAR(120), "+ExportFields.leadScore+" VARCHAR(90), "
					+ExportFields.leadAllocated+" VARCHAR(90), "+ExportFields.leadAllocatedTo+" VARCHAR(90), "
					+ExportFields.leadStatus+" VARCHAR(90), "+ExportFields.followUp+" VARCHAR(90), "
					+ExportFields.lastFollowUpDate+" DATE, "+ExportFields.lastComment+" VARCHAR(120), "
					+ExportFields.lastModified+" DATETIME)";
							
			LOGGER.info("createTempQuery::::"+createTempTableQuery);
			
			PreparedStatement preparedStatement = connection
					.prepareStatement(createTempTableQuery);
			preparedStatement.executeUpdate(createTempTableQuery);
			
			String insertQuery = "INSERT INTO tempacs "
					+ "SELECT ccl.admission_Id, ccl."+ExportFields.firstName+", ccl."+ExportFields.lastName+", "
					+ "ccl."+ExportFields.contactNum+", ccl."+ExportFields.email+", ccl."+ExportFields.interest+", ccl."+ExportFields.level+", "
					+ "ccl."+ExportFields.campus_preference_1+", ccl."+ExportFields.course_preference_1+", "
					+ " ccl."+ExportFields.campus_preference_2+", ccl."+ExportFields.course_preference_2+", "
					+ "ccl."+ExportFields.gender+", ccl."+ExportFields.category+", ccl."+ExportFields.correspondence_address+", "
					+ "ccl."+ExportFields.permanent_address+", ccl."+ExportFields.city+", "
					+ "ccl."+ExportFields.state+", ccl."+ExportFields.country+", ccl."+ExportFields.provisions+", "
					+ "ccl."+ExportFields.father_email+", ccl."+ExportFields.mother_name+", "
					+ "ccl."+ExportFields.mother_contact+", ccl."+ExportFields.mother_email+", "
					+ "ccl."+ExportFields.school_name_10th+", ccl."+ExportFields.aggregate_marks_10th+", "
					+ "ccl."+ExportFields.board_10th+", ccl."+ExportFields.year_of_passing_10th+", "
					+ "ccl."+ExportFields.intermediate_senior+", ccl."+ExportFields.projected_marks_12th+", ccl."+ExportFields.preboard_year+", "
					+ "ccl."+ExportFields.marks_12th+", ccl."+ExportFields.board_12th+", "
					+ "ccl."+ExportFields.year_of_passing_12th+", ccl."+ExportFields.higher_education_institute+", "
					+ "ccl."+ExportFields.cgpa_percentage+", ccl."+ExportFields.year_of_graduation+", ccl."+ExportFields.date_of_birth+", "
					+ "ccl."+ExportFields.lead_generation_date+", ccl."+ExportFields.age+", "
					+ "ccl."+ExportFields.prior_association+", ccl."+ExportFields.date_of_admission+", "
					+ "ccl."+ExportFields.paymentMode+", cl."+ExportFields.leadSource+", cl."+ExportFields.leadScore+", "
					+ "'', '', ccl."+ExportFields.leadStatus+", '' ,  ccl.follow_up_date, ccl.previous_comment, CURDATE()"
					+ " FROM csv_leads cl "
					+ "JOIN callcentreleads ccl "
					+ "ON cl.firstname = ccl.firstname "
					+ "AND cl.lastname = ccl.lastname "
					+ "AND cl.contactnum = ccl.contactnum "
					+ "UNION "
					+ "SELECT NULL, ccl."+ExportFields.firstName+", ccl."+ExportFields.lastName+", ccl."+ExportFields.contactNum+", "
					+ " ccl."+ExportFields.email+", ccl."+ExportFields.interest+", ccl."+ExportFields.level+", '', "
					+ "ccl."+ExportFields.course_preference_1+", '', '', ccl."+ExportFields.gender+", '' , "
					+ "ccl."+ExportFields.correspondence_address+" ,ccl."+ExportFields.permanent_address+", ccl."+ExportFields.city+", "
					+ " '', '' , '', ccl."+ExportFields.father_email+", ccl."+ExportFields.mother_name+", "
					+ "ccl."+ExportFields.mother_contact+", ccl."+ExportFields.mother_email+", "
					+ "ccl."+ExportFields.school_name_10th+", ccl."+ExportFields.aggregate_marks_10th+", "
					+ "ccl."+ExportFields.board_10th+", ccl."+ExportFields.year_of_passing_10th+", NULL, NULL, NULL, "
					+ "ccl."+ExportFields.marks_12th+", ccl."+ExportFields.board_12th+","
					+ "NULL, NULL, NULL,NULL, NULL, NULL, NULL, NULL,NULL,NULL,"
					+ "cl.leadSource, cl.leadScore, "
					+ "'','','','',NULL,'',CURDATE()"
					+ "FROM csv_leads cl "
					+ "JOIN dummyadmission ccl "
					+ "	ON cl.firstname = ccl.firstname"
					+ "	AND cl.lastname = ccl.lastname"
					+ "	AND cl.contactnum = ccl.contactnum "
					+ "WHERE NOT EXISTS "
					+ "(SELECT 1 "
					+ "FROM callcentreleads t "
					+ "WHERE cl.firstname = t.firstname AND cl.lastname = t.lastname "
					+ "	AND cl.contactnum = t.contactnum)"
					+ "UNION "
					+ "SELECT NULL, ccl."+ExportFields.firstName+", ccl."+ExportFields.lastName+", "
					+ "ccl."+ExportFields.contactNum+", ccl."+ExportFields.email+", "
					+ "ccl."+ExportFields.interest+", NULL, NULL,NULL,NULL,NULL,NULL,NULL, "
					+ " NULL,NULL, ccl."+ExportFields.city+", "
					+ "ccl."+ExportFields.state+", ccl."+ExportFields.country+", NULL, NULL, "
					+ "NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, "
					+ "NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, cl.leadsource, cl.leadscore ,'','','','', "
					+ "NULL, '', CURDATE()"
					+ "FROM csv_leads cl "
					+ " JOIN dummyenquiry ccl "
					+ "	ON cl.firstname = ccl.firstname "
					+ " AND cl.lastname = ccl.lastname "
					+ " AND cl.contactnum = ccl.contactnum "
					+ " WHERE NOT EXISTS "
					+ "(SELECT 1 FROM callcentreleads t WHERE cl.firstname = t.firstname AND cl.lastname = t.lastname "
					+ " AND cl.contactnum = t.contactnum) "
					+ " AND NOT EXISTS (SELECT 1 FROM dummyadmission t WHERE cl.firstname = t.firstname "
					+ " AND cl.lastname = t.lastname "
					+ "	AND cl.contactnum = t.contactnum)";
			LOGGER.info("insertQuery::"+insertQuery);
			
			PreparedStatement insertpreparedStatement = connection
					.prepareStatement(insertQuery);
			insertpreparedStatement.executeUpdate(insertQuery);

		} catch (SQLException e) {
			LOGGER.error("SQLException occurred in insertTempTable : "+e.getMessage());
		}
		
	}
	
	public static void updateTempTablewithAdmission(Connection connection){
		
		try{
			String updateTempQuery = "UPDATE tempacs acs, dummyadmission ad "
					+ "SET acs."+ExportFields.email+" = NULLIF(acs."+ExportFields.email+", ad."+ExportFields.email+"),"
					+ "	acs."+ExportFields.interest+" = NULLIF(acs."+ExportFields.interest+", ad."+ExportFields.interest+"),"
					+ "	acs."+ExportFields.course_preference_1+" = NULLIF(acs."+ExportFields.course_preference_1+", ad."+ExportFields.course_preference_1+"),"
					+ "	acs."+ExportFields.level+" = NULLIF(acs."+ExportFields.level+", ad."+ExportFields.level+"),"
					+ "	acs."+ExportFields.board_10th+" = NULLIF(acs."+ExportFields.board_10th+", ad."+ExportFields.board_10th+"),"
					+ "	acs."+ExportFields.school_name_10th+" = NULLIF(acs."+ExportFields.school_name_10th+", ad."+ExportFields.school_name_10th+"),"
					+ "	acs."+ExportFields.year_of_passing_10th+" = NULLIF(acs."+ExportFields.year_of_passing_10th+", ad."+ExportFields.year_of_passing_10th+"),"
					+ "	acs."+ExportFields.aggregate_marks_10th+" = NULLIF(acs."+ExportFields.aggregate_marks_10th+", ad."+ExportFields.aggregate_marks_10th+"),"
					+ "	acs."+ExportFields.board_12th+" = NULLIF(acs."+ExportFields.board_12th+", ad."+ExportFields.board_12th+"),"
					+ "	acs."+ExportFields.city+" = NULLIF(acs."+ExportFields.city+", ad."+ExportFields.city+"),"
					+ "	acs."+ExportFields.marks_12th+" = NULLIF(acs."+ExportFields.marks_12th+", ad."+ExportFields.marks_12th+"),"
					+ "	acs."+ExportFields.gender+" = NULLIF(acs."+ExportFields.gender+", ad."+ExportFields.gender+"),"
					+ "	acs."+ExportFields.correspondence_address+" = NULLIF(acs."+ExportFields.correspondence_address+", ad."+ExportFields.correspondence_address+"),"
					+ "	acs."+ExportFields.permanent_address+" = NULLIF(acs."+ExportFields.permanent_address+", ad."+ExportFields.permanent_address+"),"
					+ "	acs."+ExportFields.father_email+" = NULLIF(acs."+ExportFields.father_email+", ad."+ExportFields.father_email+"),"
					+ "	acs."+ExportFields.mother_name+" = NULLIF(acs."+ExportFields.mother_name+", ad."+ExportFields.mother_name+"),"
					+ "	acs."+ExportFields.mother_email+" = NULLIF(acs."+ExportFields.mother_email+", ad."+ExportFields.mother_email+"),"
					+ "	acs."+ExportFields.mother_contact+" = NULLIF(acs."+ExportFields.mother_contact+", ad."+ExportFields.mother_contact+"),"
					+ "	acs."+ExportFields.paymentMode+" = NULLIF(acs."+ExportFields.paymentMode+", ad."+ExportFields.paymentMode+") "
					+ " WHERE acs.firstname = ad.firstname"
					+ "	AND acs.lastname = ad.lastname"
					+ "	AND acs.contactnum = ad.contactnum";
			
			LOGGER.info("updateTempQuery:::"+updateTempQuery);
			PreparedStatement updateTempPreparedStatement = connection
					.prepareStatement(updateTempQuery);
			updateTempPreparedStatement.executeUpdate(updateTempQuery);
		} catch(SQLException e){
			LOGGER.error("SQLException occurred in updateTempTablewithAdmission : "+e.getMessage());

		}	
		
	}
	
	public static void updateTempTablewithEnquiry(Connection connection){
		
		try{
			String updateEnquiryTempQuery = "UPDATE tempacs acs, dummyenquiry ad "
					+ "SET acs."+ExportFields.email+" = NULLIF(acs."+ExportFields.email+", ad."+ExportFields.email+"),"
					+ "	acs."+ExportFields.interest+" = NULLIF(acs."+ExportFields.interest+", ad."+ExportFields.interest+"),"
					+ "	acs."+ExportFields.city+" = NULLIF(acs."+ExportFields.city+", ad."+ExportFields.city+"),"
					+ "	acs."+ExportFields.state+" = NULLIF(acs."+ExportFields.state+", ad."+ExportFields.state+"),"
					+ "	acs."+ExportFields.country+" = NULLIF(acs."+ExportFields.country+", ad."+ExportFields.country+") "				
					+ " WHERE acs.firstname = ad.firstname"
					+ "	AND acs.lastname = ad.lastname"
					+ "	AND acs.contactnum = ad.contactnum";
			
			LOGGER.info("updateEnquiryTempQuery:::"+updateEnquiryTempQuery);
			PreparedStatement updateEnquiryTempPreparedStatement = connection
					.prepareStatement(updateEnquiryTempQuery);
			updateEnquiryTempPreparedStatement.executeUpdate(updateEnquiryTempQuery);
		} catch(SQLException e){
			LOGGER.error("SQLException occurred in updateTempTablewithEnquiry : "+e.getMessage());

		}			
	}
	
	public static void updateAcsNurturedLeads(Connection connection){
		try{
			String timeStamp = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").format(Calendar.getInstance().getTime());
			LOGGER.info("timestamp:: "+timeStamp);
			
			String updateAcsLeads = "UPDATE tempacs acs, acs_nurtured_leads ad "
					+ "SET ad."+ExportFields.email+" = NULLIF(acs."+ExportFields.email+", ad."+ExportFields.email+"),"
					+ "	ad."+ExportFields.admissionId+" = NULLIF(acs."+ExportFields.admissionId+", ad."+ExportFields.admissionId+"),"
					+ "	ad."+ExportFields.interest+" = NULLIF(acs."+ExportFields.interest+", ad."+ExportFields.interest+"),"
					+ "	ad."+ExportFields.level+" = NULLIF(acs."+ExportFields.level+", ad."+ExportFields.level+"),"
					+ "	ad."+ExportFields.campus_preference_1+" = NULLIF(acs."+ExportFields.campus_preference_1+", ad."+ExportFields.campus_preference_1+"),"
					+ "	ad."+ExportFields.course_preference_1+" = NULLIF(acs."+ExportFields.course_preference_1+", ad."+ExportFields.course_preference_1+"),"
					+ "	ad."+ExportFields.campus_preference_2+" = NULLIF(acs."+ExportFields.campus_preference_2+", ad."+ExportFields.campus_preference_2+"),"
					+ "	ad."+ExportFields.course_preference_2+" = NULLIF(acs."+ExportFields.course_preference_2+", ad."+ExportFields.course_preference_2+"),"
					+ "	ad."+ExportFields.gender+" = NULLIF(acs."+ExportFields.gender+", ad."+ExportFields.gender+"),"
					+ "	ad."+ExportFields.category+" = NULLIF(acs."+ExportFields.category+", ad."+ExportFields.category+"),"
					+ "	ad."+ExportFields.correspondence_address+" = NULLIF(acs."+ExportFields.correspondence_address+", ad."+ExportFields.correspondence_address+"),"
					+ "	ad."+ExportFields.permanent_address+" = NULLIF(acs."+ExportFields.permanent_address+", ad."+ExportFields.permanent_address+"),"
					+ "	ad."+ExportFields.city+" = NULLIF(acs."+ExportFields.city+", ad."+ExportFields.city+"),"
					+ "	ad."+ExportFields.state+" = NULLIF(acs."+ExportFields.state+", ad."+ExportFields.state+"),"
					+ "	ad."+ExportFields.country+" = NULLIF(acs."+ExportFields.country+", ad."+ExportFields.country+"), "
					+ "	ad."+ExportFields.provisions+" = NULLIF(acs."+ExportFields.provisions+", ad."+ExportFields.provisions+"),"
					+ "	ad."+ExportFields.father_email+" = NULLIF(acs."+ExportFields.father_email+", ad."+ExportFields.father_email+"),"
					+ "	ad."+ExportFields.mother_name+" = NULLIF(acs."+ExportFields.mother_name+", ad."+ExportFields.mother_name+"),"
					+ "	ad."+ExportFields.mother_contact+" = NULLIF(acs."+ExportFields.mother_contact+", ad."+ExportFields.mother_contact+"),"
					+ "	ad."+ExportFields.mother_email+" = NULLIF(acs."+ExportFields.mother_email+", ad."+ExportFields.mother_email+"),"
					+ "	ad."+ExportFields.school_name_10th+" = NULLIF(acs."+ExportFields.school_name_10th+", ad."+ExportFields.school_name_10th+"),"
					+ "	ad."+ExportFields.aggregate_marks_10th+" = NULLIF(acs."+ExportFields.aggregate_marks_10th+", ad."+ExportFields.aggregate_marks_10th+"),"
					+ "	ad."+ExportFields.board_10th+" = NULLIF(acs."+ExportFields.board_10th+", ad."+ExportFields.board_10th+"),"
					+ "	ad."+ExportFields.year_of_passing_10th+" = NULLIF(acs."+ExportFields.year_of_passing_10th+", ad."+ExportFields.year_of_passing_10th+"),"
					+ "	ad."+ExportFields.intermediate_senior+" = NULLIF(acs."+ExportFields.intermediate_senior+", ad."+ExportFields.intermediate_senior+"),"
					+ "	ad."+ExportFields.projected_marks_12th+" = NULLIF(acs."+ExportFields.projected_marks_12th+", ad."+ExportFields.projected_marks_12th+"),"
					+ "	ad."+ExportFields.preboard_year+" = NULLIF(acs."+ExportFields.preboard_year+", ad."+ExportFields.preboard_year+"),"
					+ "	ad."+ExportFields.marks_12th+" = NULLIF(acs."+ExportFields.marks_12th+", ad."+ExportFields.marks_12th+"),"
					+ "	ad."+ExportFields.board_12th+" = NULLIF(acs."+ExportFields.board_12th+", ad."+ExportFields.board_12th+"),"
					+ "	ad."+ExportFields.year_of_passing_12th+" = NULLIF(acs."+ExportFields.year_of_passing_12th+", ad."+ExportFields.year_of_passing_12th+"),"
					+ "	ad."+ExportFields.higher_education_institute+" = NULLIF(acs."+ExportFields.higher_education_institute+", ad."+ExportFields.higher_education_institute+"),"
					+ "	ad."+ExportFields.cgpa_percentage+" = NULLIF(acs."+ExportFields.cgpa_percentage+", ad."+ExportFields.cgpa_percentage+"),"
					+ "	ad."+ExportFields.year_of_graduation+" = NULLIF(acs."+ExportFields.year_of_graduation+", ad."+ExportFields.year_of_graduation+"),"
					+ "	ad."+ExportFields.date_of_birth+" = NULLIF(acs."+ExportFields.date_of_birth+", ad."+ExportFields.date_of_birth+"),"
					+ "	ad."+ExportFields.lead_generation_date+" = NULLIF(acs."+ExportFields.lead_generation_date+", ad."+ExportFields.lead_generation_date+"),"
					+ "	ad."+ExportFields.age+" = NULLIF(acs."+ExportFields.age+", ad."+ExportFields.age+"),"
					+ "	ad."+ExportFields.prior_association+" = NULLIF(acs."+ExportFields.prior_association+", ad."+ExportFields.prior_association+"),"
					+ "	ad."+ExportFields.date_of_admission+" = NULLIF(acs."+ExportFields.date_of_admission+", ad."+ExportFields.date_of_admission+"),"
					+ "	ad."+ExportFields.paymentMode+" = NULLIF(acs."+ExportFields.paymentMode+", ad."+ExportFields.paymentMode+"),"
					+ "	ad."+ExportFields.leadSource+" = NULLIF(acs."+ExportFields.leadSource+", ad."+ExportFields.leadSource+"),"
					+ "	ad."+ExportFields.leadScore+" = NULLIF(acs."+ExportFields.leadScore+", ad."+ExportFields.leadScore+"),"
					+ "	ad."+ExportFields.leadAllocated+" = NULLIF(acs."+ExportFields.leadAllocated+", ad."+ExportFields.leadAllocated+"),"
					+ "	ad."+ExportFields.leadAllocatedTo+" = NULLIF(acs."+ExportFields.leadAllocatedTo+", ad."+ExportFields.leadAllocatedTo+"),"
					+ "	ad."+ExportFields.leadStatus+" = NULLIF(acs."+ExportFields.leadStatus+", ad."+ExportFields.leadStatus+"),"
					+ "	ad."+ExportFields.followUp+" = NULLIF(acs."+ExportFields.followUp+", ad."+ExportFields.followUp+"),"
					+ "	ad."+ExportFields.lastFollowUpDate+" = NULLIF(acs."+ExportFields.lastFollowUpDate+", ad."+ExportFields.lastFollowUpDate+"),"
					+ "	ad."+ExportFields.lastComment+" = NULLIF(acs."+ExportFields.lastComment+", ad."+ExportFields.lastComment+"), "
					+ " ad."+ExportFields.lastModified+"= NULLIF(CURDATE(), ad."+ExportFields.lastModified+") "
					+ " WHERE acs.firstname = ad.firstname"
					+ "	AND acs.lastname = ad.lastname"
					+ "	AND acs.contactnum = ad.contactnum";
			
			LOGGER.info("updateAcsLeads:::"+updateAcsLeads);
			PreparedStatement updateAcsLeadsPreparedStatement = connection
					.prepareStatement(updateAcsLeads);
			updateAcsLeadsPreparedStatement.executeUpdate(updateAcsLeads);
			
		} catch(SQLException e){
			LOGGER.error("SQLException occurred in updateAcsNurturedLeads : "+e.getMessage());

		}
	}
}
