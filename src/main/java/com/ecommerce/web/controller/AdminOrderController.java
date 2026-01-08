package com.ecommerce.web.controller;

import com.ecommerce.service.interfaces.OrderService;
import com.ecommerce.web.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderService service;

    @GetMapping
    public List<OrderDto> getOrdersForUser(@RequestParam Long userId) {
        return service.getUserOrders(userId);
    }
}
