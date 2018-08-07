package com.geu.aem.cca.core.listeners;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChange.ChangeType;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.cca.constants.ResourceConstants;
import com.geu.aem.cca.core.service.SFTPConnectionService;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;

@Component(
		service = ResourceChangeListener.class,
		immediate = true,
		property ={
			ResourceChangeListener.PATHS+"="+ResourceConstants.DAM_SFTP_FILE_LOCATION,
			ResourceChangeListener.CHANGES+"=ADDED"
		}
)
public class TransferFileResourceChangeListener implements ResourceChangeListener {
	
	@Reference 
	private ResourceResolverFactory resourceResolverFactory;	
	
	@Reference
	private SFTPConnectionService sftpConService;

    private final Logger LOGGER = LoggerFactory.getLogger(TransferFileResourceChangeListener.class);

	public void onChange(List<ResourceChange> changes) {
        for (final ResourceChange change : changes) {
        	InputStream stream = null; 
            ChangeType topic = change.getType();
            if(topic!=null){
	            if(topic.equals(ChangeType.ADDED)){            	
	    	        try {    	        	    	        	
	    	        	Map<String, Object> param = new HashMap<String, Object>();
	    		        param.put(ResourceResolverFactory.SUBSERVICE, "getTransferResourceResolver"); 
	                	String path = change.getPath();
						ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);                	
						Resource res = resourceResolver.getResource(path);
						if(res!=null){
							if(res.getResourceType().equalsIgnoreCase(ResourceConstants.DAM_ASSET)){
								Asset asset = res.adaptTo(Asset.class);
								if(asset!=null){
									Rendition original = asset.getOriginal(); 
									stream = original.getStream();
									if(sftpConService!=null){
										sftpConService.storeFile(stream);
									}
								}
							}
						}else{
							LOGGER.info("resource is null in onChange method : "+res);
						}
					} catch (LoginException e) {
						LOGGER.error("LoginException occurred in TransferFIleResource "
								+ "class"+e.getMessage());
					}finally{
						if(stream!=null){
							try {
								stream.close();
							} catch (IOException e) {
								LOGGER.error("IOException occurred in TransferFIleResource "
										+ "class"+e.getMessage());
							}
						}
					}
	            }
            }
        }
    }
}
