package com.ecommerce.cart.service;

import com.ecommerce.cart.domain.CartStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartStorage storage;

    public void add(long userId, long productId, int quantity) {
        storage.addItem(userId, productId, quantity);
    }

    public void setQuantity(long userId, long productId, int quantity) {
        storage.setItemQuantity(userId, productId, quantity);
    }

    public void remove(long userId, long productId) {
        storage.removeItem(userId, productId);
    }

    public Map<Long, Integer> getItems(long userId) {
        return storage.getItems(userId);
    }

    public void clear(long userId) {
        storage.clearCart(userId);
    }
}
