package com.geu.aem.web.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.models.bean.ImageGalleryBean;

/**
 * @author nitin.jangir
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ImageGalleryModel {

	@Inject
	private String headerTitle;
	
	@Inject
	private String imageGalleryPath;
	
	@Self
	private Resource resource;
	
	private List<ImageGalleryBean> imageGalleryListItems;

	public String getHeaderTitle() {
		return headerTitle;
	}

	public String getImageGalleryPath() {
		return imageGalleryPath;
	}

	public List<ImageGalleryBean> getImageGalleryListItems() {
		imageGalleryListItems = new ArrayList<ImageGalleryBean>();
		Resource imageResource = resource.getResourceResolver().getResource(imageGalleryPath);
		if (imageResource != null) {
			Iterator<Resource> resourceIterator = imageResource.listChildren();
			while (resourceIterator.hasNext()) {
				Resource resource = resourceIterator.next();
				if (resource != null && !resource.getName().equalsIgnoreCase(ResourceConstants.JCR_CONTENT)) {
					Asset asset = resource.adaptTo(Asset.class);
					ImageGalleryBean imageGalleryBean = new ImageGalleryBean();
					imageGalleryBean.setImagePath(asset.getPath());
					Rendition mobileRendition = asset.getRendition(ResourceConstants.MOBILE_RENDITION);
					Rendition tabletRendition = asset.getRendition(ResourceConstants.TABLET_RENDITION);
					imageGalleryBean.setMobileImagePath(mobileRendition.getPath());
					imageGalleryBean.setTabletImagePath(tabletRendition.getPath());
					imageGalleryListItems.add(imageGalleryBean);
				}
			}
		}
		return imageGalleryListItems;
	}

}
