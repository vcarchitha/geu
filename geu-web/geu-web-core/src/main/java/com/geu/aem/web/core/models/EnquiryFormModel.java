package com.geu.aem.web.core.models;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import com.geu.aem.web.core.models.bean.EnquiryFormBean;
import com.geu.aem.web.util.PropertiesUtil;

/**
 * @author nitin.jangir
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class EnquiryFormModel {
	
	@Inject
	private String title;
	
	@Inject
	private String ctaButtonText;

	@Inject
	private String[] areaOfInterest;
	
	@Inject
	private String[] country;
	
	private List<EnquiryFormBean> areaOfInterestList;
	
	private List<EnquiryFormBean> countryList;
	
	/**
	 * init() method
	 */
	@PostConstruct
	public void init() {
		areaOfInterestList = PropertiesUtil.getListFromStringArray(areaOfInterest, EnquiryFormBean.class);
		countryList = PropertiesUtil.getListFromStringArray(country, EnquiryFormBean.class);
	}

	public List<EnquiryFormBean> getAreaOfInterestList() {
		return areaOfInterestList;
	}

	public List<EnquiryFormBean> getCountryList() {
		return countryList;
	}
	
	public String getTitle() {		
		return title;
	}
	
	public String getCtaButtonText() {
		return ctaButtonText;
	}

}
