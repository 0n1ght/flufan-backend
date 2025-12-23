package com.flufan.service.impl;

import com.flufan.entity.Account;
import com.flufan.entity.VerificationToken;
import com.flufan.repo.VerificationTokenRepo;
import com.flufan.service.AccountService;
import com.flufan.service.VerificationTokenService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {
    private final VerificationTokenRepo tokenRepo;
    private final AccountService accountService;

    public VerificationTokenServiceImpl(VerificationTokenRepo tokenRepo, AccountService accountService) {
        this.tokenRepo = tokenRepo;
        this.accountService = accountService;
    }

    @Override
    public String generateToken(String email, Account account) {
        VerificationToken token = new VerificationToken(createUniqueToken(), email, account);
        tokenRepo.save(token);

        return token.getToken();
    }

    @Override
    @Transactional
    public boolean useToken(String token) {
        return tokenRepo.findByToken(token)
                .filter(t -> !t.isExpired() && t.getUsedAt() == null)
                .map(t -> {
                    Account account = t.getAccount();
                    account.setEmail(t.getEmail());
                    account.setVerifiedEmail(true);
                    accountService.saveAccount(account);

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
