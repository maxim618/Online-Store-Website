package com.ecommerce.controller;

import com.ecommerce.dao.UserDao;
import com.ecommerce.entities.Message;
import com.ecommerce.helper.MailMessenger;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/password")
public class ChangePasswordController {

    private final UserDao userDao;

    public ChangePasswordController(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Шаг 1 — запрос восстановления (forgot_password.jsp)
     */
    @PostMapping("/forgot")
    public String forgotPassword(
            @RequestParam("email") String email,
            HttpSession session
    ) {
        List<String> emails = userDao.getAllEmail();
        if (emails.contains(email.trim())) {
            int otp = new Random().nextInt(90000) + 10000; // 5-значный код
            session.setAttribute("otp", otp);
            session.setAttribute("email", email);

            MailMessenger.sendOtp(email, otp);

            session.setAttribute("message",
                    new Message("We've sent a password reset code to " + email, "success", "alert-success"));
            return "redirect:/otp_code.jsp";
        } else {
            session.setAttribute("message",
                    new Message("Email not found! Try with another email!", "error", "alert-danger"));
            return "redirect:/forgot_password.jsp";
        }
    }

    /**
     * Шаг 2 — проверка OTP (otp_code.jsp)
     */
    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam("code") int code,
            HttpSession session
    ) {
        Integer otp = (Integer) session.getAttribute("otp");
        if (otp != null && code == otp) {
            session.removeAttribute("otp");
            return "redirect:/change_password.jsp";
        } else {
            session.setAttribute("message",
                    new Message("Invalid verification code entered!", "error", "alert-danger"));
            return "redirect:/otp_code.jsp";
        }
    }

    /**
     * Шаг 3 — установка нового пароля (change_password.jsp)
     */
    @PostMapping("/update")
    public String updatePassword(
            @RequestParam("password") String password,
            HttpSession session
    ) {
        String email = (String) session.getAttribute("email");
        if (email != null) {
            userDao.updateUserPasswordByEmail(password, email);
            session.removeAttribute("email");

            session.setAttribute("message",
                    new Message("Password updated successfully!", "success", "alert-success"));
            return "redirect:/login.jsp";
        } else {
            session.setAttribute("message",
                    new Message("Session expired. Please try again.", "error", "alert-danger"));
            return "redirect:/forgot_password.jsp";
        }
    }
}
