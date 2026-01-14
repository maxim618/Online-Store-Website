package com.ecommerce.service.impl;

import com.ecommerce.service.interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.email", name = "enabled", havingValue = "true")
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    @Async
    @Override
    public void sendEmail(String to, String subject, String text) {
        var message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
