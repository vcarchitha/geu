package com.geu.aem.cca.core.service;

import java.sql.ResultSet;
import java.util.ArrayList;

import com.geu.aem.cca.beans.LeadBean;

public interface ExportLeadsService {

		ArrayList<LeadBean> exportLeads(ResultSet resultset);
}
