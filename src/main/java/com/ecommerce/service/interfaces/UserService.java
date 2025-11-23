package com.ecommerce.service.interfaces;

import com.ecommerce.persistence.model.UserEntity;
import com.ecommerce.web.dto.RegisterRequest;
import com.ecommerce.web.dto.UserDto;

public interface UserService {
    UserDto register(RegisterRequest request);

    UserEntity loadUserByEmail(String email); // нужно для Security

    UserDto getById(Long id);
}
