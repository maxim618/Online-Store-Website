package com.ecommerce.security;

import com.ecommerce.persistence.model.Category;
import com.ecommerce.persistence.model.Product;
import com.ecommerce.persistence.model.UserEntity;
import com.ecommerce.persistence.repository.CategoryRepository;
import com.ecommerce.persistence.repository.ProductRepository;
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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProductSecurityIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private DbCleaner dbCleaner;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired(required = false)
    ValkeyTestCleaner valkeyTestCleaner;

    private Long categoryId;
    private Long productId;

    @BeforeEach
    void setUp() {
        dbCleaner.clean();
        if (valkeyTestCleaner != null) {
            valkeyTestCleaner.clearAll();
        }

        // USER
        UserEntity user = new UserEntity();
        user.setEmail("user@mail.com");
        user.setName("User");
        user.setRole("ROLE_USER");
        user.setPassword(passwordEncoder.encode("123"));
        user.setEnabled(true);
        userRepository.save(user);

        // ADMIN
        UserEntity admin = new UserEntity();
        admin.setEmail("admin@mail.com");
        admin.setName("Admin");
        admin.setRole("ROLE_ADMIN");
        admin.setPassword(passwordEncoder.encode("123"));
        admin.setEnabled(true);
        userRepository.save(admin);

        // Category + Product (для PUT/DELETE)
        Category category = new Category();
        category.setName("Phones");
        categoryId = categoryRepository.save(category).getId();

        Product p = new Product();
        p.setTitle("iPhone");
        p.setPrice(1000.0);
        p.setQuantity(10);
        p.setCategory(category);
        productId = productRepository.save(p).getId();
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        String body = """
                { "email": "%s", "password": "%s" }
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

    // GET /api/products

    @Test
    void getAll_withoutToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAll_withUserToken_shouldReturn200() throws Exception {
        String userToken = loginAndGetToken("user@mail.com", "123");

        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    // POST /api/products (ADMIN-only expected)

    @Test
    void create_withUserToken_shouldReturn403() throws Exception {
        String userToken = loginAndGetToken("user@mail.com", "123");

        String body = """
                {
                  "title": "New product",
                  "price": 10,
                  "quantity": 1,
                  "categoryId": %d
                }
                """.formatted(categoryId);

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_withAdminToken_shouldReturn200() throws Exception {
        String adminToken = loginAndGetToken("admin@mail.com", "123");

        String body = """
                {
                  "title": "New product",
                  "price": 10,
                  "quantity": 1,
                  "categoryId": %d
                }
                """.formatted(categoryId);

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("New product"));
    }

    // PUT /api/products/{id} (ADMIN-only expected)

    @Test
    void update_withUserToken_shouldReturn403() throws Exception {
        String userToken = loginAndGetToken("user@mail.com", "123");

        String body = """
                {
                  "title": "Updated",
                  "price": 999,
                  "quantity": 5,
                  "categoryId": %d
                }
                """.formatted(categoryId);

        mockMvc.perform(put("/api/products/{id}", productId)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void update_withAdminToken_shouldReturn200() throws Exception {
        String adminToken = loginAndGetToken("admin@mail.com", "123");

        String body = """
                {
                  "title": "Updated",
                  "price": 999,
                  "quantity": 5,
                  "categoryId": %d
                }
                """.formatted(categoryId);

        mockMvc.perform(put("/api/products/{id}", productId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId.intValue()))
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    // DELETE /api/products/{id} (ADMIN-only expected)

    @Test
    void delete_withUserToken_shouldReturn403() throws Exception {
        String userToken = loginAndGetToken("user@mail.com", "123");

        mockMvc.perform(delete("/api/products/{id}", productId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void delete_withAdminToken_shouldReturn200or204() throws Exception {
        String adminToken = loginAndGetToken("admin@mail.com", "123");

        mockMvc.perform(delete("/api/products/{id}", productId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().is2xxSuccessful());
    }
}
