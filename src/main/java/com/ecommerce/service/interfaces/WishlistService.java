package com.ecommerce.service.interfaces;

import com.ecommerce.web.dto.WishlistItemDto;

import java.util.List;

public interface WishlistService {

    void addToWishlist(Long userId, Long productId);

    void removeFromWishlist(Long userId, Long productId);

    List<WishlistItemDto> getWishlist(Long userId);
}
