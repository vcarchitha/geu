package com.geu.aem.web.service;

import javax.mail.Multipart;

public interface AWSEmailService {

	public void sendEmail(String subject, Multipart part, String toAddress);
	
	public void sendEmailWithBcc(String subject,Multipart multipart, String toAddress, String bccAddress);
}
