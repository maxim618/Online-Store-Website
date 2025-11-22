package com.ecommerce.service.interfaces;

import com.ecommerce.web.dto.ProductDto;

import java.util.List;

public interface ProductService {
    ProductDto create(ProductDto dto);

    ProductDto update(Long id, ProductDto dto);

    void delete(Long id);

    ProductDto getById(Long id);

    List<ProductDto> getAll();

    List<ProductDto> search(String text);

    List<ProductDto> getByCategory(Long categoryId);
}
