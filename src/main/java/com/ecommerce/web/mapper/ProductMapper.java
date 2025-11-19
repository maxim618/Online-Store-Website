package com.ecommerce.web.mapper;

import com.ecommerce.persistence.model.Product;
import com.ecommerce.web.dto.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "categoryId", source = "category.id")
    ProductDto toDto(Product product);

    @Mapping(target = "categoryId", source = "category.id")
    Product toEntity(ProductDto dto);
}
