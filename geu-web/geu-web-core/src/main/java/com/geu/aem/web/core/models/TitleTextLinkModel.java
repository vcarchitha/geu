package com.geu.aem.web.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.util.GEUWebUtils;

/**
 * @author smrithi.g
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TitleTextLinkModel {
	
	@Inject
	private String title;	

	@Inject
	private String subTitle;
	
	@Inject
	private String linkLabel;
	
	@Inject
	private String linkURL;
	
	@Inject
	private String description;
	
	public String getTitle() {
		return title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public String getLinkLabel() {
		return linkLabel;
	}

	public String getLinkURL() {
		String linkurl = GEUWebUtils.linkTransformer(this.linkURL);
		return linkurl;	
	}	

	public String getDescription() {
		return description;
	}
	
}
