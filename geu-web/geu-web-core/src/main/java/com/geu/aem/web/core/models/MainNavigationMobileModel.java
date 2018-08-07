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
import com.geu.aem.web.core.models.bean.MobileNavigationBean;
import com.geu.aem.web.core.models.bean.NavigationBean;

import org.apache.commons.lang3.StringUtils;

/**
 * The class MainNavigationMobileModel is used in accordance with the header
 * component and provides methods for all related items.
 * 
 * @author smrithi.g
 */

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MainNavigationMobileModel {

	private static Logger LOGGER = LoggerFactory
			.getLogger(MainNavigationMobileModel.class);

	@Inject
	private PageManager pageManager;

	@Self
	@Via("resource")
	private Resource resource;

	private List<NavigationBean> homeMenuItems;

	private List<NavigationBean> subChildPages;

	private List<MobileNavigationBean> subMenuPages;
	
	private String pageID = null;

	@PostConstruct
	public void init() {
		Page currentPage = pageManager.getContainingPage(this.resource);
		String currentPath = currentPage.getPath();
		String homePage = null;

		if (currentPage != null) {
			Page parentPage = currentPage.getParent();
			String parentPath = parentPage.getPath();

			if (StringUtils.isNotBlank(parentPath)
					&& StringUtils.isNotBlank(currentPath)) {
				homeMenuItems = new ArrayList<NavigationBean>();
				subMenuPages = new ArrayList<MobileNavigationBean>();

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

							subChildPages = new ArrayList<NavigationBean>();
							Iterator<Page> childCurrentPage = child
									.listChildren();
							MobileNavigationBean mobNavBean = new MobileNavigationBean();
							pageID = child.getTitle();

							while (childCurrentPage.hasNext()) {
								NavigationBean navBeanChildPages = new NavigationBean();
								Page subChildPage = childCurrentPage.next();

								Boolean hideInNav = subChildPage.isHideInNav();
								if (!hideInNav) {
									pageID = child.getTitle();
									navBeanChildPages.setPageTitle(subChildPage
											.getTitle());
									navBeanChildPages.setPageURL(subChildPage
											.getPath());
											subChildPages.add(navBeanChildPages);
								}
							}

							mobNavBean.setPageID(pageID);
							mobNavBean.setSubMenuList(subChildPages);
							subMenuPages.add(mobNavBean);
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

	public List<NavigationBean> getSubChildPages() {
		return subChildPages;
	}
	
	public List<MobileNavigationBean> getSubMenuPages() {
		return subMenuPages;
	}

}
