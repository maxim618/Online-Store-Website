package com.ecommerce.service.interfaces;

import com.ecommerce.web.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto create(CategoryDto dto);

    CategoryDto update(Long id, CategoryDto dto);

    void delete(Long id);

    CategoryDto getById(Long id);

    List<CategoryDto> getAll();
}
