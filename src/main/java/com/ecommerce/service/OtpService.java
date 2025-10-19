package com.ecommerce.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Простой in-memory сервис OTP: хранит код и время истечения.
 * Для продакшена лучше хранить в БД и отправлять код по email.
 */
@Service
public class OtpService {

    private static class OtpEntry {
        final String code;
        final Instant expiresAt;
        OtpEntry(String code, Instant expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }
    }

    // email -> OtpEntry
    private final Map<String, OtpEntry> storage = new ConcurrentHashMap<>();
    private final Random random = new Random();

    /** Генерирует 6-значный код и сохраняет на 5 минут */
    public String generateOtp(String email) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        Instant expires = Instant.now().plusSeconds(5 * 60);
        storage.put(email, new OtpEntry(code, expires));
        return code;
    }

    /** Проверяет код; если прошёл — удаляет */
    public boolean verifyOtp(String email, String code) {
        OtpEntry entry = storage.get(email);
        if (entry == null) return false;
        if (Instant.now().isAfter(entry.expiresAt)) {
            storage.remove(email);
            return false;
        }
        boolean ok = Objects.equals(entry.code, code);
        if (ok) {
            storage.remove(email);
        }
        return ok;
    }
}
