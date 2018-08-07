package com.geu.aem.cca.util;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.lang.StringUtil;
import com.geu.aem.cca.beans.LeadBean;
import com.geu.aem.cca.beans.LeadFields;
import com.geu.aem.cca.beans.LeadUniqueKeyBean;

public class CCAMapUtil {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(CCAMapUtil.class);
	
	private static Map<LeadUniqueKeyBean, LeadBean> ccaLeadMap;
	
	private static Map<LeadUniqueKeyBean, LeadBean> enquiryMap;
	
	private static Map<LeadUniqueKeyBean, LeadBean> admissionMap;
	
	private static LeadBean ccaLeadBean = null;
	
	private static LeadBean enquiryBean = null;
	
	private static LeadBean admissionBean = null;
	
	public static Map<LeadUniqueKeyBean, LeadBean> createLeadMap(ResultSet resultSet){
		
		ccaLeadMap = new HashMap<LeadUniqueKeyBean, LeadBean>();
		//LeadBean ccaLeadBean = null;
		LeadUniqueKeyBean ccaUniqueBean = null;
		try{
			while(resultSet.next()){
				ccaLeadBean = new LeadBean();
				ccaUniqueBean = new LeadUniqueKeyBean();
				ccaLeadBean.setFirstName(resultSet.getString(LeadFields.firstName.toString()));
				ccaUniqueBean.setFirstName(resultSet.getString(LeadFields.firstName.toString()));
				ccaLeadBean.setLastName(resultSet.getString(LeadFields.lastName.toString()));
				ccaUniqueBean.setLastName(resultSet.getString(LeadFields.lastName.toString()));
				ccaLeadBean.setContactNum(resultSet.getString(LeadFields.contactNum.toString()));
				ccaUniqueBean.setContactNum(resultSet.getString(LeadFields.contactNum.toString()));
				ccaLeadBean.setInterest(resultSet.getString(LeadFields.interest.toString()));
				ccaLeadBean.setLeadGenerationDate(resultSet.getString(LeadFields.lead_generation_date.toString()));
				ccaLeadBean.setGender(resultSet.getString(LeadFields.gender.toString()));
				ccaLeadBean.setDateOfBirth(resultSet.getString(LeadFields.date_of_birth.toString()));
				ccaLeadBean.setEmail(resultSet.getString(LeadFields.email.toString()));
				ccaLeadBean.setAge(resultSet.getString(LeadFields.age.toString()));
				ccaLeadBean.setMotherName(resultSet.getString(LeadFields.mother_name.toString()));
				ccaLeadBean.setMotherContact(resultSet.getString(LeadFields.mother_contact.toString()));
				ccaLeadBean.setMotherEmail(resultSet.getString(LeadFields.mother_email.toString()));
				ccaLeadBean.setFatherEmail(resultSet.getString(LeadFields.father_email.toString()));
				
				if(StringUtils.isNotBlank(resultSet.getString(LeadFields.correspondence_address.toString()))){
					ccaLeadBean.setCorrespondenceAddress(resultSet.getString(LeadFields.correspondence_address.toString()).trim().replaceAll("[\n\r]", ""));
				}else{
					ccaLeadBean.setCorrespondenceAddress(resultSet.getString(LeadFields.correspondence_address.toString()));
				}
				LOGGER.info("corresponding address:::"+ccaLeadBean.getCorrespondenceAddress());

				if(StringUtils.isNotBlank(resultSet.getString(LeadFields.permanent_address.toString()))){
					ccaLeadBean.setPermanentAddress(resultSet.getString(LeadFields.permanent_address.toString()).trim().replaceAll("[\n\r]", ""));
				}else{
					ccaLeadBean.setPermanentAddress(resultSet.getString(LeadFields.permanent_address.toString()));
				}
				LOGGER.info("permanent address:::"+ccaLeadBean.getPermanentAddress());
				ccaLeadBean.setCity(resultSet.getString(LeadFields.city.toString()));
				ccaLeadBean.setState(resultSet.getString(LeadFields.state.toString()));
				ccaLeadBean.setCountry(resultSet.getString(LeadFields.country.toString()));
				ccaLeadBean.setLevel(resultSet.getString(LeadFields.level.toString()));
				ccaLeadBean.setCampusPreference1(resultSet.getString(LeadFields.campus_preference_1.toString()));
				ccaLeadBean.setCoursePreference1(resultSet.getString(LeadFields.course_preference_1.toString()));
				ccaLeadBean.setCampusPreference2(resultSet.getString(LeadFields.campus_preference_2.toString()));
				ccaLeadBean.setCoursePreference2(resultSet.getString(LeadFields.course_preference_2.toString()));
				ccaLeadBean.setCategory(resultSet.getString(LeadFields.category.toString()));
				ccaLeadBean.setProvisions(resultSet.getString(LeadFields.provisions.toString()));
				ccaLeadBean.setPriorAssociation(resultSet.getString(LeadFields.prior_association.toString()));
				ccaLeadBean.setBoard10th(resultSet.getString(LeadFields.board_10th.toString()));
				ccaLeadBean.setSchoolName10th(resultSet.getString(LeadFields.school_name_10th.toString()));
				ccaLeadBean.setYearOfPassing10th(resultSet.getString(LeadFields.year_of_passing_10th.toString()));
				ccaLeadBean.setAggregateMarks10th(resultSet.getString(LeadFields.aggregate_marks_10th.toString()));
				ccaLeadBean.setBoard12th(resultSet.getString(LeadFields.board_12th.toString()));
				ccaLeadBean.setIntermediateSenior(resultSet.getString(LeadFields.intermediate_senior.toString()));
				ccaLeadBean.setProjectedMarks12th(resultSet.getString(LeadFields.projected_marks_12th.toString()));
				ccaLeadBean.setPreboardYear(resultSet.getString(LeadFields.preboard_year.toString()));
				ccaLeadBean.setMarks12th(resultSet.getString(LeadFields.marks_12th.toString()));
				ccaLeadBean.setYearOfPassing12th(resultSet.getString(LeadFields.year_of_passing_12th.toString()));
				ccaLeadBean.setHigherEducationInstitute(resultSet.getString(LeadFields.higher_education_institute.toString()));
				ccaLeadBean.setCgpaPercentage(resultSet.getString(LeadFields.cgpa_percentage.toString()));
				ccaLeadBean.setYearOfGraduation(resultSet.getString(LeadFields.year_of_graduation.toString()));
				ccaLeadBean.setLeadStatus(resultSet.getString(LeadFields.leadStatus.toString()));
				ccaLeadBean.setFollowUpDate(resultSet.getString(LeadFields.follow_up_date.toString()));
				ccaLeadBean.setAdmissionId(resultSet.getString(LeadFields.admission_id.toString()));
				ccaLeadBean.setDateOfAdmission(resultSet.getString(LeadFields.date_of_admission.toString()));
				ccaLeadBean.setPaymentMode(resultSet.getString(LeadFields.paymentMode.toString()));
				ccaLeadBean.setLastComment(resultSet.getString(LeadFields.previous_comment.toString()));
				ccaLeadMap.put(ccaUniqueBean, ccaLeadBean);	
			}	
			LOGGER.info("ccaLeadMap size::: "+ccaLeadMap.size());
			
		} catch (SQLException e) {
			LOGGER.error("SQLException occured in createLeadMap method of CCAMapUtil class::"+e.getMessage());
		}
		return ccaLeadMap;		
	}	
	
	public static Map<LeadUniqueKeyBean, LeadBean> creatEnquiryMap(ResultSet resultSet){
		
		enquiryMap = new HashMap<LeadUniqueKeyBean, LeadBean>();
		//LeadBean enquiryBean = null;
		LeadUniqueKeyBean enquiryUniqueBean = null;
		
		try{
			while(resultSet.next()){
				
				enquiryBean = new LeadBean();
				enquiryUniqueBean = new LeadUniqueKeyBean();
				enquiryBean.setFirstName(resultSet.getString(LeadFields.firstName.toString()));
				enquiryUniqueBean.setFirstName(resultSet.getString(LeadFields.firstName.toString()));
				enquiryBean.setLastName(resultSet.getString(LeadFields.lastName.toString()));
				enquiryUniqueBean.setLastName(resultSet.getString(LeadFields.lastName.toString()));
				enquiryBean.setContactNum(resultSet.getString(LeadFields.contactNum.toString()));
				enquiryUniqueBean.setContactNum(resultSet.getString(LeadFields.contactNum.toString()));
				enquiryBean.setEmail(resultSet.getString(LeadFields.email.toString()));
				enquiryBean.setInterest(resultSet.getString(LeadFields.interest.toString()));
				enquiryBean.setCity(resultSet.getString(LeadFields.city.toString()));
				enquiryBean.setState(resultSet.getString(LeadFields.state.toString()));
				enquiryBean.setCountry(resultSet.getString(LeadFields.country.toString()));
				enquiryMap.put(enquiryUniqueBean, enquiryBean);
			}			
			
		} catch(Exception e){
			LOGGER.error("SQLException occured in creatEnquiryMap method of CCAMapUtil class::"+e.getMessage());

		}
		return enquiryMap;
	}
	
	public static Map<LeadUniqueKeyBean, LeadBean> createAdmissionMap(ResultSet resultSet){
		
		admissionMap = new HashMap<LeadUniqueKeyBean, LeadBean>();
		//LeadBean admissionBean = null;
		LeadUniqueKeyBean admissionUniqueBean = null;
		try{			
			while(resultSet.next()){
				admissionBean = new LeadBean();
				admissionUniqueBean = new LeadUniqueKeyBean();
				admissionBean.setFirstName(resultSet.getString(LeadFields.firstName.toString()));
				admissionUniqueBean.setFirstName(resultSet.getString(LeadFields.firstName.toString()));
				admissionBean.setLastName(resultSet.getString(LeadFields.lastName.toString()));
				admissionUniqueBean.setLastName(resultSet.getString(LeadFields.lastName.toString()));
				admissionBean.setContactNum(resultSet.getString(LeadFields.contactNum.toString()));
				admissionUniqueBean.setContactNum(resultSet.getString(LeadFields.contactNum.toString()));
				admissionBean.setInterest(resultSet.getString(LeadFields.interest.toString()));
				admissionBean.setGender(resultSet.getString(LeadFields.gender.toString()));
				admissionBean.setEmail(resultSet.getString(LeadFields.email.toString()));
				admissionBean.setMotherName(resultSet.getString(LeadFields.mother_name.toString()));
				admissionBean.setMotherContact(resultSet.getString(LeadFields.mother_contact.toString()));
				admissionBean.setMotherEmail(resultSet.getString(LeadFields.mother_email.toString()));
				admissionBean.setFatherEmail(resultSet.getString(LeadFields.father_email.toString()));
				admissionBean.setCorrespondenceAddress(resultSet.getString(LeadFields.correspondence_address.toString()));
				admissionBean.setPermanentAddress(resultSet.getString(LeadFields.permanent_address.toString()));
				admissionBean.setCity(resultSet.getString(LeadFields.city.toString()));
				admissionBean.setLevel(resultSet.getString(LeadFields.level.toString()));
				admissionBean.setCoursePreference1(resultSet.getString(LeadFields.course_preference_1.toString()));
				admissionBean.setBoard10th(resultSet.getString(LeadFields.board_10th.toString()));
				admissionBean.setSchoolName10th(resultSet.getString(LeadFields.school_name_10th.toString()));
				admissionBean.setYearOfPassing10th(resultSet.getString(LeadFields.year_of_passing_10th.toString()));
				admissionBean.setAggregateMarks10th(resultSet.getString(LeadFields.aggregate_marks_10th.toString()));
				admissionBean.setBoard12th(resultSet.getString(LeadFields.board_12th.toString()));
				admissionBean.setMarks12th(resultSet.getString(LeadFields.marks_12th.toString()));
				admissionBean.setPaymentMode(resultSet.getString(LeadFields.paymentMode.toString()));
				admissionMap.put(admissionUniqueBean, admissionBean);	
			}			
			
			LOGGER.info("admissionMap :::"+admissionMap.size());
			
		} catch(Exception e){
			LOGGER.error("SQLException occured in createAdmissionMap method of CCAMapUtil class::"+e.getMessage());
		}
		
		return admissionMap;		
	}
	
	public static Map<LeadUniqueKeyBean, LeadBean> createCombinedMap(ResultSet resultSet){
		
		Map<LeadUniqueKeyBean, LeadBean> combinedMap = new HashMap<LeadUniqueKeyBean, LeadBean>();
		try{
			for (Map.Entry<LeadUniqueKeyBean, LeadBean> entry : ccaLeadMap.entrySet()) {
				LeadUniqueKeyBean key = entry.getKey();
				LeadBean value = entry.getValue();
				
				if(value!=null){
					LeadBean leadAdmissionBean = new LeadBean();
					LeadBean finalLeadBean = new LeadBean();
					List<LeadUniqueKeyBean> admissionKeyList = new ArrayList<LeadUniqueKeyBean>();

					for (Map.Entry<LeadUniqueKeyBean, LeadBean> admissionEntry : admissionMap.entrySet()) {
						LeadUniqueKeyBean admissionKey = admissionEntry.getKey();
						
						if(key.equals(admissionKey)){
							LeadBean admissionCheckBean = admissionMap.get(admissionKey); 
							leadAdmissionBean = mergeObjects(value, admissionCheckBean);
							combinedMap.put(key, leadAdmissionBean);
							admissionKeyList.add(admissionKey);
						}else{
							leadAdmissionBean = value;
							combinedMap.put(key, value);
						}
					}
					for(LeadUniqueKeyBean leadBean: admissionKeyList){
						admissionMap.remove(leadBean);
					}
					
					if(leadAdmissionBean.getFirstName()!=null){
						List<LeadUniqueKeyBean> enquiryKeyList = new ArrayList<LeadUniqueKeyBean>();

						for(Map.Entry<LeadUniqueKeyBean, LeadBean> enquiryEntry : enquiryMap.entrySet()){
							LeadUniqueKeyBean enquiryKey = enquiryEntry.getKey();
							if(key.equals(enquiryKey)){	
								LeadBean enquiryCheckBean = enquiryMap.get(enquiryKey); 
								finalLeadBean = mergeObjects(leadAdmissionBean, enquiryCheckBean);
								combinedMap.put(key, finalLeadBean);
								enquiryKeyList.add(enquiryKey);
							}else{
								combinedMap.put(key, leadAdmissionBean);
							}
						}
						for(LeadUniqueKeyBean leadBean: enquiryKeyList){
							enquiryMap.remove(leadBean);
						}
					}else{
						combinedMap.put(key, value);
					}					
				}
			}
			combinedMap.putAll(enquiryMap);
			combinedMap.putAll(admissionMap);

		} catch (Exception e) {
			LOGGER.error("SQLException occured in createCombinedMap method of CCAMapUtil class::"+e.getMessage());

		}		
		return combinedMap;
	}
	
	public static <T> T mergeObjects(T first, T second) throws IllegalAccessException, InstantiationException {
	    Class<?> clazz = first.getClass();
	    Field[] fields = clazz.getDeclaredFields();
	    Object returnValue = clazz.newInstance();
	    for (Field field : fields) {
	        field.setAccessible(true);
	        Object value1 = field.get(first);
	        Object value2 = field.get(second);

	        Object value = null;//(value1!=null) ? value1 : value2;	
	        if(value1!=null){
	        	value = value1;
	        }else if(value2!=null) {
	        	value = value2;
	        }else {
	        	value = null;
	        }	       
	        field.set(returnValue, value);
	    }
	    return (T) returnValue;
	}

}
