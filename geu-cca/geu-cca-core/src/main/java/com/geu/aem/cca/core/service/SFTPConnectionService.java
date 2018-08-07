package com.geu.aem.cca.core.service;

import java.io.InputStream;

public interface SFTPConnectionService {
	/**
	 * This method stores file into SFTP server by passing the InpustStream object of 
	 * the asset.
	 * @param inStream
	 * @return
	 */
	String storeFile(InputStream inStream);
	
	/*This method reads the file from FTP server*/
	void readFile();
	
	String getSFTPExportDirectory();

}
