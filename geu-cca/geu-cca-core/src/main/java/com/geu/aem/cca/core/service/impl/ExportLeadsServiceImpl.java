package com.geu.aem.cca.core.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.cca.beans.ExportFields;
import com.geu.aem.cca.beans.LeadBean;
import com.geu.aem.cca.constants.ResourceConstants;
import com.geu.aem.cca.core.service.ExportLeadsService;
import com.geu.aem.cca.core.service.SFTPConnectionService;
import com.geu.aem.cca.util.GenerateExcelUtil;

@Component(service = ExportLeadsService.class, 
immediate = true
)
public class ExportLeadsServiceImpl implements ExportLeadsService {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Reference
	SFTPConnectionService sftpConnService;
	
	/* (non-Javadoc)
	 * This method creates an export bean from the result set provided as input.
	 * @see com.geu.aem.cca.core.service.ExportLeadsService#exportLeads(java.sql.ResultSet)
	 */
	public ArrayList<LeadBean> exportLeads(ResultSet resultset) {
	
		ArrayList<LeadBean> list = new ArrayList<LeadBean>();
		try {
			while (resultset.next()) {
				LeadBean exportLead = new LeadBean();
				exportLead.setAdmissionId(resultset
						.getString(ExportFields.admissionId.toString()));
				exportLead.setFirstName(resultset
						.getString(ExportFields.firstName.toString()));
				exportLead.setLastName(resultset
						.getString(ExportFields.lastName.toString()));
				exportLead.setContactNum(resultset
						.getString(ExportFields.contactNum.toString()));
				exportLead.setEmail(resultset.getString(ExportFields.email
						.toString()));
				exportLead.setInterest(resultset
						.getString(ExportFields.interest.toString()));
				exportLead.setLevel(resultset.getString(ExportFields.level
						.toString()));
				exportLead.setLeadGenerationDate(resultset
						.getString(ExportFields.lead_generation_date.toString()));
				exportLead.setCampusPreference1(resultset
						.getString(ExportFields.campus_preference_1.toString()));
				exportLead.setCoursePreference1(resultset
						.getString(ExportFields.course_preference_1.toString()));
				exportLead.setCampusPreference2(resultset
						.getString(ExportFields.campus_preference_2.toString()));
				exportLead.setCoursePreference2(resultset
						.getString(ExportFields.course_preference_2.toString()));
				exportLead.setGender(resultset.getString(ExportFields.gender
						.toString()));
				exportLead.setCategory(resultset
						.getString(ExportFields.category.toString()));
				exportLead
						.setCorrespondenceAddress(resultset
								.getString(ExportFields.correspondence_address
										.toString()));
				exportLead.setPermanentAddress(resultset
						.getString(ExportFields.permanent_address.toString()));
				exportLead.setCity(resultset.getString(ExportFields.city
						.toString()));
				exportLead.setState(resultset.getString(ExportFields.state
						.toString()));
				exportLead.setCountry(resultset
						.getString(ExportFields.country.toString()));
				exportLead.setProvisions(resultset
						.getString(ExportFields.provisions.toString()));
				exportLead.setFatherEmail(resultset
						.getString(ExportFields.father_email.toString()));
				exportLead.setMotherName(resultset
						.getString(ExportFields.mother_name.toString()));
				exportLead.setMotherContact(resultset
						.getString(ExportFields.mother_contact.toString()));
				exportLead.setMotherEmail(resultset
						.getString(ExportFields.mother_email.toString()));
				exportLead.setSchoolName10th(resultset
						.getString(ExportFields.school_name_10th.toString()));
				exportLead.setAggregateMarks10th(resultset
						.getString(ExportFields.aggregate_marks_10th.toString()));
				exportLead.setBoard10th(resultset
						.getString(ExportFields.board_10th.toString()));
				exportLead.setYearOfPassing10th(resultset
						.getString(ExportFields.year_of_passing_10th.toString()));
				exportLead.setIntermediateSenior(resultset
						.getString(ExportFields.intermediate_senior.toString()));
				exportLead.setProjectedMarks12th(resultset
						.getString(ExportFields.projected_marks_12th.toString()));
				exportLead.setPreboardYear(resultset
						.getString(ExportFields.preboard_year.toString()));
				exportLead.setMarks12th(resultset
						.getString(ExportFields.marks_12th.toString()));
				exportLead.setBoard12th(resultset
						.getString(ExportFields.board_12th.toString()));
				exportLead.setYearOfPassing12th(resultset
						.getString(ExportFields.year_of_passing_12th.toString()));
				exportLead.setHigherEducationInstitute(resultset
						.getString(ExportFields.higher_education_institute
								.toString()));
				exportLead.setCgpaPercentage(resultset
						.getString(ExportFields.cgpa_percentage.toString()));
				exportLead.setYearOfGraduation(resultset
						.getString(ExportFields.year_of_graduation.toString()));
				exportLead.setDateOfBirth(resultset
						.getString(ExportFields.date_of_birth.toString()));
				exportLead.setAge(resultset.getString(ExportFields.age
						.toString()));
				exportLead.setPriorAssociation(resultset
						.getString(ExportFields.prior_association.toString()));
				exportLead.setDateOfAdmission(resultset
						.getString(ExportFields.date_of_admission.toString()));
				exportLead.setPaymentMode(resultset
						.getString(ExportFields.paymentMode.toString()));
				exportLead.setLeadSource(resultset
						.getString(ExportFields.leadSource.toString()));
				exportLead.setLeadScore(resultset
						.getString(ExportFields.leadScore.toString()));
				exportLead.setLeadAllocated(resultset
						.getString(ExportFields.leadAllocated.toString()));
				exportLead.setLeadAllocatedTo(resultset
						.getString(ExportFields.leadAllocatedTo.toString()));
				exportLead.setLeadStatus(resultset
						.getString(ExportFields.leadStatus.toString()));
				exportLead.setFollowUp(resultset
						.getString(ExportFields.followUp.toString()));
				exportLead.setLastFollowUpDate(resultset
						.getString(ExportFields.lastFollowUpDate.toString()));
				exportLead.setLastComment(resultset
						.getString(ExportFields.lastComment.toString()));
				list.add(exportLead);
				
			}
			String directory = ResourceConstants.EXCEL_FILEPATH+"/"+ResourceConstants.EXCEL_FILENAME;
			GenerateExcelUtil.generateExcel(list, directory);
		} catch (SQLException e) {
			LOGGER.info("SQLException occurred in exportLeads "
					+ "method of ExportLeadsServiceImpl class" + e.getMessage());
		}
		return list;

	}	

}
