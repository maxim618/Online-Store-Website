package com.ecommerce.payment.provider;


public class PaymentProviderResult {

    private final String externalPaymentId;
    private final String paymentUrl;

    public PaymentProviderResult(String externalPaymentId, String paymentUrl) {
        this.externalPaymentId = externalPaymentId;
        this.paymentUrl = paymentUrl;
    }

    public String getExternalPaymentId() {
        return externalPaymentId;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }
}