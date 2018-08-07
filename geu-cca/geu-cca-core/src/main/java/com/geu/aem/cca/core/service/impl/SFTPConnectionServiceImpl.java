package com.geu.aem.cca.core.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.cca.constants.ResourceConstants;
import com.geu.aem.cca.core.service.SFTPConnectionService;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

@Component(service = SFTPConnectionService.class, immediate = true)
@Designate(ocd = SFTPConnectionConfiguration.class)
public class SFTPConnectionServiceImpl implements SFTPConnectionService {

	private static Logger LOGGER = LoggerFactory
			.getLogger(SFTPConnectionServiceImpl.class);

	private SFTPConnectionConfiguration sftpConfig;

	private ChannelSftp getConnection() {
		// TODO Auto-generated method stub
		ChannelSftp sftpChannel = null;
		Session session = null;
		try {
			LOGGER.info("in getconenction  >>>" + sftpConfig.getServer()
					+ sftpConfig.getUsername());
			JSch jsch = new JSch();
			String user = sftpConfig.getUsername();
			String host = sftpConfig.getServer();
			int port = sftpConfig.getPort();
			String privateKey = sftpConfig.getPrivateKey();			
			jsch.addIdentity(privateKey);
			session = jsch.getSession(user, host, port);		
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			sftpChannel = (ChannelSftp) session.openChannel("sftp");
			sftpChannel.connect();			
			
		} catch (Exception e) {
			LOGGER.error("Exception from getConnection method of "
					+ "SFTPConnectionServiceImpl class" + e.getMessage());
		} 
		
		return sftpChannel;	
	}

	public String storeFile(InputStream inStream) {
		// TODO Auto-generated method stub
		ChannelSftp sftpChannel =null;
		String timeStamp = new SimpleDateFormat("ddMMYYYY_HHmmss")
				.format(Calendar.getInstance().getTime());
		try {
			sftpChannel = getConnection();
			String directory = sftpConfig.getDirectoy() + "/"
					+ sftpConfig.getFileName() + "_" + timeStamp + ".csv";
			mkdirIfNotExists(sftpChannel, sftpConfig.getDirectoy());
			sftpChannel.put(inStream, directory);
		} catch (SftpException e) {
			LOGGER.info("SftpException occured in storeFile method"
					+ e.getMessage());
		} finally {			
			if(sftpChannel!=null){
				if(sftpChannel.isConnected()){
					sftpChannel.exit();
				}
			}
		}
		return null;
	}

	public void mkdirIfNotExists(ChannelSftp channel, final String directory)
			throws SftpException {
		try {
			channel.ls(directory);
		} catch (SftpException e) {
			channel.mkdir(directory);
		}
	}

	public void readFile() {
		LOGGER.info("Inside readFile of SFTP!!!");
		// TODO Auto-generated method stub
		BufferedInputStream bufferedInputStream = null;
		BufferedOutputStream bufferedOutStream = null;
		ChannelSftp sftpChannel =null;
		try {
			sftpChannel = getConnection();
			String directory = getSFTPExportDirectory();
			bufferedInputStream = new BufferedInputStream(
					sftpChannel.get(directory));
			byte[] buffer = new byte[1024];
			File newFile = new File(ResourceConstants.EXPORT_CSV_PATH+"/"+ResourceConstants.EXPORT_CSV_FILENAME);
			OutputStream outputStream = new FileOutputStream(newFile);
            bufferedOutStream = new BufferedOutputStream(outputStream);
            int readCount;
            while ((readCount = bufferedInputStream.read(buffer)) > 0) {
                bufferedOutStream.write(buffer, 0, readCount);
            }
            
		} catch (SftpException e) {
			LOGGER.info("SftpException occured in readFile method"
					+ e.getMessage());
		} catch (FileNotFoundException e) {
			LOGGER.info("FileNotFoundException occured in readFile method"
					+ e.getMessage());
		} catch (IOException e) {
			LOGGER.info("IOException occured in readFile method"
					+ e.getMessage());
		} finally{
			try {
				bufferedInputStream.close();
				bufferedOutStream.close();
				
				if(sftpChannel!=null){
					if(sftpChannel.isConnected()){
						sftpChannel.exit();
					}
				}
			} catch (IOException e) {
				LOGGER.info("IOException occured in readFile method "
						+ e.getMessage());
			}
            
		}

	}

	public String getSFTPExportDirectory() {
		String exportFilePath = sftpConfig.getExportFileDirectory() + "/"
				+ sftpConfig.getExportFileName();
		LOGGER.info("exportFilePath :::" + exportFilePath);
		return exportFilePath;
	}

	@Activate
	@Modified
	public void activate(SFTPConnectionConfiguration sftpConfig) {
		this.sftpConfig = sftpConfig;
	}

}
