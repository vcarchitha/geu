package com.geu.aem.web.core.models;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.web.core.models.bean.TimelineBean;
import com.geu.aem.web.util.PropertiesUtil;

/**
 * @author aimen.sania
 * 
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TimelineModel {

	@Inject
	private String headerTitle;

	@Inject
	private String titleInRed;

	@Inject
	private String[] timelineList;

	private List<TimelineBean> timelineItems;

	protected final static Logger log = LoggerFactory
			.getLogger(TimelineModel.class);

	/**
	 * init() method prepares tabNames and id.
	 */
	@PostConstruct
	public void init() {
		timelineItems = PropertiesUtil.getListFromStringArray(timelineList,
				TimelineBean.class);
	}

	public String getHeaderTitle() {
		return headerTitle;
	}

	public String getTitleInRed() {
		return titleInRed;
	}

	public List<TimelineBean> getTimelineItems() {
		return timelineItems;
	}

}
