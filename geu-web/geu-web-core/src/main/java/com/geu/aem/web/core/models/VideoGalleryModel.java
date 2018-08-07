package com.geu.aem.web.core.models;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import com.geu.aem.web.core.models.bean.VideoGalleryBean;
import com.geu.aem.web.util.PropertiesUtil;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class VideoGalleryModel {
	
	@Inject
	private String[] videoGallery;

	private List<VideoGalleryBean> videoGalleryItems;
	
	@PostConstruct
	public void init() {
		videoGalleryItems = PropertiesUtil.getListFromStringArray(videoGallery,
				VideoGalleryBean.class);
	}

	public List<VideoGalleryBean> getVideoGalleryItems() {
		return videoGalleryItems;
	}
	

}
