package com.geu.aem.web.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import com.geu.aem.web.util.GEUWebUtils;

/**
 * @author aimen.sania
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TourModel {
	@Inject
	private String heading;
	@Inject
	private String tourUrl;
	@Inject
	private String campusTourUrl;
	@Inject
	private String campusTourLabel;

	public String getHeading() {
		return heading;
	}

	public String getTourUrl() {
		String tourUrl = GEUWebUtils.linkTransformer(this.tourUrl);
		return tourUrl;
	}

	public String getCampusTourUrl() {
		String campusTourUrl = GEUWebUtils.linkTransformer(this.campusTourUrl);
		return campusTourUrl;
	}

	public String getCampusTourLabel() {
		return campusTourLabel;
	}
}
