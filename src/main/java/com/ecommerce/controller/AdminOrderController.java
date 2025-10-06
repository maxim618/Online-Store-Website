package com.ecommerce.controller;

import com.ecommerce.entities.Order;
import com.ecommerce.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 👀 список всех заказов
    @GetMapping
    public String getAllOrders(HttpSession session, Model model) {
        Object admin = session.getAttribute("activeAdmin");
        if (admin == null) {
            return "redirect:/"; // 👈 теперь редирект на главную
        }

        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "manage_order";
    }

    // 🔄 обновление статуса заказа
    @PostMapping("/update-status")
    public String updateOrderStatus(@RequestParam("orderId") int orderId,
                                    @RequestParam("status") String status,
                                    HttpSession session) {
        Object admin = session.getAttribute("activeAdmin");
        if (admin == null) {
            return "redirect:/"; // 👈 тоже редирект на главную
        }

        orderService.updateOrderStatus(orderId, status);
        return "redirect:/admin/orders";
    }
}
