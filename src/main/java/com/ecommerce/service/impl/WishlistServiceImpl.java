package com.ecommerce.service.impl;

import com.ecommerce.exception.EntityNotFoundException;
import com.ecommerce.persistence.model.Product;
import com.ecommerce.persistence.model.WishlistItem;
import com.ecommerce.persistence.repository.ProductRepository;
import com.ecommerce.persistence.repository.WishlistRepository;
import com.ecommerce.service.interfaces.WishlistService;
import com.ecommerce.web.dto.WishlistItemDto;
import com.ecommerce.web.mapper.WishlistMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final WishlistMapper wishlistMapper;

    @Override
    public void addToWishlist(Long userId, Long productId) {

        if (wishlistRepository.existsByUserIdAndProduct_Id(userId, productId)) {
            return; // уже в избранном — ничего не делаем
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        WishlistItem item = WishlistItem.builder()
                .userId(userId)
                .product(product)
                .build();

        wishlistRepository.save(item);
    }

    @Override
    public void removeFromWishlist(Long userId, Long productId) {
        wishlistRepository.deleteByUserIdAndProduct_Id(userId, productId);
    }

    @Override
    public List<WishlistItemDto> getWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId)
                .stream()
                .map(wishlistMapper::toDto)
                .toList();
    }
}
