package com.ecommerce.cart.infra.jpa;

import com.ecommerce.cart.domain.CartStorage;
import com.ecommerce.exception.EntityNotFoundException;
import com.ecommerce.persistence.model.CartItem;
import com.ecommerce.persistence.model.Product;
import com.ecommerce.persistence.repository.CartItemRepository;
import com.ecommerce.persistence.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@ConditionalOnProperty(name = "cart.storage", havingValue = "jpa", matchIfMissing = true)
@Component
@RequiredArgsConstructor
public class JpaCartStorage implements CartStorage {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void addItem(long userId, long productId, int deltaQty) {
        if (deltaQty <= 0) throw new IllegalArgumentException("Quantity must be positive");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        CartItem cartItem = cartItemRepository.findByUserIdAndProduct_Id(userId, productId).orElse(null);

        int newQty = deltaQty;
        if (cartItem != null) newQty = cartItem.getQuantity() + deltaQty;

        validateStock(product, newQty);

        if (cartItem == null) {
            cartItem = CartItem.builder()
                    .userId(userId)
                    .product(product)
                    .quantity(newQty)
                    .build();
        } else {
            cartItem.setQuantity(newQty);
        }

        cartItemRepository.save(cartItem);
    }

    @Override
    @Transactional
    public void setItemQuantity(long userId, long productId, int qty) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProduct_Id(userId, productId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found for product " + productId));

        if (qty <= 0) {
            cartItemRepository.delete(cartItem);
            return;
        }

        validateStock(cartItem.getProduct(), qty);
        cartItem.setQuantity(qty);
        cartItemRepository.save(cartItem);
    }

    @Override
    @Transactional
    public void removeItem(long userId, long productId) {
        cartItemRepository.deleteByUserIdAndProduct_Id(userId, productId);
    }

    @Override
    @Transactional
    public void clearCart(long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Integer> getItems(long userId) {
        return cartItemRepository.findByUserId(userId).stream()
                .collect(Collectors.toMap(
                        ci -> ci.getProduct().getId(),
                        CartItem::getQuantity
                ));
    }

    private void validateStock(Product product, int desiredQty) {
        Integer stock = product.getQuantity();
        if (stock != null && desiredQty > stock) {
            throw new IllegalArgumentException(
                    "Not enough stock for product " + product.getId() +
                            ": requested=" + desiredQty + ", available=" + stock
            );
        }
    }
}
