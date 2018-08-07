package com.geu.aem.web.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.web.core.service.ReCaptchaService;



@Component(service = { Servlet.class },
property = { "sling.servlet.paths=" + "/bin/geu/landingpage",
		"sling.servlet.methods=" + "POST"})
public class LandingPageServlet extends SlingAllMethodsServlet{
	private static final long serialVersionUID = 1L;
	final static Logger LOGGER = LoggerFactory.getLogger(LandingPageServlet.class);
	
	@Reference
	ReCaptchaService captchaService;
	
	@Override
	protected void doPost(SlingHttpServletRequest  request,
			SlingHttpServletResponse response) throws ServletException, IOException {
		
		response.getWriter().write(String.valueOf(captchaService.verify(request)));	
	}

}
