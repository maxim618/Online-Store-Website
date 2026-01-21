package com.ecommerce.payment.service;

import com.ecommerce.payment.domain.PaymentProviderType;
import com.ecommerce.payment.provider.PaymentProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentProviderResolver {

    private final List<PaymentProvider> providers;

    public PaymentProvider resolve(PaymentProviderType type) {
        return providers.stream()
                .filter(p -> p.getType() == type)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Payment provider not found for type: " + type));
    }
}
