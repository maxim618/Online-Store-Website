package com.ecommerce.controller;

import com.ecommerce.entities.Cart;
import com.ecommerce.entities.User;
import com.ecommerce.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CheckoutController {

    private final CartService cartService;

    public CheckoutController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        User user = (User) session.getAttribute("activeUser");
        if (user == null) {
            return "redirect:/login"; // если не авторизован
        }

        List<Cart> cartList = cartService.getCartByUserId(user.getUserId());
        int totalPrice = cartService.getTotalCartPriceByUserId(user.getUserId());

        model.addAttribute("user", user);
        model.addAttribute("cartList", cartList);
        model.addAttribute("totalPrice", totalPrice);

        return "checkout"; // JSP: checkout.jsp
    }
}
