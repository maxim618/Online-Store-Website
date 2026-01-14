package com.ecommerce.service.impl;

import com.ecommerce.cart.domain.CartStorage;
import com.ecommerce.exception.EntityNotFoundException;
import com.ecommerce.persistence.model.Product;
import com.ecommerce.persistence.model.UserEntity;
import com.ecommerce.persistence.model.Order;
import com.ecommerce.persistence.model.OrderItem;
import com.ecommerce.persistence.repository.OrderRepository;
import com.ecommerce.persistence.repository.ProductRepository;
import com.ecommerce.persistence.repository.UserRepository;
import com.ecommerce.service.interfaces.OrderService;
import com.ecommerce.web.dto.OrderDto;
import com.ecommerce.web.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final CartStorage cartStorage;
    private final ProductRepository productRepository;

    @Override
    public OrderDto placeOrder(Long userId) {

        // 1 Получаем пользователя
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        // 2 Получаем корзину из CartStorage (JPA или Valkey)
        Map<Long, Integer> cart = cartStorage.getItems(userId);
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty for user " + userId);
        }

        // 3 Загружаем продукты одним батчем
        List<Long> productIds = new ArrayList<>(cart.keySet());
        List<Product> products = productRepository.findAllById(productIds);

        Map<Long, Product> productById = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 4 Создаем заказ
        Order order = new Order();
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now());

        // 5 Создаем order items + считаем итог
        double totalPrice = 0.0;
        List<OrderItem> items = new ArrayList<>();

        for (Long productId : productIds) {
            Product product = productById.get(productId);
            if (product == null) {
                // Если товар исчез из каталога — можно либо пропустить, либо упасть.
                // Для стабильности тестов лучше упасть с ошибкой:
                throw new EntityNotFoundException("Product not found: " + productId);
            }

            int qty = cart.get(productId);

            // Если у продукта есть складской остаток (quantity), проверяем
            if (product.getQuantity() != null && qty > product.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock for product " + productId);
            }

            Double price = product.getPrice() != null ? product.getPrice() : 0.0;

            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(qty)
                    .price(price) // цена на момент покупки
                    .build();

            items.add(oi);
            totalPrice += price * qty;
        }

        order.setItems(items);
        order.setTotalPrice(totalPrice);

        // 6 Сохраняем заказ (каскад сохраняет items)
        Order saved = orderRepository.save(order);

        // 7 Очищаем корзину через CartStorage (работает и для JPA, и для Valkey)
        cartStorage.clearCart(userId);

        // 8 Возвращаем DTO
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

    @Override
    public OrderDto getOneForUser(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new org.springframework.security.access.AccessDeniedException("Forbidden");
        }
        return orderMapper.toDto(order);
    }
}
