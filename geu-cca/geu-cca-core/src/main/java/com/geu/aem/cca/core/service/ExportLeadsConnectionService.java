package com.geu.aem.cca.core.service;

import java.sql.ResultSet;
import java.util.ArrayList;

import com.geu.aem.cca.beans.LeadBean;

public interface ExportLeadsConnectionService {
	
	void processExportLeads();
	
	ArrayList<LeadBean> exportLeads(ResultSet resultset);

}
