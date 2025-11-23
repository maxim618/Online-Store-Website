package com.ecommerce.service.interfaces;

import com.ecommerce.persistence.model.UserEntity;
import com.ecommerce.web.dto.RegisterRequest;
import com.ecommerce.web.dto.UserDto;

public interface UserService {
    void register(String email, String name, String password);

    UserEntity loadUserByEmail(String email); // нужно для Security

    UserDto getById(Long id);
}
