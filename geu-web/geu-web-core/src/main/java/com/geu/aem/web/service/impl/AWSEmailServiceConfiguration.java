package com.geu.aem.web.service.impl;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "AWS SES Configuration", description = "AWS Email Service Configuration")
public @interface AWSEmailServiceConfiguration {

	@AttributeDefinition(name = "From Address", description = "From Address", 
			defaultValue="geuemail@gmail.com", type = AttributeType.STRING)
	String getFromAddress();
	
	@AttributeDefinition(name = "From Name", description = "From Name", 
			defaultValue="Graphic Era University", type = AttributeType.STRING)
	String getFromName();
	
	@AttributeDefinition(name = "SMTP Username", description = "SMTP Username", 
			defaultValue="AKIAIQSDWO5KEGRQAF3A", type = AttributeType.STRING)
	String getSMTPUsername();
	
	@AttributeDefinition(name = "SMTP Password", description = "SMTP Password", 
			defaultValue="Aotl5U5fEVgMlfnVyJrRb303Dari3jyeYy8SFjeZDwip", type = AttributeType.PASSWORD)
	String getSMTPPassword();
	
	@AttributeDefinition(name = "Host Name", description = "Host Name", 
			defaultValue="email-smtp.us-east-1.amazonaws.com", type = AttributeType.STRING)
	String getHostName();

	@AttributeDefinition(name = "Port Number", description = "Port Number",
			defaultValue="465", type=AttributeType.INTEGER)
	int getPortNumber();

}
