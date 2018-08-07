package com.geu.aem.web.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
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
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.models.bean.FacultyBean;
import com.google.gson.Gson;

/**
 * @author nitin.jangir
 *
 */
@Component(service = { Servlet.class },
property = { "sling.servlet.paths=" + "/bin/geu/faculty",
		"sling.servlet.methods=" + "GET", "sling.servlet.extensions=" + "json" })
public class FacultyServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;
	
	@Reference
    private QueryBuilder queryBuilder;
	
	Logger logger = LoggerFactory.getLogger(FacultyServlet.class);
	
	@Override
	protected void doGet(final SlingHttpServletRequest request,
			final SlingHttpServletResponse response) throws IOException {
		final Map<String, String> map = new HashMap<String, String>();
		List<FacultyBean> facultyList = new ArrayList<FacultyBean>();
		String departmentName = request.getParameter(ResourceConstants.DEPARTMENT_NAME);
		String engineeringDepartmentName = request.getParameter(ResourceConstants.DEPARTMENT_NAME2);
		String facultyType = request.getParameter(ResourceConstants.FACULTY_TYPE);
		if (StringUtils.isNotBlank(facultyType) && !facultyType.equalsIgnoreCase(ResourceConstants.UNDEFINED)) {
			map.put(ResourceConstants.TYPE, ResourceConstants.CQ_PAGECONTENT);
			map.put(ResourceConstants.PATH, facultyType);
			map.put(ResourceConstants.P_LIMIT, ResourceConstants.MINUS_ONE);
			if (StringUtils.isNotBlank(departmentName)) {
				map.put(ResourceConstants.TAGID_PROPERTY, ResourceConstants.DEPARTMENT_TAGS);
		        map.put(ResourceConstants.TAGID_VALUE1, departmentName);
		        map.put(ResourceConstants.TAGID_VALUE2, engineeringDepartmentName);
			}
	        ResourceResolver resourceResolver = request.getResourceResolver();
	        Query query = queryBuilder.createQuery(PredicateGroup.create(map), resourceResolver.adaptTo(Session.class));
	        TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
	        SearchResult result = query.getResult();
	        try {
	        	if (!result.getHits().isEmpty()) {
			        for (final Hit hit : result.getHits()) {
			        	FacultyBean facultyBean = new FacultyBean(); 
			        	ValueMap properties = hit.getProperties();
			        	String tags = properties.get(ResourceConstants.DEPARTMENT_TAGS, ResourceConstants.EMPTY_STRING);
			        	Tag tag = tagManager.resolve(tags);
			        	facultyBean.setName(properties.get(ResourceConstants.JCR_TITLE, ResourceConstants.EMPTY_STRING));
			        	facultyBean.setDescription(properties.get(ResourceConstants.FACULTY_DESCRIPTION, ResourceConstants.EMPTY_STRING));
			        	facultyBean.setJobTitle(properties.get(ResourceConstants.JOB_TITLE, ResourceConstants.EMPTY_STRING));
			        	facultyBean.setImageUrl(properties.get(ResourceConstants.FACULTY_IMAGE, ResourceConstants.EMPTY_STRING));
			        	if (tag != null) {
			        		facultyBean.setDepartment(tag.getTitle());
			        	}
			        	
			        	facultyList.add(facultyBean);
			        }
			        String json = new Gson().toJson(facultyList);
					response.setContentType("application/json");
					response.getWriter().write(json);
	        	} else {
	        		response.getWriter().write(ResourceConstants.NO_RESULTS_FOUND);
	        	}
	        } catch (RepositoryException e) {
				logger.error("Repository Exception in FacultyServlet" + e);
			}
		}
	}

}
