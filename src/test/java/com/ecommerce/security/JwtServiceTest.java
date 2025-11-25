package com.ecommerce.security;

import com.ecommerce.persistence.model.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();
        injectSecret();
    }

    @Test
    void generateAndValidateToken() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("user@mail.com");
        user.setName("John");
        user.setRole("ROLE_USER");
        user.setPassword(encoder.encode("123"));

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getId(),
                user.getName(),
                user.getRole()
        );

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token));
    }

    private void injectSecret() throws Exception {
        Field secretField = JwtService.class.getDeclaredField("SECRET");
        secretField.setAccessible(true);
        secretField.set(jwtService, "TEST_SECRET_KEY_12345678901234567890");
    }

}
