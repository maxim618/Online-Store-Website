package com.ecommerce.controller;

import com.ecommerce.entities.Admin;
import com.ecommerce.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/display")
public class DisplayAdminController {

    private final AdminService adminService;

    public DisplayAdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public String showAdmins(HttpSession session, Model model) {
        Object admin = session.getAttribute("activeAdmin");
        if (admin == null) {
            return "redirect:/";
        }

        List<Admin> admins = adminService.getAllAdmins();
        model.addAttribute("admins", admins);
        return "display_admin"; // JSP
    }
}
