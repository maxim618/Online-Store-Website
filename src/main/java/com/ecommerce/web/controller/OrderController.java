package com.ecommerce.web.controller;

import com.ecommerce.service.interfaces.OrderService;
import com.ecommerce.web.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping("/place")
    public OrderDto place(@RequestParam Long userId) {
        return service.placeOrder(userId);
    }

    @GetMapping
    public List<OrderDto> getUserOrders(@RequestParam Long userId) {
        return service.getUserOrders(userId);
    }

    @GetMapping("/{id}")
    public OrderDto getOne(@PathVariable Long id) {
        return service.getOrder(id);
    }
}
