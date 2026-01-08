package com.ecommerce.service.interfaces;

import com.ecommerce.web.dto.OrderDto;
import java.util.List;

public interface OrderService {
    OrderDto placeOrder(Long userId);
    OrderDto getOrder(Long id);
    List<OrderDto> getUserOrders(Long userId);
    OrderDto getOrderForUser(Long orderId, Long requesterId, boolean admin);
    OrderDto getOneForUser(Long orderId, Long userId);
}
