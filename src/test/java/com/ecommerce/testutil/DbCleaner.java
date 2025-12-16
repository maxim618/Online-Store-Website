package com.ecommerce.testutil;

import com.ecommerce.persistence.repository.OrderRepository;
import com.ecommerce.persistence.repository.OrderItemRepository;
import com.ecommerce.persistence.repository.WishlistRepository;
import com.ecommerce.persistence.repository.CartItemRepository;
import com.ecommerce.persistence.repository.ProductRepository;
import com.ecommerce.persistence.repository.CategoryRepository;
import com.ecommerce.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbCleaner {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final WishlistRepository wishlistRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public void clean() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        wishlistRepository.deleteAll();
        cartItemRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }
}
