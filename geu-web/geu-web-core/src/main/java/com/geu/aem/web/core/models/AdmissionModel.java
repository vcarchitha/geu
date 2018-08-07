package com.geu.aem.web.core.models;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import com.geu.aem.web.core.models.bean.AdmissionBean;
import com.geu.aem.web.util.GEUWebUtils;
import com.geu.aem.web.util.PropertiesUtil;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AdmissionModel {

	@Inject
	private String[] interest;

	private List<AdmissionBean> interestList;
	
	@Inject
	private String[] tenBoard;
	
	private List<AdmissionBean> tenBoardList;
	
	@Inject
	private String[] twelveBoard;	
	
	private List<AdmissionBean> twelveBoardList;
	
	@Inject
	private String[] status;
	
	private List<AdmissionBean> statusList;
	
	@Inject 
	private String videoMessage;
	
	@Inject
	private String essayQuestion1;
	
	@Inject
	private String essayQuestion2;	
	
	@Inject
	private String paymentTitle;
	
	@Inject
	private String paymentTermsLink;
	
	@Inject
	private String paymentDeclarationText;
	
	@Inject
	private String paymentAmt;
	
	@Inject
	private String paymenttermsText;	

	@Inject
	private String paymentGatewayText;
	
	@Inject
	private String offlinePaymentText;
	
	@Inject
	private String onlinePaymentText;
	
	@Inject
	private String paymentLink;	
	
	@PostConstruct
	public void init() {
		interestList = PropertiesUtil.getListFromStringArray(
				interest, AdmissionBean.class);
		
		tenBoardList = PropertiesUtil.getListFromStringArray(
				tenBoard, AdmissionBean.class);
		
		twelveBoardList = PropertiesUtil.getListFromStringArray(
				twelveBoard, AdmissionBean.class); 
		
		statusList = PropertiesUtil.getListFromStringArray(
				status, AdmissionBean.class);		
	}

	public List<AdmissionBean> getTenBoardList() {
		return tenBoardList;
	}

	public List<AdmissionBean> getTwelveBoardList() {
		return twelveBoardList;
	}

	public List<AdmissionBean> getInterestList() {
		return interestList;
	}
	
	public List<AdmissionBean> getStatusList() {
		return statusList;
	}
	
	public String getVideoMessage() {
		return videoMessage;
	}

	public String getEssayQuestion1() {
		return essayQuestion1;
	}

	public String getEssayQuestion2() {
		return essayQuestion2;
	}
	
	public String getPaymentTitle() {
		return paymentTitle;
	}

	public String getPaymentTermsLink() {
		String termsLink =  GEUWebUtils.linkTransformer(this.paymentTermsLink);
		return termsLink;
	}

	public String getPaymentDeclarationText() {
		return paymentDeclarationText;
	}

	public String getPaymentAmt() {
		return paymentAmt;
	}
	
	public String getPaymenttermsText() {
		return paymenttermsText;
	}

	public String getPaymentGatewayText() {
		return paymentGatewayText;
	}

	public String getOfflinePaymentText() {
		return offlinePaymentText;
	}

	public String getOnlinePaymentText() {
		return onlinePaymentText;
	}	

	public String getPaymentLink() {
		String paymentLinks =  GEUWebUtils.linkTransformer(this.paymentLink);
		return paymentLinks;
	}

}
