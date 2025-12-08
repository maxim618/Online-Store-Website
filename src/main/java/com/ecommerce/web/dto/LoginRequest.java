package com.ecommerce.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "email must not be empty")
    @Email(message = "invalid email format")
    private String email;

    @NotBlank(message = "Password must not be empty")
    @Size(min = 3, max = 50, message = "Password length must be between 3 and 50 characters")
    private String password;
}
