package com.ecommerce.persistence.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wishlist",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}) //уникальный индекс - защита от дублей на уровне БД
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
