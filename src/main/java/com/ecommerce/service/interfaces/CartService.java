package com.ecommerce.service.interfaces;

import com.ecommerce.web.dto.CartDto;

public interface CartService {
    void add(Long userId, Long productId, Integer quantity);
    void remove(Long userId, Long productId);
    CartDto getUserCart(Long userId);
    void clear(Long userId);
}
