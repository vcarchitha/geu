package com.geu.aem.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.geu.aem.web.constants.ResourceConstants;

public class GEUWebUtils {

	protected static final Logger LOGGER = LoggerFactory.getLogger(GEUWebUtils.class);
	private static final String CLASS_NAME = GEUWebUtils.class.getSimpleName();
	protected final static Logger log = LoggerFactory
			.getLogger(GEUWebUtils.class);

	public static String linkTransformer(String url) {

		String urlPath = null;

		if (url != null) {
			StringBuilder stringBuilder = new StringBuilder();
			if ((url.startsWith(ResourceConstants.CONTENT_DAM))) {
				return url;
			} else if ((url.startsWith(ResourceConstants.CONTENT))) {
				return url + ResourceConstants.HTML_EXTENSION;
			} else if ((url.startsWith(ResourceConstants.HTTP_PROTOCOL))
					|| (url.startsWith(ResourceConstants.HTTPS_PROTOCOL))) {
				return url;
			} else {
				return stringBuilder.append(ResourceConstants.HTTP_PROTOCOL)
						.append(url).toString();
			}
		}
		return urlPath;
	}

	public static String getJsonValue(String response,String key)
	{
		final String METHOD_NAME = new Object() {
		}.getClass().getEnclosingMethod().getName();

		String status=null;
		try 
		{
			JSONParser parser = new JSONParser();
			JSONObject jsonObject;

			jsonObject = (JSONObject) parser.parse(response);
			status= jsonObject.get(key).toString();
		} catch (ParseException e) {
			LOGGER.error("Parse Exception in "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Exception in "+METHOD_NAME+ResourceConstants.SPACE+CLASS_NAME+ResourceConstants.SPACE+e.getMessage());
		}

		return status;
	}

	/**
	 * method to read data from excel file
	 * @param resolverFactory
	 * @param excelPath
	 * @return Iterator<Row>
	 */
	public static Iterator<Row>  getExcelData(ResourceResolverFactory resolverFactory,
			String excelPath) {
		Iterator<Row> iterator = null;
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "getGEUUser");
			ResourceResolver resolver = null;
			resolver = resolverFactory.getServiceResourceResolver(param);
			Resource resource=	resolver.getResource(excelPath+ResourceConstants.RENDITION_ORIGINAL);
			ValueMap properties = resource.adaptTo(ValueMap.class);
			InputStream inputStream = properties.get("jcr:data", InputStream.class);
			Workbook workbook = new XSSFWorkbook(inputStream);
			Sheet sheet = workbook.getSheetAt(0);
			iterator = sheet.iterator();
			if(iterator.hasNext())iterator.next();
		} catch (IOException e) {
			LOGGER.error("IOException in getExcelData method of GEUWebUtils" + e);
		} catch (LoginException e) {
			LOGGER.error("LoginException in getExcelData method of GEUWebUtils" + e);
		} catch (NullPointerException e) {
			LOGGER.error("NullPointerException in getExcelData method of GEUWebUtils: " +excelPath+ "  not found " + e);
	    }
		return iterator;

	}


	/**
	 * method to read data from the json file
	 * @param resolverFactory
	 * @param jsonPath
	 * @return JSONObject
	 */
	public static JSONObject  getJSONData(ResourceResolverFactory resolverFactory,
			String jsonPath) {
		JSONObject jsonObject =null;

		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "getGEUUser");
			ResourceResolver resolver = null;
			resolver = resolverFactory.getServiceResourceResolver(param);
			Resource resource=	resolver.getResource(jsonPath+ResourceConstants.RENDITION_ORIGINAL);
			ValueMap properties = resource.adaptTo(ValueMap.class);
			InputStream inputStream = properties.get("jcr:data", InputStream.class);
			JSONParser jsonParser = new JSONParser();
			jsonObject = (JSONObject)jsonParser.parse(
					new InputStreamReader(inputStream, "UTF-8"));

		} catch (UnsupportedEncodingException e) {
			LOGGER.error("UnsupportedEncodingException in getJSONData method of GEUWebUtils" + e);
		} catch (IOException e) {
			LOGGER.error("IOException in getJSONData method of GEUWebUtils" + e);
		} catch (ParseException e) {
			LOGGER.error("ParseException in getJSONData method of GEUWebUtils" + e);
		} catch (LoginException e) {
			LOGGER.error("LoginException in getJSONData method of GEUWebUtils" + e);
		}catch (NullPointerException e) {
			LOGGER.error("NullPointerException in getJSONData method of GEUWebUtils: " +jsonPath+ " not found " + e);
	    }

		return jsonObject;

	}	
	
}
