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

import com.geu.aem.web.core.models.bean.CarouselBean;

/**
 * @author aimen.sania
 * 
 */
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CarouselModel {

	@ScriptVariable
	@Optional
	private ValueMap properties;

	@Self
	private SlingHttpServletRequest request;

	private List<CarouselBean> imagePathItems;

	private String[] parameterItems;
	private String[] defaultImages;

	protected final static Logger log = LoggerFactory
			.getLogger(CarouselModel.class);

	@PostConstruct
	protected void init() {

		this.parameterItems = this.properties.get("parameterItems",
				String[].class);
		this.defaultImages = this.properties.get("defaultImages",
				String[].class);
		if (this.parameterItems != null && this.defaultImages != null) {
			String parameter = request.getParameter("program");
			JSONParser jsnobject = new JSONParser();

			if (StringUtils.isNotBlank(parameter)) {
				for (String value : parameterItems) {
					try {
						JSONObject json = (JSONObject) jsnobject.parse(value);
						String queryParameter = (String) json
								.get("queryParameter");

						if (queryParameter.equals(parameter)) {
							JSONArray jsonArray = (JSONArray) json
									.get("imagePath");
							imagePathItems = new ArrayList<CarouselBean>();
							for (int i = 0; i < jsonArray.size(); i++) {
								JSONObject json1 = (JSONObject) jsnobject
										.parse(jsonArray.get(i).toString());
								String imagePath = json1.get("imagePath")
										.toString();
								CarouselBean carouselBean= new CarouselBean();
								carouselBean.setImagePath(imagePath);
                                if(json1.containsKey("linkPath")&& StringUtils.isNotBlank(json1.get("linkPath").toString())){
									carouselBean.setLinkPath(json1.get("linkPath").toString());
								}
								
								if (null!= carouselBean)
									imagePathItems.add(carouselBean);
							}
							break;
						}
					} catch (ParseException e) {
						log.error("Error in CarouselModel" + e.getMessage());
					}
				}
			}

			if (imagePathItems == null) {
				imagePathItems = new ArrayList<CarouselBean>();
				for (String value : defaultImages) {
					JSONObject json;
					try {
						json = (JSONObject) jsnobject.parse(value);
						String imgPath = json.get("imgPath").toString();
						CarouselBean carouselBean= new CarouselBean();
						carouselBean.setImagePath(imgPath);
						if(json.containsKey("linkPath")&& StringUtils.isNotBlank(json.get("linkPath").toString())){
							carouselBean.setLinkPath(json.get("linkPath").toString());
						}
						if (null!= carouselBean)
							imagePathItems.add(carouselBean);
					} catch (ParseException e) {
						log.error("Error in CarouselModel" + e.getMessage());
					}
				}
			}
		}
	}

	/**
	 * @return ImagePathItems
	 */
	public List<CarouselBean> getImagePathItems() {
		return imagePathItems;
	}
	/**
	 * @return ParameterItems
	 */
	public String[] getParameterItems() {
		return parameterItems;
	}
}
