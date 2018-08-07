package com.geu.aem.web.core.service.impl;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "GEU Captcha Service", description = "ReCaptcha verification service based on Google implementation")
public @interface ReCaptchaServiceConfiguration {
	
	@AttributeDefinition(name = "Private Key", defaultValue="6LeCll4UAAAAAKunYfdvJ9dfoys3Z0FzM5cDLPFK", type = AttributeType.STRING, description = "Private ReCaptcha Key")
	String privateKey();
	
	@AttributeDefinition(name = "Site Key", defaultValue="6LeCll4UAAAAAAnueG7F71Tq9FCgtxFhked8riW0", type = AttributeType.STRING, description = "Site ReCaptcha Key")
	String siteKey();
	
	@AttributeDefinition(name = "Verification URL", defaultValue="https://www.google.com/recaptcha/api/siteverify", type = AttributeType.STRING, description = "ReCaptcha Verification URL")
	String verificationURL();


}
