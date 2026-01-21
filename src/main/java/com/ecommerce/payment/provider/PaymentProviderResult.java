package com.ecommerce.payment.provider;

import lombok.Getter;

@Getter
public class PaymentProviderResult {

    private final String externalPaymentId;
    private final String paymentUrl;

    public PaymentProviderResult(String externalPaymentId, String paymentUrl) {
        this.externalPaymentId = externalPaymentId;
        this.paymentUrl = paymentUrl;
    }
}