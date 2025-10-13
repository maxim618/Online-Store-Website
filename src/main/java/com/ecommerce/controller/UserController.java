package com.ecommerce.controller;

import com.ecommerce.entities.User;
import com.ecommerce.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // === регистрация пользователя ===
    @PostMapping("/register")
    public String register(User user, Model model, HttpSession session) {

        //  проверка email
        if (user.getUserEmail() == null ||
                !Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", user.getUserEmail())) {
            model.addAttribute("error", "Invalid email format!");
            model.addAttribute("user", user);
            return "register";
        }

        //  проверка телефона
        if (user.getUserPhone() != null &&
                !Pattern.matches("^[0-9]{10,15}$", user.getUserPhone())) {
            model.addAttribute("error", "Phone number must be 10–15 digits!");
            model.addAttribute("user", user);
            return "register";
        }

        //  проверка пароля
        if (user.getUserPassword() == null || user.getUserPassword().length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters!");
            model.addAttribute("user", user);
            return "register";
        }

        //  шифрование пароля перед сохранением
        String encodedPassword = passwordEncoder.encode(user.getUserPassword());
        user.setUserPassword(encodedPassword);

        // сохранение пользователя
        boolean saved = userService.saveUser(user);
        if (!saved) {
            model.addAttribute("error", "Registration failed. Try again.");
            model.addAttribute("user", user);
            return "register";
        }

        // сразу логиним пользователя
        session.setAttribute("activeUser", user);

        return "redirect:/user/profile";
    }
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register"; // это /WEB-INF/jsp/register.jsp
    }


    // === вход пользователя ===
    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {

        User user = userService.getUserByEmail(email);

        if (user != null && passwordEncoder.matches(password, user.getUserPassword())) {
            session.setAttribute("activeUser", user);
            return "redirect:/user/profile";
        } else {
            model.addAttribute("error", "Invalid email or password!");
            return "login";
        }
    }
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // login.jsp
    }


    // === выход ===
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
