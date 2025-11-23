package com.ecommerce.web.mapper;

import com.ecommerce.persistence.model.UserEntity;
import com.ecommerce.web.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(UserEntity user);
}
