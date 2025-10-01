package com.ecommerce.controller;

import com.ecommerce.dao.CartDao;
import com.ecommerce.dao.OrderDao;
import com.ecommerce.dao.OrderedProductDao;
import com.ecommerce.dao.ProductDao;
import com.ecommerce.entities.*;
import com.ecommerce.helper.MailMessenger;
import com.ecommerce.helper.OrderIdGenerator;
import com.ecommerce.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@Controller
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // просмотр заказов пользователя
    @GetMapping("/orders")
    public String viewOrders(HttpSession session, Model model) {
        User user = (User) session.getAttribute("activeUser");
        if (user == null) return "redirect:/login";

        model.addAttribute("orders", orderService.getOrdersByUserId(user.getUserId()));
        return "order"; // order.jsp
    }

    // создание заказа (например, с checkout.jsp)
    @PostMapping("/order/place")
    public String placeOrder(HttpSession session,
                             @RequestParam("paymentType") String paymentType) {
        User user = (User) session.getAttribute("activeUser");
        if (user == null) return "redirect:/login";

        Order order = new Order();
        order.setOrderId(java.util.UUID.randomUUID().toString());
        order.setStatus("Pending");
        order.setPaymentType(paymentType);
        order.setUserId(user.getUserId());

        orderService.placeOrder(order);

        // flash message
        session.setAttribute("order", "placed");
        return "redirect:/checkout";
    }
}
