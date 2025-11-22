package com.ecommerce.persistence.repository;

import com.ecommerce.persistence.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<WishlistItem, Long> {

    List<WishlistItem> findByUserId(Long userId);

    boolean existsByUserIdAndProduct_Id(Long userId, Long productId);

    void deleteByUserIdAndProduct_Id(Long userId, Long productId);
}
