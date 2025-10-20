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

    public boolean addToWishlist(Wishlist wishlist) {
        return wishlistDao.addToWishlist(wishlist);
    }

    public void deleteWishlist(int userId, int productId) {
        wishlistDao.deleteWishlist(userId, productId);
    }

    public boolean existsInWishlist(int userId, int productId) {
        return wishlistDao.getWishlist(userId, productId);
    }
}
