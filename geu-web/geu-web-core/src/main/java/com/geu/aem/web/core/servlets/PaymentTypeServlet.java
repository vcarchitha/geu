package com.geu.aem.web.core.servlets;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.servlet.Servlet;
import javax.servlet.http.Cookie;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.service.impl.SMSApplicationServiceImpl;
import com.sun.mail.util.BASE64EncoderStream;

/**
 * @author nitin.jangir
 *
 */
@Component(service = { Servlet.class },
property = { "sling.servlet.paths=" + "/bin/geu/feePaymentType",
		"sling.servlet.methods=" + "POST"})
public class PaymentTypeServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;
	
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private static final String CLASS_NAME = SMSApplicationServiceImpl.class.getSimpleName();
	
	@Override
	protected void doPost(SlingHttpServletRequest  request,
			SlingHttpServletResponse response) {
		String feeType = request.getParameter("paymentType");
		String encryptedString = encrypt(feeType);
		deleteCookieIfPresent(request);
		Cookie cookie = createCookie(request,response, encryptedString);
		response.addCookie(cookie);	
	}
	
	/**
	 * This Method is used to encrypt the feeType using DES Algorithm
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
				if(cookie1.getName().equals("geu-token"))
				{
					cookie1.setMaxAge(0);
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
		Cookie cookie = new Cookie("geu-token", code);
		cookie.setMaxAge(3600);
		cookie.setPath(";Path=/;HttpOnly;");
		return cookie;
	}

}
