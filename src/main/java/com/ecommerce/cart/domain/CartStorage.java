package com.ecommerce.cart.domain;

import java.util.Map;

public interface CartStorage {

    void addItem(long userId, long productId, int deltaQty);

    void setItemQuantity(long userId, long productId, int qty);

    void removeItem(long userId, long productId);

    Map<Long, Integer> getItems(long userId);

    void clearCart(long userId);
}
