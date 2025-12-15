package com.ecommerce.web.controller;

import com.ecommerce.exception.EntityNotFoundException;
import com.ecommerce.exception.GlobalExceptionHandler;
import com.ecommerce.service.interfaces.ProductService;
import com.ecommerce.web.dto.ProductDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false) // контроллер-тест без security
@Import(GlobalExceptionHandler.class)     // чтобы 404/400 шли через handler
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // используем Spring-настроенный ObjectMapper

    @org.springframework.boot.test.mock.mockito.MockBean
    private ProductService productService;

    // чтобы WebMvcTest не падал из-за Security компонентов:
    @MockBean com.ecommerce.security.JwtFilter jwtFilter;
    @MockBean com.ecommerce.security.JwtService jwtService;
    @MockBean com.ecommerce.service.impl.TokenBlacklistService blacklistService;
    @MockBean com.ecommerce.security.UserDetailsServiceImpl userDetailsServiceImpl;


    @Test
    void getAll_shouldReturn200AndList() throws Exception {
        ProductDto p1 = new ProductDto();
        p1.setId(1L);
        p1.setTitle("Phone");

        ProductDto p2 = new ProductDto();
        p2.setId(2L);
        p2.setTitle("Laptop");

        when(productService.getAll()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Phone"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Laptop"));
    }

    @Test
    void getById_shouldReturn200_whenFound() throws Exception {
        ProductDto dto = new ProductDto();
        dto.setId(10L);
        dto.setTitle("Phone");

        when(productService.getById(10L)).thenReturn(dto);

        mockMvc.perform(get("/api/products/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Phone"));
    }

    @Test
    void getById_shouldReturn404_whenNotFound() throws Exception {
        when(productService.getById(10L))
                .thenThrow(new EntityNotFoundException("Product not found: 10"));

        mockMvc.perform(get("/api/products/10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getByCategory_shouldReturn200AndList() throws Exception {
        ProductDto dto = new ProductDto();
        dto.setId(1L);
        dto.setCategoryId(7L);
        dto.setTitle("Phone");

        when(productService.getByCategory(7L)).thenReturn(List.of(dto));

        // в контроллере: /category/{cid}
        mockMvc.perform(get("/api/products/category/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].categoryId").value(7))
                .andExpect(jsonPath("$[0].title").value("Phone"));
    }

    @Test
    void search_shouldReturn200AndList() throws Exception {
        ProductDto dto = new ProductDto();
        dto.setId(1L);
        dto.setTitle("Phone");

        when(productService.search("pho")).thenReturn(List.of(dto));

        // в контроллере: @RequestParam String q
        mockMvc.perform(get("/api/products/search").param("q", "pho"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Phone"));
    }

    @Test
    void create_shouldReturn200AndDto() throws Exception {
        ProductDto req = new ProductDto();
        req.setTitle("New product");
        req.setPrice(10.0);
        req.setCategoryId(7L);

        ProductDto resp = new ProductDto();
        resp.setId(100L);
        resp.setTitle("New product");
        resp.setPrice(10.0);
        resp.setCategoryId(7L);

        when(productService.create(any(ProductDto.class))).thenReturn(resp);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.title").value("New product"))
                .andExpect(jsonPath("$.categoryId").value(7));

        verify(productService).create(any(ProductDto.class));
    }

    @Test
    void create_shouldReturn404_whenCategoryNotFound() throws Exception {
        ProductDto req = new ProductDto();
        req.setTitle("New product");
        req.setPrice(10.0);
        req.setCategoryId(999L);

        when(productService.create(any(ProductDto.class)))
                .thenThrow(new EntityNotFoundException("Category not found: 999"));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void update_shouldReturn200AndDto() throws Exception {
        ProductDto req = new ProductDto();
        req.setTitle("Updated");
        req.setPrice(99.0);
        req.setCategoryId(7L);

        ProductDto resp = new ProductDto();
        resp.setId(10L);
        resp.setTitle("Updated");
        resp.setCategoryId(7L);

        when(productService.update(eq(10L), any(ProductDto.class))).thenReturn(resp);

        mockMvc.perform(put("/api/products/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Updated"));

        verify(productService).update(eq(10L), any(ProductDto.class));
    }

    @Test
    void update_shouldReturn404_whenProductNotFound() throws Exception {
        ProductDto req = new ProductDto();
        req.setTitle("Updated");
        req.setCategoryId(7L);

        when(productService.update(eq(10L), any(ProductDto.class)))
                .thenThrow(new EntityNotFoundException("Product not found: 10"));

        mockMvc.perform(put("/api/products/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldReturn200() throws Exception {
        doNothing().when(productService).delete(10L);

        mockMvc.perform(delete("/api/products/10"))
                .andExpect(status().isOk());

        verify(productService).delete(10L);
    }

    @Test
    void delete_shouldReturn404_whenNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Product not found: 10"))
                .when(productService).delete(10L);

        mockMvc.perform(delete("/api/products/10"))
                .andExpect(status().isNotFound());
    }
}
