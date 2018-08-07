package com.geu.aem.web.core.models.bean;

import com.geu.aem.web.util.GEUWebUtils;

public class LinkBean {

	private String label;

	private String linkPath;

	private String newWindow;

	public String getLabel() {
		return label;
	}

	public String getNewWindow() {
		if (newWindow.equals("on")) {
			newWindow = "_blank";
		} else {
			newWindow = "_self";
		}
		return newWindow;
	}

	public String getLinkPath() {
		String linkURL = GEUWebUtils.linkTransformer(this.linkPath);
		return linkURL;
	}
}
