package com.ecommerce.service.interfaces;

import com.ecommerce.web.dto.CartDto;

public interface CartService {

    void addItem(Long userId, Long productId, int quantity);

    void setItemQuantity(Long userId, Long productId, int quantity);

    void removeItem(Long userId, Long productId);

    void clearCart(Long userId);

    CartDto getUserCart(Long userId);

    long getCartItemCount(Long userId);
}
