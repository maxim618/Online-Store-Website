package com.ecommerce.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    // Ключ должен быть минимум 32 символа!
    @Value("${jwt.secret}")
    private String SECRET;
    private final long EXPIRE = 86400000L;  // 24 hours

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    //           GENERATE JWT
    public String generateToken(String email, Long id, String name, String role) {

        if (email == null || id == null || name == null || role == null) {
            throw new IllegalArgumentException("JWT fields must not be null");
        }

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRE))

                // Custom claims — данные пользователя
                .claims(Map.of(
                        "id", id,
                        "name", name,
                        "role", role
                ))

                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    //       EXTRACT DATA FROM JWT

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        return extractAllClaims(token).get("id", Long.class);
    }

    public String extractName(String token) {
        return extractAllClaims(token).get("name", String.class);
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    //          TOKEN VALIDATION
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    //         PARSE JWT
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
