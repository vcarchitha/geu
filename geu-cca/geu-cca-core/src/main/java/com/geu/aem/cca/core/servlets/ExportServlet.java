package com.geu.aem.cca.core.servlets;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.geu.aem.cca.constants.ResourceConstants;

@Component(service = Servlet.class, property = {
		"service.description=" + " New Export Lead Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST,
		"sling.servlet.paths=" + "/bin/exportLeadServlet" })
public class ExportServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;
	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Reference
	private QueryBuilder builder;

	private String resourcePath = null;

	private final Logger LOGGER = LoggerFactory.getLogger(ExportServlet.class);

	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		Map<String, Object> sessionParam = new HashMap<String, Object>();
		sessionParam.put(ResourceResolverFactory.SUBSERVICE,
				"getTransferResourceResolver");
		if (resourceResolverFactory != null) {
			ResourceResolver resResolver;
			try {
				resResolver = resourceResolverFactory
						.getServiceResourceResolver(sessionParam);
				Session session = resResolver.adaptTo(Session.class);
				getExportFilePath(session);
				if (resourcePath != null) {
					response.getWriter().write(resourcePath);
				}

			} catch (Exception e) {
				LOGGER.error("Exception occurred in doPost method of Export Servlet "
						+ e.getMessage());
			}

		}

	}

	private String getExportFilePath(Session session) {
		Map<String, String> map = new HashMap<String, String>();
		String path = "/content/dam/cca/exportLeads";
		map.put("path", path);
		map.put("type", "dam:Asset");
		map.put("p.offset", "0");
		map.put("p.limit", "-1");
		Query query = builder.createQuery(PredicateGroup.create(map), session);
		query.setStart(0);
		query.setHitsPerPage(20);
		SearchResult result = query.getResult();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String todaysDate=dateFormat.format(date);
		LOGGER.info("Today's Date:"+todaysDate);
		List<String> list = new ArrayList<String>();
		try {
			for (Hit hit : result.getHits()) {
				String title = hit.getTitle();
				path = hit.getPath();
				if(StringUtils.isNotBlank(title))
				{
					if (title.contains(todaysDate)) {
						resourcePath = path;
					}
				}
				
				list.add(title);
			}
		} catch (Exception e) {
			LOGGER.error("Exception occurred in doPost method of Export Servlet "
					+ e.getMessage());
		}

		return resourcePath;
	}
}
