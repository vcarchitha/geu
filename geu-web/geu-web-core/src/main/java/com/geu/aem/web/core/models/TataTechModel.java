package com.geu.aem.web.core.models;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.geu.aem.web.core.models.bean.TataTechBean;
import com.geu.aem.web.util.PropertiesUtil;

/**
 * @author aimen.sania
 * 
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TataTechModel {

	@Inject
	private String[] programValues;

	private List<TataTechBean> programItemList;

	protected final static Logger log = LoggerFactory
			.getLogger(TataTechModel.class);

	@PostConstruct
	public void init() {

		programItemList = PropertiesUtil.getListFromStringArray(programValues,
				TataTechBean.class);
	}

	public String[] getProgramValues() {
		return programValues;
	}

	public List<TataTechBean> getProgramItemList() {
		return programItemList;
	}

}
