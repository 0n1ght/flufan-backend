package com.flufan.modules.user.service;

import com.flufan.modules.user.entity.Account;

public interface RefreshTokenService {
    Account validateAndConsume(String rawToken);
    String issueNew(Account account);
    boolean invalidateToken(String rawToken);
}
