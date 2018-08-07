package com.geu.aem.web.core.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.simple.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.service.LoginAdmissionFormService;
import com.geu.aem.web.service.SMSApplicationService;
import com.geu.aem.web.util.GEUWebUtils;

/**
 * This servlet is used for inserting, updating and fetching the data from Login Tab of admission form
 * fields. 
 * @author smrithi.g
 */
@Component(service = Servlet.class, property = {
		"service.description=" + "Login Admission Form Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST,
		"sling.servlet.paths=" + "/bin/geu/loginAdmissionForm" })
public class LoginAdmissionFormServlet extends SlingAllMethodsServlet {

	@Reference
	LoginAdmissionFormService admissionService;
	
	@Reference
	private SMSApplicationService smsApplicationService;

	private static final long serialVersionUID = 1L;
	private final Logger LOGGER = LoggerFactory
			.getLogger(LoginAdmissionFormServlet.class);

	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {
		try{		
			String type = request.getParameter(ResourceConstants.TYPE);			
			if(type.equalsIgnoreCase(ResourceConstants.ADMISSION_APPLYFRESH)){
				
				RequestParameterMap requestMap = request.getRequestParameterMap();
				Map<String, String> admissionMap = new LinkedHashMap<String, String>();
				JSONObject jsonObject = new JSONObject();
				String[] cancelColumns = {ResourceConstants.ADMISSION_ENTER_OTP, ResourceConstants.RESOURCE_PATH,
							ResourceConstants.TYPE, ResourceConstants.FORM_ACTION, ResourceConstants.OTP_CODE,
							ResourceConstants.PHONE_NUMBER};
				if (requestMap != null) {
					for (String admissionField : requestMap.keySet()) {
						boolean foundColumn = false;				
						for(String cancelColumn : cancelColumns) {
		            		if(cancelColumn.equalsIgnoreCase(admissionField.toString())){
		            			foundColumn = true;
		            		}
		            	}
						if(!foundColumn){
							String value = requestMap.get(admissionField).toString();
							if (value != null) {
								if(admissionField.equalsIgnoreCase(ResourceConstants.INTEREST)){
									checkAreaOfInterest(requestMap,
											admissionMap, admissionField);
								}else{
									admissionMap.put(admissionField,
											requestMap.getValue(admissionField).toString());
								}								
							} else {
								admissionMap.put(admissionField,
										ResourceConstants.EMPTY_STRING);
							}							
						}				
					}
					boolean userIfExists = admissionService.checkUserAlreadyExists(admissionMap);
					if(userIfExists){
						String userAlreadyExists = ResourceConstants.USER_ALREADY_REGISTERED_ERROR;
						response.getWriter().write(userAlreadyExists);
					}else{
						String formAction = request.getParameter(ResourceConstants.FORM_ACTION);
						if(StringUtils.isNotBlank(formAction)){
							if(formAction.equals("sendotp")){
								String otp = smsApplicationService.generateSecureOTP();
								LOGGER.info("OTP while APPLY FRESH : SEND OTP CASE:"+otp);
								String phoneNumber = admissionMap.get(ResourceConstants.CONTACT_NUMBER);
								
								String result = smsApplicationService.sendOTP(otp,phoneNumber);
								if(StringUtils.isNotBlank(result)){
									String status=GEUWebUtils.getJsonValue(result, "status");
									if(StringUtils.isNotBlank(status)){
										if(status.equalsIgnoreCase("success")){
									    	response.getWriter().write("success");	
											response=smsApplicationService.processSendOTP(request,response,otp);

									    }else {
									    	response.getWriter().write("error");
									    }
									}
								}
								response=smsApplicationService.processSendOTP(request,response,otp);						

							}else if (formAction.equals("verifyotp")){
								boolean isOTPValid=smsApplicationService.verifyOTP(request); 
								if (!isOTPValid){
									response.getWriter().write("invalid_otp");
								}else {
									String phoneNumber=admissionMap.get(ResourceConstants.CONTACT_NUMBER);
									admissionService.insertLoginAdmissionData(admissionMap, phoneNumber);
									jsonObject = admissionService.getLoginAdmissionData(admissionMap);
									
									String jsonData=jsonObject.toJSONString();
									response.getWriter().write(jsonData);									
								}
							}
						}
						
					}
				}				
			} else if (type.equalsIgnoreCase(ResourceConstants.ADMISSION_PARTIALLYFILLED)){
				String formID = request.getParameter("formID");
				if(StringUtils.isNotBlank(formID)){
					boolean formIDCheck = admissionService.checkFormIDAlreadyExists(formID);
					if(!formIDCheck){
						String userNotFound = ResourceConstants.FORM_ID_DOESNOT_EXISTS_ERROR;
						response.getWriter().write(userNotFound);
					}else{
						boolean paymentStatus = admissionService.checkPaymentStatus(formID);
						if(paymentStatus){
							String formAction = request.getParameter(ResourceConstants.FORM_ACTION);
							if(StringUtils.isNotBlank(formAction)){
								if(formAction.equals("sendotp")){
									String otp=smsApplicationService.generateSecureOTP();
									LOGGER.info("OTP while PARTIALLY FILLED : SEND OTP CASE:"+otp);
									String contactNum = admissionService.getMobileNumberFormID(formID);
									if(StringUtils.isNotBlank(contactNum)){
										String result=smsApplicationService.sendOTP(otp,contactNum);
										if(StringUtils.isNotBlank(result))
										{
											String status=GEUWebUtils.getJsonValue(result, "status");
											if(StringUtils.isNotBlank(status))
											{
												if(status.equalsIgnoreCase("success")){
											    	response.getWriter().write("success");	
													response=smsApplicationService.processSendOTP(request,response,otp);	

											    }else {
											    	response.getWriter().write("error");
											    }
											}
										}
										response=smsApplicationService.processSendOTP(request,response,otp);
									}
								}else if(formAction.equals("verifyotp")){
									boolean isOTPValid=smsApplicationService.verifyOTP(request); 
									if (!isOTPValid){
										response.getWriter().write("invalid_otp");
									}else{
										JSONObject jsonObject = new JSONObject();
										jsonObject = admissionService.getAdmissionDataFromFormID(formID);
										String jsonBasicData=jsonObject.toJSONString();
										response.getWriter().write(jsonBasicData);
									}
								}
							}
						}else{
							String paymentStatusErrorMsg = ResourceConstants.PAYMENT_STATUS_ERROR_MSG;
							response.getWriter().write(paymentStatusErrorMsg);
						}						
					}
				}							
			}
		} catch(IOException e){
			LOGGER.error("IOException occurred in doPost "+e.getMessage());
		} catch(SQLException e) {
			LOGGER.error("Exception occurred in doPost method: "+e.getMessage());	
			try {
				response.getWriter().write("Something Went wrong");
			} catch (IOException e1) {
			}
		}catch(Exception e){
			LOGGER.error("Exception occurred in doPost method "+e.getMessage());
		}		
	}

	private void checkAreaOfInterest(RequestParameterMap requestMap,
			Map<String, String> admissionMap, String admissionField) {
		if(requestMap.getValue(admissionField).toString().equals(ResourceConstants.SELECT)){
			admissionMap.put(admissionField,
					ResourceConstants.EMPTY_STRING);
		}else{
			admissionMap.put(admissionField,
					requestMap.getValue(admissionField).toString());
		}
	}
}
