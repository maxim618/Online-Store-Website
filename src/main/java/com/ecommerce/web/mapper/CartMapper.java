package com.ecommerce.web.mapper;

import com.ecommerce.persistence.model.CartItem;
import com.ecommerce.web.dto.CartItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productTitle", source = "product.title")
    @Mapping(target = "unitPrice", source = "product.price")
    @Mapping(target = "imageUrl", source = "product.imageUrl")
    @Mapping(target = "lineTotal", expression = "java(item.getProduct().getPrice() * item.getQuantity())")
    CartItemDto toDto(CartItem item);

    List<CartItemDto> toDtoList(List<CartItem> items);
}
