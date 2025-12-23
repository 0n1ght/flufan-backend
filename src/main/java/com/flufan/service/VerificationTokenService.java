package com.flufan.service;

import com.flufan.entity.Account;

public interface VerificationTokenService {
    String generateToken(String email, Account account);
    boolean useToken(String token);
}
