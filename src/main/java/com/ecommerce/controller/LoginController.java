package com.ecommerce.controller;

import com.ecommerce.dao.AdminDao;
import com.ecommerce.dao.UserDao;
import com.ecommerce.entities.Admin;
import com.ecommerce.entities.Message;
import com.ecommerce.entities.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/login")
public class LoginController {

    private final UserDao userDao;
    private final AdminDao adminDao;

    // ✅ Теперь используем внедрение зависимостей (Spring сам подставит DAO)
    public LoginController(UserDao userDao, AdminDao adminDao) {
        this.userDao = userDao;
        this.adminDao = adminDao;
    }

    // Показать страницу логина (JSP)
    @GetMapping
    public String showLoginPage() {
        return "login"; // login.jsp
    }

    // Обработка логина (POST)
    @PostMapping
    public String processLogin(
            @RequestParam("login") String loginType,
            @RequestParam(required = false, name = "user_email") String userEmail,
            @RequestParam(required = false, name = "user_password") String userPassword,
            @RequestParam(required = false, name = "email") String adminEmail,
            @RequestParam(required = false, name = "password") String adminPassword,
            HttpSession session) {

        if ("user".equalsIgnoreCase(loginType.trim())) {
            User user = userDao.getUserByEmailPassword(userEmail, userPassword);
            if (user != null) {
                session.setAttribute("activeUser", user);
                return "redirect:/"; // index.jsp
            } else {
                session.setAttribute("message",
                        new Message("Invalid details! Try again!!", "error", "alert-danger"));
                return "redirect:/login";
            }
        } else if ("admin".equalsIgnoreCase(loginType.trim())) {
            Admin admin = adminDao.getAdminByEmailPassword(adminEmail, adminPassword);
            if (admin != null) {
                session.setAttribute("activeAdmin", admin);
                return "redirect:/admin"; // admin.jsp
            } else {
                session.setAttribute("message",
                        new Message("Invalid details! Try again!!", "error", "alert-danger"));
                return "redirect:/adminlogin";
            }
        }

        session.setAttribute("message",
                new Message("Unknown login type!", "error", "alert-danger"));
        return "redirect:/login";
    }
}
