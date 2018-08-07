package com.geu.aem.web.core.models.bean;

import java.util.List;

import com.geu.aem.web.constants.ResourceConstants;

/**
 * @author 
 *
 */
public class SiteMapBean {	

	private String pageTitle;	

	private String pageURL;	
	
	private List<NavigationBean> childPages;

	public String getPageTitle() {
		return pageTitle;
	}

	public String getPageURL() {
		return pageURL;
	}	

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
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

	public List<NavigationBean> getChildPages() {
		return childPages;
	}

	public void setChildPages(List<NavigationBean> childPages) {
		this.childPages = childPages;
	}

}
