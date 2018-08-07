package com.geu.aem.cca.services.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.cca.services.GroupConfig;
import com.geu.aem.cca.services.GroupConfigService;

/**
 * @author aimen.sania
 * 
 */
@Component(service = GroupConfigService.class, immediate = true)
@Designate(ocd = GroupConfig.class)
public class GroupConfigServiceImpl implements GroupConfigService {
	private static final Logger log = LoggerFactory
			.getLogger(GroupConfigServiceImpl.class);

	private GroupConfig config;

	/**
	 * @param config
	 */
	@Activate
	protected void activate(GroupConfig config) {
		this.config = config;
	}

	/**
	 * @param config
	 */
	@Modified
	protected void modified(GroupConfig config) {
		this.config = config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.geu.aem.cca.services.GroupConfigService#getGroupPrefix()
	 */
	public String[] getGroupPrefix() {
		String[] group = config.getGroupPrefix();
		return group;
	}
}
