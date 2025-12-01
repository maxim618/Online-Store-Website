package com.ecommerce.service.impl;

import com.ecommerce.persistence.model.BlacklistedToken;
import com.ecommerce.persistence.repository.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final BlacklistedTokenRepository repository;

    public void blacklist(String token) {
        if (!repository.existsByToken(token)) {
            repository.save(
                    BlacklistedToken.builder()
                            .token(token)
                            .createdAt(LocalDateTime.now())
                            .build()
            );
        }
    }

    public boolean isBlacklisted(String token) {
        return repository.existsByToken(token);
    }
}
