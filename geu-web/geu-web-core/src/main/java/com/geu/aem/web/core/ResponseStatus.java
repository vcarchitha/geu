/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.geu.aem.web.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;

public class ResponseStatus extends WCMUsePojo {

	protected final Logger LOGGER = LoggerFactory.getLogger(ResponseStatus.class);

	@Override
	public void activate() {
		try
		{
			String urlPath=getRequest().getRequestURI().toString();
			if(null!=urlPath)
			{
				if(urlPath.contains("/content/geu"))
				{
					getResponse().sendRedirect("/content/geu/en/error-page/404.html");
				}
				else if(urlPath.contains("/content/gehu"))
				{
					getResponse().sendRedirect("/content/gehu/en/error-page/404.html");
				}
				else if(urlPath.contains("/content/geu-cca"))
				{
					getResponse().sendRedirect("/content/geu-cca/404.html");
				}
			}
			getResponse().setStatus(404);
			getResponse().setContentType("text/html");
		} catch(Exception e)
		{
			LOGGER.error("Exception in 404 Response Status Class "+e.getMessage());
		}
	}
}