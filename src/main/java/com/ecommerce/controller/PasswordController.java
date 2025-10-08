package com.ecommerce.controller;

import com.ecommerce.entities.Message;
import com.ecommerce.service.OtpService;
import com.ecommerce.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordController {

    private final UserService userService;
    private final OtpService otpService;
    private final BCryptPasswordEncoder passwordEncoder;

    public PasswordController(UserService userService, OtpService otpService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
    }

    /* 1) Forgot password (email) */
    @GetMapping("/forgot_password")
    public String showForgotPage() {
        return "forgot_password";
    }

    @PostMapping("/password/forgot")
    public String processForgotPassword(@RequestParam("email") String email,
                                        HttpSession session,
                                        RedirectAttributes ra) {
        if (!StringUtils.hasText(email)) {
            ra.addFlashAttribute("message",
                    new Message("Email is required.", "Error!", "alert-danger"));
            return "redirect:/forgot_password";
        }

        String found = userService.getUserEmailByEmail(email);
        if (found == null) {
            ra.addFlashAttribute("message",
                    new Message("Email not found. Please try again.", "Error!", "alert-danger"));
            return "redirect:/forgot_password";
        }

        String otp = otpService.generateOtp(email);
        // TODO: send email with OTP

        session.setAttribute("otp_email", email);
        ra.addFlashAttribute("message",
                new Message("OTP has been sent to your email.", "Info", "alert-warning"));
        return "redirect:/otp";
    }

    /* 2) OTP */
    @GetMapping("/otp")
    public String showOtpPage(HttpSession session, Model model) {
        String email = (String) session.getAttribute("otp_email");
        if (email != null) {
            model.addAttribute("email", email);
        }
        return "otp";
    }

    @PostMapping("/password/otp")
    public String verifyOtp(@RequestParam("otp") String otp,
                            @RequestParam(value = "email", required = false) String emailFromForm,
                            HttpSession session,
                            RedirectAttributes ra) {
        String email = StringUtils.hasText(emailFromForm) ? emailFromForm : (String) session.getAttribute("otp_email");

        if (!StringUtils.hasText(email)) {
            ra.addFlashAttribute("message",
                    new Message("Email context lost. Please restart password reset.", "Error!", "alert-danger"));
            return "redirect:/forgot_password";
        }

        if (!StringUtils.hasText(otp) || !otpService.verifyOtp(email, otp)) {
            ra.addFlashAttribute("message",
                    new Message("Invalid or expired OTP. Please try again.", "Error!", "alert-danger"));
            return "redirect:/otp";
        }

        session.setAttribute("verified_email", email);
        return "redirect:/password/change";
    }

    /* 3) Change password */
    @GetMapping("/password/change")
    public String showChangePasswordPage(HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("verified_email") == null) {
            ra.addFlashAttribute("message",
                    new Message("Unauthorized attempt. Please restart password reset.", "Error!", "alert-danger"));
            return "redirect:/forgot_password";
        }
        return "change_password";
    }

    @PostMapping("/password/change")
    public String changePassword(@RequestParam("password") String newPassword,
                                 @RequestParam("confirm") String confirm,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        String email = (String) session.getAttribute("verified_email");
        if (email == null) {
            ra.addFlashAttribute("message",
                    new Message("Unauthorized attempt. Please restart password reset.", "Error!", "alert-danger"));
            return "redirect:/forgot_password";
        }

        if (!StringUtils.hasText(newPassword) || newPassword.length() < 6) {
            ra.addFlashAttribute("message",
                    new Message("Password must be at least 6 characters.", "Error!", "alert-danger"));
            return "redirect:/password/change";
        }
        if (!newPassword.equals(confirm)) {
            ra.addFlashAttribute("message",
                    new Message("Passwords do not match.", "Error!", "alert-danger"));
            return "redirect:/password/change";
        }

        // Шифруем пароль
        String encoded = passwordEncoder.encode(newPassword);
        userService.updateUserPasswordByEmail(encoded, email);

        session.removeAttribute("otp_email");
        session.removeAttribute("verified_email");

        ra.addFlashAttribute("message",
                new Message("Password updated successfully. Please login.", "Success!", "alert-success"));
        return "redirect:/login";
    }
}
