package com.geu.aem.web.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class PropertiesUtil {
		
	private static Logger LOGGER = LoggerFactory.getLogger(PropertiesUtil.class);
	
	public static <T> List<T> getListFromStringArray(final String[] values,
			Class<T> type) {
		List<T> items = new ArrayList<T>();
		if (null == values) {
			return items;
		}
		for (String value : values) {
			Gson gson = new Gson();
			items.add(gson.fromJson(value, type));
		}
		return items;
	}

}
