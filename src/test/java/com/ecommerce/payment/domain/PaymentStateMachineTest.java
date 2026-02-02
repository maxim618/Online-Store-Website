package com.ecommerce.payment.domain;

import com.ecommerce.payment.testfactory.PaymentTestFactory;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ActiveProfiles("test")
public class PaymentStateMachineTest {

    @Test
    void shouldAllowCreatedToPending() {
        Payment payment = PaymentTestFactory.created(1L, "test-key");

        payment.markPending("ext-1", "https://pay");

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    void shouldNotAllowSucceededToPending() {
        Payment payment = PaymentTestFactory.succeeded(1L, "test-key");

        assertThatThrownBy(() ->
                payment.markPending("ext", "test-key")
        ).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldIgnoreDuplicateSucceededTransition() {
        Payment payment = PaymentTestFactory.pending(1L, "test-key");

        payment.markSucceeded();
        payment.markSucceeded();

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCEEDED);
    }

}
