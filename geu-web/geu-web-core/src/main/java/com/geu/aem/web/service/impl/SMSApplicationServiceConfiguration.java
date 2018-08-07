package com.geu.aem.web.service.impl;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "SMS Configuration", description = "SMS Application Service Configuration")
public @interface SMSApplicationServiceConfiguration {

	@AttributeDefinition(name = "API Key", description = "SMS Service API Key", 
			defaultValue="cagV3cWqalw-7H1OgNINQSgtBNyMYi2fJEgzdXt6ep", type = AttributeType.STRING)
	String getAPIKey();

	@AttributeDefinition(name = "OTP Length", description = "OTP Length",
			defaultValue="6", type=AttributeType.INTEGER)
	int getOTPLength();

	@AttributeDefinition(name = "OTP Template", description = "OTP Template used in Textlocal API", 
			defaultValue="Graphic Era University", type = AttributeType.STRING)
	String getOTPTemplate();

	@AttributeDefinition(name = "Send URL", description = "Textlocal API Send URL", 
			defaultValue="https://api.textlocal.in/send/?", type = AttributeType.STRING)
	String getSendURL();

	@AttributeDefinition(name = "Get Templates URL", description = "Textlocal API Get Templates URL", 
			defaultValue="https://api.textlocal.in/get_templates/?", type = AttributeType.STRING)
	String getTemplatesURL();

	@AttributeDefinition(name = "Admission Form Template", description = "Admission Form Template used in Textlocal API", 
			defaultValue="Graphic Era Admission Template", type = AttributeType.STRING)
	String getAdmissionFormTemplate();
	
	@AttributeDefinition(name = "Admission Submission Template", description = "Admission Form Submission Template used in Textlocal API", 
			defaultValue="Graphic Era Admission Form Submission", type = AttributeType.STRING)
	String getSubmissionTemplate();

}
