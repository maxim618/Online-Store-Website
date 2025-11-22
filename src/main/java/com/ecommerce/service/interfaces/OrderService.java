package com.ecommerce.service.interfaces;


import com.ecommerce.persistence.model.Order;
import com.ecommerce.web.dto.OrderDto;

import java.util.List;

public interface OrderService {
    OrderDto placeOrder(Long userId);
    OrderDto getOrder(Long id);
    List<OrderDto> getUserOrders(Long userId);

//    Order createOrder(Long userId, Order orderData);
//
//    Order getOrderById(Long id);
//
//    List<Order> getOrdersByUser(Long userId);
//
//    Order updateOrderStatus(Long orderId, String newStatus);
//
//    void deleteOrder(Long id);
//
//    double calculateOrderTotal(Long orderId);
}
