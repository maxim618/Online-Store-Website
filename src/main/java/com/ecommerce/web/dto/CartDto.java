package com.ecommerce.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartDto {
    private List<CartItemDto> items;
    private Double totalPrice;
}
