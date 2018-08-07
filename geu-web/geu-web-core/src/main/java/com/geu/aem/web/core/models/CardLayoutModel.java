package com.geu.aem.web.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.geu.aem.web.core.models.bean.CardLayoutBean;
import com.geu.aem.web.util.GEUWebUtils;
import com.geu.aem.web.util.PropertiesUtil;

/**
 * @author aimen.sania
 * 
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CardLayoutModel {

	@Self
	private Resource resource;

	@Inject
	private ResourceResolver resourceResolver;

	@Inject
	private String parentPath;

	@Inject
	private String[] staticContent;

	private List<CardLayoutBean> columnItemList;

	private static Logger LOGGER = LoggerFactory
			.getLogger(CardLayoutModel.class);

	@PostConstruct
	public void init() {
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		Page currentPage = pageManager.getContainingPage(this.resource);
		if (null != parentPath) {
			if (currentPage != null) {
				Iterator<Page> childPages = currentPage.listChildren();
				columnItemList = new ArrayList<CardLayoutBean>();
				while (childPages.hasNext()) {
					CardLayoutBean cardLayoutBean = new CardLayoutBean();
					Page childpage = childPages.next();
					String pageUrl = GEUWebUtils.linkTransformer(childpage
							.getPath());
					if (StringUtils.isNotBlank(pageUrl)) {
						cardLayoutBean.setPageUrl(pageUrl);
					}
					ValueMap property = childpage.getProperties();
					String contentPageImage = property.get("contentPageImage",
							"");
					if (StringUtils.isNotBlank(contentPageImage)) {
						cardLayoutBean.setImage(contentPageImage);
					}
					String contentPageTitle = property.get("jcr:title", "");
					if (StringUtils.isNotBlank(contentPageTitle)) {
						cardLayoutBean.setTitle(contentPageTitle);
					}
					String contentPageDescription = property.get(
							"contentPageDescription", "");
					if (StringUtils.isNotBlank(contentPageDescription)) {
						cardLayoutBean.setDescription(contentPageDescription);
					}
					columnItemList.add(cardLayoutBean);
				}
			}
		} else if (null != staticContent) {
			columnItemList = new ArrayList<CardLayoutBean>();
			columnItemList = PropertiesUtil.getListFromStringArray(
					staticContent, CardLayoutBean.class);
		}
	}

	/**
	 * @return ColumnItemList
	 */
	public List<CardLayoutBean> getColumnItemList() {
		return columnItemList;
	}
}
