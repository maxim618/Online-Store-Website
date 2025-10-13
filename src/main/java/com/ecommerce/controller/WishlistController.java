package com.ecommerce.controller;

import com.ecommerce.entities.Message;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    // ✅ Показать список желаемого
    @GetMapping("/wishlist")
    public String viewWishlist(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("activeUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("message",
                    new Message("You are not logged in! Please login first.", "Error!", "alert-danger"));
            return "redirect:/login";
        }

        List<Wishlist> wlist = wishlistService.getWishlistByUserId(user.getUserId());

        List<Product> products = wlist.stream()
                .map(w -> productService.getProductById(w.getProductId()))
                .collect(Collectors.toList());

        model.addAttribute("products", products);
        return "wishlist"; // JSP
    }

    // ✅ Удалить товар из списка
    @PostMapping("/wishlist/delete")
    public String deleteFromWishlist(@RequestParam("pid") int pid,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("activeUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("message",
                    new Message("You must login to remove items.", "Error!", "alert-danger"));
            return "redirect:/login";
        }

        wishlistService.deleteWishlist(user.getUserId(), pid);
        redirectAttributes.addFlashAttribute("message",
                new Message("Item removed from wishlist.", "Success!", "alert-success"));

        return "redirect:/wishlist";
    }
}
