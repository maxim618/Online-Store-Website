package com.ecommerce.web.mapper;

import com.ecommerce.persistence.model.Order;
import com.ecommerce.persistence.model.OrderItem;
import com.ecommerce.web.dto.OrderDto;
import com.ecommerce.web.dto.OrderItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    // ---------------------
    // Order → OrderDto
    // ---------------------
    @Mapping(target = "items", source = "items")
    OrderDto toDto(Order order);

    List<OrderItemDto> toItemDtoList(List<OrderItem> items);

    // ---------------------
    // OrderItem → OrderItemDto
    // ---------------------
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productTitle", source = "product.title")
    @Mapping(target = "lineTotal", expression = "java(item.getPrice() * item.getQuantity())")
    OrderItemDto toItemDto(OrderItem item);
}
