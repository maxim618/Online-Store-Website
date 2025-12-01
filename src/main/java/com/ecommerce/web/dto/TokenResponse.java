package com.ecommerce.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {

    private String token;       // сам JWT
    private String email;       // email пользователя
    private String name;        // имя пользователя
    private long expiresIn;     // через сколько мс истечёт токен
}
