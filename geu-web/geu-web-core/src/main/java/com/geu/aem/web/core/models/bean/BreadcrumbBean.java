package com.geu.aem.web.core.models.bean;

import com.day.cq.wcm.api.Page;

public class BreadcrumbBean {
	private Page page;

	private boolean active;

	public BreadcrumbBean(Page page, boolean active) {
		this.page = page;
		this.active = active;
	}

	public Page getPage() {
		return this.page;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
