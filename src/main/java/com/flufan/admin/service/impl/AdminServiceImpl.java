package com.flufan.admin.service.impl;

import com.flufan.admin.service.AdminService;
import com.flufan.dto.*;
import com.flufan.mapper.*;
import com.flufan.repo.*;
import com.flufan.service.AccountService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
    private final AccountService accountService;

    public AdminServiceImpl(AccountRepo accountRepo,
                            BannedAccountRepo bannedAccountRepo,
                            SuspendedAccountRepo suspendedAccountRepo,
                            MessageRepo messageRepo,
                            ProfileRepo profileRepo,
                            UserReviewRepo userReviewRepo,
                            AccountMapper accountMapper,
                            ProfileMapper profileMapper,
                            UserReviewMapper userReviewMapper,
                            MessageMapper messageMapper,
                            AccountService accountService) {
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
        this.accountService = accountService;
    }

    // --- Helpers ---
    private <T> T findByIdOrThrow(Long id, Function<Long, T> repoFindById) {
        return repoFindById.apply(id);
    }

    private <E, D> List<D> mapListToDto(List<E> entities, Function<E, D> mapper) {
        return entities.stream().map(mapper).collect(Collectors.toList());
    }

    // --- Account Management ---
    public List<AccountDto> getAllAccounts() {
        return mapListToDto(accountRepo.findAll(), accountMapper::toAccountDto);
    }

    public List<AccountDto> getAllBannedAccounts() {
        return mapListToDto(bannedAccountRepo.findAll(), accountMapper::toAccountDto);
    }

    public AccountDto getAccountById(Long id) {
        return accountMapper.toAccountDto(findByIdOrThrow(id, accountRepo::findById).orElseThrow());
    }

    public AccountDto getBannedAccountById(Long id) {
        return accountMapper.toAccountDto(findByIdOrThrow(id, bannedAccountRepo::findById).orElseThrow());
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
        String email = accountRepo.findById(accountId).orElseThrow().getEmail();
        accountService.requestPasswordReset(email);
    }

    // --- Profile Management ---
    public ProfileResDto getProfileByAccountId(Long accountId) {
        return profileMapper.toProfileResDto(accountRepo.findById(accountId).orElseThrow().getProfile());
    }

    // --- Messages and Content Moderation ---
    public List<MessageDto> getAllMessages(long acc1Id, long acc2Id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return mapListToDto(
                messageRepo.findConversation(acc1Id, acc2Id, pageable).getContent(),
                messageMapper::toMessageDto
        );
    }

    public void deleteMessage(Long messageId) {
        messageRepo.deleteById(messageId);
    }

    public List<UserReviewDto> getAllReviews(long profileId) {
        return mapListToDto(userReviewRepo.findAll(), userReviewMapper::toUserReviewDto);
    }

    public void deleteReview(Long reviewId) {
        userReviewRepo.deleteById(reviewId);
    }

    // --- Statistics ---
    public long countAccounts() {
        return accountRepo.count();
    }

    public long countMessages() {
        return messageRepo.count();
    }

    public Map<String, Long> getSystemStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("accounts", accountRepo.count() + suspendedAccountRepo.count());
        stats.put("banned_accounts", bannedAccountRepo.count());
        stats.put("profiles", profileRepo.count());
        stats.put("messages", messageRepo.count());
        stats.put("reviews", userReviewRepo.count());
        return stats;
    }
}