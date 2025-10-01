package com.ecommerce.service;

import com.ecommerce.dao.WishlistDao;
import com.ecommerce.entities.Wishlist;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistService {
    private final WishlistDao wishlistDao;

    public WishlistService(WishlistDao wishlistDao) {
        this.wishlistDao = wishlistDao;
    }

    public List<Wishlist> getWishlistByUserId(int userId) {
        return wishlistDao.getListByUserId(userId);
    }

    public void addToWishlist(Wishlist wishlist) {
        wishlistDao.addToWishlist(wishlist);
    }

    public void removeFromWishlist(int userId, int productId) {
        wishlistDao.deleteWishlist(userId, productId);
    }
}
