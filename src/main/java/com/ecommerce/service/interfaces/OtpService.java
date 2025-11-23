package com.ecommerce.service.interfaces;

public interface OtpService {
    String generateOtp(String email);
    boolean validateOtp(String email, String otp);
}
