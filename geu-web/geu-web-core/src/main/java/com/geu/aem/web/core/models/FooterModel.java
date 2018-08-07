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
 * The class FooterModel is used in accordance with the footer component and
 * provides methods for footer related items.
 * 
 * @author smrithi.g
 */
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FooterModel {
	/**
	 * The static logger.
	 */
	private static Logger LOGGER = LoggerFactory.getLogger(FooterModel.class);
	
	@Inject @Source("script-bindings")
	private Style currentStyle;	
	
	private String footerLogo;	
	private String footerURL;
	private String facebookURL;	
	private String twitterURL;	
	private String linkedInURL;	
	private String youtubeURL;	
	private String googlePlusURL;	
	private String instagramURL;	
	private String snapchatURL;	
	private String tourInLabel;		
	private String virtualURL;	
	private String virtualImage;		
	private String[] footerLinks;	
	private String facebookCheck;	
	private String twitterCheck;	
	private String linkedInCheck;	
	private String youtubeCheck;	
	private String googleCheck;	
	private String instagramCheck;	
	private String snapchatCheck;
	
	private List<LinkBean> footerLinksList;
	
	private String copyrightText;

	@PostConstruct
	public void init() {
		this.footerLinks = currentStyle.get("footerLinks",String[].class);
		footerLinksList = PropertiesUtil.getListFromStringArray(footerLinks,
				LinkBean.class);		
	}

	public String getFacebookURL() {
		this.facebookURL = currentStyle.get("facebookURL",String.class);		
		String fbURL = GEUWebUtils.linkTransformer(this.facebookURL);
		return fbURL;			
	}

	public String getLinkedInURL() {
		this.linkedInURL = currentStyle.get("linkedInURL",String.class);
		String linkedinURL = GEUWebUtils.linkTransformer(this.linkedInURL);
		return linkedinURL;		
	}

	public String getYoutubeURL() {
		this.youtubeURL = currentStyle.get("youtubeURL",String.class);
		String youTubeURL = GEUWebUtils.linkTransformer(this.youtubeURL);
		return youTubeURL;
	}

	public String getGooglePlusURL() {
		this.googlePlusURL = currentStyle.get("googlePlusURL",String.class);
		String googleplusURL = GEUWebUtils.linkTransformer(this.googlePlusURL);
		return googleplusURL;
	}

	public String getInstagramURL() {
		this.instagramURL = currentStyle.get("instagramURL",String.class);
		String instagramurl = GEUWebUtils.linkTransformer(this.instagramURL);
		return instagramurl;
	}	
	
	public String getSnapchatURL() {
		this.snapchatURL = currentStyle.get("snapchatURL",String.class);
		String snapchaturl = GEUWebUtils.linkTransformer(this.snapchatURL);
		return snapchaturl;
	}

	public String getFooterLogo() {
		return this.footerLogo = currentStyle.get("footerLogo",String.class);
	}

	public String getFooterURL() {
		this.footerURL = currentStyle.get("footerURL",String.class);
		String footerurl = GEUWebUtils.linkTransformer(this.footerURL);
		return footerurl;
		
	}
	
	public String getTourInLabel() {
		return this.tourInLabel = currentStyle.get("tourInLabel",String.class);
	}

	public String getVirtualURL() {
		this.virtualURL = currentStyle.get("virtualURL",String.class);
		String virtualurl = GEUWebUtils.linkTransformer(this.virtualURL);
		return virtualurl;
	}
	
	public String getTwitterURL() {			
		this.twitterURL = currentStyle.get("twitterURL",String.class);
		String twitterurl = GEUWebUtils.linkTransformer(this.twitterURL);
		return twitterurl;
	}	
	
	public List<LinkBean> getFooterLinksList() {
		return footerLinksList;
	}	
	
	public String getCopyrightText() {
		return this.copyrightText = currentStyle.get("copyrightText",String.class);
	}

	public String getVirtualImage() {
		return this.virtualImage = currentStyle.get("virtualImage",String.class);
	}	

	public String getFacebookCheck() {
		return this.facebookCheck = currentStyle.get("facebookCheck",String.class);
	}

	public String getTwitterCheck() {
		return this.twitterCheck = currentStyle.get("twitterCheck",String.class);
	}

	public String getLinkedInCheck() {
		return this.linkedInCheck = currentStyle.get("linkedInCheck",String.class);
	}
	
	public String getGoogleCheck() {
		return this.googleCheck = currentStyle.get("googleCheck",String.class);
	}

	public String getInstagramCheck() {
		return this.instagramCheck = currentStyle.get("instagramCheck",String.class);
	}

	public String getSnapchatCheck() {
		return this.snapchatCheck = currentStyle.get("snapchatCheck",String.class);
	}
	
	public String getYoutubeCheck() {
		return this.youtubeCheck = currentStyle.get("youtubeCheck",String.class);
	}
}	
	
