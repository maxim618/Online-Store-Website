package com.ecommerce.controller;

import com.ecommerce.entities.Category;
import com.ecommerce.entities.Product;
import com.ecommerce.service.CategoryService;
import com.ecommerce.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductsController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductsController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/products")
    public String viewProducts(@RequestParam("category") int categoryId, Model model) {
        Category category = categoryService.getCategoryById(categoryId);
        List<Product> products = productService.getProductsByCategory(categoryId);

        model.addAttribute("category", category);
        model.addAttribute("products", products);

        return "products"; // JSP: products.jsp
    }
}
