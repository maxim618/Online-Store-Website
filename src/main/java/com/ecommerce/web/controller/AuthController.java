package com.ecommerce.web.controller;

import com.ecommerce.persistence.model.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.security.JwtService;
import com.ecommerce.web.dto.LoginRequest;
import com.ecommerce.web.dto.RegisterRequest;
import com.ecommerce.web.dto.TokenResponse;
import com.ecommerce.service.interfaces.UserService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

//        String token = jwtService.generateToken(request.getEmail());
        UserDetails user = (UserDetails) auth.getCredentials();
        String token = jwtService.generateToken(user.getUsername());

        return ResponseEntity.ok(
                new TokenResponse(token,
                user.getUsername(),
                null,    //временный вариант
                86400000
                )
        );
        // EXPIRE в JwtService = 24h));
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody RegisterRequest request) {

        userService.register(
                request.getEmail(),
                request.getName(),
                request.getPassword()
        );

        String token = jwtService.generateToken(request.getEmail());

        return ResponseEntity.ok(
                new TokenResponse(
                        token,
                        request.getEmail(),
                        request.getName(),
                        86400000
                )
        );
    }

}
