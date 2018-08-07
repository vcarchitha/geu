package com.geu.aem.web.core.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;
import com.geu.aem.web.core.models.bean.BreadcrumbBean;


/**
 * The class BreadcrumbModel is used in accordance with the breadcrumb component and
 * provides methods for breadcrumb related items.
 * 
 * @author smrithi.g
 */
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BreadcrumbModel {
	
	private static Logger LOGGER = LoggerFactory.getLogger(BreadcrumbModel.class);

	  @ScriptVariable
	  private ValueMap properties;

	  @ScriptVariable
	  private Style currentStyle;

	  @ScriptVariable
	  private Page currentPage;
	  private boolean showHidden;
	  private boolean hideCurrent;
	  private int startLevel;
	  private List<BreadcrumbBean> items;

	  /**
	 * PostConstruct method
	 */
	@PostConstruct
	  private void initModel()
	  {
	    this.startLevel = ((Integer)this.properties.get("startLevel", 
	    		this.currentStyle.get("startLevel", Integer.valueOf(2)))).intValue();
	    this.showHidden = ((Boolean)this.properties.get("showHidden", 
	    		this.currentStyle.get("showHidden", Boolean.valueOf(false)))).booleanValue();
	    this.hideCurrent = ((Boolean)this.properties.get("hideCurrent", 
	    		this.currentStyle.get("hideCurrent", Boolean.valueOf(false)))).booleanValue();
 	  }

	  /**
	 * @return Collection<NavigationItem>
	 */
	public Collection<BreadcrumbBean> getItems()
	  {
	    if (items == null) {
	      items = new ArrayList();
	      items = createItems();
	    }
	    return items;
	  }

	  /**
	 * @return List<NavigationItem>
	 */
	private List<BreadcrumbBean> createItems() {
	    int currentLevel = currentPage.getDepth();
	    addNavigationItems(currentLevel);
	    return this.items;
	  }

	  /**
	 * @param currentLevel
	 */
	private void addNavigationItems(int currentLevel) {
	    while (this.startLevel < currentLevel) {
	      Page page = this.currentPage.getAbsoluteParent(this.startLevel);
	      if (page != null) {
	        boolean isActivePage = page.equals(this.currentPage);
	        if ((isActivePage) && (this.hideCurrent)) {
	          break;
	        }
	        if (checkIfNotHidden(page)) {
	          BreadcrumbBean breadcrumBean = new BreadcrumbBean(page, isActivePage);
	          this.items.add(breadcrumBean);
	        }
	      }
	      this.startLevel += 1;
	    }
	  }

	  /**
	 * @param page
	 * @return boolean
	 */
	private boolean checkIfNotHidden(Page page) {
	    return (!page.isHideInNav()) || (this.showHidden);
	  }

}
