package com.ecommerce.web.controller;

import com.ecommerce.security.CustomUserDetails;
import com.ecommerce.security.JwtService;
import com.ecommerce.service.impl.TokenBlacklistService;
import com.ecommerce.service.interfaces.UserService;
import com.ecommerce.web.dto.LoginRequest;
import com.ecommerce.web.dto.RegisterRequest;
import com.ecommerce.web.dto.TokenResponse;
import com.ecommerce.web.dto.UpdateProfileRequest;
import com.ecommerce.web.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    // ===========================
    // LOGIN
    // ===========================
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getId(),
                user.getName(),
                user.getRole()
        );


        return ResponseEntity.ok(
                new TokenResponse(
                        token,
                        user.getId(),
                        user.getEmail(),
                        user.getName(),
                        user.getRole(),
                        86400000
                )
        );
    }

    // ===========================
    // REGISTER
    // ===========================
    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody RegisterRequest request) {

        userService.register(
                request.getEmail(),
                request.getName(),
                request.getPassword()
        );

        // После регистрации логиним автоматически
        String token = jwtService.generateToken(
                request.getEmail(),
                null,
                request.getName(),
                "USER"
        );

        return ResponseEntity.ok(
                new TokenResponse(
                        token,
                        null,
                        request.getEmail(),
                        request.getName(),
                        "USER",
                        86400000
                )
        );
    }

    // ===========================
    // ME (PROFILE)
    // ===========================
    @GetMapping("/me")
    public ResponseEntity<UserDto> me(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        UserDto dto = new UserDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        );

        return ResponseEntity.ok(dto);
    }

    // ===========================
    // UPDATE PROFILE
    // ===========================
    @PutMapping("/me")
    public ResponseEntity<String> updateProfile(
            Authentication authentication,
            @RequestBody UpdateProfileRequest request
    ) {

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        userService.updateProfile(
                user.getEmail(),
                request.getName(),
                request.getPassword()
        );

        return ResponseEntity.ok("Profile updated");
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("No token found");
        }

        String token = authHeader.substring(7);

        tokenBlacklistService.blacklist(token);

        return ResponseEntity.ok("Logged out");
    }

}
