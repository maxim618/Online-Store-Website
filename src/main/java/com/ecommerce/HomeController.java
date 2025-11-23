package com.ecommerce;

import com.ecommerce.service.interfaces.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Legacy controller for old JSP views.
 * Will be removed after frontend migration.
 */

@Controller
public class HomeController {

    private final ProductService productService;

    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("productList", productService.getAll());
        model.addAttribute("topDeals", productService.getAll());
        return "index"; // JSP index.jsp
    }
}

