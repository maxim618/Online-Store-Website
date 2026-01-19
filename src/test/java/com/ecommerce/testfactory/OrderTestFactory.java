package com.ecommerce.testfactory;

import com.ecommerce.persistence.model.Order;
import com.ecommerce.persistence.model.UserEntity;
import com.ecommerce.persistence.repository.OrderRepository;
import com.ecommerce.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OrderTestFactory {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderTestFactory(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

     // Создаёт заказ в статусе CREATED
    public Long createOrderInStatusCreated() {
        Order order = createBaseOrder();
        Order saved = orderRepository.save(order);
        return saved.getId();
    }

    private Order createBaseOrder() {

        // Создание или получение пользователя для тестов
        UserEntity testUser = userRepository.findByEmail("test@test.com")
                .orElseGet(() -> {
                    UserEntity user = new UserEntity();
                    user.setEmail("test@test.com");
                    user.setName("Test User");
                    user.setPassword("encoded");
                    user.setRole("ROLE_USER");
                    user.setEnabled(true);
                    return userRepository.save(user);
                });

        Order order = new Order();
        order.setUser(testUser);
        order.setTotalPrice(100.0);
        order.setCreatedAt(LocalDateTime.now());
        return order;
    }
}
