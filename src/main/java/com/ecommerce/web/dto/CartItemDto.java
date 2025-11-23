package com.ecommerce.web.dto;

import lombok.Data;

@Data
public class CartItemDto {
    private Long productId;
    private String productTitle;
    private Double unitPrice;
    private Integer quantity;
    private Double lineTotal;
    private String imageUrl;
}
