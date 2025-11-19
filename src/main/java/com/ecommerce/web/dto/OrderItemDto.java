package com.ecommerce.web.dto;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long productId;
    private String productTitle;
    private Integer quantity;
    private Double price;
}
