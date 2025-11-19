package com.ecommerce.web.dto;

import lombok.Data;

@Data
public class ProductDto {
    private Long id;
    private String title;
    private String description;
    private Double price;
    private Integer quantity;
    private Integer discount;
    private String imageUrl;
    private Long categoryId;
}
