package com.ecommerce.controller;

import com.ecommerce.entities.User;
import com.ecommerce.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        User user = (User) session.getAttribute("activeUser");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("cartList", cartService.getCartByUserId(user.getUserId()));
        model.addAttribute("totalPrice", cartService.getTotalCartPriceByUserId(user.getUserId()));
        return "cart"; // JSP: cart.jsp
    }
}
