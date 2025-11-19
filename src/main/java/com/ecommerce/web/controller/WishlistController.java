package com.ecommerce.web.controller;

import com.ecommerce.service.interfaces.WishlistService;
import com.ecommerce.web.dto.WishlistItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/add")
    public void add(
            @RequestParam Long userId,
            @RequestParam Long productId
    ) {
        wishlistService.addToWishlist(userId, productId);
    }

    @DeleteMapping("/remove")
    public void remove(
            @RequestParam Long userId,
            @RequestParam Long productId
    ) {
        wishlistService.removeFromWishlist(userId, productId);
    }

    @GetMapping
    public List<WishlistItemDto> get(@RequestParam Long userId) {
        return wishlistService.getWishlist(userId);
    }
}
