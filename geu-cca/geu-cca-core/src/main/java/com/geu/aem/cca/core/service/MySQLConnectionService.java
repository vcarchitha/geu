package com.geu.aem.cca.core.service;

import java.util.Map;

import com.geu.aem.cca.beans.LeadBean;
import com.geu.aem.cca.beans.LeadUniqueKeyBean;

public interface MySQLConnectionService {
	
	Map<LeadUniqueKeyBean, LeadBean> readFile();

	void writeCSV();
	
	void updateTableFromCSV(String exportPath);

}
