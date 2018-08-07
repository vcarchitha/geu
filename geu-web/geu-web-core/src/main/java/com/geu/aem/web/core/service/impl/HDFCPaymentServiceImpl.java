package com.geu.aem.web.core.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourceNotFoundException;
import com.day.commons.datasource.poolservice.DataSourcePool;
import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.service.HDFCPaymentService;
import com.geu.aem.web.service.PGHdfcService;

@Component(service = HDFCPaymentService.class, immediate = true)
public class HDFCPaymentServiceImpl implements HDFCPaymentService{
	
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Reference
	private DataSourcePool source;
	
	@Reference
	private PGHdfcService pgHdfcService;

	/**
	 * Returns connection using the configured DataSourcePool
	 * @return connection
	 */
	private Connection getConnection() {
		Connection connection = null;
		try {
			DataSource dataSource = (DataSource) source
					.getDataSource(ResourceConstants.DATASOURCE_NAME);
			connection = dataSource.getConnection();
			LOGGER.info("connection:: " + connection);			

		} catch (SQLException e) {
			LOGGER.error("SQLException occurred in getConnection method "
					+ e.getMessage());
		} catch (DataSourceNotFoundException e) {
			LOGGER.error("DataSourceNotFoundException occurred in getConnection method "
					+ e.getMessage());
		}
		return connection;
	}
	
	public String fetchAmountFromFormID(String formID) {
		// TODO Auto-generated method stub
		String amount = null;
		Connection connection = null;
		try{
			String checkAmount = "SELECT totalFees FROM gehu_admission WHERE formId = '"
					+ formID + "'";
			
			LOGGER.info("checkAmount::::" + checkAmount);
			connection = getConnection();
			PreparedStatement checkPreparedStmt = connection
					.prepareStatement(checkAmount);
			ResultSet result = checkPreparedStmt.executeQuery(checkAmount);
			
			if (result.next()) {
				amount = result.getString("totalFees");							
			}
		}catch(Exception e){
			LOGGER.error("Exception occurred in fetchAmountFromFormID :"+e.getMessage());
		}finally{
			try {
				if(connection!=null){
					connection.close();
				}
			} catch (SQLException e) {
				LOGGER.error("SQLException occurred in insertLoginAdmissionData method "
						+ e.getMessage());
			}
		}
		return amount;
	}

	public Map<String, String> fetchHDFCConfigFromFormID(String formID, String selectionMode, String feeType, String partialAmount) {
		// TODO Auto-generated method stub
		Map <String,String> hdfcConfigMap = new HashMap<String,String>();
		String amount = null;
		String redirectURL = null;
		String cancelURL = null;
		String merchantID = pgHdfcService.getMerchantId();
		if(StringUtils.isNotBlank(merchantID)){
			hdfcConfigMap.put("merchant_id", merchantID);
		}
		String uuid = UUID.randomUUID().toString();
		String orderId = uuid.substring(0, uuid.length() - 8);
		if(StringUtils.isNotBlank(orderId)){
			hdfcConfigMap.put("order_id", orderId);
		}
		String currency = pgHdfcService.getCurrency();
		if(StringUtils.isNotBlank(currency)){
			hdfcConfigMap.put("currency", currency);
		}
		
		if(selectionMode.equalsIgnoreCase(ResourceConstants.SELECTION_MODE_REGISTRATION)){
			amount = pgHdfcService.getRegistrationAmount();
			redirectURL = pgHdfcService.getRegistrationRedirectURL();
			cancelURL = pgHdfcService.getRegistrationReturnURL();
		}else{
			if(feeType.equalsIgnoreCase(ResourceConstants.PARTIAL_FEE)){
				amount = partialAmount;
			}else{
				amount = fetchAmountFromFormID(formID);
			}			
			redirectURL = pgHdfcService.getRedirectURL();
			cancelURL = pgHdfcService.getReturnURL();
		}
		
		if(StringUtils.isNotBlank(amount)){
			hdfcConfigMap.put("amount", amount);
		}
		
		if(StringUtils.isNotBlank(redirectURL)){
			hdfcConfigMap.put("redirect_url", redirectURL);
		}		
		
		if(StringUtils.isNotBlank(cancelURL)){
			hdfcConfigMap.put("cancel_url", cancelURL);
		}
		String language = pgHdfcService.getLanguage();
		if(StringUtils.isNotBlank(language)){
			hdfcConfigMap.put("language", language);
		}
		
		return hdfcConfigMap;
	}

}
