package com.geu.aem.web.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.models.bean.NavigationBean;
import com.geu.aem.web.core.models.bean.SiteMapBean;

/**
 * @author 
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SiteMapModel {

	 @Inject
	 private ResourceResolver resourceResolver;
	 @Self
	 private Resource resource;
	
	 @Inject
	 private String rootPath;
	
	 private List<SiteMapBean> withChildPages;
	
	 private List<NavigationBean> withoutChildPages;
	
	 protected final static Logger log = LoggerFactory.getLogger(SiteMapModel.class);
	
	 /**
	 * init() method to fetch all the
	 *  child pages of rootPath
	 */
	@PostConstruct
	 protected void init() {
		try{
			  if (null == rootPath){ 
				  setDefaultRootPath();}
			   Page rootPage = resourceResolver.adaptTo(PageManager.class).getPage(rootPath);
			   Iterator < Page > pages = rootPage.listChildren();
			   withChildPages = new ArrayList < SiteMapBean > ();
			   withoutChildPages = new ArrayList < NavigationBean > ();
			   while (pages.hasNext()) {
			    Page parentPage = pages.next();
			    if (showInSitemap(parentPage)) {
			     if (hasPageChildren(parentPage)) {
			
			      SiteMapBean parentItem = new SiteMapBean();
			      List < NavigationBean > childPagesList = new ArrayList < NavigationBean > ();
			      Iterator < Page > childPages = parentPage.listChildren();
			      while (childPages.hasNext()) {
			       Page childPage = childPages.next();
			       if (showInSitemap(childPage)) {
			        childPagesList.add(addToBean(childPage));
			       }
			      }
			      parentItem.setPageTitle(parentPage.getTitle());
			      parentItem.setPageURL(parentPage.getPath());
			      parentItem.setChildPages(childPagesList);
			      if (childPagesList.size() >= 1)
			       withChildPages.add(parentItem);
			      else {
			       withoutChildPages.add(addToBean(parentPage));
			      }
			
			     } else {
			      withoutChildPages.add(addToBean(parentPage));
			     }
			
			    }
			
			   }
			  }
		     catch(Exception e){
			log.error("Exception occurred in doPost :"+e.getMessage());
		}
		  
	 }
	
	
	 /**
	 * @return rootPath
	 */
	public String getRootPath() {
	  return rootPath;
	 }
	
	 /**
	 * @param page
	 * @return true if the hideInSitemap 
	 * property is not set in page 
	 */
	public Boolean showInSitemap(Page page) {
	  Boolean showInSitemap = false;
	  ValueMap prop = page.getProperties();
	  if (prop.get("hideInSitemap") == null || prop.get("hideInSitemap").toString() == "false") {
	   showInSitemap = true;
	  }
	  return showInSitemap;
	 }
	
	 
	 /**
	 * @return PageFilter
	 */
	public static PageFilter getPageFilter() {
	  PageFilter pf = new PageFilter();
	  return pf;
	 }
	
	 /**
	 * @param rootPage
	 * @return true if the rootPage has child pages
	 */
	public static boolean hasPageChildren(Page rootPage) {
	  boolean isTrue = false;
	  if (rootPage != null && rootPage.listChildren(getPageFilter()).hasNext()) {
	   isTrue = true;
	  }
	  return isTrue;
	 }
	
	 /**
	 * @return List < SiteMapBean > items
	 */
	public List < SiteMapBean > getWithChildPages() {
	  return withChildPages;
	 }
	
	 /**
	 * @return List < NavigationBean > items
	 */
	public List < NavigationBean > getWithoutChildPages() {
	  return withoutChildPages;
	 }
	
	 /**
	 * @param page
	 * @return NavigationBean object
	 */
	public NavigationBean addToBean(Page page) {
	  NavigationBean navBean = new NavigationBean();
	  navBean.setPageTitle(page.getTitle());
	  navBean.setPageURL(page.getPath());
	  return navBean;
	 }
	
	/**
	 * method to set the default rootpath
	 */
	public void setDefaultRootPath() {
		PageManager pageManager = this.resourceResolver.adaptTo(PageManager.class);
		  Page currentPage = pageManager.getContainingPage(resource);
		  String lang = currentPage.getAbsoluteParent(2).getName();
		  StringBuilder strBuilder = new StringBuilder();	
		  if(StringUtils.isNotBlank(lang)){
				 this.rootPath = strBuilder.append(ResourceConstants.CONTENT_GEU_PATH)
						.append(ResourceConstants.FORWARD_SLASH).append(lang).toString();
				 }
		 }


}