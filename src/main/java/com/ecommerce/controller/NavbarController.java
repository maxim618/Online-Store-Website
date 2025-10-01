package com.ecommerce.controller;

import com.ecommerce.entities.Admin;
import com.ecommerce.entities.User;
import com.ecommerce.service.CartService;
import com.ecommerce.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class NavbarController {

    private final CategoryService categoryService;
    private final CartService cartService;

    public NavbarController(CategoryService categoryService, CartService cartService) {
        this.categoryService = categoryService;
        this.cartService = cartService;
    }

    @ModelAttribute
    public void addNavbarData(Model model, HttpSession session) {
        // Категории
        model.addAttribute("categoryList", categoryService.getAllCategories());

        // Пользователь и админ
        User user = (User) session.getAttribute("activeUser");
        Admin admin = (Admin) session.getAttribute("activeAdmin");
        model.addAttribute("activeUser", user);
        model.addAttribute("activeAdmin", admin);

        // Количество товаров в корзине
        if (user != null) {
            int cartCount = cartService.getCartCountByUserId(user.getUserId());
            model.addAttribute("cartCount", cartCount);
        }
    }
}
