package com.geu.aem.web.core.models.bean;

public class NewsEventsTeaserListBean {	

	private String newsEventsHeadline;		

	private String newsEventsSubTitle;	
	
	private String thumbnailImage;
	
	private String newsEventsImage;
	
	private String newsEventsDescription;	
	
	private String pageURL;

	public String getNewsEventsHeadline() {
		return newsEventsHeadline;
	}

	public void setNewsEventsHeadline(String newsEventsHeadline) {
		this.newsEventsHeadline = newsEventsHeadline;
	}

	public String getNewsEventsSubTitle() {
		return newsEventsSubTitle;
	}

	public void setNewsEventsSubTitle(String newsEventsSubTitle) {
		this.newsEventsSubTitle = newsEventsSubTitle;
	}	

	public String getThumbnailImage() {
		return thumbnailImage;
	}

	public void setThumbnailImage(String thumbnailImage) {
		this.thumbnailImage = thumbnailImage;
	}	

	public void setNewsEventsImage(String newsEventsImage) {
		this.newsEventsImage = newsEventsImage;
	}

	public String getNewsEventsImage() {
		return newsEventsImage;
	}
	
	public String getNewsEventsDescription() {
		return newsEventsDescription;
	}

	public void setNewsEventsDescription(String newsEventsDescription) {
		this.newsEventsDescription = newsEventsDescription;
	}

	public String getPageURL() {
		return pageURL;
	}

	public void setPageURL(String pageURL) {
		this.pageURL = pageURL;
	}
}
