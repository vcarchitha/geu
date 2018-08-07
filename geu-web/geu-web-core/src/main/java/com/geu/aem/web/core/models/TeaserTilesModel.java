package com.geu.aem.web.core.models;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.geu.aem.web.core.models.bean.TeaserTileBean;
import com.geu.aem.web.util.PropertiesUtil;

/**
 * @author aimen.sania
 *
 * @param <LinkBean>
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TeaserTilesModel<LinkBean> {

	@Inject
	private String[] tiles;

	private List<TeaserTileBean> ListItems;

	Logger logger = LoggerFactory.getLogger(TeaserTilesModel.class);

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		ListItems = PropertiesUtil.getListFromStringArray(tiles,
				TeaserTileBean.class);
	}

	public String[] getTiles() {
		return tiles;
	}

	public List<TeaserTileBean> getListItems() {
		return ListItems;
	}

}
