package com.geu.aem.cca.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.cca.beans.ExportFields;

public class DeleteTemporaryTableUtil {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(DeleteTemporaryTableUtil.class);
	
	public static void deleteTempTable(Connection connection){
		try{
			String deleteTempQuery = "DELETE FROM tempacs "
					+ "WHERE EXISTS (SELECT 1 FROM acs_nurtured_leads ad "
					+ "	WHERE tempacs.firstname = ad.firstname "
					+ "	AND tempacs.lastname = ad.lastname"
					+ "	AND tempacs.contactnum = ad.contactnum)";
			
			LOGGER.info("deleteTempQuery:::"+deleteTempQuery);
			PreparedStatement deleteTempPreparedStatement = connection
					.prepareStatement(deleteTempQuery);
			deleteTempPreparedStatement.executeUpdate(deleteTempQuery);
			
		} catch(SQLException e) {
			LOGGER.error("SQLException occurred in deleteTempTable : "+e.getMessage());
		}
	}
	
	public static void insertIntoACSLeads(Connection connection){
		try{
			String insertAcsQuery = "INSERT INTO acs_nurtured_leads "
					+ " SELECT " +ExportFields.admissionId+" , "+ExportFields.firstName+" , "+ExportFields.lastName+", "
					+ExportFields.contactNum+", "+ExportFields.email+",  "+ExportFields.interest+", "
					+ExportFields.level+", "+ExportFields.campus_preference_1+",  "+ExportFields.course_preference_1+", "
					+ExportFields.campus_preference_2+", "+ExportFields.course_preference_2+",  "+ExportFields.gender+", "
					+ExportFields.category+", "+ExportFields.correspondence_address+",  "+ExportFields.permanent_address+", "
					+ExportFields.city+", "+ExportFields.state+",  "+ExportFields.country+", "
					+ExportFields.provisions+", "+ExportFields.father_email+",  "+ExportFields.mother_name+", "
					+ExportFields.mother_contact+", "+ExportFields.mother_email+",  "+ExportFields.school_name_10th+", "
					+ExportFields.aggregate_marks_10th+", "+ExportFields.board_10th+",  "+ExportFields.year_of_passing_10th+", "
					+ExportFields.intermediate_senior+", "+ExportFields.projected_marks_12th+",  "+ExportFields.preboard_year+", "
					+ExportFields.marks_12th+", "+ExportFields.board_12th+",  "+ExportFields.year_of_passing_12th+", "
					+ExportFields.higher_education_institute+", "+ExportFields.cgpa_percentage+",  "+ExportFields.year_of_graduation+", "
					+ExportFields.date_of_birth+", "+ExportFields.lead_generation_date+",  "+ExportFields.age+", "
					+ExportFields.prior_association+", "+ExportFields.date_of_admission+",  "+ExportFields.paymentMode+", "
					+ExportFields.leadSource+", "+ExportFields.leadScore+",  "+ExportFields.leadAllocated+", "
					+ExportFields.leadAllocatedTo+", "+ExportFields.leadStatus+",  "+ExportFields.followUp+", "
					+ExportFields.lastFollowUpDate+", "+ExportFields.lastComment+",  "+ExportFields.lastModified+" "				
					+ "FROM tempacs";
			
			LOGGER.info("insertAcsQuery:::"+insertAcsQuery);
			PreparedStatement insertPreparedStatement = connection
					.prepareStatement(insertAcsQuery);
			insertPreparedStatement.executeUpdate(insertAcsQuery);
			
		} catch(SQLException e){
			LOGGER.error("SQLException occurred in insertIntoACSLeads : "+e.getMessage());
		}
	}
	
	public static void dropTempTable(Connection connection){
		try{
			String dropQuery= "DROP TEMPORARY TABLE tempacs";
			
			LOGGER.info("dropQuery:::"+dropQuery);
			PreparedStatement dropPreparedStatement = connection
					.prepareStatement(dropQuery);
			dropPreparedStatement.executeUpdate(dropQuery);
		} catch(SQLException e){
			LOGGER.error("SQLException occurred in dropTempTable : "+e.getMessage());
	
		}
	}
	
	public static void deleteCSVLeadsTable(Connection connection){
		try{
			String deleteQuery= "DELETE FROM csv_leads";
			
			LOGGER.info("deleteQuery:::"+deleteQuery);
			PreparedStatement deletePreparedStatement = connection
					.prepareStatement(deleteQuery);
			deletePreparedStatement.executeUpdate(deleteQuery);
		} catch(SQLException e){
			LOGGER.error("SQLException occurred in dropTempTable : "+e.getMessage());
	
		}
	}
	
}
