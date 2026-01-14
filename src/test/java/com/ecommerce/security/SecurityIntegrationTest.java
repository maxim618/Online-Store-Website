package com.ecommerce.security;

import com.ecommerce.persistence.model.UserEntity;
import com.ecommerce.persistence.repository.UserRepository;
import com.ecommerce.testutil.DbCleaner;
import com.ecommerce.testutil.ValkeyTestCleaner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)  // чтобы контекст очищался между тестами
class SecurityIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private DbCleaner dbCleaner;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired(required = false)
    ValkeyTestCleaner valkeyTestCleaner;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
        if (valkeyTestCleaner != null) {
            valkeyTestCleaner.clearAll();
        }

        // создаём обычного пользователя
        createUser("user@mail.com", "ROLE_USER", "123");

        // создаём администратора
        createUser("admin@mail.com", "ROLE_ADMIN", "123");
    }

    private void createUser(String email, String role, String rawPassword) {
        UserEntity user = UserEntity.builder()
                .email(email)
                .name("Test User")
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .enabled(true)
                .build();

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
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(json);
        return node.get("token").asText();
    }

    // 1 публичный эндпоинт без токена
    @Test
    void publicEndpointShouldNotBeAccessibleWithoutToken() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isUnauthorized());
    }

    // 2 защищённый эндпоинт без токена → 401
    @Test
    void adminEndpointWithoutTokenShouldReturn401() throws Exception {
        mockMvc.perform(get("/admin/status"))
                .andExpect(status().isUnauthorized());
    }

    // 3 user-токен не должен пускать в /admin/status → 403
    @Test
    void userTokenShouldNotAccessAdminEndpoint() throws Exception {
        String token = loginAndGetToken("user@mail.com", "123");

        mockMvc.perform(get("/admin/status")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    // 4 admin-токен должен пускать в /admin/status → 200
    @Test
    void adminTokenShouldAccessAdminEndpoint() throws Exception {
        String token = loginAndGetToken("admin@mail.com", "123");

        mockMvc.perform(get("/admin/status")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    // 5 user-токен должен иметь доступ к любому authenticated эндпоинту
    // (например /api/cart, если он не находится в permitAll)
    @Test
    void userTokenShouldAccessAuthenticatedEndpoint() throws Exception {
        String token = loginAndGetToken("user@mail.com", "123");

        mockMvc.perform(get("/api/cart")
                        .param("userId", "1")
                        .header("Authorization", "Bearer " + token))
                // должно быть не 401/403, а 200,
                // либо другая бизнес-ошибка, если что-то не так в сервисе
                .andExpect(status().isOk());
    }
}
