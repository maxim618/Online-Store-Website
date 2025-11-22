package com.ecommerce.service.interfaces;

import com.ecommerce.web.dto.UserDto;

public interface UserService {
    UserDto registerUser (String email, String  name, String password);
    UserDto getByEmail (String email);
    boolean emailExists (String email);
    boolean validateCredentials (String email, String rawPassword);
}
