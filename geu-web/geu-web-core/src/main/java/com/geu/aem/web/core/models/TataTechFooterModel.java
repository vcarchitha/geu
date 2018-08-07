package com.geu.aem.web.core.models;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.web.core.models.bean.LinkBean;
import com.geu.aem.web.util.PropertiesUtil;

/**
 * @author 
 * 
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TataTechFooterModel {
	
	@Inject
	private String socialMediaText;
	@Inject
	private String channelText;
	@Inject
	private String technologyGlobalText;
	@Inject
	private String technologyGlobalLabel;
	@Inject
	private String technologyGlobalPath;

	private List<LinkBean> socialLinkItems;

	@Inject
	private String[] socialLinks;

	protected final static Logger log = LoggerFactory
			.getLogger(TataTechFooterModel.class);

	/**
	 * PostConstruct
	 */
	@PostConstruct
	public void init() {
		socialLinkItems = PropertiesUtil.getListFromStringArray(socialLinks,
				LinkBean.class);
	}

	/**
	 * @return socialMediaText
	 */
	public String getSocialMediaText() {
		return socialMediaText;
	}

	/**
	 * @return channelText
	 */
	public String getChannelText() {
		return channelText;
	}

	/**
	 * @return technologyGlobalText
	 */
	public String getTechnologyGlobalText() {
		return technologyGlobalText;
	}

	/**
	 * @return technologyGlobalLabel
	 */
	public String getTechnologyGlobalLabel() {
		return technologyGlobalLabel;
	}

	/**
	 * @return technologyGlobalPath
	 */
	public String getTechnologyGlobalPath() {
		return technologyGlobalPath;
	}

	/**
	 * @return socialLinkItems
	 */
	public List<LinkBean> getSocialLinkItems() {
		return socialLinkItems;
	}


}