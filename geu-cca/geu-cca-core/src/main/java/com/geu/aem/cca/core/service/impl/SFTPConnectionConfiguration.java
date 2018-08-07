package com.geu.aem.cca.core.service.impl;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "SFTP Connection Configuration", description = "Server details for SFTP Connection")
public @interface SFTPConnectionConfiguration {
	
	@AttributeDefinition(name = " SFTP Server", description = "Server detials of SFTP", 
			defaultValue="ec2-13-126-132-168.ap-south-1.compute.amazonaws.com", type = AttributeType.STRING)
	String getServer();
	
	@AttributeDefinition(name = " SFTP Username", description = "Username of SFTP", 
				defaultValue = "cca-sftp-user", type = AttributeType.STRING)
	String getUsername();
		
	@AttributeDefinition(name = " SFTP Private Key Path", description = "Path of Private Key for SFTP", 
			type = AttributeType.STRING, defaultValue = "/home/admin/SFTPFile/cca-sftp-user.ppk")
	String getPrivateKey();
	
	@AttributeDefinition(name = "Port", description = "Port for SFTP server", 
			defaultValue="22", type = AttributeType.INTEGER)
	int getPort();
	
	@AttributeDefinition(name = "Directory path", description = "Directory detials of SFTP", 
			defaultValue="/home/cca-sftp-user/GEUFolder/input_leads_data", type = AttributeType.STRING)
	String getDirectoy();	
	
	@AttributeDefinition(name = "File Name", description = "File name to be saved as in SFTP", 
			defaultValue="input_leads", type = AttributeType.STRING)
	String getFileName();
	
	@AttributeDefinition(name = "Export CSV File Directory", description = "Export File Directory as in SFTP server", 
			defaultValue="/home/cca-sftp-user/GEUFolder/output_leads_data", type = AttributeType.STRING)
	String getExportFileDirectory();
	
	@AttributeDefinition(name = "Export CSV File Name", description = "Export File name as in SFTP", 
			defaultValue="ExportLeads.csv", type = AttributeType.STRING)
	String getExportFileName();
}
