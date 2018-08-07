package com.geu.aem.web.service;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Axis PG Configuration", description = "Axis PG Configuration")
public @interface PGAxisServiceConfiguration {

	@AttributeDefinition(name = "Version", description = "Version", 
			defaultValue="1", type = AttributeType.STRING)
	String getVersion();

	@AttributeDefinition(name = "Merchant AccessCode", description = "Merchant AccessCode",
			defaultValue="IKFM2423", type=AttributeType.STRING)
	String getMerchantAccessCode();

	@AttributeDefinition(name = "Command Type", description = "Command Type", 
			defaultValue="pay", type = AttributeType.STRING)
	String getCommandType();

	@AttributeDefinition(name = "MerchantID", description = "MerchantID", 
			defaultValue="TESTGRAPHIMERCH", type = AttributeType.STRING)
	String getMerchantID();

	@AttributeDefinition(name = "Receipt ReturnURL", description = "Receipt ReturnURL", 
			defaultValue="http://localhost:4502/content/geu/en/axis-pg-response/jcr:content", type = AttributeType.STRING)
	String getReceiptReturnURL();
	
	@AttributeDefinition(name = "Registration Receipt ReturnURL", description = "Registration Receipt ReturnURL", 
			defaultValue="http://localhost:4502/content/geu/en/registrationresponsepage/jcr:content", type = AttributeType.STRING)
	String getRegistrationReceiptReturnURL();
	
}
