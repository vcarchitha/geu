package com.geu.aem.web.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.service.PaymentFormService;

/**
 * @author nitin.jangir
 *
 */
@Component(service = { Servlet.class },
property = { "sling.servlet.paths=" + "/bin/geu/paymentFormNumber",
		"sling.servlet.methods=" + "GET"})
public class PaymentFormNumberServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	
	protected final Logger LOGGER = LoggerFactory.getLogger(PaymentFormNumberServlet.class);
	
	@Reference
	PaymentFormService paymentFormService;

	@Override
	protected void doGet(SlingHttpServletRequest  request,
			SlingHttpServletResponse response) throws ServletException, IOException {
		String formNumber = request.getParameter(ResourceConstants.FORM_NUMBER);
		String formType = request.getParameter("formType");
		String contactNumber = ResourceConstants.EMPTY_STRING;
		if (StringUtils.isNotBlank(formNumber) && StringUtils.isNotBlank(formType)) {
			contactNumber = paymentFormService.getContactNumber(formNumber, formType);
			response = paymentFormService.processFormId(request, response, formNumber);
		}
		if (StringUtils.isNotBlank(contactNumber)) {
			response.getWriter().write(contactNumber);
		} else {
			response.getWriter().write(ResourceConstants.INVALID_FORM_NUMBER);
		}
		
	}
}
