package com.ecommerce.security;

import com.ecommerce.persistence.model.UserEntity;
import com.ecommerce.persistence.repository.UserRepository;
import com.ecommerce.testutil.DbCleaner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AuthMeIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired DbCleaner dbCleaner;
    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        dbCleaner.clean();

        UserEntity user = new UserEntity();
        user.setEmail("john@mail.com");
        user.setName("John");
        user.setRole("ROLE_USER");
        user.setPassword(passwordEncoder.encode("123"));
        user.setEnabled(true);  // включение аккаунта

        userRepository.save(user);
    }

    // 1 /auth/me - must return user with valid token
    @Test
    void authMeShouldReturnUserWithValidToken() throws Exception {

        String loginJson = """
                {
                  "email": "john@mail.com",
                  "password": "123"
                }
                """;

        // 1.1 Login -> receive token
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = mapper.readTree(response);
        String token = json.get("token").asText();

        // 1.2 Call /auth/me with token
        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@mail.com"))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    // 2 Invalid token -> 401
    @Test
    void authMeShouldReturn401WithInvalidToken() throws Exception {
        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer INVALID_TOKEN"))
                .andExpect(status().isUnauthorized());
    }

    // 3 No token -> 401
    @Test
    void authMeShouldReturn401WhenNoToken() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}
