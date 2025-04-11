package com.frinkan.service.impl;

import com.frinkan.entity.Account;
import com.frinkan.entity.UserReview;
import com.frinkan.dto.UserReviewDto;
import com.frinkan.mapper.UserReviewMapper;
import com.frinkan.repo.UserReviewRepo;
import com.frinkan.service.AccountService;
import com.frinkan.service.MessageService;
import com.frinkan.service.ProfileService;
import com.frinkan.service.UserReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserReviewServiceImpl implements UserReviewService {

    private final UserReviewRepo userReviewRepo;
    private final UserReviewMapper userReviewMapper;
    private final MessageService messageService;
    private final AccountService accountService;
    private final ProfileService profileService;

    @Autowired
    public UserReviewServiceImpl(UserReviewRepo userReviewRepo, UserReviewMapper userReviewMapper,
                                 MessageService messageService, AccountService accountService,
                                 ProfileService profileService) {
        this.userReviewRepo = userReviewRepo;
        this.userReviewMapper = userReviewMapper;
        this.messageService = messageService;
        this.accountService = accountService;
        this.profileService = profileService;
    }

    @Override
    public List<UserReviewDto> getReviewsForProfile(Long profileId) {
        List<UserReview> reviews = userReviewRepo.findByProfileId(profileId);
        return reviews.stream()
                .map(userReviewMapper::toUserReviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserReviewDto> getReviewById(Long id) {
        Optional<UserReview> reviewOpt = userReviewRepo.findById(id);
        return reviewOpt.map(userReviewMapper::toUserReviewDto);
    }

    @Override
    public void saveReview(UserReviewDto reviewDto) {
        reviewDto.setReviewerId(accountService.getAuthenticatedAccount().getId());

        if (messageService.wasConversation(reviewDto.getReviewerId(), profileService.findById(reviewDto.getProfileId()).getAccount().getId())) {
            UserReview review = userReviewMapper.toUserReview(reviewDto);
            UserReview savedReview = userReviewRepo.save(review);
            userReviewMapper.toUserReviewDto(savedReview);
        }
    }

    @Override
    public void deleteReview(Long id) {
        Optional<UserReview> userReviewOptional = userReviewRepo.findById(id);
        if (userReviewOptional.isPresent()) {
            UserReview userReview = userReviewOptional.get();
            if (Objects.equals(userReview.getReviewer().getId(), accountService.getAuthenticatedAccount().getId()))
                userReviewRepo.deleteById(id);
        }
    }
}
