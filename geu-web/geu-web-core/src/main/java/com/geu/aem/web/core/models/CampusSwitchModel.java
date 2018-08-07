package com.geu.aem.web.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import com.day.cq.wcm.api.designer.Style;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CampusSwitchModel {

	@Inject @Source("script-bindings")
	private Style currentStyle;
	
	private String campusA;
	
	private String campusB;

	@PostConstruct
	public void init() {
		this.campusA = currentStyle.get("campusA",String.class);
		this.campusB = currentStyle.get("campusB",String.class);
	}

	public String getCampusA() {
		return campusA;
	}

	public String getCampusB() {
		return campusB;
	}
}
