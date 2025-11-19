package com.ecommerce.service;

import com.ecommerce.dao.OrderDaoLegacy;
import com.ecommerce.entities.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderDaoLegacy orderDao;

    public OrderService(OrderDaoLegacy orderDao) {
        this.orderDao = orderDao;
    }

    public boolean placeOrder(Order order) {
        return orderDao.placeOrder(order);
    }

    public List<Order> getOrdersByUserId(int userId) {
        return orderDao.getOrdersByUserId(userId);
    }

    public void updateOrderStatus(int id, String status) {
        orderDao.updateOrderStatus(id, status);
    }

    // ✅ для админки — список всех заказов
    public List<Order> getAllOrders() {
        return orderDao.getAllOrders();
    }
    /** ✅ Получить заказ по id (PK) */
    public Order getOrderById(int id) {
        return orderDao.getOrderById(id);
    }
    /** (опционально) По строковому orderId */
    public Order getOrderByOrderId(String orderId) {
        return orderDao.getOrderByOrderId(orderId);
    }
}
