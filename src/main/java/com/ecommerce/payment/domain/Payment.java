package com.ecommerce.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Entity
@Table(
        name = "payments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_payment_order_id",
                        columnNames = "order_id"
                )
        }
)

public class Payment {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    private PaymentProviderType provider;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String externalPaymentId;

    private Instant createdAt;
    private Instant updatedAt;

    // Конструктор для создания нового Payment
    public Payment(Long orderId, PaymentProviderType provider, BigDecimal amount, 
                   String currency, PaymentStatus status) {
        this.orderId = orderId;
        this.provider = provider;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.createdAt = Instant.now();
    }

    // Конструктор по умолчанию для JPA
    protected Payment() {
    }

    public void markPending(String externalPaymentId) {
        this.status = PaymentStatus.PENDING;
        this.externalPaymentId = externalPaymentId;
        this.updatedAt = Instant.now();
    }
}
