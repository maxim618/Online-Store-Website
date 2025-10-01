package com.ecommerce.service;

import com.ecommerce.dao.CartDao;
import com.ecommerce.entities.Cart;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    private final CartDao cartDao;

    public CartService(CartDao cartDao) {
        this.cartDao = cartDao;
    }

    public List<Cart> getCartByUserId(int userId) {
        return cartDao.getCartListByUserId(userId);
    }

    public int getTotalCartPriceByUserId(int userId) {
        return cartDao.getTotalCartPriceByUserId(userId);
    }
    public int getCartCountByUserId(int userId) {
        return cartDao.getCartCountByUserId(userId);
    }

}
