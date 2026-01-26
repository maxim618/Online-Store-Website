package com.ecommerce.payment.service;

import com.ecommerce.abstractTestClasses.AbstractFullDatabaseCleanupTest;
import com.ecommerce.abstractTestClasses.AbstractPartialDatabaseCleanupTest;
import com.ecommerce.payment.exception.PaymentAlreadyExistsException;
import com.ecommerce.payment.repository.PaymentRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
public class PaymentServiceIntegrationTest extends AbstractFullDatabaseCleanupTest {

    @Autowired private PaymentRepository paymentRepository;
    @Autowired private PaymentService paymentService;

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
    void shouldAllowOnlyOnePaymentWhenCalledConcurrently() throws Exception {
        Long orderId = 1L;

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);

        Future<Object> f1 = executor.submit(() -> {
            latch.await();
            return tryInit(orderId, "key-1");
        });

        Future<Object> f2 = executor.submit(() -> {
            latch.await();
            return tryInit(orderId, "key-2");
        });

        latch.countDown();

        f1.get();
        f2.get();

        assertThat(paymentRepository.findAll()).hasSize(1);
    }

    private Object tryInit(Long orderId, String key) {
        try {
            return paymentService.initPayment(orderId, key);
        } catch (PaymentAlreadyExistsException | DataIntegrityViolationException ex) {
            return ex;
        }
    }
}
