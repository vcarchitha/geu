package com.geu.aem.web.core.models;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.models.TabSwitchModel.Tab;
import com.geu.aem.web.core.models.bean.CarouselBean;
import com.geu.aem.web.core.models.bean.ImageContainerBean;
import com.geu.aem.web.core.models.bean.TeaserTileBean;
import com.geu.aem.web.util.PropertiesUtil;

/**
 * @author aimen.sania
 * 
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ImageContainerModel {

	@Inject
	private String[] buttonValues;

	private List<ImageContainerBean> buttonItemList;

	protected final static Logger log = LoggerFactory
			.getLogger(ImageContainerModel.class);

	@PostConstruct
	public void init() {

		buttonItemList = PropertiesUtil.getListFromStringArray(buttonValues,
				ImageContainerBean.class);
	}

	/**
	 * @return ButtonValues
	 */
	public String[] getButtonValues() {
		return buttonValues;
	}

	/**
	 * @return ButtonItemList
	 */
	public List<ImageContainerBean> getButtonItemList() {
		return buttonItemList;
	}
}
