package com.flufan.modules.user.service.impl;

import com.flufan.modules.user.entity.Account;
import com.flufan.modules.user.repo.AccountRepo;
import com.flufan.modules.user.service.AccountCleanupService;
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
