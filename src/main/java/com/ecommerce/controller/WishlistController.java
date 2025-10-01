package com.ecommerce.controller;

import com.ecommerce.entities.Product;
import com.ecommerce.entities.User;
import com.ecommerce.entities.Wishlist;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.WishlistService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class WishlistController {

    private final WishlistService wishlistService;
    private final ProductService productService;

    public WishlistController(WishlistService wishlistService, ProductService productService) {
        this.wishlistService = wishlistService;
        this.productService = productService;
    }

    @GetMapping("/wishlist")
    public String viewWishlist(HttpSession session, Model model) {
        User user = (User) session.getAttribute("activeUser");
        if (user == null) {
            return "redirect:/login";
        }

        List<Wishlist> wlist = wishlistService.getWishlistByUserId(user.getUserId());

        List<Product> products = wlist.stream()
                .map(w -> productService.getProductById(w.getProductId()))
                .collect(Collectors.toList());

        model.addAttribute("products", products);
        return "wishlist"; // JSP
    }

    @PostMapping("/wishlist/delete")
    public String deleteFromWishlist(@RequestParam("pid") int pid, HttpSession session) {
        User user = (User) session.getAttribute("activeUser");
        if (user != null) {
            wishlistService.removeFromWishlist(user.getUserId(), pid);
        }
        return "redirect:/wishlist";
    }
}
