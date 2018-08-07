package com.geu.aem.cca.core.models;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.cca.beans.LeadCaptureBean;
import com.geu.aem.cca.core.util.PropertiesUtil;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LeadCaptureModel {
	
	@Inject
	private String[] areaOfInterest;
	
	@Inject
	private String[] gender;
	
	@Inject
	private String[] level;
	
	@Inject
	private String[] coursePreference;
	
	@Inject
	private String[] campusPreference;
	
	@Inject
	private String[] category;
	
	@Inject
	private String[] specialProvisions;
	
	@Inject
	private String[] priorAssociation;
	
	@Inject
	private String[] leadStatus;
	
	@Inject
	private String[] paymentMode;

	private List<LeadCaptureBean> areaOfInterestList;	
	private List<LeadCaptureBean> genderList;
	private List<LeadCaptureBean> levelList;
	private List<LeadCaptureBean> coursePreferenceList;
	private List<LeadCaptureBean> campusPreferenceList;
	private List<LeadCaptureBean> categoryList;
	private List<LeadCaptureBean> specialProvisionsList;
	private List<LeadCaptureBean> priorAssociationList;
	private List<LeadCaptureBean> leadStatusList;
	private List<LeadCaptureBean> paymentModeList;

	protected final static Logger log = LoggerFactory
			.getLogger(LeadCaptureModel.class);

	/**
	 * init() method prepares tabNames
	 */
	@PostConstruct
	public void init() {
		log.info("In Leadcpturemodel init"+areaOfInterest);
		areaOfInterestList = PropertiesUtil.getListFromStringArray(areaOfInterest, LeadCaptureBean.class);
		genderList = PropertiesUtil.getListFromStringArray(gender, LeadCaptureBean.class);
		levelList = PropertiesUtil.getListFromStringArray(level, LeadCaptureBean.class);
		coursePreferenceList = PropertiesUtil.getListFromStringArray(coursePreference, LeadCaptureBean.class);
		campusPreferenceList = PropertiesUtil.getListFromStringArray(campusPreference, LeadCaptureBean.class);
		categoryList = PropertiesUtil.getListFromStringArray(category, LeadCaptureBean.class);
		specialProvisionsList = PropertiesUtil.getListFromStringArray(specialProvisions, LeadCaptureBean.class);
		priorAssociationList = PropertiesUtil.getListFromStringArray(priorAssociation, LeadCaptureBean.class);
		leadStatusList = PropertiesUtil.getListFromStringArray(leadStatus, LeadCaptureBean.class);
		paymentModeList = PropertiesUtil.getListFromStringArray(paymentMode, LeadCaptureBean.class);
	}
	
	public String[] getAreaofinterest() {
		return areaOfInterest;
	}

	public List<LeadCaptureBean> getAreaOfInterestList() {
		log.info("List Size"+areaOfInterestList.size());
		return areaOfInterestList;
	}

	public String[] getGender() {
		return gender;
	}

	public List<LeadCaptureBean> getGenderList() {
		return genderList;
	}

	public String[] getLevel() {
		return level;
	}

	public List<LeadCaptureBean> getLevelList() {
		return levelList;
	}

	public String[] getCoursePreference() {
		return coursePreference;
	}

	public String[] getCampusPreference() {
		return campusPreference;
	}

	public String[] getCategory() {
		return category;
	}

	public String[] getSpecialProvisions() {
		return specialProvisions;
	}

	public String[] getPriorAssociation() {
		return priorAssociation;
	}

	public String[] getLeadStatus() {
		return leadStatus;
	}

	public String[] getPaymentMode() {
		return paymentMode;
	}

	public List<LeadCaptureBean> getCoursePreferenceList() {
		return coursePreferenceList;
	}

	public List<LeadCaptureBean> getCampusPreferenceList() {
		return campusPreferenceList;
	}

	public List<LeadCaptureBean> getCategoryList() {
		return categoryList;
	}

	public List<LeadCaptureBean> getSpecialProvisionsList() {
		return specialProvisionsList;
	}

	public List<LeadCaptureBean> getPriorAssociationList() {
		return priorAssociationList;
	}

	public List<LeadCaptureBean> getLeadStatusList() {
		return leadStatusList;
	}

	public List<LeadCaptureBean> getPaymentModeList() {
		return paymentModeList;
	}
}
