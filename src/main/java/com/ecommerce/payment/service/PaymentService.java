package com.ecommerce.payment.service;

import com.ecommerce.payment.domain.Payment;
import com.ecommerce.payment.domain.PaymentProviderType;
import com.ecommerce.payment.domain.PaymentStatus;
import com.ecommerce.payment.dto.PaymentInitResponseDto;
import com.ecommerce.payment.exception.PaymentAlreadyExistsException;
import com.ecommerce.payment.mapper.PaymentResponseMapper;
import com.ecommerce.payment.provider.PaymentProvider;
import com.ecommerce.payment.provider.PaymentProviderResult;
import com.ecommerce.payment.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentProviderResolver providerResolver;
    private final PaymentResponseMapper paymentResponseMapper;

    @Transactional
    public PaymentInitResponseDto initPayment(Long orderId) {

        if (paymentRepository.existsByOrderId(orderId)) {
            throw new PaymentAlreadyExistsException(orderId);
        }

        Payment payment = new Payment(
                orderId,
                PaymentProviderType.MOCK,
                new BigDecimal("100.00"),
                "RUB",
                PaymentStatus.CREATED
        );

        try {
            paymentRepository.save(payment);
        } catch (DataIntegrityViolationException ex) {
            throw new PaymentAlreadyExistsException(orderId);
        }

        PaymentProvider provider = providerResolver.resolve(payment.getProvider());
        PaymentProviderResult result = provider.createPayment(payment);
        payment.markPending(result.getExternalPaymentId());

        return paymentResponseMapper.toDto(payment, result);
    }
}
