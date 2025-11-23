package com.ecommerce.web.mapper;

import com.ecommerce.persistence.model.Category;
import com.ecommerce.web.dto.CategoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    List<CategoryDto> toDtoList(List<Category> categories);

    // products заполняются отдельно (из Product), поэтому игнорируем
    @Mapping(target = "products", ignore = true)
    Category toEntity(CategoryDto dto);
}
