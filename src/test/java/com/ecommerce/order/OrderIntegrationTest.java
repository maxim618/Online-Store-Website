package com.ecommerce.order;

import com.ecommerce.persistence.model.Category;
import com.ecommerce.persistence.model.Product;
import com.ecommerce.persistence.model.UserEntity;
import com.ecommerce.persistence.repository.UserRepository;
import com.ecommerce.persistence.repository.ProductRepository;
import com.ecommerce.persistence.repository.CategoryRepository;
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

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class OrderIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private DbCleaner dbCleaner;
    @Autowired private PasswordEncoder passwordEncoder;
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
        product.setPrice(100.0); // удобнее для проверки totalPrice
        product.setQuantity(100);
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
    void place_withoutToken_shouldReturn401() throws Exception {
        mockMvc.perform(post("/api/orders/place").param("userId", userId.toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void place_withEmptyCart_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/orders/place")
                        .param("userId", userId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void orderFlow_shouldPlaceOrder_clearCart_andAllowReadingOrders() throws Exception {
        // 1 Добавляем товар в корзину (quantity=2)
        mockMvc.perform(post("/api/cart/add")
                        .param("userId", userId.toString())
                        .param("productId", productId.toString())
                        .param("quantity", "2")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 2 Оформляем заказ
        String orderJson = mockMvc.perform(post("/api/orders/place")
                        .param("userId", userId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.createdAt").value(
                        matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*")
                ))
                .andExpect(jsonPath("$.totalPrice").value(200.0))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].productId").value(productId.intValue()))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].price").value(100.0))
                .andExpect(jsonPath("$.items[0].lineTotal").value(200.0))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long orderId = objectMapper.readTree(orderJson).get("id").asLong();


        // 3 Корзина должна стать пустой
        mockMvc.perform(get("/api/cart/count")
                        .param("userId", userId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));

        // 4 Получаем список заказов пользователя
        mockMvc.perform(get("/api/orders")
                        .param("userId", userId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value((int) orderId));

        // 5 Получаем заказ по id
        mockMvc.perform(get("/api/orders/{id}", orderId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value((int) orderId))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].productId").value(productId.intValue()))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }
}
