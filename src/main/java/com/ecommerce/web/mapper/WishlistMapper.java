package com.ecommerce.web.mapper;

import com.ecommerce.persistence.model.WishlistItem;
import com.ecommerce.web.dto.WishlistItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WishlistMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productTitle", source = "product.title")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "imageUrl", source = "product.imageUrl")
    WishlistItemDto toDto(WishlistItem item);

    List<WishlistItemDto> toDtoList(List<WishlistItem> items);
}
