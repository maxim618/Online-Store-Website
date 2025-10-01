package com.ecommerce.service;

import com.ecommerce.dao.OrderDao;
import com.ecommerce.entities.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderDao orderDao;

    public OrderService(OrderDao orderDao) {
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
}
