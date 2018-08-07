package com.geu.aem.web.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.service.SMSApplicationService;
import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.BASE64EncoderStream;

@Component(service= {SMSApplicationService.class},immediate=true)
@Designate(ocd = SMSApplicationServiceConfiguration.class)
public class SMSApplicationServiceImpl implements SMSApplicationService{

	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	private static final String CLASS_NAME = SMSApplicationServiceImpl.class.getSimpleName();
	private SMSApplicationServiceConfiguration smsConfiguration;

	@Activate
	@Modified
	public void activate(SMSApplicationServiceConfiguration smsConfiguration){
		this.smsConfiguration = smsConfiguration;		
	}

	/**
	 * This Method is used to send the OTP to the number given
	 * in admission form using textlocal api
	 * @param otp
	 * @param phoneNumber
	 */
	public String sendOTP(String otp,String phoneNumber) {

		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		LOGGER.info(ResourceConstants.ENTERING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);

		String url="";
		String data="";

		if(StringUtils.isNotBlank(smsConfiguration.getAPIKey()) && StringUtils.isNotBlank(smsConfiguration.getOTPTemplate()) 
				&& StringUtils.isNotBlank(smsConfiguration.getSendURL()))
		{
			String apiKey = "apikey=" + smsConfiguration.getAPIKey();
			url=smsConfiguration.getSendURL();

			String templates=getTemplates();
			Map<String,String> templateMap=new LinkedHashMap<String, String>();
			templateMap=getTemplatesJsonMap(templates,smsConfiguration.getOTPTemplate());
			if(templateMap.size()!=0)
			{
				String templateTitle=templateMap.get("title");
				String senderName=templateMap.get("senderName");
				String templateBody=templateMap.get("body");

				Pattern pattern = Pattern.compile("\\%\\%\\|OTP.*\\%\\%");
				Matcher match = pattern.matcher(templateBody);
				templateBody=match.replaceAll(otp);				
				
				if(StringUtils.isNotBlank(templateTitle) && StringUtils.isNotBlank(senderName) && StringUtils.isNotBlank(templateBody))
				{
					String template = "&template=" + templateTitle;
					String sender = "&sender=" + senderName;
					String message= "&message="+ templateBody;
					String numbers = "&numbers=" + phoneNumber;
					data = apiKey + numbers + message + sender + template;
				}else
				{
					LOGGER.error("Please check the SMS configuration in Felix Console");
				}
			}
			else
			{
				LOGGER.error("Templates JSON Map Empty in "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME);
			}
		}
		else
		{
			LOGGER.error("Please check the SMS configuration in Felix Console");
		}
		LOGGER.info(ResourceConstants.EXITING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);
		
		return getSMSServiceResponse(data, url);
	}

	/**
	 * This Method is used to generate secure OTP
	 * @param otpLength
	 */
	public String generateSecureOTP(){
		int otpLength=6;
		
		if(smsConfiguration.getOTPLength()!=0)
		{
			otpLength=smsConfiguration.getOTPLength();
		}
		
		String otp = new String();
		int otpSample=0;
		for(int i=0;i<otpLength;i++){
			otp=otp+"9";
		}

		otpSample=Integer.parseInt(otp);

		//Number Generation Algorithm
		SecureRandom secureRandom=new SecureRandom();
		try {
			LOGGER.info("Algorithm used"+secureRandom.getAlgorithm());
			otp = new Integer(secureRandom.nextInt(otpSample)).toString();
			otp = (otp.length() < otpLength) ? padleft(otp, otpLength, '0') : otp;
		} catch (Exception e) {
			LOGGER.error("Error in "+CLASS_NAME + e.getMessage());
		}
		return otp;
	}

	/**
	 * This Method is used to pad characters to make more secure
	 * @param request
	 */
	private static String padleft(String otp, int otpLength, char padding) { 
		otp = otp.trim();
		StringBuffer stringBuffer = new StringBuffer(otpLength);
		int fill = otpLength - otp.length();
		while (fill-- > 0)
			stringBuffer.append(padding);
		stringBuffer.append(otp);
		return stringBuffer.toString();
	}

	/**
	 * This Method is used to verify user entered OTP
	 * @param request
	 */
	public boolean verifyOTP(SlingHttpServletRequest request) {
		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		LOGGER.info(ResourceConstants.ENTERING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);

		boolean isOTPvalid=false;
		String otp = request.getParameter("otpcode");

		String cookieValue=getCookie(request);
		String storedOTPValue=null;
		if(StringUtils.isNotBlank(cookieValue)){
			storedOTPValue=decrypt(cookieValue);
		}

		if(storedOTPValue.equals(otp)){
			isOTPvalid = true;
		}
		LOGGER.info(ResourceConstants.EXITING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);

		return isOTPvalid;
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
			String delimiter="=&crypto=&";
			String encodedSecretKey = Base64.getEncoder().encodeToString(key.getEncoded());
			String cookieValue=encryptedOTP+delimiter+encodedSecretKey;

			LOGGER.info(ResourceConstants.EXITING + ResourceConstants.SPACE + METHOD_NAME
					+ ResourceConstants.SPACE+CLASS_NAME);

			return cookieValue;
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("Algorithm Exception in "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		} catch (NoSuchPaddingException e) {
			LOGGER.error("Padding Exception in "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		} catch (InvalidKeyException e) {
			LOGGER.error("Invalid Key Exception in "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Exception in "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		}
		return null;
	}

	/**
	 * This Method is used to decrypt the OTP using DES Algorithm
	 * @param str
	 */
	private String decrypt(String str) {
		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		LOGGER.info(ResourceConstants.ENTERING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);
		try
		{
			String decryptedString=null;
			Cipher dcipher=Cipher.getInstance("DES");

			String[] arr=str.split("\\=&crypto=&");
			if(null!=arr)
			{
				String encryptedKey=arr[0];
				String secretKey=arr[1];

				byte[] decodedKey = Base64.getDecoder().decode(secretKey);
				DESKeySpec desKey = new DESKeySpec(decodedKey);
				SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
				SecretKey key = keyFactory.generateSecret(desKey);
				dcipher.init(Cipher.DECRYPT_MODE, key);

				// decode with base64 to get bytes
				byte[] dec = BASE64DecoderStream.decode(encryptedKey.getBytes());
				byte[] utf8 = dcipher.doFinal(dec);

				decryptedString=new String(utf8, "UTF8");
			}
			return decryptedString;
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
		return null;
	}

	/**
	 * This Method is used to create HttpOnly cookie
	 * @param request
	 */
	private Cookie createCookie(SlingHttpServletRequest request,SlingHttpServletResponse response, String code)
	{
		Cookie cookie = new Cookie("validtoken", code);
		cookie.setMaxAge(180);
		//cookie.setPath(";Path=/;HttpOnly;");
		cookie.setPath("/");
		return cookie;
	}

	/**
	 * This Method is used to get the validtoken cookie
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
				if(cookie1.getName().equals("validtoken"))
				{
					cookieValue=cookie1.getValue();
				}
			}
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
				if(cookie1.getName().equals("validtoken"))
				{
					cookie1.setMaxAge(0);
				}
			}
		}
	}

	/**
	 * This Method is called after sending OTP to encrypt OTP
	 * value and to create a cookie
	 * @param response
	 * @param otp
	 */
	public SlingHttpServletResponse processSendOTP(SlingHttpServletRequest request,SlingHttpServletResponse response,String otp) {
		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		LOGGER.info(ResourceConstants.ENTERING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);
		
		String encryptedString=encrypt(otp);
		deleteCookieIfPresent(request);
		Cookie cookie=createCookie(request,response, encryptedString);
		response.addCookie(cookie);

		LOGGER.info(ResourceConstants.EXITING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);
		return response;
	}

	/**
	 * This Method is used to send the message to the number given
	 * in admission form using textlocal api
	 * @param otp
	 * @param phoneNumber
	 */
	public String sendMessage(String formId,String phoneNumber,boolean hasVariable) {
		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		LOGGER.info(ResourceConstants.ENTERING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);

		String data="";
		String url="";

		if(StringUtils.isNotBlank(smsConfiguration.getAPIKey()) && StringUtils.isNotBlank(smsConfiguration.getAdmissionFormTemplate()) 
				&& StringUtils.isNotBlank(smsConfiguration.getSendURL()) && StringUtils.isNotBlank(smsConfiguration.getSubmissionTemplate()))
		{
			String apiKey = "apikey=" + smsConfiguration.getAPIKey();
			String numbers = "&numbers=" + phoneNumber;
			url=smsConfiguration.getSendURL();

			String templates=getTemplates();
			Map<String,String> templateMap=new LinkedHashMap<String, String>();
			
			if(hasVariable)
			{
				templateMap=getTemplatesJsonMap(templates,smsConfiguration.getAdmissionFormTemplate());
			}
			else
			{
				templateMap=getTemplatesJsonMap(templates,smsConfiguration.getSubmissionTemplate());
			}

			if(templateMap.size()!=0)
			{
				String templateTitle=templateMap.get("title");
				String senderName=templateMap.get("senderName");
				String templateBody=templateMap.get("body");
				
				if(hasVariable)
				{
					Pattern pattern = Pattern.compile("\\%\\%\\|FORMID.*\\%\\%");
					//Pattern pattern = Pattern.compile("\\%\\%\\|FORMID.*\\%\\%");
					Matcher match = pattern.matcher(templateBody);
					templateBody=match.replaceAll(formId);
				}
				

				if(StringUtils.isNotBlank(templateTitle) && StringUtils.isNotBlank(senderName) && StringUtils.isNotBlank(templateBody))
				{
					String template = "&template=" + templateTitle;
					String sender = "&sender=" + senderName;
					String message= "&message="+ templateBody;
					data = apiKey + numbers + message + sender + template;
				}
				else
				{
					LOGGER.error("Template or SenderName or Message from textlocal API is Blank");
				}
			}
		}
		else
		{
			LOGGER.error("API Key or Message Template not configured in Felix Console"+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME);
		}

		LOGGER.info(ResourceConstants.EXITING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);
		return getSMSServiceResponse(data, url);
	}

	/**
	 * This Method is used to get the templates in textlocal API
	 */
	private String getTemplates() {

		String url="";
		String data="";
		if(StringUtils.isNotBlank(smsConfiguration.getAPIKey()) && StringUtils.isNotBlank(smsConfiguration.getTemplatesURL()))
		{
			url=smsConfiguration.getTemplatesURL();
			data = "apikey=" + smsConfiguration.getAPIKey();
		}
		else
		{
			LOGGER.error("API Key or Get Templates URL is not configured in Felix Console");
		}

		return getSMSServiceResponse(data, url);
	}

	/**
	 * This Method is used to get the JSON Map after parsing the response
	 * @param response
	 * @param key
	 * @param templateName
	 */
	private Map<String,String> getTemplatesJsonMap(String response,String templateName)
	{
		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		LOGGER.info(ResourceConstants.ENTERING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);

		boolean isTemplateValid=false;
		Map<String,String> jsonMap=new LinkedHashMap<String, String>();
		JSONParser parser = new JSONParser();
		JSONObject jsonObject;
		
		if(StringUtils.isNotBlank(response))
		{
			try {
				jsonObject = (JSONObject) parser.parse(response);
				JSONArray jsonArray = (JSONArray) jsonObject.get("templates");
				Iterator iterator = jsonArray.iterator();

				while (iterator.hasNext()) {
					JSONObject item = (JSONObject) iterator.next();
					String title = item.get("title").toString();
					if(title.equals(templateName))
					{
						isTemplateValid=true;
						jsonMap.put("title",title);
						jsonMap.put("senderName",item.get("senderName").toString());
						jsonMap.put("body",item.get("body").toString());
					}
				}
			} catch (ParseException e) {
				LOGGER.error("JSON Parse Exception in "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME);
			} catch (Exception e) {
				LOGGER.error("Exception in "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME);
			}
		}
		else
		{
			LOGGER.error("JSON Response Empty in "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME);
		}

		if(!isTemplateValid)
		{
			LOGGER.error("Template Not Found "+templateName);
		}
		LOGGER.info(ResourceConstants.EXITING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);
		LOGGER.info("jsonMap ::::"+jsonMap.toString());
		return jsonMap;
	}

	/**
	 * This Method is used to send request to the textlocal service and get the response
	 * @param data
	 * @param url
	 */
	private String getSMSServiceResponse(String data,String url)
	{
		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		LOGGER.info(ResourceConstants.ENTERING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);

		String response=null;
		HttpURLConnection connection=null;
		try {
			// Send data
			if(StringUtils.isNotBlank(data) && StringUtils.isNotBlank(url))
			{
				connection = (HttpURLConnection) new URL(url).openConnection();
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Length", Integer.toString(data.length()));
				connection.getOutputStream().write(data.getBytes("UTF-8"));
				final BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				final StringBuffer stringBuffer = new StringBuffer();
				String line;
				while ((line = rd.readLine()) != null) {
					stringBuffer.append(line);
				}
				rd.close();
				response=stringBuffer.toString();
			}
			else
			{
				LOGGER.error("Data or URL is Blank in "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME);
			}
		} catch (MalformedURLException e) {
			LOGGER.error("Error in Textlocal URL "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		} catch (IOException e) {
			LOGGER.error("Unable to Connect to the Textlocal Service "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Exception in "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		} finally{
			connection.disconnect();
		}
		LOGGER.info(ResourceConstants.EXITING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);
		return response;
	}


}
