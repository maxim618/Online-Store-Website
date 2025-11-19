package com.ecommerce.service.interfaces;

import com.ecommerce.web.dto.ProductDto;

import java.util.List;

public interface WishlistService {
    void add(Long userId, Long productId);
    void remove(Long userId, Long productId);
    List<ProductDto> getUserWishlist(Long userId);
}
