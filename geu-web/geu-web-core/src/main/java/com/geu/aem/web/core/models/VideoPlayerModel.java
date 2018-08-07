package com.geu.aem.web.core.models;

import javax.inject.Inject;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class VideoPlayerModel {

	@Inject
	private String imagePath;
	
	@Inject
	private String videoId;
	
	public String getImagePath() {
		return imagePath;
	}

	
	public String getVideoId() {
		return videoId;
	}

	
}
