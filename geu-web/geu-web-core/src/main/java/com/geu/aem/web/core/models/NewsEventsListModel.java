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
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.geu.aem.web.core.models.bean.NewsEventsTeaserListBean;
import com.geu.aem.web.util.GEUWebUtils;

@Model(adaptables=Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NewsEventsListModel {
	
	private static Logger LOGGER = LoggerFactory.getLogger(NewsEventsListModel.class);

	
	@Self
	private Resource resource;
	
	@Inject
	private ResourceResolver resourceResolver;
	
	private List<NewsEventsTeaserListBean> newEventsDetailedList;	
	
	@PostConstruct
	public void init(){
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		Page currentPage = pageManager.getContainingPage(this.resource);
		
		if(currentPage!=null){			
			Iterator<Page> childPages = currentPage.listChildren();
			newEventsDetailedList = new ArrayList<NewsEventsTeaserListBean>();
			
			while(childPages.hasNext()){
				NewsEventsTeaserListBean newsEventsBean = new NewsEventsTeaserListBean();
				Page childpage = childPages.next();
				String pageUrl = GEUWebUtils.linkTransformer(childpage.getPath());
				if(StringUtils.isNotBlank(pageUrl)){
					newsEventsBean.setPageURL(pageUrl);	
				}
				
				ValueMap property = childpage.getProperties();				
				String title = property.get("newsEventsHeadline", " ");
				String contentTitle = property.get("jcr:title", "");
				if(StringUtils.isNotBlank(title)){
					newsEventsBean.setNewsEventsHeadline(property
							.get("newsEventsHeadline", " "));	
				}else if(StringUtils.isNotBlank(contentTitle)){
					newsEventsBean.setNewsEventsHeadline(property
							.get("jcr:title", " "));
				}				
				String image = property.get("newsEventsImage", " ");
				if(StringUtils.isNotBlank(image)){
					newsEventsBean.setNewsEventsImage(property
							.get("newsEventsImage", " "));
				}				
				String description = property.get("newsEventsDescription", " ");
				if(StringUtils.isNotBlank(description)){
					newsEventsBean.setNewsEventsDescription(property
							.get("newsEventsDescription", " "));
				}
				newEventsDetailedList.add(newsEventsBean);
			}
		}				
	}	

	public List<NewsEventsTeaserListBean> getNewEventsDetailedList() {
		return newEventsDetailedList;
	}
}
