package com.ecommerce.payment.provider;

import com.ecommerce.payment.domain.Payment;
import com.ecommerce.payment.domain.PaymentProviderType;

public interface PaymentProvider {
    PaymentProviderType getType();
    PaymentProviderResult createPayment(Payment payment);
}
