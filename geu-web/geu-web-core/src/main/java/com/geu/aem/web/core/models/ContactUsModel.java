package com.geu.aem.web.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ContactUsModel {
	
	@Inject
	private String title;
	
	@Inject
	private String callLabel;
	
	@Inject
	private String phoneNumber;
	
	@Inject
	private String jsonPath;
	
	@PostConstruct
	public void init() {
	}

	public String getTitle() {
		return title;
	}

	public String getCallLabel() {
		return callLabel;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
	public String getJsonPath() {
		return jsonPath;
	}
}
