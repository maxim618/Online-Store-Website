package com.ecommerce.persistence.repository;

import com.ecommerce.persistence.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser_Id(Long userId);
    Optional<Order> findByIdAndUserId(Long orderId, Long userId);
}
