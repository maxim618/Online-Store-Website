package com.ecommerce.service.impl;

import com.ecommerce.service.interfaces.OtpService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpServiceImpl implements OtpService {

    private final Map<String, String> otpStore = new ConcurrentHashMap<>();

    @Override
    public String generateOtp(String email) {
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        otpStore.put(email, otp);
        return otp;
    }

    @Override
    public boolean validateOtp(String email, String otp) {
        return otp.equals(otpStore.get(email));
    }
}
