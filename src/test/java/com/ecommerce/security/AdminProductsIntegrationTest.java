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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get; // если GET
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post; // для /auth/login
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AdminProductsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired private DbCleaner dbCleaner;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
//        userRepository.deleteAll();

        // обычный пользователь
        UserEntity user = new UserEntity();
        user.setEmail("user@mail.com");
        user.setName("User");
        user.setRole("ROLE_USER");
        user.setPassword(passwordEncoder.encode("123"));
        user.setEnabled(true);
        userRepository.save(user);

        // админ
        UserEntity admin = new UserEntity();
        admin.setEmail("admin@mail.com");
        admin.setName("Admin");
        admin.setRole("ROLE_ADMIN");
        admin.setPassword(passwordEncoder.encode("123"));
        admin.setEnabled(true);
        userRepository.save(admin);
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
    void userTokenShouldNotAccessAdminProducts() throws Exception {
        String userToken = loginAndGetToken("user@mail.com", "123");

        mockMvc.perform(get("/admin/status")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminTokenShouldAccessAdminProducts() throws Exception {
        String adminToken = loginAndGetToken("admin@mail.com", "123");

        mockMvc.perform(get("/admin/status")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}
