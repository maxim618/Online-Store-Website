package com.ecommerce.security;

import com.ecommerce.service.impl.TokenBlacklistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtFilterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private TokenBlacklistService blacklistService;

    /** 1. БЕЗ ТОКЕНА → 401*/
    @Test
    void requestWithoutTokenShouldReturn401() throws Exception {
        mockMvc.perform(get("/test/secure"))
                .andExpect(status().isUnauthorized());
    }

    /** 2. ТОКЕН ЕСТЬ, НО НЕДЕЙСТВИТЕЛЬНЫЙ → 401*/
    @Test
    void invalidTokenShouldReturn401() throws Exception {

        when(jwtService.isTokenValid(anyString())).thenReturn(false);

        mockMvc.perform(get("/test/secure")
                        .header("Authorization", "Bearer INVALID"))
                .andExpect(status().isUnauthorized());
    }

    /** 3. ТОКЕН В BLACKLIST → 401*/
    @Test
    void blacklistedTokenShouldReturn401() throws Exception {

        when(jwtService.isTokenValid(anyString())).thenReturn(true);
        when(blacklistService.isBlacklisted(anyString())).thenReturn(true);

        mockMvc.perform(get("/test/secure")
                        .header("Authorization", "Bearer TOKEN123"))
                .andExpect(status().isUnauthorized());
    }

    /** 4. ВАЛИДНЫЙ ТОКЕН → 200 ОК*/
    @Test
    void validTokenShouldPassFilter() throws Exception {

        // токен валидный
        when(jwtService.isTokenValid("TOKEN123")).thenReturn(true);
        when(blacklistService.isBlacklisted("TOKEN123")).thenReturn(false);

        // данные пользователя
        when(jwtService.extractEmail("TOKEN123")).thenReturn("user@mail.com");
        when(jwtService.extractUserId("TOKEN123")).thenReturn(1L);
        when(jwtService.extractName("TOKEN123")).thenReturn("John");
        when(jwtService.extractRole("TOKEN123")).thenReturn("ROLE_USER");

        mockMvc.perform(get("/test/secure")
                        .header("Authorization", "Bearer TOKEN123"))
                .andExpect(status().isOk());
    }
}
