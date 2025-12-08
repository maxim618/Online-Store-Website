package com.ecommerce.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {


    private String token;       // сам JWT
    private Long id;
    private String email;       // email пользователя
    private String role;
    private String name;        // имя пользователя
    private long expiresIn;     // через сколько мс истечёт токен
}
