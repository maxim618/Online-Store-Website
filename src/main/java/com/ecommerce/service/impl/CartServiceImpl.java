package com.ecommerce.service.impl;

import com.ecommerce.cart.domain.CartStorage;
import com.ecommerce.persistence.model.Product;
import com.ecommerce.persistence.repository.ProductRepository;
import com.ecommerce.service.interfaces.CartService;
import com.ecommerce.web.dto.CartDto;
import com.ecommerce.web.dto.CartItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartStorage cartStorage;
    private final ProductRepository productRepository;

    @Override
    public void addItem(Long userId, Long productId, int quantity) {
        cartStorage.addItem(userId, productId, quantity);
    }

    @Override
    public void setItemQuantity(Long userId, Long productId, int quantity) {
        cartStorage.setItemQuantity(userId, productId, quantity);
    }

    @Override
    public void removeItem(Long userId, Long productId) {
        cartStorage.removeItem(userId, productId);
    }

    @Override
    public void clearCart(Long userId) {
        cartStorage.clearCart(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public CartDto getUserCart(Long userId) {

        Map<Long, Integer> items = cartStorage.getItems(userId); // productId -> qty

        if (items.isEmpty()) {
            CartDto empty = new CartDto();
            empty.setItems(List.of());
            empty.setTotalPrice(0.0);
            empty.setTotalQuantity(0);
            return empty;
        }

        List<Long> productIds = new ArrayList<>(items.keySet());

        List<Product> products = productRepository.findAllById(productIds);

        Map<Long, Product> productById = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        List<CartItemDto> itemDtos = productIds.stream()
                .map(pid -> {
                    Product p = productById.get(pid);
                    if (p == null) {
                        // если товар удалили из каталога — пропускаем позицию
                        return null;
                    }

                    int qty = items.get(pid);

                    CartItemDto dto = new CartItemDto();
                    dto.setProductId(p.getId());
                    dto.setProductTitle(p.getTitle());
                    dto.setUnitPrice(p.getPrice());
                    dto.setQuantity(qty);

                    double lineTotal = (p.getPrice() != null ? p.getPrice() : 0.0) * qty;
                    dto.setLineTotal(lineTotal);

                    return dto;
                })
                .filter(Objects::nonNull)
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
        return cartStorage.getItems(userId).size();
    }
}
