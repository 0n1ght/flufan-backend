package com.flufan.modules.user.service;

import com.flufan.modules.user.entity.Account;

public interface VerificationTokenService {
    String generateToken(String email, Account account);
    boolean useToken(String token);
}
