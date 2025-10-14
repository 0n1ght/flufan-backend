package com.frinkan.admin.service.impl;

import com.frinkan.admin.service.AdminService;
import com.frinkan.dto.*;
import com.frinkan.mapper.AccountMapper;
import com.frinkan.mapper.MessageMapper;
import com.frinkan.mapper.ProfileMapper;
import com.frinkan.mapper.UserReviewMapper;
import com.frinkan.repo.*;
import com.frinkan.service.PasswordResetService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {
    private final AccountRepo accountRepo;
    private final BannedAccountRepo bannedAccountRepo;
    private final SuspendedAccountRepo suspendedAccountRepo;
    private final MessageRepo messageRepo;
    private final ProfileRepo profileRepo;
    private final UserReviewRepo userReviewRepo;
    private final AccountMapper accountMapper;
    private final ProfileMapper profileMapper;
    private final UserReviewMapper userReviewMapper;
    private final MessageMapper messageMapper;
    private final PasswordResetService passwordResetService;

    public AdminServiceImpl(AccountRepo accountRepo,
                            BannedAccountRepo bannedAccountRepo,
                            SuspendedAccountRepo suspendedAccountRepo,
                            MessageRepo messageRepo,
                            PasswordResetTokenRepo passwordResetTokenRepo,
                            ProfileRepo profileRepo,
                            UserReviewRepo userReviewRepo,
                            AccountMapper accountMapper,
                            ProfileMapper profileMapper,
                            UserReviewMapper userReviewMapper,
                            MessageMapper messageMapper,
                            PasswordResetService passwordResetService) {
        this.accountRepo = accountRepo;
        this.bannedAccountRepo = bannedAccountRepo;
        this.suspendedAccountRepo = suspendedAccountRepo;
        this.messageRepo = messageRepo;
        this.profileRepo = profileRepo;
        this.userReviewRepo = userReviewRepo;
        this.accountMapper = accountMapper;
        this.profileMapper = profileMapper;
        this.userReviewMapper = userReviewMapper;
        this.messageMapper = messageMapper;
        this.passwordResetService = passwordResetService;
    }


    // Account Management
    public List<AccountDto> getAllAccounts() {
        return accountRepo.findAll().stream()
                .map(accountMapper::toAccountDto)
                .collect(Collectors.toList());
    }

    public List<AccountDto> getAllBannedAccounts() {
        return bannedAccountRepo.findAll().stream()
                .map(accountMapper::toAccountDto)
                .collect(Collectors.toList());
    }

    public AccountDto getAccountById(Long id) {
        return accountMapper.toAccountDto(accountRepo.findById(id).orElseThrow());
    }

    public AccountDto getBannedAccountById(Long id) {
        return accountMapper.toAccountDto(bannedAccountRepo.findById(id).orElseThrow());
    }

    public void banAccount(Long id) {
        bannedAccountRepo.save(accountRepo.findById(id).orElseThrow());
        accountRepo.deleteById(id);
    }

    public void unbanAccount(Long id) {
        accountRepo.save(bannedAccountRepo.findById(id).orElseThrow());
        bannedAccountRepo.deleteById(id);
    }

    public void deleteAccount(Long id) {
        accountRepo.deleteById(id);
        bannedAccountRepo.deleteById(id);
    }

    public void sendResetPassword(Long accountId) {
        passwordResetService.requestPasswordReset(accountRepo.findById(accountId).orElseThrow().getEmail());
    }


    // Profile Management
    public ProfileResDto getProfileByAccountId(Long accountId) {
        return profileMapper.toProfileResDto(accountRepo.findById(accountId).orElseThrow().getProfile());
    }


    // Messages and Content Moderation
    public List<MessageDto> getAllMessages(long acc1Id, long acc2Id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageRepo.findConversation(acc1Id, acc2Id, pageable).stream()
                .map(messageMapper::toMessageDto)
                .collect(Collectors.toList());
    }

    public void deleteMessage(Long messageId) {
        messageRepo.deleteById(messageId);
    }

    public List<UserReviewDto> getAllReviews(long profileId) {
        return userReviewRepo.findAll().stream()
                .map(userReviewMapper::toUserReviewDto)
                .collect(Collectors.toList());
    }

    public void deleteReview(Long reviewId) {
        userReviewRepo.deleteById(reviewId);
    }


    // Statistics
    public long countAccounts() {
        return accountRepo.findAll().size();
    }

    public long countMessages() {
        return messageRepo.findAll().size();
    }

    public Map<String, Long> getSystemStats() {
        Map<String, Long> stats = new HashMap<>();

        stats.put("accounts", (long) accountRepo.findAll().size()+suspendedAccountRepo.findAll().size());
        stats.put("banned_accounts", (long) bannedAccountRepo.findAll().size());
        stats.put("profiles", (long) profileRepo.findAll().size());
        stats.put("messages", (long) messageRepo.findAll().size());
        stats.put("reviews", (long) userReviewRepo.findAll().size());

        return stats;
    }
}
