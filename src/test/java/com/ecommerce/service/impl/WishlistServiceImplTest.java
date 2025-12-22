package com.ecommerce.service.impl;

import com.ecommerce.exception.EntityNotFoundException;
import com.ecommerce.persistence.model.Product;
import com.ecommerce.persistence.model.WishlistItem;
import com.ecommerce.persistence.repository.ProductRepository;
import com.ecommerce.persistence.repository.WishlistRepository;
import com.ecommerce.web.dto.WishlistItemDto;
import com.ecommerce.web.mapper.WishlistMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WishlistServiceImplTest {

    private WishlistRepository wishlistRepository;
    private ProductRepository productRepository;
    private WishlistMapper wishlistMapper;

    private WishlistServiceImpl wishlistService;

    @BeforeEach
    void setUp() {
        wishlistRepository = mock(WishlistRepository.class);
        productRepository = mock(ProductRepository.class);
        wishlistMapper = mock(WishlistMapper.class);

        wishlistService = new WishlistServiceImpl(wishlistRepository, productRepository, wishlistMapper);
    }

        @Test
        void addToWishlist_shouldSaveNewItem_whenNotExists() {
            Long userId = 1L;
            Long productId = 10L;

            // в wishlist ещё нет такого товара
            when(wishlistRepository.existsByUserIdAndProduct_Id(userId, productId))
                    .thenReturn(false);

            Product product = new Product();
            product.setId(productId);
            when(productRepository.findById(productId))
                    .thenReturn(Optional.of(product));

            wishlistService.addToWishlist(userId, productId);

            // проверяем, что в репозиторий ушёл правильный объект
            var captor = org.mockito.ArgumentCaptor.forClass(WishlistItem.class);
            verify(wishlistRepository).save(captor.capture());

            WishlistItem saved = captor.getValue();
            assertEquals(userId, saved.getUserId());
            assertEquals(product, saved.getProduct());
        }

        @Test
        void addToWishlist_shouldDoNothing_whenAlreadyExists() {
            Long userId = 1L;
            Long productId = 10L;

            // уже есть запись — сервис должен просто выйти
            when(wishlistRepository.existsByUserIdAndProduct_Id(userId, productId))
                    .thenReturn(true);

            wishlistService.addToWishlist(userId, productId);

            // продукт не ищем, ничего не сохраняем
            verify(productRepository, Mockito.never()).findById(anyLong());
            verify(wishlistRepository, Mockito.never()).save(any());
        }

        @Test
        void addToWishlist_shouldThrow_whenProductNotFound() {
            Long userId = 1L;
            Long productId = 10L;

            when(wishlistRepository.existsByUserIdAndProduct_Id(userId, productId))
                    .thenReturn(false);
            when(productRepository.findById(productId))
                    .thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> wishlistService.addToWishlist(userId, productId)
            );
        }

        @Test
        void removeFromWishlist_shouldDelegateToRepository() {
            Long userId = 1L;
            Long productId = 10L;

            wishlistService.removeFromWishlist(userId, productId);

            verify(wishlistRepository).deleteByUserIdAndProduct_Id(userId, productId);
        }

        @Test
        void getWishlist_shouldReturnMappedDto() {

            Long userId = 1L;

            WishlistItem item1 = new WishlistItem();
            item1.setId(1L);
            WishlistItem item2 = new WishlistItem();
            item2.setId(2L);

            List<WishlistItem> entities = List.of(item1, item2);
            when(wishlistRepository.findByUserId(userId)).thenReturn(entities);

            WishlistItemDto dto1 = new WishlistItemDto();
            dto1.setProductId(100L);
            WishlistItemDto dto2 = new WishlistItemDto();
            dto2.setProductId(200L);

            when(wishlistMapper.toDto(item1)).thenReturn(dto1);
            when(wishlistMapper.toDto(item2)).thenReturn(dto2);

            List<WishlistItemDto> result = wishlistService.getWishlist(userId);

            assertEquals(2, result.size());
            assertEquals(dto1, result.get(0));
            assertEquals(dto2, result.get(1));
        }
}
