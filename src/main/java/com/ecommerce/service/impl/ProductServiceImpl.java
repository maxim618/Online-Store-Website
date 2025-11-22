package com.ecommerce.service.impl;

import com.ecommerce.exception.EntityNotFoundException;
import com.ecommerce.persistence.model.Category;
import com.ecommerce.persistence.model.Product;
import com.ecommerce.persistence.repository.CategoryRepository;
import com.ecommerce.persistence.repository.ProductRepository;
import com.ecommerce.service.interfaces.ProductService;
import com.ecommerce.web.dto.ProductDto;
import com.ecommerce.web.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductDto create(ProductDto dto) {

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category not found: " + dto.getCategoryId()));

        Product product = productMapper.toEntity(dto);
        product.setCategory(category);

        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    public ProductDto update(Long id, ProductDto dto) {

        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category not found: " + dto.getCategoryId()));

        // обновляем вручную, чтобы сохранить entity state
        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setQuantity(dto.getQuantity());
        existing.setDiscount(dto.getDiscount());
        existing.setImageUrl(dto.getImageUrl());
        existing.setCategory(category);

        return productMapper.toDto(productRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public ProductDto getById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
    }

    @Override
    public List<ProductDto> getAll() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    public List<ProductDto> search(String q) {
        return productRepository.findByTitleContainingIgnoreCase(q)
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    public List<ProductDto> getByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(productMapper::toDto)
                .toList();
    }
}
