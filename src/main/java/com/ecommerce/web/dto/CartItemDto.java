package com.ecommerce.web.dto;

import lombok.Data;

@Data
public class CartItemDto {
    private Long productId;
    private String productTitle;
    private Double price;
    private Integer quantity;
}
