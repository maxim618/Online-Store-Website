package com.ecommerce.web.controller;

import com.ecommerce.security.CustomUserDetails;
import com.ecommerce.service.interfaces.OrderService;
import com.ecommerce.web.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping("/place")
    public OrderDto place(
//            @RequestParam Long userId
            @AuthenticationPrincipal CustomUserDetails me
    ) {
        return service.placeOrder(me.getId());
    }

    @GetMapping
    public List<OrderDto> getUserOrders(
            @AuthenticationPrincipal CustomUserDetails me,
            @RequestParam(required = false) Long userId
    ) {
        // если клиент пытается подсунуть userId - запрещаем чужой
        if (userId != null && !userId.equals(me.getId())) {
            throw new AccessDeniedException("Forbidden");
        }

        // выдаём только заказы текущего пользователя (безопасно)
        return service.getUserOrders(me.getId());
    }

    @GetMapping("/{orderId}")
    public OrderDto getOne(
            @AuthenticationPrincipal CustomUserDetails me,
            @PathVariable Long orderId) {
        return service.getOneForUser(orderId, me.getId());
    }
}
