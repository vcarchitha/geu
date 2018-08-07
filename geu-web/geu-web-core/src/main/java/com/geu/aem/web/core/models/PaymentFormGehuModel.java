package com.geu.aem.web.core.models;

import java.util.UUID;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import com.geu.aem.web.service.PGHdfcService;
import com.geu.aem.web.util.GEUWebUtils;

/**
 * @author nitin.jangir
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PaymentFormGehuModel {
	
	@OSGiService
	private PGHdfcService pgHdfcService;

	@Inject
	private String title;
	
	@Inject
	private String termsText;
	
	@Inject
	private String declarationText;

	@Inject
	private String termsLink;
	
	@Inject
	private String partialPayment;
	
	@Inject
	private String paymentGatewayText;
	
	@Inject
	private String offlinePaymentText;
	
	@Inject
	private String onlinePaymentText;
	
	public String getOnlinePaymentText() {
		return onlinePaymentText;
	}

	public String getOfflinePaymentText() {
		return offlinePaymentText;
	}

	public String getPaymentGatewayText() {
		return paymentGatewayText;
	}

	public String getDeclarationText() {
		return declarationText;
	}

	public String getTermsText() {
		return termsText;
	}
	
	public String getPartialPayment() {
		return partialPayment;
	}

	public String getMerchantId() {
		return pgHdfcService.getMerchantId();
	}

	public String getCurrency() {
		return pgHdfcService.getCurrency();
	}

	public String getRedirectURL() {
		return pgHdfcService.getRedirectURL();
	}
	
	public String getRegistrationRedirectURL() {
		return pgHdfcService.getRegistrationRedirectURL();
	}

	public String getLanguage() {
		return pgHdfcService.getLanguage();
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getRegistrationReturnURL() {
		return pgHdfcService.getRegistrationReturnURL();
	}
	
	public String getReturnURL() {
		return pgHdfcService.getReturnURL();
	}
	
	public String getOrderId() {
		String uuid = UUID.randomUUID().toString();
		String orderId = uuid.substring(0, uuid.length() - 8);
		return orderId;
	}

	public String getTermsLink() {
		return GEUWebUtils.linkTransformer(termsLink);
	}
}
