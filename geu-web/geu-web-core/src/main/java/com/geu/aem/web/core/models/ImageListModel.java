package com.geu.aem.web.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.models.bean.ImageBean;
import com.geu.aem.web.util.PropertiesUtil;

/**
 * @author aimen.sania
 * 
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ImageListModel {
	@Inject
	private String heading;

	protected final static Logger log = LoggerFactory
			.getLogger(ImageListModel.class);
	@Inject
	private String[] imageList;

	@Inject
	private String imagePath;

	@Self
	private Resource resource;

	private List<ImageBean> imageListItems;

	public String getHeading() {
		return heading;
	}

	public String getImagePath() {
		return imagePath;
	}

	public List<ImageBean> getImageListItems() {
		imageListItems = new ArrayList<ImageBean>();
		Resource imageResource = resource.getResourceResolver().getResource(
				imagePath);

		if (imageResource != null) {
			Iterator<Resource> resourceIterator = imageResource.listChildren();
			while (resourceIterator.hasNext()) {
				Resource resource = resourceIterator.next();
				if (resource != null
						&& !resource.getName().equalsIgnoreCase(
								ResourceConstants.JCR_CONTENT)) {
					Asset asset = resource.adaptTo(Asset.class);
					ImageBean imageBean = new ImageBean();
					imageBean.setImgPath(asset.getPath());
					Rendition mobileRendition = asset
							.getRendition(ResourceConstants.MOBILE_RENDITION);
					Rendition tabletRendition = asset
							.getRendition(ResourceConstants.TABLET_RENDITION);
					imageBean.setMobileImagePath(mobileRendition.getPath());
					imageBean.setTabletImagePath(tabletRendition.getPath());
					imageListItems.add(imageBean);
				}
			}
		} else {
			imageListItems = PropertiesUtil.getListFromStringArray(imageList,
					ImageBean.class);
		}
		return imageListItems;
	}

}