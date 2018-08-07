package com.geu.aem.web.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import com.day.cq.dam.api.Asset;
import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.util.GEUWebUtils;

/**
 * @author aimen.sania
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ImageComponentModel {

	@Inject
	private String fileReference;

	@Inject
	private String linkURL;

	@Inject
	private String alt;

	@Inject
	private String caption;

	@Inject
	private String headerTitle;

	private String mobileImagePath;

	private String tabletImagePath;

	@Self
	private Resource resource;

	public String getHeaderTitle() {
		return headerTitle;
	}

	@PostConstruct
	public void init() {

		Resource fileReferenceResource = resource.getResourceResolver()
				.getResource(fileReference);
		if (fileReferenceResource != null) {
			Asset asset = fileReferenceResource.adaptTo(Asset.class);
			this.mobileImagePath = asset.getRendition(
					ResourceConstants.MOBILE_RENDITION).getPath();
			this.tabletImagePath = asset.getRendition(
					ResourceConstants.TABLET_RENDITION).getPath();
		}
	}

	public String getMobileImagePath() {
		return mobileImagePath;
	}

	public String getTabletImagePath() {
		return tabletImagePath;
	}
	public String getFileReference() {
		return fileReference;
	}

	public String getLinkURL() {
		String linkURL = GEUWebUtils.linkTransformer(this.linkURL);
		return linkURL;
	}

	public String getAlt() {
		return alt;
	}

	public String getCaption() {
		return caption;
	}

}
