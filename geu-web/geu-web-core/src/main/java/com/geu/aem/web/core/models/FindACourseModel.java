package com.geu.aem.web.core.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.jcr.query.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.geu.aem.web.core.models.bean.NavigationBean;

@Model(adaptables=SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FindACourseModel {

	private static Logger LOGGER = LoggerFactory.getLogger(FindACourseModel.class);
	private static final String CLASS_NAME = FindACourseModel.class.getSimpleName();

	@ScriptVariable
	@Optional
	private ValueMap properties;

	@Self
	private SlingHttpServletRequest request;

	@Self @Via("resource")
	private Resource resource;

	private String title;

	private String parentPagePath;
	
	private String findCourseUrlParameter;

	private String[] courseItems;
	
	private List<NavigationBean> departmentList;

	@PostConstruct
	public void init() {

		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		this.title=this.properties.get("title",String.class);
		this.parentPagePath=this.properties.get("parentPagePath",String.class);
		this.courseItems=this.properties.get("courseItems",String[].class);
		this.findCourseUrlParameter=this.properties.get("findCourseUrlParameter",String.class);
		
		String courseParameter=null;
		JSONParser jsonParser = new JSONParser();
		NavigationBean bean=null;
		departmentList=new ArrayList<NavigationBean>();
		ResourceResolver resolver=resource.getResourceResolver();
		
		if(StringUtils.isNotBlank(findCourseUrlParameter))
		{
			courseParameter=request.getParameter(findCourseUrlParameter);
		}
		if(StringUtils.isNotBlank(courseParameter))
		{
			for(String course:courseItems)
			{
				try {
					JSONObject json = (JSONObject) jsonParser.parse(course);
					String queryParameter = (String) json
							.get("queryParameter");

					if (queryParameter.equals(courseParameter)) {
						String departmentPagePath = (String) json
								.get("departmentPagePath");
						
						Resource departmentResource=resolver.getResource(departmentPagePath);
						Page departmentPage=departmentResource.adaptTo(Page.class);
						if(null!=departmentPage)
						{
							String[] courseList=(String[]) departmentPage.getProperties().get("courseList");
							bean=new NavigationBean();
							bean.setPageURL(departmentPagePath);
							bean.setPageTitle(departmentPage.getTitle());
							departmentList.add(bean);
							
							if(courseList.length!=0)
							{
								for(int i=0;i<courseList.length;i++)
								{
									bean=new NavigationBean();
									bean.setPageURL(courseList[i]);
									Resource courseResource=resolver.getResource(courseList[i]);
									Page coursePage=courseResource.adaptTo(Page.class);
									bean.setPageTitle(coursePage.getTitle());
									departmentList.add(bean);
								}
							}
						}
					}
				} catch (ParseException e) {
					LOGGER.error("ParseException in "+CLASS_NAME + e.getMessage());
				} catch (Exception e) {
					LOGGER.error("Exception in "+CLASS_NAME + e.getMessage());
				}
			}
		}
		else
		{
			if(StringUtils.isNotBlank(parentPagePath))
			{
				String queryString="SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE(["+parentPagePath+"]) and s.[cq:template] = '/apps/geu-web/templates/departmentpage'";

				Iterator<Resource> queryResult = resolver.findResources(queryString, Query.JCR_SQL2);
				if(null!=queryResult)
				{
					while(queryResult.hasNext())
					{
						Resource departmentResource=queryResult.next().getParent();
						if(null!=departmentResource)
						{
							Page page=departmentResource.adaptTo(Page.class);
							boolean flag=false;
							if(null!=page.getProperties().get("hideInFindCourse"))
							{
								boolean hideInFindCourse = Boolean.valueOf(page.getProperties().get("hideInFindCourse").toString());
								if(hideInFindCourse)
								{
									flag=true;
								}
							}
							if(!flag)
							{
								bean=new NavigationBean();
								bean.setPageTitle(page.getTitle());
								bean.setPageURL(page.getPath());
								departmentList.add(bean);
							}
						}
					}
				}

				Collections.reverse(departmentList);
			}
		}
	}

	public String getParentPagePath() {
		return parentPagePath;
	}

	public String getTitle() {
		return title;
	}

	public List<NavigationBean> getDepartmentList() {
		return departmentList;
	}
}
