package com.geu.aem.web.core.models.bean;

public class DownloadBean {

	private String documentPath;
	
	private String documentTitle;
	
	private String documentFormat;
	
	private String documentFormatIcon;
	
	private String documentSize;

	public String getDocumentPath() {
		return documentPath;
	}

	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}

	public String getDocumentTitle() {
		return documentTitle;
	}

	public void setDocumentTitle(String documentTitle) {
		this.documentTitle = documentTitle;
	}

	public String getDocumentFormat() {
		return documentFormat;
	}

	public void setDocumentFormat(String documentFormat) {
		this.documentFormat = documentFormat;
	}

	public String getDocumentSize() {
		return documentSize;
	}

	public void setDocumentSize(String documentSize) {
		this.documentSize = documentSize;
	}

	public String getDocumentFormatIcon() {
		return documentFormatIcon;
	}

	public void setDocumentFormatIcon(String documentFormatIcon) {
		this.documentFormatIcon = documentFormatIcon;
	}
	
	
}
