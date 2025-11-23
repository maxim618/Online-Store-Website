package com.ecommerce.service.impl;

import com.ecommerce.persistence.model.UserEntity;
import com.ecommerce.security.JwtService;
import com.ecommerce.service.interfaces.AuthService;
import com.ecommerce.service.interfaces.UserService;
import com.ecommerce.web.dto.AuthResponse;
import com.ecommerce.web.dto.LoginRequest;
import com.ecommerce.web.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final UserMapper mapper;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public AuthResponse login(LoginRequest request) {

        UserEntity user = userService.loadUserByEmail(request.getEmail());

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail());

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUser(mapper.toDto(user));
        return response;
    }
}
