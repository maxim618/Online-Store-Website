package com.ecommerce.controller;

import com.ecommerce.dao.OrderDao;
import com.ecommerce.dao.UserDaoLegacy;
import com.ecommerce.entities.Order;
import com.ecommerce.helper.MailMessenger;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/order/update")
public class UpdateOrderController {

    private final OrderDao orderDao;
    private final UserDaoLegacy userDao;

    public UpdateOrderController(OrderDao orderDao, UserDaoLegacy userDao) {
        this.orderDao = orderDao;
        this.userDao = userDao;

    }

    @PostMapping
    public void updateOrder(
            @RequestParam("oid") int oid,
            @RequestParam("status") String status,
            HttpServletResponse response
    ) throws IOException {

        // Обновляем статус заказа
        orderDao.updateOrderStatus(oid, status);

        // Если заказ "Shipped" или "Out For Delivery" — отправляем уведомление
        if ("Shipped".equalsIgnoreCase(status.trim()) || "Out For Delivery".equalsIgnoreCase(status.trim())) {
            Order order = orderDao.getOrderById(oid);

            String userName = userDao.getUserName(order.getUserId());
            String userEmail = userDao.getUserEmail(order.getUserId());
            MailMessenger.orderShipped(userName, userEmail, order.getOrderId(), order.getDate().toString());
        }

        // После обновления возвращаемся к списку заказов
        response.sendRedirect("display_orders.jsp");
    }

    @GetMapping
    public void handleGet(
            @RequestParam("oid") int oid,
            @RequestParam("status") String status,
            HttpServletResponse response
    ) throws IOException {
        updateOrder(oid, status, response);
    }
}
