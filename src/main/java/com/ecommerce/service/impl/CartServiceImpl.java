package com.ecommerce.service.impl;

import com.ecommerce.persistence.model.CartItem;
import com.ecommerce.persistence.model.Product;
import com.ecommerce.persistence.repository.CartItemRepository;
import com.ecommerce.persistence.repository.ProductRepository;
import com.ecommerce.service.interfaces.CartService;
import com.ecommerce.web.dto.CartDto;
import com.ecommerce.web.dto.CartItemDto;
import com.ecommerce.web.mapper.CartMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartRepo;
    private final ProductRepository productRepo;
    private final CartMapper mapper;

    @Override
    public void add(Long userId, Long productId, Integer quantity) {

        CartItem item = cartRepo.findByUserIdAndProductId(userId, productId)
                .orElse(null);

        // Если товар уже в корзине → увеличиваем количество
        if (item != null) {
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));

            item = CartItem.builder()
                    .userId(userId)
                    .product(product)
                    .quantity(quantity)
                    .build();
        }

        cartRepo.save(item);
    }

    @Override
    public void remove(Long userId, Long productId) {
        cartRepo.deleteByUserIdAndProductId(userId, productId);
    }

    @Override
    public void clear(Long userId) {
        cartRepo.deleteByUserId(userId);
    }

    @Override
    public CartDto getUserCart(Long userId) {

        List<CartItem> items = cartRepo.findByUserId(userId);

        List<CartItemDto> itemDtos = items.stream()
                .map(mapper::toDto)
                .toList();

        double total = items.stream()
                .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum();

        CartDto cart = new CartDto();
        cart.setItems(itemDtos);
        cart.setTotalPrice(total);

        return cart;
    }
}
