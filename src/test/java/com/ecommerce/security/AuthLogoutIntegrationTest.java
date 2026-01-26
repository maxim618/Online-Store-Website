package com.ecommerce.security;

import com.ecommerce.abstractTestClasses.AbstractFullDatabaseCleanupTest;
import com.ecommerce.persistence.model.UserEntity;
import com.ecommerce.persistence.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuthLogoutIntegrationTest extends AbstractFullDatabaseCleanupTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {

        UserEntity user = new UserEntity();
        user.setEmail("john_logout@mail.com");
        user.setName("John Logout");
        user.setRole("ROLE_USER");
        user.setPassword(passwordEncoder.encode("123"));
        user.setEnabled(true); // иначе DisabledException при логине

        userRepository.save(user);
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        String body = """
                {
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(email, password);

        String json = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(json);
        return node.get("token").asText();
    }

    @Test
    void logoutShouldBlacklistTokenAndDenyFurtherAccess() throws Exception {
        // 1. Логинимся и получаем токен
        String token = loginAndGetToken("john_logout@mail.com", "123");

        // 2. Проверяем, что до logout /auth/me с этим токеном работает
        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 3. Делаем logout — токен должен попасть в blacklist
        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer " + token))
                // если у тебя 204 NO_CONTENT — поменяй здесь .isOk() на .isNoContent()
                .andExpect(status().isOk());

        // 4. Повторный запрос с тем же токеном — теперь должен быть 401
        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }
}
