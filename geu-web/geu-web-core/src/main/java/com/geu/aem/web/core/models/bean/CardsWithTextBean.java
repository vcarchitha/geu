package com.geu.aem.web.core.models.bean;

import com.geu.aem.web.util.GEUWebUtils;

public class CardsWithTextBean {

	private String cardText;

	private String redirectUrl;

	private String newWindow;

	public String getNewWindow() {
		if (newWindow.equals("on")) {
			newWindow = "_blank";
		} else {
			newWindow = "_self";
		}
		return newWindow;
	}

	public String getCardText() {
		return cardText;
	}

	public String getRedirectUrl() {
		String redirectUrl = GEUWebUtils.linkTransformer(this.redirectUrl);
		return redirectUrl;
	}
}
