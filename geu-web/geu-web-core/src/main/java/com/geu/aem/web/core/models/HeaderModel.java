package com.geu.aem.web.core.models;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.designer.Style;
import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.models.bean.LinkBean;
import com.geu.aem.web.util.GEUWebUtils;
import com.geu.aem.web.util.PropertiesUtil;

/**
 * The class HeaderModel is used in accordance with the header component and
 * provides methods for header related items.
 * 
 * @author smrithi.g
 */
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderModel {
	
	/**
	 * The static logger.
	 */
	private static Logger LOGGER = LoggerFactory.getLogger(HeaderModel.class);
	
	@Inject @Source("script-bindings")
	private Style currentStyle;
	
	private String homeURL;	
	private String homeLogo;
	private String yearsLogo;	
	private String[] homeLinks;	
	private List<LinkBean> homeLinkLists;		
	private String[] quickLinks;	
	private List<LinkBean> quickLinksLists;
	private String quickLabel;	
	private String applyNowLink;	
	private String applyNowLabel;
	private String searchResultsUrl;
	
	@Inject
	SlingHttpServletRequest request;
	
	@PostConstruct
	public void init() {
		this.homeLinks = currentStyle.get("homeLinks",String[].class);
		homeLinkLists = PropertiesUtil.getListFromStringArray(this.homeLinks,
				LinkBean.class);
		this.quickLinks = currentStyle.get("quickLinks",String[].class);
		quickLinksLists = PropertiesUtil.getListFromStringArray(this.quickLinks,
				LinkBean.class);
	}

	public String getSearchResultsUrl() {
		String searchResultsUrl = ResourceConstants.EMPTY_STRING;
		if (request.getPathInfo().contains(ResourceConstants.CONTENT_GEU_PATH)) {
			searchResultsUrl = ResourceConstants.GEU_SEARCH_RESULTS_URL;
		} else if (request.getPathInfo().contains(ResourceConstants.CONTENT_GEHU_PATH)) {
			searchResultsUrl = ResourceConstants.GEHU_SEARCH_RESULTS_URL;
		}
		return searchResultsUrl;
	}
	
	public String getHomeURL() {
		this.homeURL = currentStyle.get("homeURL",String.class);
		String homeurl = GEUWebUtils.linkTransformer(this.homeURL);
		return homeurl;
	}
	
	public String getHomeLogo() {		
		return this.homeLogo = currentStyle.get("homeLogo",String.class);
	}

	public String getYearsLogo() {
		return this.yearsLogo = currentStyle.get("yearsLogo",String.class);		
	}

	public List<LinkBean> getHomeLinkLists() {
		return homeLinkLists;
	}
	
	public String getQuickLabel() {
		return this.quickLabel = currentStyle.get("quickLabel",String.class);
	}
	
	public List<LinkBean> getQuickLinksLists() {
		return quickLinksLists;
	}

	public String getApplyNowLink() {
		
		this.applyNowLink = currentStyle.get("applyNowLink",String.class);
		String applyNowURL = GEUWebUtils.linkTransformer(this.applyNowLink);
		return applyNowURL;
	}
	
	public String getApplyNowLabel() {
		return applyNowLabel = currentStyle.get("applyNowLabel",String.class);
	}
	
}
