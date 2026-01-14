package com.ecommerce.service.noop;

import com.ecommerce.service.interfaces.EmailService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "app.email", name = "enabled", havingValue = "false", matchIfMissing = true)
public class NoopEmailService implements EmailService {
    // Класс - заглушка
    @Override
    public void sendEmail(String to, String subject, String text) {
        log.debug("Email disabled. Would send to={}, subject={}", to, subject);
    }
}
