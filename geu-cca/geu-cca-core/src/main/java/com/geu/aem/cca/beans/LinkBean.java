package com.geu.aem.cca.beans;

public class LinkBean {

	private String linkLabel;
	private String linkPath;

	public LinkBean(String linkLabel, String linkPath) {
		super();
		this.linkLabel = linkLabel;
		this.linkPath = linkPath;
	}

	public String getLinkLabel() {
		return linkLabel;
	}

	public void setLinkLabel(String linkLabel) {
		this.linkLabel = linkLabel;
	}

	public String getLinkPath() {
		return linkPath;
	}

	public void setLinkPath(String linkPath) {
		this.linkPath = linkPath;
	}

}
