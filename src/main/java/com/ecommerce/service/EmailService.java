package com.ecommerce.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendOtpEmail(String to, String otp) {
        // 🚧 Заглушка: в реальном проекте подключаем JavaMailSender
        System.out.println("=== EMAIL DEBUG ===");
        System.out.println("To: " + to);
        System.out.println("Subject: OTP Verification");
        System.out.println("Body: Your OTP code is " + otp);
        System.out.println("===================");
    }
}
