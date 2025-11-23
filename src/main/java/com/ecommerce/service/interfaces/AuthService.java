package com.ecommerce.service.interfaces;

import com.ecommerce.web.dto.AuthResponse;
import com.ecommerce.web.dto.LoginRequest;

public interface AuthService {

    AuthResponse login(LoginRequest request);
}
