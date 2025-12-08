package com.flufan.admin.service;

import com.flufan.dto.*;
import java.util.List;
import java.util.Map;

public interface AdminService {

    // Account Management
    List<AccountDto> getAllAccounts();
    List<AccountDto> getAllBannedAccounts();
    AccountDto getAccountById(Long id);
    AccountDto getBannedAccountById(Long id);
    void banAccount(Long id);
    void unbanAccount(Long id);
    void deleteAccount(Long id);
    void sendResetPassword(Long accountId);

    // Profile Management
    ProfileResDto getProfileByAccountId(Long accountId);

    // Messages and Content Moderation
    List<MessageDto> getAllMessages(long acc1Id, long acc2Id, int page, int size);
    void deleteMessage(Long messageId);
    List<UserReviewDto> getAllReviews(long profileId);
    void deleteReview(Long reviewId);

    // Statistics
    long countAccounts();
    long countMessages();
    Map<String, Long> getSystemStats();
}
