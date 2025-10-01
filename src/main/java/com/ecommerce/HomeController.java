package com.ecommerce;

import com.ecommerce.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ProductService productService;

    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("productList", productService.getAllLatestProducts());
        model.addAttribute("topDeals", productService.getDiscountedProducts());
        return "index"; // JSP index.jsp
    }
}

