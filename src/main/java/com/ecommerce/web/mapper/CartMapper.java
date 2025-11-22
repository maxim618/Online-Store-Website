package com.ecommerce.web.mapper;

import com.ecommerce.persistence.model.CartItem;
import com.ecommerce.web.dto.CartItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productTitle", source = "product.title")
    @Mapping(target = "price", source = "product.price")
    CartItemDto toDto(CartItem item);
}
