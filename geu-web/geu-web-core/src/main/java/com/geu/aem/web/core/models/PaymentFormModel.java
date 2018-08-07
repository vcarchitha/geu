package com.geu.aem.web.core.models;

import java.util.UUID;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import com.geu.aem.web.service.PaymentFormService;
import com.geu.aem.web.util.GEUWebUtils;

/**
 * @author nitin.jangir
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PaymentFormModel {
	
	@OSGiService
	private PaymentFormService paymentFormService;

	@Inject
	private String title;
	
	@Inject
	private String termsLink;
	
	@Inject
	private String partialPayment;
	
	@Inject
	private String declarationText;
	
	public String getDeclarationText() {
		return declarationText;
	}

	public String getPartialPayment() {
		return partialPayment;
	}

	public String getVersion() {
		return paymentFormService.getVersion();
	}

	public String getMerchantAccessCode() {
		return paymentFormService.getMerchantAccessCode();
	}

	public String getCommandType() {
		return paymentFormService.getCommandType();
	}

	public String getMerchantID() {
		return paymentFormService.getMerchantID();
	}

	public String getReceiptReturnURL() {
		return paymentFormService.getReceiptReturnURL();
	}
	
	public String getRegistrationReceiptURL() {
		return paymentFormService.getRegistrationReceiptReturnURL();
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getMerchTxnRef() {
		return UUID.randomUUID().toString();
	}

	public String getTermsLink() {
		return GEUWebUtils.linkTransformer(termsLink);
	}
}
