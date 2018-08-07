package com.geu.aem.web.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import com.geu.aem.web.util.GEUWebUtils;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TwoButtonModel {
	
	@Inject
	private String buttonA;
	
	@Inject
	private String buttonALink;
	
	@Inject
	private String buttonB;
	
	@Inject
	private String buttonBLink;

	public String getButtonA() {
		return buttonA;
	}

	public String getButtonB() {
		return buttonB;
	}

	public String getButtonALink() {
		return GEUWebUtils.linkTransformer(buttonALink);
	}

	public String getButtonBLink() {
		return GEUWebUtils.linkTransformer(buttonBLink);
	}

}
