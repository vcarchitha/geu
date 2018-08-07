package com.geu.aem.web.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.service.AWSEmailService;

@Component(service= {AWSEmailService.class},immediate=true)
@Designate(ocd = AWSEmailServiceConfiguration.class)
public class AWSEmailServiceImpl implements AWSEmailService{

	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	private static final String CLASS_NAME = AWSEmailServiceImpl.class.getSimpleName();
	private AWSEmailServiceConfiguration emailConfiguration;

	@Activate
	@Modified
	public void activate(AWSEmailServiceConfiguration emailConfiguration){
		this.emailConfiguration = emailConfiguration;		
	}

	/**
	 * This Method is used to send the mail using Amazon SES
	 * @param subject
	 * @param multipart
	 * @param toAddress
	 */
	public void sendEmail(String subject,Multipart multipart, String toAddress) {

		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		LOGGER.info(ResourceConstants.ENTERING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);

		Transport transport=null ;

		try {

			if(StringUtils.isNotBlank(emailConfiguration.getFromAddress()) && StringUtils.isNotBlank(emailConfiguration.getFromName()) 
					&& StringUtils.isNotBlank(emailConfiguration.getSMTPUsername()) && StringUtils.isNotBlank(emailConfiguration.getSMTPPassword()) 
					&& StringUtils.isNotBlank(emailConfiguration.getHostName()))
			{

				String fromAddress=emailConfiguration.getFromAddress();
				String fromName=emailConfiguration.getFromName();
				String smtpUsername=emailConfiguration.getSMTPUsername();
				String smtpPassword=emailConfiguration.getSMTPPassword();
				String hostname=emailConfiguration.getHostName();
				int port=emailConfiguration.getPortNumber();


				// Create a Properties object to contain connection configuration information.
				Properties props = System.getProperties();
				props.put("mail.transport.protocol", "smtp");
				props.put("mail.smtp.port", port); 
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.ssl.enable", "true");

				// Create a Session object to represent a mail session with the specified properties. 
				Session session = Session.getDefaultInstance(props);
				session.setDebug(true);

				// Create a message with the specified information. 
				MimeMessage message = new MimeMessage(session);
				InternetAddress[] emailToAddresses = InternetAddress.parse(toAddress , true);

				message.setFrom(new InternetAddress(fromAddress,fromName));
				message.setRecipients(Message.RecipientType.TO, emailToAddresses);
				message.setSubject(subject);

				if(null!=multipart)
				{
					message.setContent(multipart);
				}
				transport = session.getTransport();

				// Send the message.
				LOGGER.info("Sending...");

				// Connect to Amazon SES using the SMTP username and password you specified above.
				transport.connect(hostname, smtpUsername, smtpPassword);

				// Send the email.
				transport.sendMessage(message, message.getAllRecipients());
				LOGGER.info("Email Sent Successfully...");

			} 
		}	catch (UnsupportedEncodingException e) {
			LOGGER.error("Invalid Email Address "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		} catch (AuthenticationFailedException e) {
			LOGGER.error("Authentication Failure "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		} catch (SendFailedException e) {
			LOGGER.error("Failed to send Email because of Invalid Address "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		} catch (MessagingException e) {
			LOGGER.error("Unable to connect to the service "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		} catch (Exception e) {
			LOGGER.error("The email was not sent.");
			LOGGER.error("Exception in "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		}
		finally
		{
			// Close and terminate the connection.
			try {
				transport.close();
			} catch (MessagingException e) {
				LOGGER.error("Unable to close the connection "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
			}
		}

		LOGGER.info(ResourceConstants.EXITING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);
	}
	
	/**
	 * This Method is used to send the mail using Amazon SES with Bcc
	 * @param subject
	 * @param multipart
	 * @param toAddress
	 */
	public void sendEmailWithBcc(String subject,Multipart multipart, String toAddress, String bccAddress) {

		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		LOGGER.info(ResourceConstants.ENTERING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);

		Transport transport=null ;

		try {

			if(StringUtils.isNotBlank(emailConfiguration.getFromAddress()) && StringUtils.isNotBlank(emailConfiguration.getFromName()) 
					&& StringUtils.isNotBlank(emailConfiguration.getSMTPUsername()) && StringUtils.isNotBlank(emailConfiguration.getSMTPPassword()) 
					&& StringUtils.isNotBlank(emailConfiguration.getHostName()))
			{

				String fromAddress=emailConfiguration.getFromAddress();
				String fromName=emailConfiguration.getFromName();
				String smtpUsername=emailConfiguration.getSMTPUsername();
				String smtpPassword=emailConfiguration.getSMTPPassword();
				String hostname=emailConfiguration.getHostName();
				int port=emailConfiguration.getPortNumber();


				// Create a Properties object to contain connection configuration information.
				Properties props = System.getProperties();
				props.put("mail.transport.protocol", "smtp");
				props.put("mail.smtp.port", port); 
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.ssl.enable", "true");

				// Create a Session object to represent a mail session with the specified properties. 
				Session session = Session.getDefaultInstance(props);
				session.setDebug(true);

				// Create a message with the specified information. 
				MimeMessage message = new MimeMessage(session);
				InternetAddress[] emailToAddresses = InternetAddress.parse(toAddress , true);
				InternetAddress[] emailBccAddresses = InternetAddress.parse(bccAddress , true);
				
				message.setFrom(new InternetAddress(fromAddress,fromName));
				message.setRecipients(Message.RecipientType.TO, emailToAddresses);
				message.setRecipients(Message.RecipientType.BCC, emailBccAddresses);
				message.setSubject(subject);

				if(null!=multipart)
				{
					message.setContent(multipart);
				}
				transport = session.getTransport();

				// Send the message.
				LOGGER.info("Sending...");

				// Connect to Amazon SES using the SMTP username and password you specified above.
				transport.connect(hostname, smtpUsername, smtpPassword);

				// Send the email.
				transport.sendMessage(message, message.getAllRecipients());
				LOGGER.info("Email Sent Successfully...");

			} 
		}	catch (UnsupportedEncodingException e) {
			LOGGER.error("Invalid Email Address "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		} catch (AuthenticationFailedException e) {
			LOGGER.error("Authentication Failure "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		} catch (SendFailedException e) {
			LOGGER.error("Failed to send Email because of Invalid Address "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		} catch (MessagingException e) {
			LOGGER.error("Unable to connect to the service "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		} catch (Exception e) {
			LOGGER.error("The email was not sent.");
			LOGGER.error("Exception in "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		}
		finally
		{
			// Close and terminate the connection.
			try {
				transport.close();
			} catch (MessagingException e) {
				LOGGER.error("Unable to close the connection "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
			}
		}

		LOGGER.info(ResourceConstants.EXITING + ResourceConstants.SPACE + METHOD_NAME
				+ ResourceConstants.SPACE+CLASS_NAME);
	}

}
