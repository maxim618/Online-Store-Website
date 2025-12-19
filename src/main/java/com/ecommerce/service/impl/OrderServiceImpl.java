package com.ecommerce.service.impl;

import com.ecommerce.exception.EntityNotFoundException;
import com.ecommerce.persistence.model.UserEntity;
import com.ecommerce.persistence.model.CartItem;
import com.ecommerce.persistence.model.Order;
import com.ecommerce.persistence.model.OrderItem;
import com.ecommerce.persistence.repository.OrderRepository;
import com.ecommerce.persistence.repository.UserRepository;
import com.ecommerce.persistence.repository.CartItemRepository;
import com.ecommerce.service.interfaces.OrderService;
import com.ecommerce.web.dto.OrderDto;
import com.ecommerce.web.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderDto placeOrder(Long userId) {

        // 1. Получаем пользователя - UserEntity
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        // 2. Получаем корзину
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty for user " + userId);
        }

        // 3. Вычисляем итоговую стоимость
        double totalPrice = cartItems.stream()
                .mapToDouble(ci -> ci.getProduct().getPrice() * ci.getQuantity())
                .sum();

        // 4. Создаем заказ
        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(totalPrice);
        order.setCreatedAt(LocalDateTime.now());

        // 5. Создаем order items
        List<OrderItem> items = cartItems.stream()
                .map(ci -> OrderItem.builder()
                        .order(order)
                        .product(ci.getProduct())
                        .quantity(ci.getQuantity())
                        .price(ci.getProduct().getPrice())
                        .build())
                .toList();

        order.setItems(items);

        // 6. Сохраняем заказ (каскад сохраняет items)
        Order saved = orderRepository.save(order);

        // 7. Очищаем корзину
        cartItemRepository.deleteByUserId(userId);

        // 8. Возвращаем DTO
        return orderMapper.toDto(saved);
    }

    @Override
    public OrderDto getOrder(Long id) {
        return orderRepository.findById(id)
                .map(orderMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));
    }

    @Override
    public List<OrderDto> getUserOrders(Long userId) {
        return orderRepository.findByUser_Id(userId).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public OrderDto getOrderForUser(Long orderId, Long requesterId, boolean admin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        if (!admin && !order.getUser().getId().equals(requesterId)) {
            throw new AccessDeniedException("Forbidden");
        }
        return getOrder(orderId);
    }
}
