package com.frinkan.service;

public interface VerificationTokenService {
    String generateToken(String email);
    void useToken(String email, String token);
}
