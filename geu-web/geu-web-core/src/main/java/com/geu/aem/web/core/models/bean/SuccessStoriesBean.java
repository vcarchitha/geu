package com.geu.aem.web.core.models.bean;

import com.geu.aem.web.util.GEUWebUtils;

public class SuccessStoriesBean {

	private String title;
	private String description;
	private String image;
	private String buttonLabel;
	private String buttonURL;

	public SuccessStoriesBean(String title, String description, String image,
			String buttonLabel, String buttonURL) {
		super();
		this.title = title;
		this.description = description;
		this.image = image;
		this.buttonLabel = buttonLabel;
		this.buttonURL = buttonURL;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getImage() {
		return image;
	}

	public String getButtonLabel() {
		return buttonLabel;
	}

	public String getButtonURL() {
		return GEUWebUtils.linkTransformer(buttonURL);
	}
}
