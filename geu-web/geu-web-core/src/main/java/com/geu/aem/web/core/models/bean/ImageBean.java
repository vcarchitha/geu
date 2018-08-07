package com.geu.aem.web.core.models.bean;

public class ImageBean {

	private String imgPath;
	private String altText;
	private String mobileImagePath;
	private String tabletImagePath;

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public void setAltText(String altText) {
		this.altText = altText;
	}

	public String getAltText() {
		return altText;
	}

	public String getMobileImagePath() {
		return mobileImagePath;
	}

	public String getTabletImagePath() {
		return tabletImagePath;
	}

	public void setMobileImagePath(String mobileImagePath) {
		this.mobileImagePath = mobileImagePath;
	}

	public void setTabletImagePath(String tabletImagePath) {
		this.tabletImagePath = tabletImagePath;
	}

}