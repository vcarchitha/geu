package com.geu.aem.web.core.models;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.geu.aem.web.core.models.bean.SuccessStoriesBean;
import com.geu.aem.web.util.GEUWebUtils;

/**
 * @author aimen.sania
 * 
 */
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SuccessStoriesModel {

	@ScriptVariable
	@Optional
	private ValueMap properties;

	@Self
	private SlingHttpServletRequest request;

	private List<SuccessStoriesBean> storiesItems;

	private String readMoreText;

	private String readMoreUrl;
	private String[] parameterItems;
	private String[] defaultStories;

	protected final static Logger log = LoggerFactory
			.getLogger(SuccessStoriesModel.class);

	@PostConstruct
	protected void init() {

		this.readMoreText = this.properties.get("readMoreText", String.class);
		this.readMoreUrl = this.properties.get("readMoreUrl", String.class);
		this.parameterItems = this.properties.get("parameterItems",
				String[].class);
		this.defaultStories = this.properties.get("defaultStories",
				String[].class);
		String parameter = request.getParameter("program");
		JSONParser jsnobject = new JSONParser();
		if (StringUtils.isNotBlank(parameter)) {
			if (this.parameterItems != null) {
				for (String value : parameterItems) {
					try {
						JSONObject json = (JSONObject) jsnobject.parse(value);
						String queryParameter = (String) json
								.get("queryParameter");
						if (queryParameter.equals(parameter)) {
							JSONArray jsonArray = (JSONArray) json
									.get("values");
							storiesItems = new ArrayList<SuccessStoriesBean>();
							for (int i = 0; i < jsonArray.size(); i++) {
								JSONObject json1 = (JSONObject) jsnobject
										.parse(jsonArray.get(i).toString());
								String title = json1.get("title").toString();
								String description = json1.get("description")
										.toString();
								String buttonURL = json1.get("buttonURL")
										.toString();
								String buttonLabel = json1.get("buttonLabel")
										.toString();
								String image = json1.get("image").toString();
								SuccessStoriesBean successStoriesBean = new SuccessStoriesBean(
										title, description, image, buttonLabel,
										buttonURL);
								if (!StringUtils.isBlank(title)
										&& !StringUtils.isBlank(description)
										&& !StringUtils.isBlank(image))
									storiesItems.add(successStoriesBean);
							}
							break;
						}
					} catch (ParseException e) {
						log.error("Error in SuccessStoriesModel"
								+ e.getMessage());
					}
				}
			}
		}
		if (storiesItems == null) {
			storiesItems = new ArrayList<SuccessStoriesBean>();
			if (this.defaultStories != null) {
				for (String value : defaultStories) {
					JSONObject json;
					try {
						json = (JSONObject) jsnobject.parse(value);
						String title = json.get("title").toString();
						String description = json.get("description").toString();
						String buttonURL = json.get("buttonURL").toString();
						String buttonLabel = json.get("buttonLabel").toString();
						String image = json.get("image").toString();
						SuccessStoriesBean successStoriesBean = new SuccessStoriesBean(
								title, description, image, buttonLabel,
								buttonURL);
						if (!StringUtils.isBlank(title)
								&& !StringUtils.isBlank(description)
								&& !StringUtils.isBlank(image))
							storiesItems.add(successStoriesBean);
					} catch (ParseException e) {
						log.error("Error in SuccessStoriesModel"
								+ e.getMessage());
					}
				}
			}
		}

	}

	/**
	 * @return ParameterItems
	 */
	public String[] getParameterItems() {
		return parameterItems;
	}

	public String getReadMoreText() {
		return readMoreText;
	}

	public String getReadMoreUrl() {
		return GEUWebUtils.linkTransformer(readMoreUrl);
	}

	public List<SuccessStoriesBean> getStoriesItems() {
		return storiesItems;
	}

}
