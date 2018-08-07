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
import com.geu.aem.web.core.models.bean.AwardsBean;
import com.geu.aem.web.core.models.bean.CarouselBean;
import com.geu.aem.web.core.models.bean.ImageContainerBean;
import com.geu.aem.web.core.models.bean.CardsWithTextBean;
import com.geu.aem.web.core.models.bean.TeaserTileBean;
import com.geu.aem.web.util.PropertiesUtil;

/**
 * @author aimen.sania
 * 
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AwardsModel {

	@Inject
	private String[] awardValues;

	@Inject
	private String heading;

	private List<AwardsBean> awardsList;

	protected final static Logger log = LoggerFactory
			.getLogger(AwardsModel.class);

	@PostConstruct
	public void init() {

		awardsList = PropertiesUtil.getListFromStringArray(awardValues,
				AwardsBean.class);
	}


	public List<AwardsBean> getAwardsList() {
		return awardsList;
	}


	public String[] getAwardValues() {
		return awardValues;
	}

	public String getHeading() {
		return heading;
	}
}
