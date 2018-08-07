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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.geu.aem.web.core.models.bean.NewsEventsTeaserListBean;
import com.geu.aem.web.util.GEUWebUtils;

@Model(adaptables=Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NewsEventsTeaserListModel {
	
	private static Logger LOGGER = LoggerFactory.getLogger(NewsEventsTeaserListModel.class);

	@Inject
	private String newsEventsTitle;	

	@Inject 
	private String buttonLabel;
	
	@Inject
	private String buttonURL;
	
	@Inject
	private ResourceResolver resourceResolver;
	
	private List<NewsEventsTeaserListBean> newEventsList;		

	@PostConstruct
	public void init(){
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

		if(StringUtils.isNotBlank(buttonURL)){
			Page page = pageManager.getPage(buttonURL);
			
			if(page!=null){
				Iterator<Page> childPages = page.listChildren();
				newEventsList = new ArrayList<NewsEventsTeaserListBean>();
				int numberOfTimes = 0;
				
				while(childPages.hasNext() && numberOfTimes<=3){
					
					NewsEventsTeaserListBean newsEventsBean = new NewsEventsTeaserListBean();
					Page childpage = childPages.next();
					String pageUrl = GEUWebUtils.linkTransformer(childpage.getPath());
					
					if(StringUtils.isNotBlank(pageUrl)){
						newsEventsBean.setPageURL(pageUrl);	
					}
					ValueMap property = childpage.getProperties();	
					String subTitle = property.get("newsEventsSubTitle", " ");
					if(StringUtils.isNotBlank(subTitle)){
						newsEventsBean.setNewsEventsSubTitle(property
								.get("newsEventsSubTitle", " "));
					}
					String title = property.get("newsEventsHeadline", " ");
					String contentTitle = property.get("jcr:title", "");
					if(StringUtils.isNotBlank(title)){
						newsEventsBean.setNewsEventsHeadline(property
								.get("newsEventsHeadline", " "));	
					}else if(StringUtils.isNotBlank(contentTitle)){
						newsEventsBean.setNewsEventsHeadline(property
								.get("jcr:title", " "));
					}				
					String thumbnailImage = property.get("thumbnailImage", " ");
					if(StringUtils.isNotBlank(thumbnailImage)){
						newsEventsBean.setThumbnailImage(property
								.get("thumbnailImage", " "));
					}					
					newEventsList.add(newsEventsBean);
					numberOfTimes++;
				}	
			}					
		}
	}	
	
	public String getNewsEventsTitle() {
		return newsEventsTitle;
	}

	public String getButtonLabel() {
		return buttonLabel;
	}

	public String getButtonURL() {
		String buttonurl = GEUWebUtils.linkTransformer(this.buttonURL);
		return buttonurl;
	}
	
	public List<NewsEventsTeaserListBean> getNewEventsList() {
		return newEventsList;
	}
}
