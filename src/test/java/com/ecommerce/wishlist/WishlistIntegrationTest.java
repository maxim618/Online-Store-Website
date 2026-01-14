package com.ecommerce.wishlist;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class WishlistIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private DbCleaner dbCleaner;
    @Autowired(required = false)
    ValkeyTestCleaner valkeyTestCleaner;

    private Long userId;
    private Long productId;
    private String token;

    @BeforeEach
    void setUp() throws Exception {
        dbCleaner.clean();
        if (valkeyTestCleaner != null) {
            valkeyTestCleaner.clearAll();
        }

        UserEntity user = new UserEntity();
        user.setEmail("user@mail.com");
        user.setName("User");
        user.setRole("ROLE_USER");
        user.setPassword(passwordEncoder.encode("123"));
        user.setEnabled(true);
        userId = userRepository.save(user).getId();

        Category category = new Category();
        category.setName("Phones");
        category = categoryRepository.save(category);

        Product product = new Product();
        product.setTitle("iPhone");
        product.setPrice(1000.0);
        product.setQuantity(10);
        product.setCategory(category);
        productId = productRepository.save(product).getId();

        token = loginAndGetToken("user@mail.com", "123");
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        String body = """
                { "email": "%s", "password": "%s" }
                """.formatted(email, password);

        String json = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode node = objectMapper.readTree(json);
        return node.get("token").asText();
    }

    @Test
    void wishlistCrud_shouldWork_forAuthorizedUser() throws Exception {

        // 0 без токена -> 401
        mockMvc.perform(get("/api/wishlist").param("userId", userId.toString()))
                .andExpect(status().isUnauthorized());

        // 1 add
        mockMvc.perform(post("/api/wishlist/add")
                        .param("userId", userId.toString())
                        .param("productId", productId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 2 exists -> true
        mockMvc.perform(get("/api/wishlist/exists")
                        .param("userId", userId.toString())
                        .param("productId", productId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // 3 get -> список содержит товар
        mockMvc.perform(get("/api/wishlist")
                        .param("userId", userId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                // поле зависит от WishlistItemDto: часто productId лежит прямо в dto
                .andExpect(jsonPath("$[0].productId").value(productId.intValue()));

        // 4 remove
        mockMvc.perform(delete("/api/wishlist/remove")
                        .param("userId", userId.toString())
                        .param("productId", productId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 5 exists -> false
        mockMvc.perform(get("/api/wishlist/exists")
                        .param("userId", userId.toString())
                        .param("productId", productId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        // 6 get -> пусто
        mockMvc.perform(get("/api/wishlist")
                        .param("userId", userId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
    @Test
    void addDuplicate_shouldNotCreateSecondItem() throws Exception {

        // 1 добавили первый раз
        mockMvc.perform(post("/api/wishlist/add")
                        .param("userId", userId.toString())
                        .param("productId", productId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 2 добавили второй раз (дубликат)
        mockMvc.perform(post("/api/wishlist/add")
                        .param("userId", userId.toString())
                        .param("productId", productId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 3 проверяем: список всё равно из 1 элемента (дубль не появился)
        mockMvc.perform(get("/api/wishlist")
                        .param("userId", userId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].productId").value(productId.intValue()));
    }
}
