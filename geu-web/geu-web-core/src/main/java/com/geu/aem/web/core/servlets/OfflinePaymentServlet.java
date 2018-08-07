package com.geu.aem.web.core.servlets;

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
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.Servlet;
import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.simple.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourcePool;
import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.service.HDFCPaymentService;
import com.geu.aem.web.service.AWSEmailService;
import com.geu.aem.web.service.PaymentFormService;
import com.geu.aem.web.util.GEUWebUtils;
import com.geu.aem.web.util.SQLConnectionUtil;
import com.sun.mail.util.BASE64DecoderStream;

/**
 * @author nitin.jangir
 *
 */
@Component(service = { Servlet.class },
property = { "sling.servlet.paths=" + "/bin/geu/offlinePayment",
		"sling.servlet.methods=" + "POST"})
public class OfflinePaymentServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;
	
	protected final Logger LOGGER = LoggerFactory.getLogger(OfflinePaymentServlet.class);
	
	private static final String CLASS_NAME = OfflinePaymentServlet.class.getSimpleName();
	
	@Reference
	PaymentFormService paymentFormService;
	
	@Reference
	HDFCPaymentService hdfcService;
	
	@Reference
	private DataSourcePool source;
	
	@Reference
	ResourceResolverFactory resolverFactory;
	
	@Reference
	AWSEmailService awsEmailService;

	@Override
	protected void doPost(SlingHttpServletRequest  request,
			SlingHttpServletResponse response) {
		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		LOGGER.info(ResourceConstants.ENTERING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);

		String feeType = request.getParameter(ResourceConstants.FEE_TYPE);
		String feeAmount = request.getParameter(ResourceConstants.FEE_AMOUNT);
		String formType = request.getParameter("formType");
		
		String cookieValue = getCookie(request);
		String storedFormValue = null;
		if(StringUtils.isNotBlank(cookieValue)){
			storedFormValue = decrypt(cookieValue);
		}
		if(feeType.equalsIgnoreCase(ResourceConstants.FULL_FEE)){
			feeAmount = hdfcService.fetchAmountFromFormID(storedFormValue);
		}
		
		if (StringUtils.isNotBlank(storedFormValue)) {
			updateOfflinePaymentData(storedFormValue, feeType, feeAmount, formType);
			String email = getEmailId(storedFormValue, formType);
			
			if (formType.equalsIgnoreCase("GEU")) {
				sendPaymentMail(email);
			} 
		}
		LOGGER.info(ResourceConstants.EXITING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);
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
				if(cookie1.getName().equals(ResourceConstants.VALID_ID))
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
		String decryptedCookieString = null;
		try
		{
			Cipher dcipher = Cipher.getInstance(ResourceConstants.DES);

			String[] cookiearray = str.split(ResourceConstants.COOKIE_DELIMITER);
			if(null != cookiearray)
			{
				String encryptedKey=cookiearray[0];
				String secretKey=cookiearray[1];

				byte[] decodedKey = Base64.getDecoder().decode(secretKey);
				DESKeySpec desKey = new DESKeySpec(decodedKey);
				SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ResourceConstants.DES);
				SecretKey key = keyFactory.generateSecret(desKey);
				dcipher.init(Cipher.DECRYPT_MODE, key);

				// decode with base64 to get bytes
				byte[] decodedCookieValue = BASE64DecoderStream.decode(encryptedKey.getBytes());
				byte[] decryptedCookieValue = dcipher.doFinal(decodedCookieValue);

				decryptedCookieString = new String(decryptedCookieValue, ResourceConstants.UTF8);
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
		return decryptedCookieString;
	}
	
	private void updateOfflinePaymentData(String storedFormValue, String storedFeeType, String feeAmount, String formType) {
		
		Connection connection = SQLConnectionUtil.getConnection(source);
		String query = ResourceConstants.EMPTY_STRING;
		try {
		if (formType.equalsIgnoreCase("GEU")) {
			query="UPDATE admission SET paymentMode = ? , feePaymentType = ? , feeAmount = ? WHERE FormId = ?";
		} else if (formType.equalsIgnoreCase("GEHU")) {
			query="UPDATE gehu_admission SET paymentMode = ? , feePaymentType = ? , feeAmount = ? WHERE FormId = ?";
		}
		
		PreparedStatement preparedStatement = connection.prepareStatement(query);
				
		preparedStatement.setString(1, ResourceConstants.OFFLINE);
		preparedStatement.setString(2, storedFeeType);
		preparedStatement.setString(3, feeAmount);
		preparedStatement.setString(4, storedFormValue);
		
		preparedStatement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.error("SQLException in OfflinePaymentServlet" + e.getMessage());
		}
		finally {
			try
			{
				if (connection != null) {
					connection.close();
				}
			}

			catch (SQLException e) {
				LOGGER.error("SQLException in OfflinePaymentServlet" + e.getMessage());
			}
		}
	}
	
	private String getEmailId(String storedFormValue, String formType) {
		Connection connection = SQLConnectionUtil.getConnection(source);
		String email = ResourceConstants.EMPTY_STRING;
		String query = ResourceConstants.EMPTY_STRING;
		try {
		
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;

		if (formType.equalsIgnoreCase("GEU")) {
			query = "Select * FROM admission where FormId='" + storedFormValue + "';";
		} else if (formType.equalsIgnoreCase("GEHU")) {
			query = "Select * FROM gehu_admission where FormId='" + storedFormValue + "';";
		}
		
		preparedStatement = connection.prepareStatement(query);
		
		resultSet = preparedStatement.executeQuery();
		ResultSetMetaData paymentFormData = resultSet.getMetaData();
		
		while(resultSet.next())
		{
			for (int i = 1; i <= paymentFormData.getColumnCount(); i++) {
                String paymentFormHeader = paymentFormData.getColumnName(i);
                Object paymentFormValue = resultSet.getObject(paymentFormHeader);
              
                if(null != paymentFormValue && paymentFormHeader.equals(ResourceConstants.EMAIL))
				{
                	email = paymentFormValue.toString();
				}
			}
		}
		} catch (SQLException e) {
			LOGGER.error("SQLException in OfflinePaymentServlet" + e.getMessage());
		}
		finally {
			try
			{
				if (connection != null) {
					connection.close();
				}
			}

			catch (SQLException e) {
				LOGGER.error("SQLException in OfflinePaymentServlet" + e.getMessage());
			}
		}
		return email;
	}
	
	private void sendPaymentMail(String email) {
		StringBuilder stringBuilder=new StringBuilder();
		try {
	        String jsonPath=ResourceConstants.EMAIL_CONFIG + ResourceConstants.OFFLINE_JSON;
	        JSONObject emailObject=	GEUWebUtils.getJSONData(resolverFactory, jsonPath);

			String subject=emailObject.get("subject").toString();
			String bccEmail = emailObject.get("email").toString();
			
			String body=String.join(
					System.getProperty("line.separator"),
					emailObject.get("body").toString(),
					stringBuilder.toString()
					);

			Multipart multipart = new MimeMultipart();
			
			BodyPart bodyPart = new MimeBodyPart();
			bodyPart.setContent(body, "text/html");
			multipart.addBodyPart(bodyPart);
			awsEmailService.sendEmailWithBcc(subject, multipart, email, bccEmail);
			}catch (MessagingException e) {
				LOGGER.error("Messaging Exception in  OfflinePaymentServlet" + e.getMessage());
			}catch (NullPointerException e) {
				 LOGGER.error("NullPointerException Exception in  OfflinePaymentServlet" + e.getMessage()); 
			}
	}
	
	
}
