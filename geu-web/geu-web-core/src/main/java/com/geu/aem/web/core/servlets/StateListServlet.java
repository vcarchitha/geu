package com.geu.aem.web.core.servlets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.models.bean.TextValueBean;
import com.google.gson.Gson;
/**
 * @author nitin.jangir
 *
 */
@Component(service = { Servlet.class },
property = { "sling.servlet.paths=" + "/bin/geu/statelist",
		"sling.servlet.methods=" + "GET"})
public class StateListServlet extends SlingAllMethodsServlet{

	private static final long serialVersionUID = 1L;
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	private static final String CLASS_NAME = StateListServlet.class.getSimpleName();

	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) {
		List<TextValueBean> textValueList = new ArrayList<TextValueBean>();
		try {
		String stateName = request.getParameter(ResourceConstants.STATE_NAME_PARAM);
		Resource stateResource = null;
		boolean city = false;
		if (StringUtils.isNotBlank(stateName)) {
			stateResource = request.getResourceResolver().getResource(ResourceConstants.CITIES_PATH + ResourceConstants.FORWARD_SLASH + stateName);
			city = true;
		} else {
			stateResource = request.getResourceResolver().getResource(ResourceConstants.CITIES_PATH);
		}
		if (stateResource != null) {
			Iterator<Resource> resourceIterator = stateResource.listChildren();
			while (resourceIterator.hasNext()) {
				Resource r = resourceIterator.next();
				TextValueBean textValueBean = new TextValueBean();
				if (!r.getName().equalsIgnoreCase(ResourceConstants.JCR_CONTENT)) {
					if (city) {
						textValueBean.setValue(r.getValueMap().get(ResourceConstants.JCR_TITLE, ResourceConstants.EMPTY_STRING));
					} else {
						textValueBean.setValue(r.getName());
					}
					textValueBean.setText(r.getValueMap().get(ResourceConstants.JCR_TITLE, ResourceConstants.EMPTY_STRING));
					textValueList.add(textValueBean);	
				}
			}
			Collections.sort(textValueList);
		}
		String json = new Gson().toJson(textValueList);
		response.setContentType(ResourceConstants.APPLICATION_JSON);
		response.getWriter().write(json);
	} catch (Exception e) {
			LOGGER.error("Exception in "+CLASS_NAME + e.getMessage());
	}

		
	}
}
