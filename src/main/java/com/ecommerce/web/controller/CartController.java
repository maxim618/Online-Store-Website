package com.ecommerce.web.controller;

import com.ecommerce.service.interfaces.CartService;
import com.ecommerce.web.dto.CartDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService service;

    @PostMapping("/add")
    public void add(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity
    ) {
        service.add(userId, productId, quantity);
    }

    @DeleteMapping("/remove")
    public void remove(
            @RequestParam Long userId,
            @RequestParam Long productId
    ) {
        service.remove(userId, productId);
    }

    @DeleteMapping("/clear")
    public void clear(@RequestParam Long userId) {
        service.clear(userId);
    }

    @GetMapping
    public CartDto get(@RequestParam Long userId) {
        return service.getUserCart(userId);
    }
}
