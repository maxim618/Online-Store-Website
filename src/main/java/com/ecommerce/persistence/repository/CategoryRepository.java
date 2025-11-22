package com.ecommerce.persistence.repository;

import com.ecommerce.persistence.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // при необходимости можно добавить методы поиска:
    // Optional<Category> findByName(String name);
}
