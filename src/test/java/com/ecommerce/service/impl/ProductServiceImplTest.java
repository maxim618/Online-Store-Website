package com.ecommerce.service.impl;

import com.ecommerce.exception.EntityNotFoundException;
import com.ecommerce.persistence.model.Category;
import com.ecommerce.persistence.model.Product;
import com.ecommerce.persistence.repository.CategoryRepository;
import com.ecommerce.persistence.repository.ProductRepository;
import com.ecommerce.web.dto.ProductDto;
import com.ecommerce.web.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.anyLong;

class ProductServiceImplTest {

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private ProductMapper productMapper;

    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        productMapper = mock(ProductMapper.class);

        productService = new ProductServiceImpl(productRepository, categoryRepository, productMapper);
    }

    // create()

    @Test
    void create_shouldSaveProductWithCategory_whenCategoryExists() {
        ProductDto dto = new ProductDto();
        dto.setCategoryId(7L);
        dto.setTitle("Phone");
        dto.setPrice(100.0);

        Category category = new Category();
        category.setId(7L);

        Product entityFromMapper = new Product();
        entityFromMapper.setTitle("Phone");

        Product saved = new Product();
        saved.setId(1L);
        saved.setTitle("Phone");
        saved.setCategory(category);

        ProductDto expectedDto = new ProductDto();
        expectedDto.setId(1L);
        expectedDto.setTitle("Phone");
        expectedDto.setCategoryId(7L);

        when(categoryRepository.findById(7L)).thenReturn(Optional.of(category));
        when(productMapper.toEntity(dto)).thenReturn(entityFromMapper);
        when(productRepository.save(any(Product.class))).thenReturn(saved);
        when(productMapper.toDto(saved)).thenReturn(expectedDto);

        ProductDto result = productService.create(dto);

        assertEquals(1L, result.getId());
        assertEquals("Phone", result.getTitle());

        // Проверяем, что в save ушёл product с проставленной category
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());

        Product toSave = captor.getValue();
        assertSame(category, toSave.getCategory());
        assertEquals("Phone", toSave.getTitle());
    }

    @Test
    void create_shouldThrow_whenCategoryNotFound() {
        ProductDto dto = new ProductDto();
        dto.setCategoryId(7L);

        when(categoryRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.create(dto));

        verify(productMapper, never()).toEntity(any());
        verify(productRepository, never()).save(any());
    }

    // update()

    @Test
    void update_shouldUpdateFieldsAndSave_whenProductAndCategoryExist() {
        Long id = 10L;

        ProductDto dto = new ProductDto();
        dto.setCategoryId(7L);
        dto.setTitle("New title");
        dto.setDescription("New desc");
        dto.setPrice(123.0);
        dto.setQuantity(5);
        dto.setDiscount(10);
        dto.setImageUrl("img.png");

        Product existing = new Product();
        existing.setId(id);
        existing.setTitle("Old title");

        Category category = new Category();
        category.setId(7L);

        // JPA вернёт тот же объект
        Product saved = existing;

        ProductDto mappedDto = new ProductDto();
        mappedDto.setId(id);
        mappedDto.setTitle("New title");
        mappedDto.setCategoryId(7L);

        when(productRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(7L)).thenReturn(Optional.of(category));
        when(productRepository.save(existing)).thenReturn(saved);
        when(productMapper.toDto(saved)).thenReturn(mappedDto);

        ProductDto result = productService.update(id, dto);

        assertEquals(id, result.getId());
        assertEquals("New title", result.getTitle());

        // Проверяем, что поля реально обновились на existing
        assertEquals("New title", existing.getTitle());
        assertEquals("New desc", existing.getDescription());
        assertEquals(123.0, existing.getPrice());
        assertEquals(5, existing.getQuantity());
        assertEquals(10, existing.getDiscount());
        assertEquals("img.png", existing.getImageUrl());
        assertSame(category, existing.getCategory());

        verify(productRepository).save(existing);
    }

    @Test
    void update_shouldThrow_whenProductNotFound() {
        Long id = 10L;
        ProductDto dto = new ProductDto();
        dto.setCategoryId(7L);

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.update(id, dto));

        verify(categoryRepository, never()).findById(anyLong());
        verify(productRepository, never()).save(any());
    }

    @Test
    void update_shouldThrow_whenCategoryNotFound() {
        Long id = 10L;
        ProductDto dto = new ProductDto();
        dto.setCategoryId(7L);

        Product existing = new Product();
        existing.setId(id);

        when(productRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.update(id, dto));

        verify(productRepository, never()).save(any());
    }

    // delete()

    @Test
    void delete_shouldDelete_whenExists() {
        Long id = 10L;

        when(productRepository.existsById(id)).thenReturn(true);

        productService.delete(id);

        verify(productRepository).deleteById(id);
    }

    @Test
    void delete_shouldThrow_whenNotExists() {
        Long id = 10L;

        when(productRepository.existsById(id)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> productService.delete(id));

        verify(productRepository, never()).deleteById(anyLong());
    }

    // getById()

    @Test
    void getById_shouldReturnDto_whenFound() {
        Long id = 10L;

        Product product = new Product();
        product.setId(id);

        ProductDto dto = new ProductDto();
        dto.setId(id);

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(dto);

        ProductDto result = productService.getById(id);

        assertEquals(id, result.getId());
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        Long id = 10L;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.getById(id));
    }

    // getAll(), search(), getByCategory()

    @Test
    void getAll_shouldMapAllEntitiesToDto() {
        Product p1 = new Product(); p1.setId(1L);
        Product p2 = new Product(); p2.setId(2L);

        ProductDto d1 = new ProductDto(); d1.setId(1L);
        ProductDto d2 = new ProductDto(); d2.setId(2L);

        when(productRepository.findAll()).thenReturn(List.of(p1, p2));
        when(productMapper.toDto(p1)).thenReturn(d1);
        when(productMapper.toDto(p2)).thenReturn(d2);

        List<ProductDto> result = productService.getAll();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void search_shouldReturnMappedDto() {
        Product p1 = new Product(); p1.setId(1L);
        ProductDto d1 = new ProductDto(); d1.setId(1L);

        when(productRepository.findByTitleContainingIgnoreCase("pho")).thenReturn(List.of(p1));
        when(productMapper.toDto(p1)).thenReturn(d1);

        List<ProductDto> result = productService.search("pho");

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getByCategory_shouldReturnMappedDto() {
        Product p1 = new Product(); p1.setId(1L);
        ProductDto d1 = new ProductDto(); d1.setId(1L);

        when(productRepository.findByCategoryId(7L)).thenReturn(List.of(p1));
        when(productMapper.toDto(p1)).thenReturn(d1);

        List<ProductDto> result = productService.getByCategory(7L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }
}
