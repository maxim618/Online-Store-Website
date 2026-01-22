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
    void should_create_payment_and_return_payment_url() {

        // 1 given
        Long orderId = orderTestFactory.createOrderInStatusCreated();

        // 2 when
        PaymentInitResponseDto response = paymentService.initPayment(orderId);

        // 3 then
        assertThat(response.getPaymentUrl()).isNotBlank();
        assertThat(response.getPaymentId()).isNotNull();

        Payment payment = paymentRepository.findById(response.getPaymentId())
                .orElseThrow();

        assertThat(payment.getOrderId()).isEqualTo(orderId);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getExternalPaymentId()).isNotNull();
    }

    @Test
    void shouldThrowExceptionIfPaymentAlreadyExistsForOrder() {
        // given
        Long orderId = 1L;

        paymentService.initPayment(orderId);

        // when / then
        assertThatThrownBy(() -> paymentService.initPayment(orderId))
                .isInstanceOf(PaymentAlreadyExistsException.class)
                .hasMessageContaining("Payment already exists for order");
    }

}
