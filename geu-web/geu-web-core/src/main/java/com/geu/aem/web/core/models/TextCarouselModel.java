package com.geu.aem.web.core.models;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import com.geu.aem.web.core.models.bean.TextCarouselBean;
import com.geu.aem.web.util.GEUWebUtils;
import com.geu.aem.web.util.PropertiesUtil;

/**
 * @author nitin.jangir
 *
 */

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TextCarouselModel {
	
	@Inject
	private String headerTitle;
	
	@Inject
	private String ctaButtonText;
	
	@Inject
	private String ctaButtonUrl;

	@Inject
	private String[] textCarouselList;

	private List<TextCarouselBean> textCarouselItems;
	
	/**
	 * init() method prepares text carousel items.
	 */
	@PostConstruct
	public void init() {
		textCarouselItems = PropertiesUtil.getListFromStringArray(textCarouselList,
				TextCarouselBean.class);
	}

	public String getHeaderTitle() {
		return headerTitle;
	}

	public String getCtaButtonText() {
		return ctaButtonText;
	}

	public List<TextCarouselBean> getTextCarouselItems() {
		return textCarouselItems;
	}
	
	public String getCtaButtonUrl() {
		return GEUWebUtils.linkTransformer(ctaButtonUrl);
	}

}
