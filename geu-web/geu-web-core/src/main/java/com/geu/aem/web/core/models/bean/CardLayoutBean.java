package com.geu.aem.web.core.models.bean;

import org.apache.commons.lang3.StringUtils;

import com.geu.aem.web.constants.ResourceConstants;

public class CardLayoutBean {

	private String image;
	private String title;
	private String description;
	private String pageUrl;

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPageUrl() {
		if (!StringUtils.endsWith(pageUrl, ResourceConstants.HTML_EXTENSION))
			return pageUrl + ResourceConstants.HTML_EXTENSION;
		return pageUrl;

	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
