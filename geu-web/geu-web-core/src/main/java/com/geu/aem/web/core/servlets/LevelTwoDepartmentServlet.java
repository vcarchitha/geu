package com.geu.aem.web.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.models.bean.FacultyDepartmentBean;
import com.google.gson.Gson;

/**
 * @author nitin.jangir
 *
 */
@Component(service = { Servlet.class },
property = { "sling.servlet.paths=" + "/bin/geu/levelTwoDepartment",
		"sling.servlet.methods=" + "GET", "sling.servlet.extensions=" + "json" })
public class LevelTwoDepartmentServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(final SlingHttpServletRequest request,
			final SlingHttpServletResponse response) throws IOException {
		Resource resource = request.getResourceResolver().getResource(ResourceConstants.LEVELTWO_DEPARTMENT_TAG_PATH);
		Iterator<Resource> resourceIterator = resource.listChildren();
		List<FacultyDepartmentBean> departmentList = new ArrayList<FacultyDepartmentBean>();
		ResourceResolver resourceResolver = request.getResourceResolver();
		TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
		while (resourceIterator != null && resourceIterator.hasNext()) {
			Resource tagResource = resourceIterator.next();
			FacultyDepartmentBean facultyDepartmentBean = new FacultyDepartmentBean();
			Tag tag = tagManager.resolve(tagResource.getPath());
			facultyDepartmentBean.setDepartmentTitle(tag.getTitle());
			facultyDepartmentBean.setDepartmentValue(tag.getTagID());
			departmentList.add(facultyDepartmentBean);
		}
		String json = new Gson().toJson(departmentList);
		response.setContentType("application/json");
		response.getWriter().write(json);
	}
}
