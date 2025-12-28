package com.ecommerce.order;

import com.ecommerce.persistence.model.Category;
import com.ecommerce.persistence.model.Product;
import com.ecommerce.persistence.model.UserEntity;
import com.ecommerce.persistence.repository.UserRepository;
import com.ecommerce.persistence.repository.ProductRepository;
import com.ecommerce.persistence.repository.CategoryRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class OrderOwnershipSecurityIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private DbCleaner dbCleaner;

    @Autowired private PasswordEncoder passwordEncoder;

    private Long user1Id;
    private Long user2Id;
    private Long productId;

    private String user1Token;
    private String user2Token;

    private long user1OrderId;

    @BeforeEach
    void setUp() throws Exception {

        dbCleaner.clean();

        // два пользователя
        user1Id = userRepository.save(mkUser("user1@mail.com", "User1", "ROLE_USER")).getId();
        user2Id = userRepository.save(mkUser("user2@mail.com", "User2", "ROLE_USER")).getId();

        // товар
        Category category = new Category();
        category.setName("Phones");
        category = categoryRepository.save(category);

        Product product = new Product();
        product.setTitle("iPhone");
        product.setPrice(100.0);
        product.setQuantity(100);
        product.setCategory(category);
        productId = productRepository.save(product).getId();

        user1Token = loginAndGetToken("user1@mail.com", "123");
        user2Token = loginAndGetToken("user2@mail.com", "123");

        // создаём заказ user1
        addToCart(user1Id, user1Token, productId, 2);
        user1OrderId = placeOrder(user1Id, user1Token);
    }

    private UserEntity mkUser(String email, String name, String role) {
        UserEntity u = new UserEntity();
        u.setEmail(email);
        u.setName(name);
        u.setRole(role);
        u.setPassword(passwordEncoder.encode("123"));
        u.setEnabled(true);
        return u;
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

    private void addToCart(Long userId, String token, Long productId, int qty) throws Exception {
        mockMvc.perform(post("/api/cart/add")
                        .param("userId", userId.toString())
                        .param("productId", productId.toString())
                        .param("quantity", String.valueOf(qty))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private long placeOrder(Long userId, String token) throws Exception {
        String json = mockMvc.perform(post("/api/orders/place")
                        .param("userId", userId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(json).get("id").asLong();
    }

    // Ownership tests

    @Test
    void getOrderById_otherUserShouldBeForbidden() throws Exception {
        mockMvc.perform(get("/api/orders/{id}", user1OrderId)
                        .header("Authorization", "Bearer " + user2Token))
                .andExpect(status().isForbidden()); // правильная модель магазина
    }

    @Test
    void listOrders_otherUserIdParamShouldBeForbidden() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .param("userId", user1Id.toString())
                        .header("Authorization", "Bearer " + user2Token))
                .andExpect(status().isForbidden()); // иначе утечка заказов по userId
    }

    @Test
    void listOrders_selfShouldBeOk() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .param("userId", user1Id.toString())
                        .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value((int) user1OrderId));
    }
}
