package com.ecommerce.persistence.repository;

import com.ecommerce.persistence.model.WishlistEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<WishlistEntry, Long> {
    List<WishlistEntry> findByUserId(Long userId);
    boolean existsByUserIdAndProductId(Long userId, Long productId);
}
