package com.ecommerce.controller;

import com.ecommerce.entities.Admin;
import com.ecommerce.entities.Message;
import com.ecommerce.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminController(AdminService adminService, BCryptPasswordEncoder passwordEncoder) {
        this.adminService = adminService;
        this.passwordEncoder = passwordEncoder;
    }

    // страница логина
    @GetMapping("/login")
    public String showLoginPage() {
        return "admin_login"; // JSP с формой входа
    }

    // обработка логина
    @PostMapping("/login")
    public String loginAdmin(@RequestParam("email") String email,
                             @RequestParam("password") String password,
                             HttpSession session,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        Admin admin = adminService.getAdminByEmail(email);

        if (admin == null || !passwordEncoder.matches(password, admin.getPassword())) {
            redirectAttributes.addFlashAttribute("message",
                    new Message("Invalid admin credentials!", "Error!", "alert-danger"));
            return "redirect:/admin/login";
        }

        session.setAttribute("activeAdmin", admin);
        redirectAttributes.addFlashAttribute("message",
                new Message("Welcome, " + admin.getName() + "!", "Success!", "alert-success"));
        return "redirect:/admin/dashboard";
    }

    // выход
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("activeAdmin");
        return "redirect:/";
    }
}
