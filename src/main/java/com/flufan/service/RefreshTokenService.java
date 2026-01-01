package com.flufan.service;

import com.flufan.entity.Account;

public interface RefreshTokenService {
    Account validateAndConsume(String rawToken);
    String issueNew(Account account);
    boolean invalidateToken(String rawToken);
}
