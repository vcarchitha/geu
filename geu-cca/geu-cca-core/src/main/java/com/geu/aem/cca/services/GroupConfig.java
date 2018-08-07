package com.geu.aem.cca.services;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * @author aimen.sania
 * 
 */
@ObjectClassDefinition(name = "GEU-CCA Group Configuration", description = "Author group prefix")
public @interface GroupConfig {

	@AttributeDefinition(name = "Group Prefix", description = "Author only geu-cca group prefix", defaultValue = {
			"geu-cca-callers", "geu-cca-admins" })
	String[] getGroupPrefix();

}
