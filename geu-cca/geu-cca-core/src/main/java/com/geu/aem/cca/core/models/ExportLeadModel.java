package com.geu.aem.cca.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import com.geu.aem.cca.util.GEUCCAUtils;

@Model(adaptables=Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExportLeadModel {

	@Inject
	private String title;
	
	@Inject
	private String exportLeadLabel;
	
	@Inject
	private String backButtonLabel;	
	
	@Inject
	private String backButtonURL;	

	public String getTitle() {
		return title;
	}

	public String getExportLeadLabel() {
		return exportLeadLabel;
	}

	public String getBackButtonLabel() {
		return backButtonLabel;
	}
	
	public String getBackButtonURL() {
		String buttonurl = GEUCCAUtils.linkTransformer(backButtonURL);
		return buttonurl;
	}
}
