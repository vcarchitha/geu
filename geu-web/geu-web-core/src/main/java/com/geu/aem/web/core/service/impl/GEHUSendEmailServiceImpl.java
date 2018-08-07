package com.geu.aem.web.core.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.json.simple.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.models.bean.AdmissionFields;
import com.geu.aem.web.core.service.GEHUSendEmailService;
import com.geu.aem.web.service.AWSEmailService;
import com.geu.aem.web.util.GEUWebUtils;

@Component(service = GEHUSendEmailService.class, immediate = true)
public class GEHUSendEmailServiceImpl implements GEHUSendEmailService{
	
	private final Logger LOGGER = LoggerFactory
			.getLogger(GEHUSendEmailService.class);	

	@Reference
	ResourceResolverFactory resolverFactory;
	
	@Reference
	AWSEmailService awsEmailService;
	
	// Send using 10 concurrent threads
	private ExecutorService executor = Executors.newFixedThreadPool(10);
	
	public void sendEmailToUser(String emailID, String formID){
		
		StringBuilder stringBuilder = new StringBuilder();
		String subject = null;
		final String toAddress = emailID;
		String body = null;
		
		try{
			String jsonPath = ResourceConstants.EMAIL_CONFIG+ResourceConstants.GEHU_ADMISSIONFORMID_JSON;
			JSONObject emailObject = GEUWebUtils.getJSONData(resolverFactory, jsonPath);
			if(emailObject.get("subject")!=null){
				subject = emailObject.get("subject").toString();
			}
			
			stringBuilder.append(formID+".");
			
			if(emailObject.get("body")!=null){
				body = String.join(
						System.getProperty("line.separator"),
						emailObject.get("body").toString(),
						stringBuilder.toString()
						);
			}
			
			final Multipart multipart = createMultiPartForEmailWithoutAttachment(body);
			
			final String subjectToAdd = subject;
			
			runEmailService(toAddress, multipart, subjectToAdd);
			
		}catch (MessagingException e) {
			LOGGER.error("Messaging Exception in sendEmailToUser "+e.getMessage());
		}catch (Exception e) {
			 LOGGER.error("Exception Exception in sendEmailToUser "+e.getMessage()); 
		}
	}	
	
	public void sendSubmissionEmailToUser(String emailID){
		
		String subject = null;
		String body = null;
		final String toAddress = emailID;
		
		try{
			String jsonPath = ResourceConstants.EMAIL_CONFIG+ResourceConstants.GEHU_ADMISSIONSUBMITTED_JSON;
			JSONObject emailObject = GEUWebUtils.getJSONData(resolverFactory, jsonPath);
			if(emailObject.get("subject")!=null){
				subject = emailObject.get("subject").toString();
			}
			
			if(emailObject.get("body")!=null){
				body = String.join(
						System.getProperty("line.separator"),
						emailObject.get("body").toString()
						);
			}
			
			final Multipart multipart = createMultiPartForEmailWithoutAttachment(body);
			
			final String subjectToAdd = subject;
			
			runEmailService(toAddress, multipart, subjectToAdd);
			
		}catch (MessagingException e) {
			LOGGER.error("Messaging Exception in sendSubmissionEmailToUser "+e.getMessage());
		}catch (Exception e) {
			 LOGGER.error("Exception Exception in sendSubmissionEmailToUser "+e.getMessage()); 
		}
	}
	
	public void sendAdmissionDataToAdmin(String formID, Map<String, String> admissionMap, 
			Map<String, InputStream> admissionFormIDMap, Map<String, String> fileNameMap){
		
		StringBuilder stringBuilder = new StringBuilder();
		String subject = null;
		String body = null;
		String address = null;
		try{
			String jsonPath = ResourceConstants.EMAIL_CONFIG+ResourceConstants.GEHU_ADMISSIONADMIN_JSON;
			JSONObject emailObject = GEUWebUtils.getJSONData(resolverFactory, jsonPath);
			
			if(emailObject.get("subject")!=null){
				subject = emailObject.get("subject").toString();
			}
			
			stringBuilder.append("<b>"+formID+".</b>");
			stringBuilder.append("<br/>"+ResourceConstants.ADMISSION_ADMIN_BODYCONTENT+"<br/>" );
			StringBuilder stringBuilderDetails = new StringBuilder();
	
			addContent(admissionMap, stringBuilderDetails);
			
			if(emailObject.get("body")!=null){
				body = String.join(
						System.getProperty("line.separator"),
						emailObject.get("body").toString(),
						stringBuilder.toString(),						
						System.getProperty("line.separator"),
						stringBuilderDetails.toString()						
						);
			}
			
			if(emailObject.get("toAddress")!=null){
				address = emailObject.get("toAddress").toString();
			}
			
			final Multipart multipart = new MimeMultipart();
			BodyPart bodyPart = new MimeBodyPart();
			bodyPart.setContent(body, "text/html");
			attachment(admissionFormIDMap, multipart, fileNameMap);
			multipart.addBodyPart(bodyPart);		
			
			final String subjectToAdd = subject;
			final String toAddress = address;
			
			runEmailService(toAddress, multipart, subjectToAdd);
			
		}catch (MessagingException e) {
			LOGGER.error("Messaging Exception in sendSubmissionEmailToUser "+e.getMessage());
		} catch (IOException e) {
			 LOGGER.error("IOException Exception in sendSubmissionEmailToUser "+e.getMessage()); 
		} catch (Exception e) {
			 LOGGER.error("Exception in sendSubmissionEmailToUser "+e.getMessage()); 
		}
	}

	/**
	 * This method returns the content to be added in the <BodyPart> of the email.
	 * @param admissionMap
	 * @param stringBuilderDetails
	 * @throws Exception
	 */
	private void addContent(Map<String, String> admissionMap,
			StringBuilder stringBuilderDetails) throws Exception{
		for(String admissionData:admissionMap.keySet()){
			if(admissionData.equalsIgnoreCase(AdmissionFields.firstName.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.FIRSTNAME_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.lastName.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.LASTNAME_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");		
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.interest.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.AREAOFINTEREST_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");						
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.contactNum.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.MOBILE_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.formId.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.FORM_ID_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.course_preference_1.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.PROGRAM_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");		
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.board_10th.toString())){
				stringBuilderDetails.append("<br/><b>"+ResourceConstants.CLASS_X_ADMINMAIL.toUpperCase()+" :</b><br/>");			
				stringBuilderDetails.append("<br/>"+ResourceConstants.BOARD_10TH_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.school_name_10th.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.SCHOOL_NAME_10TH_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.rollno_10th.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.ROLL_10TH_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.year_of_passing_10th.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.PASSINGYEAR_10TH_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.marking_system_10th.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.MARKINGSYSTEM_10TH_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.aggregate_marks_10th.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.AGGREGATE_10TH_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.board_12th.toString())){
				stringBuilderDetails.append("<br/><b>"+ResourceConstants.CLASS_XII_ADMINMAIL.toUpperCase()+" :</b><br/>");			
				stringBuilderDetails.append("<br/>"+ResourceConstants.BOARD_12TH_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.intermediate_senior.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.SCHOOL_NAME_12TH_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.status_12th.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.STATUS_12TH_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.rollno_12th.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.ROLL_12TH_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.year_of_passing_12th.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.PASSINGYEAR_12TH_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.city_12th.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.CITY_12TH_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.pincode_12th.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.PINCODE_12TH_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.resultsDeclared_12th.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.RESULTSDECLARED_12TH_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.projected_marks_12th.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.AGGREGATE_12TH_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.higher_education_institute.toString())){
				stringBuilderDetails.append("<br/><b>"+ResourceConstants.GRADUATION_ADMINMAIL.toUpperCase()+" :</b><br/>");			
				stringBuilderDetails.append("<br/>"+ResourceConstants.GRADUATION_COLLEGE_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.graduationUniversityName.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.GRADUATION_UNIVERSITY_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.graduationCourse.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.GRADUATION_COURSE_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.graduationSpecialisation.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.GRADUATION__SPECIALISATION_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.year_of_graduation.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.GRADUATION_PASSINGYEAR_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.cityGraduation.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.GRADUATION_CITY_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.pincodeGraduation.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.GRADUATION_PINCODE_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.resultsDeclaredGraduation.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.GRADUATION_RESULTSDECLARED_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.cgpa_percentage.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.GRADUATION_PERCENTAGE_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.gender.toString())){
				stringBuilderDetails.append("<br/><b>"+ResourceConstants.PERSONALDETAILS_ADMINMAIL.toUpperCase()+" :</b><br/>");			
				stringBuilderDetails.append("<br/>"+ResourceConstants.GENDER_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.caste.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.CASTE_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.date_of_birth.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.DATEOFBIRTH_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.aadharNo.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.AADHAR_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.correspondence_address.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.CORRESPONDENCE_ADDRESS_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.permanent_address.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.PERMANENT_ADDRESS_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.illness.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.ILLNESS_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.name_illness.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.ILLNESS_NAME_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.father_name.toString())){
				stringBuilderDetails.append("<br/><b>"+ResourceConstants.FAMILY_INFO_ADMINMAIL.toUpperCase()+" :</b><br/>");
				stringBuilderDetails.append("<br/><b>"+ResourceConstants.FATHER_DETAILS_ADMINMAIL.toUpperCase()+" :</b><br/>");
				stringBuilderDetails.append("<br/>"+ResourceConstants.NAME_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.father_email.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.EMAIL_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.father_contact.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.MOBILE_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.mother_name.toString())){
				stringBuilderDetails.append("<br/><b>"+ResourceConstants.MOTHER_DETAILS_ADMINMAIL.toUpperCase()+" :</b><br/>");
				stringBuilderDetails.append("<br/>"+ResourceConstants.NAME_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.mother_email.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.EMAIL_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.mother_contact.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.MOBILE_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.association_GEU.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.ASSOCIATION_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.videoWhatsApp.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.VIDEO_WATSAPP_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.selfVideoURL.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.SELF_VIDEO_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.essayQuestion1.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.ESSAY1_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else if(admissionData.equalsIgnoreCase(AdmissionFields.essayQuestion2.toString())){
				stringBuilderDetails.append("<br/>"+ResourceConstants.ESSAY2_ADMINMAIL.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}else {
				stringBuilderDetails.append("<br/>"+admissionData.toUpperCase()+" :"+admissionMap.get(admissionData).toString()+"<br/>");			
			}
		}
	}

	/**
	 * This method attaches the files to the <Multipart> object along with the mime type for each file.
	 * @param admissionFormIDMap
	 * @param multipart
	 * @param fileNameMap
	 * @throws IOException
	 * @throws MessagingException
	 */
	private void attachment(Map<String, InputStream> admissionFormIDMap, Multipart multipart, Map<String, String> fileNameMap) 
			throws IOException, MessagingException {	
		for(String key:admissionFormIDMap.keySet()){
			BodyPart messageBodyPart = new MimeBodyPart();
			String mimeType = null;
			for(String nameKey :fileNameMap.keySet()){
				if(nameKey.contains(key)){
					String fileName = fileNameMap.get(nameKey);
					String extension = FilenameUtils.getExtension(fileName);
					if(StringUtils.isNotBlank(extension)){
						if(extension.equalsIgnoreCase(ResourceConstants.JPG_FORMAT)){
							mimeType = "image/jpg";
						}else if(extension.equalsIgnoreCase(ResourceConstants.JPEG_FORMAT)){
							mimeType = "image/jpeg";		
						}else if(extension.equalsIgnoreCase(ResourceConstants.BMP_FORMAT)){
							mimeType = "image/bmp";		
						}else if(extension.equalsIgnoreCase(ResourceConstants.GIF_FORMAT)){
							mimeType = "image/gif";	
						}else if(extension.equalsIgnoreCase(ResourceConstants.PNG_FORMAT)){
							mimeType = "image/png";	
						}else if(extension.equalsIgnoreCase(ResourceConstants.PDF_FORMAT)){
							mimeType = "application/pdf";	
						}
					}					
				}
			}
			ByteArrayDataSource source = new ByteArrayDataSource(admissionFormIDMap.get(key), mimeType);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(key.toUpperCase());
			multipart.addBodyPart(messageBodyPart);
		}
	
	}
	
	/**
	 * This method executes the email service.
	 * @param toAddress
	 * @param multipart
	 * @param subjectToAdd
	 * @throws Exception
	 */
	private void runEmailService(final String toAddress,
			final Multipart multipart, final String subjectToAdd) throws Exception{		
		executor.execute(new Runnable() {
			public void run() {
				awsEmailService.sendEmail(subjectToAdd, multipart, toAddress);
			} 
		});
	}
	
	/**
	 * This method create the <Multipart> object for the email service without any attachments.
	 * @param body
	 * @return
	 * @throws MessagingException
	 */
	private Multipart createMultiPartForEmailWithoutAttachment(String body)
			throws MessagingException {
		final Multipart multipart = new MimeMultipart();
		BodyPart bodyPart = new MimeBodyPart();
		bodyPart.setContent(body, "text/html");
		multipart.addBodyPart(bodyPart);
		return multipart;
	}	
}
