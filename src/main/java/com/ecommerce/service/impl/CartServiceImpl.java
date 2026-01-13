package com.ecommerce.service.impl;

import com.ecommerce.exception.EntityNotFoundException;
import com.ecommerce.persistence.model.CartItem;
import com.ecommerce.persistence.model.Product;
import com.ecommerce.persistence.repository.CartItemRepository;
import com.ecommerce.persistence.repository.ProductRepository;
import com.ecommerce.service.interfaces.CartService;
import com.ecommerce.web.dto.CartDto;
import com.ecommerce.web.dto.CartItemDto;
import com.ecommerce.web.mapper.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    @Override
    public void addItem(Long userId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        CartItem cartItem = cartItemRepository
                .findByUserIdAndProduct_Id(userId, productId)
                .orElse(null);

        int newQty = quantity;
        if (cartItem != null) {
            newQty = cartItem.getQuantity() + quantity;
        }

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
    public void setItemQuantity(Long userId, Long productId, int quantity) {
        CartItem cartItem = cartItemRepository
                .findByUserIdAndProduct_Id(userId, productId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Cart item not found for product " + productId));

        if (quantity <= 0) {
            // если 0 или меньше — просто убираем товар из корзины
            cartItemRepository.delete(cartItem);
            return;
        }

        validateStock(cartItem.getProduct(), quantity);
        cartItem.setQuantity(quantity);

        cartItemRepository.save(cartItem);
    }

    @Override
    public void removeItem(Long userId, Long productId) {
        cartItemRepository.deleteByUserIdAndProduct_Id(userId, productId);
    }

    @Override
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public CartDto getUserCart(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);

        List<CartItemDto> itemDtos = items.stream()
                .map(cartMapper::toDto)
                .toList();

        double totalPrice = itemDtos.stream()
                .mapToDouble(CartItemDto::getLineTotal)
                .sum();

        int totalQuantity = itemDtos.stream()
                .mapToInt(CartItemDto::getQuantity)
                .sum();

        CartDto dto = new CartDto();
        dto.setItems(itemDtos);
        dto.setTotalPrice(totalPrice);
        dto.setTotalQuantity(totalQuantity);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public long getCartItemCount(Long userId) {
        return cartItemRepository.countByUserId(userId);
    }

    /**
     * Проверка, что мы не заказываем больше, чем есть на складе.
     * Ожидается, что в Product есть поле quantity (или stock).
     */
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
