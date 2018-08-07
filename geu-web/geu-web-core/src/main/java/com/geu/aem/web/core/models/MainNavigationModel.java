package com.geu.aem.web.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.models.bean.NavigationBean;

import org.apache.commons.lang3.StringUtils;

/**
 * The class MainNavigationModel is used in accordance with the mainNav
 * component and provides methods for all related items.
 * 
 * @author smrithi.g
 */

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MainNavigationModel {

	private static Logger LOGGER = LoggerFactory
			.getLogger(MainNavigationModel.class);

	@Inject
	private PageManager pageManager;

	@Self
	@Via("resource")
	private Resource resource;

	private List<NavigationBean> homeMenuItems;

	private List<NavigationBean> subChildPages;

	private String pageID;

	@PostConstruct
	public void init() {
		Page currentPage = pageManager.getContainingPage(this.resource);
		String currentPath = currentPage.getPath();

		if (currentPage != null) {
			Page parentPage = currentPage.getParent();
			String parentPath = parentPage.getPath();
			String homePage = null;
			if (StringUtils.isNotBlank(parentPath)
					&& StringUtils.isNotBlank(currentPath)) {
				homeMenuItems = new ArrayList<NavigationBean>();
				StringBuilder strBuilder = new StringBuilder();
				String lang = currentPage.getAbsoluteParent(2).getName();
				if (StringUtils.isNotBlank(lang)) {
					if (currentPath
							.contains(ResourceConstants.CONTENT_GEU_PATH)) {
						homePage = strBuilder
								.append(ResourceConstants.CONTENT_GEU_PATH)
								.append(ResourceConstants.FORWARD_SLASH)
								.append(lang).toString();
					} else {
						homePage = strBuilder
								.append(ResourceConstants.CONTENT_GEHU_PATH)
								.append(ResourceConstants.FORWARD_SLASH)
								.append(lang).toString();
					}
					/* Fetching the children of home page */

					if (StringUtils.isNotBlank(homePage)) {
						Page homepage = pageManager.getPage(homePage);
						Iterator<Page> childPages = homepage.listChildren();
						while (childPages.hasNext()) {
							NavigationBean navBean = new NavigationBean();
							Page child = childPages.next();

							/*
							 * Checking if the child is the current page's
							 * absolute parent and not at the same level, If
							 * yes, creating a new list for its children
							 */

							if (currentPage.getAbsoluteParent(3) != null
									&& (currentPage.getDepth() != 4)) {
								subChildPages = new ArrayList<NavigationBean>();
								Iterator<Page> childCurrentPage = currentPage
										.getAbsoluteParent(3).listChildren();
								while (childCurrentPage.hasNext()) {
									NavigationBean navBeanChildPages = new NavigationBean();
									Page subChildPage = childCurrentPage.next();
									Boolean hideInNav = subChildPage
											.isHideInNav();
									if (!hideInNav) {
										pageID = currentPage.getAbsoluteParent(
												3).getTitle();
										if (currentPath.equals(subChildPage
												.getPath())) {
											navBeanChildPages
													.setActivePage("active");
										}
										navBeanChildPages
												.setPageTitle(subChildPage
														.getTitle());
										navBeanChildPages
												.setPageURL(subChildPage
														.getPath());
										subChildPages.add(navBeanChildPages);
									}
								}

							}
							Boolean hideInNav = child.isHideInNav();
							if (!hideInNav) {
								navBean.setPageTitle(child.getTitle());
								navBean.setPageURL(child.getPath());
								homeMenuItems.add(navBean);
							}
						}
					}

				}
			}
		}
	}

	public List<NavigationBean> getHomeMenuItems() {
		return homeMenuItems;
	}

	public String getPageID() {
		return pageID;
	}

	public List<NavigationBean> getSubChildPages() {
		return subChildPages;
	}
}
