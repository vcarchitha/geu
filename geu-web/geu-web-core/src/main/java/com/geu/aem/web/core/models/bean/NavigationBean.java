package com.geu.aem.web.core.models.bean;

import com.geu.aem.web.constants.ResourceConstants;

public class NavigationBean {	

	private String pageTitle;	

	private String pageURL;	
	
	private String activePage;	

	public String getPageTitle() {
		return pageTitle;
	}

	public String getPageURL() {
		return pageURL;
	}	

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public String getActivePage() {
		return activePage;
	}

	public void setActivePage(String activePage) {
		this.activePage = activePage;
	}

	public void setPageURL(String pageURL) {
	
		StringBuilder stringBuilder = new StringBuilder();
		if(pageURL.equals(null)){
			this.pageURL = pageURL;
		}else{			
			this.pageURL = stringBuilder.append(pageURL)
					.append(ResourceConstants.HTML_EXTENSION).toString();			
		}
	}

}
