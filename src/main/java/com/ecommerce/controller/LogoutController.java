package com.ecommerce.controller;

import com.ecommerce.entities.Message;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LogoutController {

    @GetMapping("/logout")
    public String logout(
            @RequestParam("user") String userType,
            HttpSession session) {

        if ("user".equalsIgnoreCase(userType.trim())) {
            session.removeAttribute("activeUser");
            session.setAttribute("message",
                    new Message("Logout successfully!!", "success", "alert-success"));
            return "redirect:/login"; // login.jsp
        } else if ("admin".equalsIgnoreCase(userType.trim())) {
            session.removeAttribute("activeAdmin");
            session.setAttribute("message",
                    new Message("Logout successfully!!", "success", "alert-success"));
            return "redirect:/adminlogin"; // adminlogin.jsp
        }

        // fallback, если параметр некорректный
        session.setAttribute("message",
                new Message("Unknown logout type!", "error", "alert-danger"));
        return "redirect:/login";
    }
}
