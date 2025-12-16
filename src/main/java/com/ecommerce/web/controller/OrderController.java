package com.ecommerce.web.controller;

import com.ecommerce.security.CustomUserDetails;
import com.ecommerce.service.interfaces.OrderService;
import com.ecommerce.web.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
        CustomUserDetails user = authUser();

        if (!isAdmin() && !user.getId().equals(userId)) {
            throw new AccessDeniedException("Forbidden");
        }

        return service.getUserOrders(userId);
    }

    @GetMapping("/{id}")
    public OrderDto getOne(@PathVariable Long id) {
        CustomUserDetails user = authUser();
        return service.getOrderForUser(id, user.getId(), isAdmin());
    }

    private CustomUserDetails authUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) auth.getPrincipal();
    }

    private boolean isAdmin() {
        return authUser().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
