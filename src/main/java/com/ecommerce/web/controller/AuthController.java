package com.ecommerce.web.controller;

import com.ecommerce.security.CustomUserDetails;
import com.ecommerce.security.JwtService;
import com.ecommerce.service.impl.TokenBlacklistService;
import com.ecommerce.service.interfaces.UserService;
import com.ecommerce.web.dto.AuthResponse;
import com.ecommerce.web.dto.LoginRequest;
import com.ecommerce.web.dto.RegisterRequest;
import com.ecommerce.web.dto.UserDto;
import com.ecommerce.web.dto.UpdateProfileRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;



    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getId(),
                user.getName(),
                user.getRole()
        );

        AuthResponse resp = new AuthResponse();
        resp.setToken(token);
        resp.setUser(new UserDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        ));

        return ResponseEntity.ok(resp);
    }

    // REGISTER (автоматически логиним после регистрации)
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {

        userService.register(
                registerRequest.getEmail(),
                registerRequest.getName(),
                registerRequest.getPassword()
        );

        // Важно: после register делаем реальную аутентификацию,
        // чтобы получить корректные id/role (без null)
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequest.getEmail(),
                        registerRequest.getPassword()
                )
        );

        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getId(),
                user.getName(),
                user.getRole()
        );

        AuthResponse resp = new AuthResponse();
        resp.setToken(token);
        resp.setUser(new UserDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        ));

        return ResponseEntity.ok(resp);
    }

    // ME (PROFILE)
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

    // UPDATE PROFILE
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
