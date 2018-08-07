package com.geu.aem.cca.util;

import com.geu.aem.cca.constants.ResourceConstants;

public class GEUCCAUtils {
	
	public static String linkTransformer(String url){
		String urlPath = null;
		
		if(url!=null){
			StringBuilder stringBuilder = new StringBuilder();
	                if((url.startsWith(ResourceConstants.CONTENT))){
	                    return   url+ResourceConstants.HTML_EXTENSION;
			}
			else if((url.startsWith(ResourceConstants.HTTP_PROTOCOL)) || (url.startsWith(ResourceConstants.HTTPS_PROTOCOL))){
				return url;
			}else{
				return stringBuilder.append(ResourceConstants.HTTP_PROTOCOL)
						.append(url).toString();
			}
		}			
		return urlPath;		
	}
}

