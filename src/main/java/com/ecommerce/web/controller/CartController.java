package com.ecommerce.web.controller;

import com.ecommerce.service.interfaces.CartService;
import com.ecommerce.web.dto.CartDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public void add(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity
    ) {
        cartService.addItem(userId, productId, quantity);
    }

    @PostMapping("/set")
    public void setQuantity(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam int quantity
    ) {
        cartService.setItemQuantity(userId, productId, quantity);
    }

    @DeleteMapping("/remove")
    public void remove(
            @RequestParam Long userId,
            @RequestParam Long productId
    ) {
        cartService.removeItem(userId, productId);
    }

    @DeleteMapping("/clear")
    public void clear(@RequestParam Long userId) {
        cartService.clearCart(userId);
    }

    @GetMapping
    public CartDto getCart(@RequestParam Long userId) {
        return cartService.getUserCart(userId);
    }

    @GetMapping("/count")
    public long count(@RequestParam Long userId) {
        return cartService.getCartItemCount(userId);
    }
}
