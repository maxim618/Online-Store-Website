package com.ecommerce.web.controller;

import com.ecommerce.service.interfaces.AuthService;
import com.ecommerce.service.interfaces.UserService;
import com.ecommerce.web.dto.AuthResponse;
import com.ecommerce.web.dto.LoginRequest;
import com.ecommerce.web.dto.RegisterRequest;
import com.ecommerce.web.dto.UserDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public UserDto register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
