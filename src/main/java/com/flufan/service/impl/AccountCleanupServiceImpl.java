package com.flufan.service.impl;

import com.flufan.entity.Account;
import com.flufan.repo.AccountRepo;
import com.flufan.service.AccountCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountCleanupServiceImpl implements AccountCleanupService {

    private final AccountRepo accountRepo;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    @Override
    public void purgeDeletedAccounts() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);

        List<Account> accounts =
                accountRepo.findAllByDeletedAtBefore(cutoff);

        accounts.forEach(accountRepo::delete);
    }
}
