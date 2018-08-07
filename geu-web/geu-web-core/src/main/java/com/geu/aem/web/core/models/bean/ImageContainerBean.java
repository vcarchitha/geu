package com.geu.aem.web.core.models.bean;

import com.geu.aem.web.util.GEUWebUtils;

public class ImageContainerBean {

	private String buttonURL;

	private String buttonLabel;

	public String getButtonURL() {
		String buttonURL = GEUWebUtils.linkTransformer(this.buttonURL);
		return buttonURL;
	}

	public String getButtonLabel() {
		return buttonLabel;
	}
}
