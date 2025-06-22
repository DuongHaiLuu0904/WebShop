package com.ptit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VNPayConfig {
    
    @Value("${vnpay.tmn-code}")
    private String tmnCode;
    
    @Value("${vnpay.hash-secret}")
    private String hashSecret;
    
    @Value("${vnpay.url}")
    private String vnpUrl;
    
    @Value("${vnpay.return-url}")
    private String returnUrl;
    
    @Value("${vnpay.ipn-url}")
    private String ipnUrl;
    
    public String getTmnCode() {
        return tmnCode;
    }
    
    public String getHashSecret() {
        return hashSecret;
    }
    
    public String getVnpUrl() {
        return vnpUrl;
    }
    
    public String getReturnUrl() {
        return returnUrl;
    }
    
    public String getIpnUrl() {
        return ipnUrl;
    }
}
