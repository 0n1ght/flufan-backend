package com.flufan.service.impl;

import com.flufan.config.security.util.TokenHashUtil;
import com.flufan.entity.Account;
import com.flufan.entity.RefreshToken;
import com.flufan.exception.InvalidRefreshTokenException;
import com.flufan.exception.RefreshTokenExpiredException;
import com.flufan.repo.RefreshTokenRepo;
import com.flufan.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final Duration EXPIRATION = Duration.ofDays(60);

    private final RefreshTokenRepo refreshTokenRepo;
    private final TokenHashUtil tokenHashUtil;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    @Override
    public Account validateAndConsume(String rawToken) {
        String hash = tokenHashUtil.hash(rawToken);

        Optional<RefreshToken> reused = refreshTokenRepo.findByTokenHashAndUsedTrue(hash);
        if (reused.isPresent()) {
            Account account = reused.get().getAccount();
            refreshTokenRepo.deleteAllByAccount_Id(account.getId());
            return null;
        }

        RefreshToken rt = refreshTokenRepo
                .findByTokenHashAndUsedFalse(hash)
                .orElseThrow(InvalidRefreshTokenException::new);

        if (rt.getExpirationDate().isBefore(Instant.now())) {
            rt.setUsed(true);
            throw new RefreshTokenExpiredException();
        }

        rt.setUsed(true);
        return rt.getAccount();
    }

    @Transactional
    @Override
    public String issueNew(Account account) {
        while (true) {
            byte[] bytes = new byte[32];
            secureRandom.nextBytes(bytes);
            String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

            String hash = tokenHashUtil.hash(rawToken);

            RefreshToken rt = new RefreshToken();
            rt.setAccount(account);
            rt.setTokenHash(hash);
            rt.setExpirationDate(Instant.now().plus(EXPIRATION));

            try {
                refreshTokenRepo.save(rt);
                return rawToken;
            } catch (DataIntegrityViolationException _) {
            }
        }
    }

    @Transactional
    @Override
    public boolean invalidateToken(String rawToken) {
        String hash = tokenHashUtil.hash(rawToken);
        return refreshTokenRepo.invalidateByHash(hash) == 1;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanup() {
        refreshTokenRepo.deleteAllByExpirationDateBefore(Instant.now());
    }
}
