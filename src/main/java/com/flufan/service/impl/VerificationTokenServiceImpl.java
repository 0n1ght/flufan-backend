package com.flufan.service.impl;

import com.flufan.entity.VerificationToken;
import com.flufan.repo.VerificationTokenRepo;
import com.flufan.service.AccountService;
import com.flufan.service.VerificationTokenService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
    public String generateToken(String email) {
        VerificationToken token = new VerificationToken(createUniqueToken(email), email);
        tokenRepo.save(token);

        return token.getToken();
    }

    @Override
    public void useToken(String email, String token) {
        List<VerificationToken> verificationTokens = tokenRepo.findAllByEmail(email);
        System.out.println(verificationTokens);
        for (VerificationToken verificationToken : verificationTokens) {
            if (verificationToken != null && !verificationToken.isExpired() && verificationToken.getToken().equals(token) && verificationToken.getUsedAt() == null) {
                accountService.verifyAccountEmail(email);
                verificationToken.setUsedAt(LocalDateTime.now());
                tokenRepo.save(verificationToken);
                break;
            }
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
