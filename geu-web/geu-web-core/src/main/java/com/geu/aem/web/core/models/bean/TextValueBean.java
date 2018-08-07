package com.geu.aem.web.core.models.bean;

public class TextValueBean implements Comparable<TextValueBean> {

	private String text;

	private String value;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int compareTo(TextValueBean o) {
		return value.compareTo(o.value);
	}
}
