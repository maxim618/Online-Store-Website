package com.ecommerce.persistence.repository;

import com.ecommerce.persistence.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserId(Long userId);

    void deleteByUserId(Long userId);

    Optional<CartItem> findByUserIdAndProduct_Id(Long userId, Long productId);


    void deleteByUserIdAndProduct_Id(Long userId, Long productId);

    long countByUserId(Long userId);

}
