package com.ecommerce.web.dto;

import lombok.Data;

@Data
public class WishlistItemDto {
    private Long productId;
    private String productTitle;
    private Double price;
    private String imageUrl;
}
