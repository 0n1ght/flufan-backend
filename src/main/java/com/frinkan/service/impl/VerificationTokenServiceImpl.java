package com.frinkan.service.impl;

import com.frinkan.entity.Account;
import com.frinkan.entity.VerificationToken;
import com.frinkan.repo.VerificationTokenRepo;
import com.frinkan.service.AccountService;
import com.frinkan.service.VerificationTokenService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
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
    public void generateToken(String email) {
        if (tokenRepo.findByEmail(email) != null) tokenRepo.deleteByEmail(email);
        tokenRepo.save(new VerificationToken(createUniqueToken(email), email, LocalDateTime.now().plusMinutes(20)));
    }

    @Override
    public void useToken(String email, String token) {
        VerificationToken verificationToken = tokenRepo.findByEmail(email);
        if (verificationToken != null && !verificationToken.isExpired() && verificationToken.getToken().equals(token)) {
            accountService.verifyAccountEmail(email);
        }
    }

    private String createUniqueToken(String email) {
        String token;
        do {
            token = UUID.randomUUID().toString();
        } while (tokenRepo.existsByToken(token));
        return token;
    }
}
