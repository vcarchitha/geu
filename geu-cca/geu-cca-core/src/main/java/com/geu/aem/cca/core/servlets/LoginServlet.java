package com.geu.aem.cca.core.servlets;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.auth.Authenticator;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.cca.services.GroupConfigService;

/**
 * @author aimen.sania
 * 
 */

@Component(service = Servlet.class, property = {
		"service.description=" + " CUG login servlet",
		"sling.servlet.methods=" + "POST",
		"sling.servlet.paths=" + "/bin/CCALoginServlet" })
public class LoginServlet extends SlingAllMethodsServlet {
	private static final long serialVersionUID = 1L;

	@Reference
	private GroupConfigService groupConfigService;

	@Reference
	private SlingSettingsService slingSettingsService;

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Reference
	private Authenticator authenticator;

	private ResourceResolver resourceResolver;

	private static final Logger logger = LoggerFactory
			.getLogger(LoginServlet.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.sling.api.servlets.SlingAllMethodsServlet#doPost
	 * (org.apache.sling.api.SlingHttpServletRequest, org.apache.sling.
	 * api.SlingHttpServletResponse)
	 */
	@Override
	protected void doPost(final SlingHttpServletRequest request,

	final SlingHttpServletResponse response) throws ServletException,
			IOException {

		try {
			this.resourceResolver = request.getResourceResolver();
			String username = request.getParameter("username");
			String[] configGroup = groupConfigService.getGroupPrefix();
			Session session = resourceResolver.adaptTo(Session.class);

			if (isRegisteredUser(session, username, configGroup)) {
				response.getWriter().write("success");
			} else {
				if (!isAuthorMode()) {
					if (null != authenticator) {
						authenticator.logout(request, response);
					}
				}
				response.getWriter().write("error");
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			logger.info("error" + exception);
		}
	}

	/**
	 * @param session
	 * @param username
	 * @param configGroup
	 * @return
	 */
	private boolean isRegisteredUser(Session session, String username,
			String[] configGroup) {
		boolean registeredUser = false;
		try {
			UserManager userManager;

			userManager = ((JackrabbitSession) session).getUserManager();
			if (StringUtils.isNotBlank(username)) {
				Authorizable user = userManager.getAuthorizable(username);
				if (!user.isGroup()) {
					Iterator<Group> groupList = user.memberOf();
					if (null != groupList) {
						while (groupList.hasNext()) {
							Group group = groupList.next();
							String groupName = group.getID().toString();
							for (String configGroupItem : configGroup) {
								if (groupName.startsWith(configGroupItem
										.toString())) {
									registeredUser = true;
									break;
								}

							}
						}

					}
				}
			}
		} catch (Exception exception) {
			logger.error("Error in LoginServlet -" + exception.getMessage());
		}
		return registeredUser;
	}

	/**
	 * @return boolean
	 */
	private boolean isAuthorMode() {
		if (null != slingSettingsService) {
			Set<String> runmodes = slingSettingsService.getRunModes();
			for (String runmode : runmodes) {
				if (runmode.equals("author")) {
					return true;
				}
			}
		}
		return false;

	}
}
