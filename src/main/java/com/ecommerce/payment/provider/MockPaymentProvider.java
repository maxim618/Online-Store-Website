package com.ecommerce.payment.provider;

import com.ecommerce.payment.domain.Payment;
import com.ecommerce.payment.domain.PaymentProviderType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MockPaymentProvider implements PaymentProvider {

    @Override
    public PaymentProviderType getType() {
        return PaymentProviderType.MOCK;
    }

    @Override
    public PaymentProviderResult createPayment(Payment payment) {
        return new PaymentProviderResult(
                "mock-" + UUID.randomUUID(),
                "https://mock-payments.local/pay/" + payment.getId()
        );
    }
}
