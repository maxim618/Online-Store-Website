package com.ecommerce.payment.dto;

import lombok.Data;

@Data
public class PaymentInitResponseDto {
    private Long paymentId;
    private String paymentUrl;
}