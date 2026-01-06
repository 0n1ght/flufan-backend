package com.flufan.modules.user.service.impl;

import com.flufan.modules.user.entity.Account;
import com.flufan.modules.user.entity.VerificationToken;
import com.flufan.modules.user.repo.VerificationTokenRepo;
import com.flufan.modules.user.service.AccountService;
import com.flufan.modules.user.service.VerificationTokenService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {
    private final VerificationTokenRepo tokenRepo;
    private final AccountService accountService;

    @Override
    public String generateToken(String email, Account account) {
        VerificationToken token = new VerificationToken(createUniqueToken(), email, account);
        tokenRepo.save(token);

        return token.getToken();
    }

    @Transactional
    @Override
    public boolean useToken(String token) {
        return tokenRepo.findByToken(token)
                .filter(t -> !t.isExpired() && t.getUsedAt() == null)
                .map(t -> {
                    Account account = t.getAccount();
                    account.setEmail(t.getEmail());
                    account.setVerifiedEmail(true);
                    accountService.updateAccount(account);

                    t.setUsedAt(LocalDateTime.now());
                    tokenRepo.save(t);
                    return true;
                })
                .orElse(false);
    }


    private String createUniqueToken() {
        String token;
        do {
            token = UUID.randomUUID().toString();
        } while (tokenRepo.existsByToken(token));
        return token;
    }
}
