package com.ecommerce.payment.testfactory;

import com.ecommerce.payment.domain.Payment;
import com.ecommerce.payment.domain.PaymentProviderType;
import com.ecommerce.payment.domain.PaymentStatus;

import java.math.BigDecimal;

public final class PaymentTestFactory {

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal("100.00");
    private static final String DEFAULT_CURRENCY = "RUB";
    private static final PaymentProviderType DEFAULT_PROVIDER = PaymentProviderType.MOCK;

    private PaymentTestFactory() {
    }

    public static Payment created(Long orderId, String idempotencyKey) {
        return new Payment(
                orderId,
                DEFAULT_PROVIDER,
                DEFAULT_AMOUNT,
                DEFAULT_CURRENCY,
                PaymentStatus.CREATED,
                idempotencyKey
        );
    }

    public static Payment pending(Long orderId, String idempotencyKey) {
        Payment payment = created(orderId, idempotencyKey);
        payment.markPending("ext-payment-123", "https://mock.pay/123" );
        return payment;
    }

    public static Payment succeeded(Long orderId, String idempotencyKey) {
        Payment payment = pending(orderId, idempotencyKey);
        payment.markSucceeded();
        return payment;
    }

    public static Payment failed(Long orderId, String idempotencyKey) {
        Payment payment = pending(orderId, idempotencyKey);
        payment.markFailed("DECLINED");
        return payment;
    }
}