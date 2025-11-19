package com.ecommerce.web.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private Long orderId;
    private LocalDateTime createdAt;
    private Double totalPrice;
    private List<OrderItemDto> items;
}
