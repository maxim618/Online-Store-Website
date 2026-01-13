package com.ecommerce.cart.domain;

import java.util.Map;

public interface CartStorage {

    /**
     * Increase quantity by deltaQty (deltaQty > 0).
     */
    void addItem(long userId, long productId, int deltaQty);

    /**
     * Set exact quantity. If qty <= 0 -> remove item.
     */
    void setItemQuantity(long userId, long productId, int qty);

    void removeItem(long userId, long productId);

    /**
     * Returns productId -> qty
     */
    Map<Long, Integer> getItems(long userId);

    void clearCart(long userId);
}
