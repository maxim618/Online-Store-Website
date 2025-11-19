package com.ecommerce.service.impl;

import com.ecommerce.exception.EntityNotFoundException;
import com.ecommerce.persistence.model.Order;
import com.ecommerce.persistence.model.OrderItem;
import com.ecommerce.persistence.repository.OrderRepository;
import com.ecommerce.persistence.repository.CartItemRepository;
import com.ecommerce.persistence.repository.ProductRepository;
import com.ecommerce.service.interfaces.OrderService;
import com.ecommerce.web.dto.OrderDto;
import com.ecommerce.web.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
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
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderDto placeOrder(Long userId) {
        // 1) получить список элементов корзины (CartItem содержит product и quantity)
        var cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty for user " + userId);
        }

        // 2) посчитать итог
        double total = cartItems.stream()
                .mapToDouble(ci -> ci.getProduct().getPrice() * ci.getQuantity())
                .sum();

        // 3) построить сущность Order (используем методы, которые точно есть: setUserId, setTotalPrice, setCreatedAt)
        Order order = new Order();
        // если в Order нет setUserId — это будет компиляционная ошибка; тогда см. Вариант 2
        order.setUserId(userId);           // <- если поле называется иначе — замените
        order.setTotalPrice(total);       // <- имя поля/метода заменить по реальному классу
        order.setCreatedAt(LocalDateTime.now());

        // 4) создать OrderItem-ы (зависит от структуры OrderItem в persistence.model)
        List<OrderItem> items = cartItems.stream().map(ci -> {
            OrderItem it = new OrderItem();
            it.setProduct(ci.getProduct());    // если OrderItem хранит product
            it.setQuantity(ci.getQuantity());
            it.setPrice(ci.getProduct().getPrice());
            it.setOrder(order);               // привязать к order, если нужно
            return it;
        }).toList();

        // 5) присоединяем элементы к заказу — имя списка в Order может быть items, orderItems или иначе
        order.setItems(items); // замените на order.setOrderItems(items) если так называется

        // 6) сохранить заказ (каскад должен сохранить элементы)
        Order saved = orderRepository.save(order);

        // 7) очистить корзину
        cartItemRepository.deleteByUserId(userId);

        return orderMapper.toDto(saved);
    }

    @Override
    public OrderDto getOrder(Long id) {
        Order o = orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Order " + id + " not found"));
        return orderMapper.toDto(o);
    }

    @Override
    public List<OrderDto> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream().map(orderMapper::toDto).toList();
    }
}
