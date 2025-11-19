package com.ecommerce.service.impl;

import com.ecommerce.persistence.model.Product;
import com.ecommerce.persistence.repository.ProductRepository;
import com.ecommerce.service.interfaces.ProductService;
import com.ecommerce.web.dto.ProductDto;
import com.ecommerce.web.mapper.ProductMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;
    private final CategoryRepository categoryRepo;
    private final ProductMapper mapper;

    @Override
    public ProductDto create(ProductDto dto) {
        Product entity = mapper.toEntity(dto);
        entity.setCategory(categoryRepo.getReferenceById(dto.getCategoryId()));
        Product save = repo.save(entity);

        return mapper.toDto(save);
    }

    @Override
    public ProductDto update(Long id, ProductDto dto) {
        Product entity = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found"));
        entity.setTitle(dto.getTitle());
        entity.setPrice(dto.getPrice());
        entity.setQuantity(dto.getQuantity());
        entity.setDescription(dto.getDescription());
        entity.setImageUrl(dto.getImageUrl());
        entity.setCategory(categoryRepo.getReferenceById(dto.getCategoryId()));

        return mapper.toDto(repo.save(entity));
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public ProductDto getById(Long id) {
        return repo.findById(id).map(mapper::toDto).orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    @Override
    public List<ProductDto> getAll() {
        return repo.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public List<ProductDto> search(String text) {
        return repo.findByTitleContainingIgnoreCase(text).stream().map(mapper::toDto).toList();
    }

    @Override
    public List<ProductDto> getByCategory(Long categoryId) {
        return repo.findByCategoryId(categoryId).stream().map(mapper::toDto).toList();
    }
}
