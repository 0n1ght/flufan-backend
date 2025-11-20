package com.frinkan.service;

public interface VerificationTokenService {
    void generateToken(String email);
    void useToken(String email, String token);
}
