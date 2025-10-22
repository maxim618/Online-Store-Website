package com.ecommerce.controller;

import com.ecommerce.entities.User;
import com.ecommerce.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // === регистрация пользователя ===
    @PostMapping("/register")
    public String register(User user, Model model, HttpSession session) {
        logger.info("Attempting to register user with email: {}", user.getUserEmail());

        //  проверка email
        if (user.getUserEmail() == null ||
                !Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", user.getUserEmail())) {
            logger.warn("Invalid email format provided: {}", user.getUserEmail());
            model.addAttribute("error", "Invalid email format!");
            model.addAttribute("user", user);
            return "register";
        }

        //  проверка телефона
        if (user.getUserPhone() != null &&
                !Pattern.matches("^[0-9]{10,15}$", user.getUserPhone())) {
            logger.warn("Invalid phone format provided: {}", user.getUserPhone());
            model.addAttribute("error", "Phone number must be 10–15 digits!");
            model.addAttribute("user", user);
            return "register";
        }

        //  проверка пароля
        if (user.getUserPassword() == null || user.getUserPassword().length() < 6) {
            logger.warn("Password too short for user: {}", user.getUserEmail());
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
            logger.error("Failed to save user: {}", user.getUserEmail());
            model.addAttribute("error", "Registration failed. Try again.");
            model.addAttribute("user", user);
            return "register";
        }

        logger.info("User successfully registered: {}", user.getUserEmail());
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

        logger.info("Login attempt for email: {}", email);
        User user = userService.getUserByEmail(email);

        if (user != null && passwordEncoder.matches(password, user.getUserPassword())) {
            logger.info("Successful login for user: {}", email);
            session.setAttribute("activeUser", user);
            return "redirect:/user/profile";
        } else {
            logger.warn("Failed login attempt for email: {}", email);
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
