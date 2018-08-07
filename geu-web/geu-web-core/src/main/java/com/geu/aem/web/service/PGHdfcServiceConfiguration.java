package com.geu.aem.web.service;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "HDFC PG Configuration", description = "HDFC PG Configuration")
public @interface PGHdfcServiceConfiguration {

	@AttributeDefinition(name = "Merchant Id", description = "Merchant Id", 
			defaultValue="169554", type = AttributeType.STRING)
	String getMerchantId();

	@AttributeDefinition(name = "Currency", description = "Currency",
			defaultValue="INR", type=AttributeType.STRING)
	String getCurrency();

	@AttributeDefinition(name = "Redirect URL", description = "Redirect URL", 
			defaultValue="http://www.gehu.ac.in/content/gehu/en/admission-aid/payment/hdfc-pg-response/jcr:content", type = AttributeType.STRING)
	String getRedirectURL();
	
	@AttributeDefinition(name = "Registration Redirect URL", description = "Registration Redirect URL", 
			defaultValue="http://www.gehu.ac.in/content/geu/en/registration-hdfcresponse/jcr:content", type = AttributeType.STRING)
	String getRegistrationRedirectURL();

	@AttributeDefinition(name = "Language", description = "Language", 
			defaultValue="EN", type = AttributeType.STRING)
	String getLanguage();
	
	@AttributeDefinition(name = "Return URL", description = "Return URL", 
			defaultValue="http://localhost:4502/content/gehu/en/admission-aid/payment/hdfc-pg-response/jcr:content", type = AttributeType.STRING)
	String getReturnURL();
	
	@AttributeDefinition(name = "Registration Return URL", description = "Registration Return URL", 
			defaultValue="http://localhost:4502/content/geu/en/registration-hdfcresponse/jcr:content", type = AttributeType.STRING)
	String getRegistrationReturnURL();
	
	@AttributeDefinition(name = "Registration Amount", description = "Registration Amount", 
			defaultValue="1200", type = AttributeType.STRING)
	String getRegistrationAmount();
	
}
