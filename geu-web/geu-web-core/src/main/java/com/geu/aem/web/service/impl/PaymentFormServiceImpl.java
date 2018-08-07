package com.geu.aem.web.service.impl;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.Cookie;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.json.simple.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourcePool;
import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.service.AWSEmailService;
import com.geu.aem.web.service.PGAxisServiceConfiguration;
import com.geu.aem.web.service.PaymentFormService;
import com.geu.aem.web.util.GEUWebUtils;
import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.BASE64EncoderStream;

@Component(service= {PaymentFormService.class},immediate=true,configurationPolicy=ConfigurationPolicy.REQUIRE)
@Designate(ocd = PGAxisServiceConfiguration.class)
public class PaymentFormServiceImpl implements PaymentFormService {

	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private static final String CLASS_NAME = SMSApplicationServiceImpl.class.getSimpleName();
	
	@Reference
	private DataSourcePool source;
	
	@Reference
	ResourceResolverFactory resolverFactory;
	
	@Reference
	AWSEmailService awsEmailService;
	
	private PGAxisServiceConfiguration pgAxisConfiguration;

	@Activate
	@Modified
	public void activate(PGAxisServiceConfiguration pgAxisConfiguration){
		this.pgAxisConfiguration = pgAxisConfiguration;		
	}

	@Override
	public String getVersion() {
		return pgAxisConfiguration.getVersion();
	}

	@Override
	public String getMerchantAccessCode() {
		return pgAxisConfiguration.getMerchantAccessCode();
	}

	@Override
	public String getCommandType() {
		return pgAxisConfiguration.getCommandType();
	}

	@Override
	public String getMerchantID() {
		return pgAxisConfiguration.getMerchantID();
	}

	@Override
	public String getReceiptReturnURL() {
		return pgAxisConfiguration.getReceiptReturnURL();
	}
	
	@Override
	public String getRegistrationReceiptReturnURL() {
		return pgAxisConfiguration.getRegistrationReceiptReturnURL();
	}
	
	@Override
	public String getContactNumber(String formNumber, String formType) {
		Connection connection = getConnection();
		String contactNumber = ResourceConstants.EMPTY_STRING;
		String submitStatus = ResourceConstants.EMPTY_STRING;
		String paymentStatus = ResourceConstants.EMPTY_STRING;
		String returnString = ResourceConstants.EMPTY_STRING;
		String query = ResourceConstants.EMPTY_STRING;
		try {
		
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		if (formType.equalsIgnoreCase("GEU")) {
			query = "Select * FROM admission where FormId='" + formNumber + "';";
		} else if (formType.equalsIgnoreCase("GEHU")) {
			query = "Select * FROM gehu_admission where FormId='" + formNumber + "';";
		}
		 
		preparedStatement = connection.prepareStatement(query);
		
		resultSet = preparedStatement.executeQuery();
		ResultSetMetaData paymentFormData = resultSet.getMetaData();
		
		while(resultSet.next())
		{
			for (int i = 1; i <= paymentFormData.getColumnCount(); i++) {
                String paymentFormHeader = paymentFormData.getColumnName(i);
                Object paymentFormValue = resultSet.getObject(paymentFormHeader);
              
                if(null != paymentFormValue && paymentFormHeader.equals(ResourceConstants.CONTACT_NUMBER))
				{
                	contactNumber = paymentFormValue.toString();
				} 
                if(null != paymentFormValue && paymentFormHeader.equals(ResourceConstants.SUBMIT_STATUS))
				{
                	submitStatus = paymentFormValue.toString();
				} 
                if (null != paymentFormValue && paymentFormHeader.equals(ResourceConstants.PAYMENT_STATUS)) {
                	paymentStatus = paymentFormValue.toString();
				}
			}
			returnString = getReturnStatus(submitStatus, paymentStatus, contactNumber);
		}
		} catch (SQLException e) {
			LOGGER.error("SQLException in PaymentFormService" + e.getMessage());
		}
		finally {
			try
			{
				if (connection != null) {
					connection.close();
				}
			}

			catch (SQLException e) {
				LOGGER.error("SQLException in PaymentFormService" + e.getMessage());
			}
		}
		
		return returnString;
	}

	private String getReturnStatus(String submitStatus, String paymentStatus, String contactNumber) {
		String returnString = null;
		if (!submitStatus.equalsIgnoreCase(ResourceConstants.SUBMITTED)) {
			returnString = ResourceConstants.NOT_SUBMITTED;
		} else if (paymentStatus.equalsIgnoreCase(ResourceConstants.SUCCESSFUL)) {
			returnString = paymentStatus;
		} else {
			returnString = contactNumber;
		}
		return returnString;
	}

	/**
	 * This Method is used to connect to mySQL using JDBC Connection pool
	 */

	private Connection getConnection()
	{
		DataSource datasource=null;
		Connection connection=null;

		try
		{
			datasource=(DataSource)source.getDataSource(ResourceConstants.DATASOURCE_NAME);
			connection=datasource.getConnection();
		}
		catch(Exception e)
		{
			LOGGER.error("Exception in Datasource Connection!" + e.getMessage());
		}
		return connection;

	}

	@Override
	public SlingHttpServletResponse processFormId(SlingHttpServletRequest request, SlingHttpServletResponse response,
			String formId) {
		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		LOGGER.info(ResourceConstants.ENTERING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);
		
		String encryptedString = encrypt(formId);
		deleteCookieIfPresent(request);
		Cookie cookie = createCookie(request,response, encryptedString);
		response.addCookie(cookie);

		LOGGER.info(ResourceConstants.EXITING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);
		return response;
	}
	
	@Override
	public SlingHttpServletResponse processRegistrationFormId(SlingHttpServletRequest request, SlingHttpServletResponse response,
			String formId, String type) {
		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		LOGGER.info(ResourceConstants.ENTERING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);
		
		String encryptedString = encrypt(formId);
		deleteRegistrationCookieIfPresent(request, type);
		Cookie cookie = createRegistrationCookie(request,response, encryptedString, type);
		response.addCookie(cookie);

		LOGGER.info(ResourceConstants.EXITING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);
		return response;
	}
	
	/**
	 * This Method is used to encrypt the OTP using DES Algorithm
	 * @param str
	 */
	private String encrypt(String str) {
		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		LOGGER.info(ResourceConstants.ENTERING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);
		String cookieValue = null;
		try
		{
			SecretKey key=KeyGenerator.getInstance("DES").generateKey();
			Cipher ecipher=Cipher.getInstance("DES");

			ecipher.init(Cipher.ENCRYPT_MODE, key);
			// encode the string into a sequence of bytes using the named charset
			// storing the result into a new byte array.
			byte[] strBytes = str.getBytes("UTF8");
			byte[] enc = ecipher.doFinal(strBytes);

			// encode to base64
			enc = BASE64EncoderStream.encode(enc);

			String encryptedOTP=new String(enc);
			String delimiter = "=&crypto=&";
			String encodedSecretKey = Base64.getEncoder().encodeToString(key.getEncoded());
			cookieValue = encryptedOTP+delimiter+encodedSecretKey;

			LOGGER.info(ResourceConstants.EXITING + ResourceConstants.SPACE + METHOD_NAME
					+ ResourceConstants.SPACE+CLASS_NAME);

		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("Algorithm Exception in "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		} catch (NoSuchPaddingException e) {
			LOGGER.error("Padding Exception in "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		} catch (InvalidKeyException e) {
			LOGGER.error("Invalid Key Exception in "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Exception in "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		}
		return cookieValue;
	}
	
	/**
	 * This Method is used to delete cookie if its already present
	 * @param request
	 */
	private void deleteCookieIfPresent(SlingHttpServletRequest request)
	{
		Cookie[] cookies = request.getCookies();
		if(null!=cookies)
		{
			for (int i = 0; i < cookies.length; i++)
			{
				Cookie cookie1 = cookies[i];
				if(cookie1.getName().equals("validId"))
				{
					cookie1.setMaxAge(0);
				}
			}
		}
	}
	
	/**
	 * This Method is used to delete cookie if its already present
	 * @param request
	 */
	private void deleteRegistrationCookieIfPresent(SlingHttpServletRequest request, String type)
	{
		Cookie[] cookies = request.getCookies();
		if(null!=cookies)
		{
			for (int i = 0; i < cookies.length; i++)
			{
				Cookie cookie1 = cookies[i];
				
				if(type.equalsIgnoreCase(ResourceConstants.GEU)){
					if(cookie1.getName().equals("registrationId"))
					{
						cookie1.setMaxAge(0);
					}
				}else if(type.equalsIgnoreCase(ResourceConstants.GEHU)){
					if(cookie1.getName().equals("registrationGEHUId"))
					{
						cookie1.setMaxAge(0);
					}
				}				
			}
		}
	}
	
	/**
	 * This Method is used to create HttpOnly cookie
	 * @param request
	 */
	private Cookie createCookie(SlingHttpServletRequest request,SlingHttpServletResponse response, String code)
	{
		Cookie cookie = new Cookie("validId", code);
		cookie.setMaxAge(36000);
		cookie.setPath(";Path=/;HttpOnly;");
		return cookie;
	}

	/**
	 * This Method is used to create HttpOnly cookie
	 * @param request
	 */
	private Cookie createRegistrationCookie(SlingHttpServletRequest request,SlingHttpServletResponse response, String code,
			String type)
	{
		Cookie cookie = null;
		if(type.equalsIgnoreCase(ResourceConstants.GEU)){
			cookie = new Cookie("registrationId", code);
			cookie.setMaxAge(36000);
			cookie.setPath(";Path=/;HttpOnly;");
		}else if(type.equalsIgnoreCase(ResourceConstants.GEHU)){
			cookie = new Cookie("registrationGEHUId", code);
			cookie.setMaxAge(36000);
			cookie.setPath(";Path=/;HttpOnly;");
		}		
		return cookie;
	}
	@Override
	public void savePaymentStatus(SlingHttpServletRequest request, String amount, String transactionReference, String transactionResponseMessage, String formType) {
		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		LOGGER.info(ResourceConstants.ENTERING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);

		String cookieValue = getCookie(request);
		String feeTypeCookie = getFeeCookie(request);
		String storedFeeType = null;
		if(StringUtils.isNotBlank(feeTypeCookie)){
			storedFeeType = decrypt(feeTypeCookie);
		}
		String storedFormValue = null;
		if(StringUtils.isNotBlank(cookieValue)){
			storedFormValue = decrypt(cookieValue);
		}
		updatePaymentData(storedFormValue, amount, transactionReference, storedFeeType, transactionResponseMessage, formType);
		String email = getEmailId(storedFormValue, formType);
		String type = ResourceConstants.ADMISSION_PAYMENT;
		sendPaymentMail(email, transactionResponseMessage, type);
		LOGGER.info(ResourceConstants.EXITING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);

	}
	
	@Override
	public void saveRegistrationPaymentStatus(SlingHttpServletRequest request, String amount, String transactionReference, String transactionResponseMessage, String formType) {
		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		LOGGER.info(ResourceConstants.ENTERING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);

		String cookieValue = getRegistrationCookie(request, formType);
		
		String storedFormValue = null;
		if(StringUtils.isNotBlank(cookieValue)){
			storedFormValue = decrypt(cookieValue);
		}
		updateRegistrationPaymentData(storedFormValue, amount, transactionReference, transactionResponseMessage, formType);
		String email = getEmailId(storedFormValue, formType);
		String type = null;
		
		if(formType.equalsIgnoreCase(ResourceConstants.GEU)){
			type = ResourceConstants.REGISTRATION;
		}else if(formType.equalsIgnoreCase(ResourceConstants.GEHU)){
			type = ResourceConstants.GEHU_REGISTRATION;
		}
		sendPaymentMail(email, transactionResponseMessage, type);
		LOGGER.info(ResourceConstants.EXITING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);

	}
	
	private void sendPaymentMail(String email, String transactionResponseMessage, String type) {
		StringBuilder stringBuilder=new StringBuilder();
		try {
			String jsonPath = "";
			if (transactionResponseMessage.equalsIgnoreCase(ResourceConstants.SUCCESSFUL)) {
				if(type.equalsIgnoreCase(ResourceConstants.REGISTRATION)){
					jsonPath = ResourceConstants.EMAIL_CONFIG + ResourceConstants.REGISTRATION_PAYMENT_JSON;
				}else if(type.equalsIgnoreCase(ResourceConstants.GEHU_REGISTRATION)){
					jsonPath = ResourceConstants.EMAIL_CONFIG + ResourceConstants.GEHU_REGISTRATION_PAYMENT_JSON;
				}else{
					jsonPath = ResourceConstants.EMAIL_CONFIG + ResourceConstants.ONLINE_JSON;
				}
			} else {
				if(type.equalsIgnoreCase(ResourceConstants.REGISTRATION)){
					jsonPath = ResourceConstants.EMAIL_CONFIG + ResourceConstants.REGISTRATION_PAYMENT_FAILED_JSON;
				}else if(type.equalsIgnoreCase(ResourceConstants.GEHU_REGISTRATION)){
					jsonPath = ResourceConstants.EMAIL_CONFIG + ResourceConstants.GEHU_REGISTRATION_PAYMENT_FAILED_JSON;
				}else{
					jsonPath = ResourceConstants.EMAIL_CONFIG + ResourceConstants.ONLINE_PAYMENT_FAILED_JSON;
				}
			}
			
	        JSONObject emailObject=	GEUWebUtils.getJSONData(resolverFactory, jsonPath);

			String subject=emailObject.get("subject").toString();
			String body=String.join(
					System.getProperty("line.separator"),
					emailObject.get("body").toString(),
					stringBuilder.toString()
					);

			Multipart multipart = new MimeMultipart();
			
			BodyPart bodyPart = new MimeBodyPart();
			bodyPart.setContent(body, "text/html");
			multipart.addBodyPart(bodyPart);
			awsEmailService.sendEmail(subject, multipart, email);
			}catch (MessagingException e) {
				LOGGER.error("Messaging Exception in  PaymentFormService" + e.getMessage());
			}catch (NullPointerException e) {
				 LOGGER.error("NullPointerException Exception in  PaymentFormService" + e.getMessage()); 
			}
	}

	private String getEmailId(String storedFormValue, String formType) {
		Connection connection = null;
		String email = "";
		String query = ResourceConstants.EMPTY_STRING;
		try {
		connection = getConnection();
		ResultSet resultSet = null;

		if (formType.equalsIgnoreCase("GEU")) {
			query="Select * FROM admission where FormId='" + storedFormValue + "';";
		} else if (formType.equalsIgnoreCase("GEHU")) {
			query="Select * FROM gehu_admission where FormId='" + storedFormValue + "';";
		}
		
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		
		resultSet = preparedStatement.executeQuery();
		ResultSetMetaData paymentFormData = resultSet.getMetaData();
		
		while(resultSet.next())
		{
			for (int i = 1; i <= paymentFormData.getColumnCount(); i++) {
                String paymentFormHeader = paymentFormData.getColumnName(i);
                Object paymentFormValue = resultSet.getObject(paymentFormHeader);
              
                if(null != paymentFormValue && paymentFormHeader.equals("email"))
				{
                	email = paymentFormValue.toString();
				}
			}
		}
		} catch (SQLException e) {
			LOGGER.error("SQLException in PaymentFormService" + e.getMessage());
		}
		finally {
			try
			{
				if (connection != null) {
					connection.close();
				}
			}

			catch (SQLException e) {
				LOGGER.error("SQLException in PaymentFormService" + e.getMessage());
			}
		}
		return email;
		
	}

	private void updatePaymentData(String storedFormValue, String amount, String transactionReference, String storedFeeType, String transactionResponseMessage, String formType) {
		Connection connection = null;
		String transactionAmount = amount.substring(0, amount.length() - 2);
		String query = ResourceConstants.EMPTY_STRING;
		try {
		connection = getConnection();
		if (formType.equalsIgnoreCase("GEU")) {
			query="UPDATE admission SET paymentStatus = ? , paymentMode = ?, feeAmount = ?, transactionId = ?, feePaymentType = ? WHERE FormId = ?";
		} else if (formType.equalsIgnoreCase("GEHU")) {
			query="UPDATE gehu_admission SET paymentStatus = ? , paymentMode = ?, feeAmount = ?, transactionId = ?, feePaymentType = ? WHERE FormId = ?";
		}
		
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		
		preparedStatement.setString(1, transactionResponseMessage);
		preparedStatement.setString(2, "Online");
		preparedStatement.setString(3, transactionAmount);
		preparedStatement.setString(4, transactionReference);
		preparedStatement.setString(5, storedFeeType);
		preparedStatement.setString(6, storedFormValue);
		
		preparedStatement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.error("SQLException in PaymentFormService" + e.getMessage());
		}
		finally {
			try
			{
				if (connection != null) {
					connection.close();
				}
			}

			catch (SQLException e) {
				LOGGER.error("SQLException in PaymentFormService" + e.getMessage());
			}
		}
	}
	
	private void updateRegistrationPaymentData(String storedFormValue, String amount, String transactionReference, String transactionResponseMessage, String formType) {
		Connection connection = null;
		String transactionAmount = amount.substring(0, amount.length() - 2);
		String query = ResourceConstants.EMPTY_STRING;
		try {
		connection = getConnection();
		if (formType.equalsIgnoreCase("GEU")) {
			query="UPDATE admission SET registrationPaymentStatus = ? , registrationFeeAmount = ?, registrationTranscId = ?, registrationPaymentMode = ? WHERE FormId = ?";
		} else if (formType.equalsIgnoreCase("GEHU")) {
			query="UPDATE gehu_admission SET registrationPaymentStatus = ?, registrationFeeAmount = ?, registrationTranscId = ?, registrationPaymentMode = ? WHERE FormId = ?";
		}
		
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		
		preparedStatement.setString(1, transactionResponseMessage);
		preparedStatement.setString(2, transactionAmount);
		preparedStatement.setString(3, transactionReference);
		preparedStatement.setString(4, "Online");
		preparedStatement.setString(5, storedFormValue);
		
		preparedStatement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.error("SQLException in PaymentFormService" + e.getMessage());
		}
		finally {
			try
			{
				if (connection != null) {
					connection.close();
				}
			}

			catch (SQLException e) {
				LOGGER.error("SQLException in PaymentFormService" + e.getMessage());
			}
		}
	}

	/**
	 * This Method is used to get the validId cookie
	 * @param request
	 */
	private String getCookie(SlingHttpServletRequest request)
	{
		String cookieValue=null;
		Cookie[] cookies = request.getCookies();
		if(null!=cookies)
		{
			for (int i = 0; i < cookies.length; i++)
			{
				Cookie cookie1 = cookies[i];
				if(cookie1.getName().equals("validId"))
				{
					cookieValue=cookie1.getValue();
				}
			}
		}
		return cookieValue;
	}
	
	/**
	 * This Method is used to get the registrationId cookie
	 * @param request
	 */
	private String getRegistrationCookie(SlingHttpServletRequest request, String formType)
	{
		String cookieValue=null;
		Cookie[] cookies = request.getCookies();
		if(null!=cookies)
		{
			for (int i = 0; i < cookies.length; i++)
			{
				Cookie cookie1 = cookies[i];
				if(formType.equalsIgnoreCase(ResourceConstants.GEU)){
					if(cookie1.getName().equals("registrationId"))
					{
						cookieValue=cookie1.getValue();
					}
				}else if(formType.equalsIgnoreCase(ResourceConstants.GEHU)){
					if(cookie1.getName().equals("registrationGEHUId"))
					{
						cookieValue=cookie1.getValue();
					}
				}
				
			}
		}
		return cookieValue;
	}
	
	/**
	 * This Method is used to get the geu-token cookie
	 * @param request
	 */
	private String getFeeCookie(SlingHttpServletRequest request)
	{
		String cookieValue=null;
		Cookie[] cookies = request.getCookies();
		if(null!=cookies)
		{
			for (int i = 0; i < cookies.length; i++)
			{
				Cookie cookie1 = cookies[i];
				if(cookie1.getName().equals("geu-token"))
				{
					cookieValue=cookie1.getValue();
				}
			}
		}
		return cookieValue;
	}
	
	/**
	 * This Method is used to decrypt the FormId using DES Algorithm
	 * @param str
	 */
	private String decrypt(String str) {
		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		LOGGER.info(ResourceConstants.ENTERING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);
		String decryptedString=null;
		try
		{
			
			Cipher dcipher=Cipher.getInstance("DES");

			String[] cookieArray = str.split("\\=&crypto=&");
			if(null != cookieArray)
			{
				String encryptedKey=cookieArray[0];
				String secretKey=cookieArray[1];

				byte[] decodedKey = Base64.getDecoder().decode(secretKey);
				DESKeySpec desKey = new DESKeySpec(decodedKey);
				SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
				SecretKey key = keyFactory.generateSecret(desKey);
				dcipher.init(Cipher.DECRYPT_MODE, key);

				// decode with base64 to get bytes
				byte[] decodedCookie = BASE64DecoderStream.decode(encryptedKey.getBytes());
				byte[] decryptedCookieValue = dcipher.doFinal(decodedCookie);

				decryptedString = new String(decryptedCookieValue, "UTF8");
			}
		}catch (NoSuchAlgorithmException e) {
			LOGGER.error("Algorithm Exception in "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		} catch (NoSuchPaddingException e) {
			LOGGER.error("Padding Exception in "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		} catch (InvalidKeyException e) {
			LOGGER.error("Invalid Key Exception in "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		} catch (InvalidKeySpecException e) {
			LOGGER.error("Invalid Key Spec Exception in "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Exception in "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		}
		return decryptedString;
	}	

}
