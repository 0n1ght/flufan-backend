package com.flufan.service;

public interface VerificationTokenService {
    String generateToken(String email);
    boolean useToken(String email, String token);
}
