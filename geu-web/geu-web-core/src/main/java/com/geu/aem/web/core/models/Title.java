package com.geu.aem.web.core.models;


import org.osgi.annotation.versioning.ConsumerType;

@ConsumerType
public abstract interface Title
{
  public static final String PN_DESIGN_DEFAULT_TYPE = "type";

  public abstract String getText();

  public abstract String getType();
}