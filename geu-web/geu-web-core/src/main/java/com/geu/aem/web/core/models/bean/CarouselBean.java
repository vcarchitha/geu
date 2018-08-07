package com.geu.aem.web.core.models.bean;

import com.geu.aem.web.util.GEUWebUtils;

public class CarouselBean {
	private String imagePath;
	private String linkPath;
	
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	public String getLinkPath() {
		return  GEUWebUtils.linkTransformer(this.linkPath);
	}
	public void setLinkPath(String linkPath) {
		this.linkPath = linkPath;
	}
}
