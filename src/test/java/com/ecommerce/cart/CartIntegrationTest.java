package com.ecommerce.cart;

import com.ecommerce.persistence.model.Product;
import com.ecommerce.persistence.model.UserEntity;
import com.ecommerce.persistence.repository.CartItemRepository;
import com.ecommerce.persistence.repository.ProductRepository;
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
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CartIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Long userId;
    private Long productId;

    @BeforeEach
    void setUp() {
        // сначала дочерние записи (cart_items)
        cartItemRepository.deleteAll();
        // потом продукты
        productRepository.deleteAll();
        // потом пользователи
        userRepository.deleteAll();

        // 1. Пользователь
        UserEntity user = new UserEntity();
        user.setEmail("cart-user@mail.com");
        user.setName("Cart User");
        user.setRole("ROLE_USER");
        user.setPassword(passwordEncoder.encode("123"));
        user.setEnabled(true); //  если false то DisabledException при логине
        userId = userRepository.save(user).getId();

        // 2. Продукт
        Product product = new Product();
        product.setTitle("Test product");
        product.setPrice(10.0);
        product.setQuantity(100);
        productId = productRepository.save(product).getId();
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
    void fullCartCrudFlow() throws Exception {
        String token = loginAndGetToken("cart-user@mail.com", "123");

        // 0) Стартовое состояние – корзина пустая
        mockMvc.perform(get("/api/cart/count")
                        .param("userId", userId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));

        // 1) CREATE: добавляем товар в корзину (quantity=2)
        // Примечание: getCartItemCount возвращает количество разных товаров (записей), а не общее количество
        mockMvc.perform(post("/api/cart/add")
                        .param("userId", userId.toString())
                        .param("productId", productId.toString())
                        .param("quantity", "2")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 2) READ: проверяем count = 1 (одна запись в корзине, но при этом quantity=2)
        mockMvc.perform(get("/api/cart/count")
                        .param("userId", userId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        //2.1 проверяем quantity = 2
        mockMvc.perform(get("/api/cart")
                        .param("userId", userId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].quantity").value(2));




        // 3) UPDATE: меняем количество на 5
        mockMvc.perform(post("/api/cart/set")
                        .param("userId", userId.toString())
                        .param("productId", productId.toString())
                        .param("quantity", "5")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 4) READ: count все еще = 1 (одна запись, но quantity теперь 5)
        mockMvc.perform(get("/api/cart/count")
                        .param("userId", userId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        //4.1) проверяем, что quntity = 5
        mockMvc.perform(get("/api/cart")
                        .param("userId", userId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].quantity").value(5));


        // 5) DELETE (remove): удаляем товар
        mockMvc.perform(delete("/api/cart/remove")
                        .param("userId", userId.toString())
                        .param("productId", productId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 6) READ: корзина пустая
        mockMvc.perform(get("/api/cart/count")
                        .param("userId", userId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));

        // 7) ADD снова + CLEAR
        mockMvc.perform(post("/api/cart/add")
                        .param("userId", userId.toString())
                        .param("productId", productId.toString())
                        .param("quantity", "3")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/cart/clear")
                        .param("userId", userId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/cart/count")
                        .param("userId", userId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }
}
