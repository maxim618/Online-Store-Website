package com.ecommerce.web.mapper;

import com.ecommerce.persistence.model.Order;
import com.ecommerce.persistence.model.OrderItem;
import com.ecommerce.web.dto.OrderDto;
import com.ecommerce.web.dto.OrderItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productTitle", source = "product.title")
    OrderItemDto toItemDto(OrderItem item);

    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "items", expression = "java(order.getItems().stream().map(this::toItemDto).toList())")
    OrderDto toDto(Order order);
}
