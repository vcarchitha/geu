package com.geu.aem.web.core.models.bean;

import java.util.List;

public class MobileNavigationBean {

	private String pageID;
	
	private List<NavigationBean> subMenuList;

	public List<NavigationBean> getSubMenuList() {
		return subMenuList;
	}

	public void setSubMenuList(List<NavigationBean> subMenuList) {
		this.subMenuList = subMenuList;
	}

	public String getPageID() {
		return pageID;
	}

	public void setPageID(String pageID) {
		this.pageID = pageID;
	}
}
