package com.ecommerce.web.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {

    private String name;
    private String password;       // новый пароль (если меняется)
    //Если нужно — можно расширить (телефон, адрес и т.д.).
}
