package com.geu.aem.cca.core.service.impl;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "MySQL Database Related Configuration", description = "MySQL Database Related Configuration")
public @interface MySQLDatabaseConfiguration {

	@AttributeDefinition(name = "Name of the University", description = "Name of the University", 
			defaultValue="Graphic Era University", type = AttributeType.STRING)
	String getUniversityName();
	
	
}
