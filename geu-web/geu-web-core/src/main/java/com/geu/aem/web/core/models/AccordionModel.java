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
import com.geu.aem.web.util.PropertiesUtil;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AccordionModel {
	
	@Inject
	private String[] tabs;

	private List<Tab> tabNames;

	protected final static Logger log = LoggerFactory
			.getLogger(AccordionModel.class);

	/**
	 * init() method prepares tabNames
	 */
	@PostConstruct
	public void init() {
		tabNames = PropertiesUtil.getListFromStringArray(tabs, Tab.class);
	}

	/**
	 * @return the tabName
	 */
	public String[] getTabs() {

		return tabs;
	}

	/**
	 * @return the tabNames
	 */
	public List<Tab> getTabNames() {
		return tabNames;
	}

	public class Tab {
		private String name;

		/**
		 * @return this method will return the name removing all the space
		 */
		public String getTrimmedName() {
			if (StringUtils.isNotBlank(name)) {
				return StringUtils
						.deleteWhitespace(name)
						.toLowerCase()
						.replaceAll(ResourceConstants.SPECIAL_CHARACTERS,
								StringUtils.EMPTY);
			} else {
				return StringUtils.EMPTY;
			}
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
	}
}
