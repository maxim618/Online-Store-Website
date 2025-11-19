package com.ecommerce.web.controller;

import com.ecommerce.service.interfaces.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/login")
    String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session){

        if (userService.validateCredentials(email,password)) {
            session.setAttribute("user", userService.getByEmail(email));
            return "redirect:/";
        }

        session.setAttribute("error", "invalid credentials");
        return "redirect:/login";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String email,
            @RequestParam String name,
            @RequestParam String password,
            HttpSession session){

        try {
            userService.registerUser(email, name, password);
            session.setAttribute("msg", "registration successfull");
            return "redirect:/login";
        } catch (Exception e) {
            session.setAttribute("error", e.getMessage());
            return "redirect: register/";
        }
    }
}
