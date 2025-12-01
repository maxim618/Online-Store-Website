package com.ecommerce.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

/**
 * Красивый и структурированный обработчик ошибок Security / JWT.
 */
@ControllerAdvice
public class SecurityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(SecurityExceptionHandler.class);

    // --- 403: Нет прав доступа -----------------------------------------------------

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Access denied: insufficient permissions.");
    }

    // --- 401: Неправильные креды ----------------------------------------------------

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials(BadCredentialsException e) {
        log.warn("Bad credentials: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Invalid email or password.");
    }

    // --- 401: Ошибки аутентификации -------------------------------------------------

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthException(AuthenticationException e) {
        log.warn("Authentication error: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Authentication failed.");
    }

    // --- 401: JWT истёк -------------------------------------------------------------

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<String> handleExpiredJwt(ExpiredJwtException e) {
        log.warn("JWT expired: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Token expired. Please login again.");
    }

    // --- 401: JWT подпись неверная -------------------------------------------------

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<String> handleSignatureError(SignatureException e) {
        log.warn("Invalid JWT signature: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Invalid token signature.");
    }

    // --- 401: JWT malformed ---------------------------------------------------------

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<String> handleMalformedJwt(MalformedJwtException e) {
        log.warn("Malformed JWT: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Token is malformed.");
    }

    // --- 401: Общая JWT ошибка ------------------------------------------------------

    @ExceptionHandler({ io.jsonwebtoken.JwtException.class })
    public ResponseEntity<String> handleJwtException(Exception e) {
        log.warn("JWT error: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Invalid or expired token.");
    }
}
