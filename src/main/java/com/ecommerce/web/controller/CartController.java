package com.ecommerce.web.controller;

import com.ecommerce.security.CustomUserDetails;
import com.ecommerce.service.interfaces.CartService;
import com.ecommerce.web.dto.CartDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public void add(
            @AuthenticationPrincipal CustomUserDetails me,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity
    ) {
        cartService.addItem(me.getId(), productId, quantity);
    }

    @PostMapping("/set")
    public void setQuantity(
            @AuthenticationPrincipal CustomUserDetails me,
            @RequestParam Long productId,
            @RequestParam int quantity
    ) {
        cartService.setItemQuantity(me.getId(), productId, quantity);
    }

    @DeleteMapping("/remove")
    public void remove(
            @AuthenticationPrincipal CustomUserDetails me,
            @RequestParam Long productId
    ) {
        cartService.removeItem(me.getId(), productId);
    }

    @DeleteMapping("/clear")
    public void clear(@AuthenticationPrincipal CustomUserDetails me) {
        cartService.clearCart(me.getId());
    }

    @GetMapping
    public CartDto getCart(@AuthenticationPrincipal CustomUserDetails me) {
        return cartService.getUserCart(me.getId());
    }

    @GetMapping("/count")
    public long count(
            @AuthenticationPrincipal CustomUserDetails me) {
        return cartService.getCartItemCount(me.getId());
    }
}
