package com.flufan.service.impl;

import com.flufan.entity.Account;
import com.flufan.repo.AccountRepo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

class AccountCleanupServiceImplTest {

    @Test
    void testPurgeDeletedAccounts() {
        AccountRepo mockRepo = mock(AccountRepo.class);
        Account oldAccount = new Account();
        oldAccount.setDeletedAt(LocalDateTime.now().minusDays(31));

        when(mockRepo.findAllByDeletedAtBefore(any(LocalDateTime.class)))
                .thenReturn(List.of(oldAccount));

        AccountCleanupServiceImpl service = new AccountCleanupServiceImpl(mockRepo);

        service.purgeDeletedAccounts();

        verify(mockRepo, times(1)).delete(oldAccount);
    }

    @Test
    void testRecentAccountIsNotDeleted() {
        AccountRepo mockRepo = mock(AccountRepo.class);
        Account recentAccount = new Account();
        recentAccount.setDeletedAt(LocalDateTime.now().minusDays(10));

        when(mockRepo.findAllByDeletedAtBefore(any(LocalDateTime.class)))
                .thenReturn(List.of());
        AccountCleanupServiceImpl service = new AccountCleanupServiceImpl(mockRepo);
        service.purgeDeletedAccounts();

        verify(mockRepo, never()).delete(recentAccount);
    }
}
