package com.geu.aem.web.core.models;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.models.bean.DownloadBean;
import com.geu.aem.web.util.PropertiesUtil;

/**
 * @author nitin.jangir
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DownloadModel {
	
	@Inject
	private String headerTitle;
	
	@Inject
	private String[] downloads;
	
	@Self
	private Resource resource;

	private List<DownloadBean> tempListItems;
	
	private List<DownloadBean> downloadListItems;
	
	@PostConstruct
	public void init() {
		tempListItems = PropertiesUtil.getListFromStringArray(downloads,
				DownloadBean.class);
		downloadListItems = new ArrayList<DownloadBean>();
		Iterator<DownloadBean> iterator = tempListItems.iterator();
		while (iterator.hasNext()) {
			String documentPath = iterator.next().getDocumentPath();
			Resource assetResource = resource.getResourceResolver().getResource(documentPath);
			if (assetResource != null) {
				Asset asset = assetResource.adaptTo(Asset.class);
				if (asset != null) {
					DownloadBean downloadBean = new DownloadBean();
					downloadBean.setDocumentSize(getFileSize(asset.getOriginal().getSize()));
					downloadBean.setDocumentPath(documentPath);
					
					downloadBean.setDocumentTitle(asset.getName());
					String documentFormat = getDocFormat(asset.getMetadataValue(DamConstants.DC_FORMAT));
					downloadBean.setDocumentFormatIcon(documentFormat);
					downloadBean.setDocumentFormat(documentFormat.toUpperCase());
					
					downloadListItems.add(downloadBean);
				}
			}
		}
	}

	private String getFileSize(long size) {
		DecimalFormat df = new DecimalFormat(ResourceConstants.DECIMAL_FORMAT);
		String fileSize = ResourceConstants.EMPTY_STRING;
	    float sizeKb = 1024.0f;
	    float sizeMb = sizeKb * sizeKb;
	    float sizeGb = sizeMb * sizeKb;

	    if(size < sizeMb) {
	    	fileSize = df.format(size / sizeKb)+ ResourceConstants.KB;
	        return fileSize;
	    }
	    else if(size < sizeGb) { 
	    	fileSize = df.format(size / sizeMb) + ResourceConstants.MB;
	        return fileSize;
	    }
	    return fileSize;
	}

	private String getDocFormat(String metadataValue) {
		String docFormat = "";
		String[] docArray = metadataValue.split(ResourceConstants.FORWARD_SLASH);
		if (docArray[docArray.length-1].equalsIgnoreCase(ResourceConstants.PDF)) {
			docFormat = ResourceConstants.PDF;
		} 
		if (docArray[docArray.length-1].equalsIgnoreCase(ResourceConstants.EXCEL_FORMAT)) {
			docFormat = ResourceConstants.EXCEL;
		} 
		if (docArray[docArray.length-1].equalsIgnoreCase(ResourceConstants.WORD_FORMAT)) {
			docFormat = ResourceConstants.WORD;
		} 
		if (docArray[docArray.length-1].equalsIgnoreCase(ResourceConstants.PPT_FORMAT)) {
			docFormat = ResourceConstants.PPT;
		}
		return docFormat;
	}

	public String[] getDownloads() {
		return downloads;
	}

	public List<DownloadBean> getDownloadListItems() {
		return downloadListItems;
	}

	public String getHeaderTitle() {
		return headerTitle;
	}

}
