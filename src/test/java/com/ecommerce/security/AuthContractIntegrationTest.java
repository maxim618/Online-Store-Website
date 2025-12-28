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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AuthContractIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired DbCleaner dbCleaner;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        dbCleaner.clean();

        UserEntity user = new UserEntity();
        user.setEmail("john@mail.com");
        user.setName("John");
        user.setRole("ROLE_USER");
        user.setPassword(passwordEncoder.encode("123"));
        user.setEnabled(true);

        userRepository.save(user);
    }

    @Test
    void login_shouldReturnAuthResponseContract() throws Exception {
        String loginJson = """
                {
                  "email": "john@mail.com",
                  "password": "123"
                }
                """;

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                // AuthResponse contract
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.id").exists())
                .andExpect(jsonPath("$.user.email").value("john@mail.com"))
                .andExpect(jsonPath("$.user.name").value("John"))
                .andExpect(jsonPath("$.user.role").value("ROLE_USER"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = mapper.readTree(response);

        // token is string
        assertTrue(json.get("token").isTextual());

        // user is "object"
        assertTrue(json.get("user").isObject());
        assertTrue(json.get("user").get("id").isNumber());
    }

    @Test
    void register_shouldReturnAuthResponse_andTokenShouldWorkOnMe() throws Exception {
        String registerJson = """
                {
                  "email": "newuser@mail.com",
                  "name": "New User",
                  "password": "123"
                }
                """;

        String response = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk())
                // AuthResponse contract
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.id").exists())
                .andExpect(jsonPath("$.user.email").value("newuser@mail.com"))
                .andExpect(jsonPath("$.user.name").value("New User"))
                .andExpect(jsonPath("$.user.role").value("ROLE_USER"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = mapper.readTree(response).get("token").asText();

        // token из register должен быть валидным для /auth/me
        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newuser@mail.com"))
                .andExpect(jsonPath("$.name").value("New User"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }
}
