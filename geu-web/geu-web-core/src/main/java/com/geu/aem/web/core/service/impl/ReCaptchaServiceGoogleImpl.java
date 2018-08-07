package com.geu.aem.web.core.service.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.web.core.service.ReCaptchaService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@Component(service = ReCaptchaService.class,configurationPolicy=ConfigurationPolicy.REQUIRE)
@Designate(ocd = ReCaptchaServiceConfiguration.class)
public class ReCaptchaServiceGoogleImpl implements ReCaptchaService {
	
	private ReCaptchaServiceConfiguration config;

	private static final Logger log = LoggerFactory.getLogger(ReCaptchaServiceGoogleImpl.class);
	public static final String DEF_USER_AGENT = "Mozilla/5.0";


	@Activate
	@Modified
	public void activate(ReCaptchaServiceConfiguration config) {
		this.config = config;
	}
	

	@Override
	public String getSiteKey() {
		return config.siteKey();
	}


	@Override
	public boolean verify(SlingHttpServletRequest request) {
		
		boolean isCaptchaVerified = false;
		String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
		if (gRecaptchaResponse == null || "".equals(gRecaptchaResponse)) {
			return false;
		}

		HttpsURLConnection urlConnection = null;
		DataOutputStream writer = null;
		BufferedReader reader = null;
		try {
			URL obj = new URL(config.verificationURL());
			urlConnection = (HttpsURLConnection) obj.openConnection();

			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("User-Agent", DEF_USER_AGENT);
			urlConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			String postParams = "secret=" + config.privateKey() + "&response=" + gRecaptchaResponse;

			urlConnection.setDoOutput(true);
			writer = new DataOutputStream(urlConnection.getOutputStream());
			writer.writeBytes(postParams);
			writer.flush();
			writer.close();

			int responseCode = urlConnection.getResponseCode();
			
			log.debug("Sending 'POST' request to URL ", config.verificationURL());
			log.debug("Post parameters " + postParams);
			log.debug("Response Code " + responseCode);
			
			if(responseCode == 200){
				reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
	
				while ((inputLine = reader.readLine()) != null) {
					response.append(inputLine);
				}
				reader.close();
	            
				JsonObject responseObj = (JsonObject) new JsonParser().parse(response.toString());
	
				log.debug("Response status ", responseObj.get("success"));
				isCaptchaVerified = responseObj.get("success").getAsBoolean();
			}
	
		} catch (Exception e) {
			
			log.error("Exception Occurred while validating captcha", e.getMessage());
			
		} finally {
            if (urlConnection != null){
            	urlConnection.disconnect();
            }
            if (writer != null) {
                try {
                	writer.close();
                } catch (Exception e) {
                	log.error("Exception Occurred while closing DataOutputStream ", e.getMessage());
                }
            }     
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                	log.error("Exception Occurred while closing BufferedReader", e.getMessage());
                }
            }     
		}
		
		return isCaptchaVerified;
	}

}
