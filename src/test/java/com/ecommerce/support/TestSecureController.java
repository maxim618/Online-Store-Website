package com.ecommerce.support;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestSecureController {

    @GetMapping("/test/secure")
    public String secure() {
        return "OK";
    }
}
