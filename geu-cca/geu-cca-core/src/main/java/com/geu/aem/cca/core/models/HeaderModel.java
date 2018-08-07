package com.geu.aem.cca.core.models;

import java.util.ArrayList;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.AccessDeniedException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;
import com.geu.aem.cca.beans.LinkBean;
import com.geu.aem.cca.constants.ResourceConstants;
import com.geu.aem.cca.services.GroupConfigService;
import com.geu.aem.cca.util.GEUCCAUtils;

/**
 * @author aimen.sania
 * 
 */
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderModel {

	@Inject
	@Source("script-bindings")
	private Style currentStyle;

	@ScriptVariable
	private Page currentPage;

	@Inject
	private ResourceResolver resourceResolver;

	@ScriptVariable
	private ValueMap properties;

	@OSGiService
	private GroupConfigService groupConfigService;
	private boolean typeOfUser;
	private boolean loggedInUser;
	private String userID;
	private ArrayList<LinkBean> topNavList = new ArrayList<LinkBean>();

	protected final static Logger log = LoggerFactory
			.getLogger(HeaderModel.class);

	/**
	 * post construct method to set loggedUser,topNavList
	 */
	@SuppressWarnings("restriction")
	@PostConstruct
	public void init() {

		String[] configGroup = groupConfigService.getGroupPrefix();

		Session session = this.resourceResolver.adaptTo(Session.class);
		this.userID = session.getUserID().toString();
		this.loggedInUser = getLoggedInSession(session, userID, configGroup);
		this.topNavList = getNavigationList();
	}

	/**
	 * @param session
	 * @param userID
	 * @param configGroup
	 * @return loggedUser
	 */
	private Boolean getLoggedInSession(Session session, String userID,
			String[] configGroup) {
		Boolean loggedInUser = false;
		UserManager userManager;

		try {
			if (StringUtils.isNotBlank(userID)) {
				userManager = ((JackrabbitSession) session).getUserManager();
				Authorizable user = userManager.getAuthorizable(userID);
				if (!user.isGroup()) {
					Iterator<Group> groupList = user.memberOf();
					if (null != groupList) {
						while (groupList.hasNext()) {
							Group group = groupList.next();
							String groupName = group.getID().toString();
							for (String configGroupItem : configGroup) {
								if (groupName.startsWith(configGroupItem
										.toString())) {
									if (configGroupItem
											.equals("geu-cca-admins")) {
										typeOfUser = true;
									} else {
										typeOfUser = false;
									}
									loggedInUser = true;
									break;

								}

							}

						}

					}
				}
			}
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			log.error("Error in HeaderModel -" + e.getMessage());
		} catch (UnsupportedRepositoryOperationException e) {
			// TODO Auto-generated catch block
			log.error("Error in HeaderModel -" + e.getMessage());
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			log.error("Error in HeaderModel -" + e.getMessage());
		}
		return loggedInUser;

	}

	/**
	 * @param startLevel
	 * @return navList
	 */
	private ArrayList<LinkBean> getNavigationList() {

		ArrayList<LinkBean> navList = new ArrayList<LinkBean>();
		try {
			Iterator<Page> childPages = currentPage.getAbsoluteParent(2)
					.listChildren();
			if (null != childPages) {
				while (childPages.hasNext()) {
					Page page = (Page) childPages.next();
					ValueMap valuemap = page.getProperties();
					String hideInNav = valuemap.get("hideInNav", "false");
					if (!currentPage.equals(page)) {
						if (!hideInNav.equals("true")) {
							navList.add(new LinkBean(page.getTitle(), page
									.getPath()
									+ ResourceConstants.HTML_EXTENSION));
						}
					}

				}
			}

		} catch (Exception exception) {
			log.error("Error in HeaderModel -" + exception.getMessage());
		}
		return navList;
	}

	/**
	 * @return pageList
	 */
	public ArrayList<LinkBean> getPageList() {
		return topNavList;
	}

	/**
	 * @return homeLogo
	 */
	public String getHomeLogo() {

		return currentStyle.get("homeLogo", String.class);
	}

	/**
	 * @return homeURL
	 */

	public String getHomeURL() {
		if (loggedInUser) {
			if (currentPage.getPath().toString().contains("formpage.html")) {
				return currentPage.getPath().toString()
						+ ResourceConstants.HTML_EXTENSION;
			} else {
				return GEUCCAUtils.linkTransformer(currentStyle.get("homeURL",
						String.class));
			}

		} else {
			return currentPage.getPath().toString()
					+ ResourceConstants.HTML_EXTENSION;
		}
	}

	/**
	 * @return userID
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * @return logoutLabel
	 */
	public String getLogoutLabel() {
		return currentStyle.get("logoutLabel", String.class);
	}

	/**
	 * @return loggedUser
	 */
	public boolean getLoggedInUser() {
		return loggedInUser;
	}

	public boolean isTypeOfUser() {
		return typeOfUser;
	}

	public void setTypeOfUser(boolean typeOfUser) {
		this.typeOfUser = typeOfUser;
	}
}