package com.geu.aem.web.service.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import com.geu.aem.web.service.PGHdfcService;
import com.geu.aem.web.service.PGHdfcServiceConfiguration;

@Component(service= {PGHdfcService.class},immediate=true,configurationPolicy=ConfigurationPolicy.REQUIRE)
@Designate(ocd = PGHdfcServiceConfiguration.class)
public class PGHdfcServiceImpl implements PGHdfcService {
	
	private PGHdfcServiceConfiguration pgHdfcConfiguration;

	@Activate
	@Modified
	public void activate(PGHdfcServiceConfiguration pgHdfcConfiguration){
		this.pgHdfcConfiguration = pgHdfcConfiguration;		
	}

	@Override
	public String getMerchantId() {
		return pgHdfcConfiguration.getMerchantId();
	}

	@Override
	public String getCurrency() {
		return pgHdfcConfiguration.getCurrency();
	}

	@Override
	public String getRedirectURL() {
		return pgHdfcConfiguration.getRedirectURL();
	}

	@Override
	public String getLanguage() {
		return pgHdfcConfiguration.getLanguage();
	}

	@Override
	public String getReturnURL() {
		return pgHdfcConfiguration.getReturnURL();
	}
	
	@Override
	public String getRegistrationRedirectURL() {
		return pgHdfcConfiguration.getRegistrationRedirectURL();
	}
	
	@Override
	public String getRegistrationReturnURL() {
		return pgHdfcConfiguration.getRegistrationReturnURL();
	}
	
	@Override
	public String getRegistrationAmount() {
		return pgHdfcConfiguration.getRegistrationAmount();
	}
}
