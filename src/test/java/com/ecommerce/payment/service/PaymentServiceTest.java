package com.ecommerce.payment.service;

import com.ecommerce.payment.domain.Payment;
import com.ecommerce.payment.domain.PaymentStatus;
import com.ecommerce.payment.dto.PaymentInitResponseDto;
import com.ecommerce.payment.exception.PaymentAlreadyExistsException;
import com.ecommerce.payment.repository.PaymentRepository;
import com.ecommerce.payment.testfactory.OrderTestFactory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderTestFactory orderTestFactory;

    @Test
    void shouldCreatePaymentAndReturnPaymentUrl() {

        Long orderId = orderTestFactory.createOrderInStatusCreated();
        String idempotencyKey = "test-key";

        PaymentInitResponseDto response =
                paymentService.initPayment(orderId, idempotencyKey);

        assertThat(response.getPaymentUrl()).isNotBlank();
        assertThat(response.getPaymentId()).isNotNull();

        Payment payment = paymentRepository.findById(response.getPaymentId())
                .orElseThrow();

        assertThat(payment.getOrderId()).isEqualTo(orderId);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getExternalPaymentId()).isNotNull();
    }

    @Test
    void shouldFailWhenCreatingSecondPaymentForSameOrder() {
        Long orderId = 1L;

        paymentService.initPayment(orderId, "key-1");

        assertThatThrownBy(() ->
                paymentService.initPayment(orderId, "key-2")
        )
                .isInstanceOf(PaymentAlreadyExistsException.class)
                .hasMessageContaining("Payment already exists for order");
    }

    @Test
    void shouldReturnSamePaymentForSameIdempotencyKey() {
        Long orderId = 1L;
        String key = "idem-key-123";

        PaymentInitResponseDto first =
                paymentService.initPayment(orderId, key);

        PaymentInitResponseDto second =
                paymentService.initPayment(orderId, key);

        assertThat(second.getPaymentId())
                .isEqualTo(first.getPaymentId());

        assertThat(second.getPaymentUrl())
                .isEqualTo(first.getPaymentUrl());
    }
}
