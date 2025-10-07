package com.ecommerce.controller;

import com.ecommerce.entities.Message;
import com.ecommerce.entities.Order;
import com.ecommerce.entities.User;
import com.ecommerce.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 📦 создание нового заказа
    @PostMapping("/order/place")
    public String placeOrder(HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("activeUser");
        if (user == null) {
            ra.addFlashAttribute("message",
                    new Message("Please login to place an order.", "Error!", "alert-danger"));
            return "redirect:/login";
        }

        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setStatus("Placed");
        order.setPaymentType("COD"); // можно из формы
        order.setUserId(user.getUserId());
        order.setDate(LocalDateTime.now());

        boolean saved = orderService.placeOrder(order);

        if (saved) {
            ra.addFlashAttribute("message",
                    new Message("Your order has been placed successfully!", "Success!", "alert-success"));
        } else {
            ra.addFlashAttribute("message",
                    new Message("Failed to place order. Try again later.", "Error!", "alert-danger"));
        }

        return "redirect:/order";
    }

    // 📑 просмотр заказов пользователя
    @GetMapping("/order")
    public String viewOrders(HttpSession session, Model model, RedirectAttributes ra) {
        User user = (User) session.getAttribute("activeUser");
        if (user == null) {
            ra.addFlashAttribute("message",
                    new Message("You must login to see your orders.", "Error!", "alert-danger"));
            return "redirect:/login";
        }

        List<Order> orders = orderService.getOrdersByUserId(user.getUserId());
        model.addAttribute("orders", orders);

        return "order"; // order.jsp
    }
}
