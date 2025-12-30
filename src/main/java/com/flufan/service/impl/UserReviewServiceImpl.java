package com.flufan.service.impl;

import com.flufan.entity.UserReview;
import com.flufan.dto.UserReviewDto;
import com.flufan.mapper.UserReviewMapper;
import com.flufan.repo.UserReviewRepo;
import com.flufan.service.AccountService;
import com.flufan.service.MessageService;
import com.flufan.service.ProfileService;
import com.flufan.service.UserReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserReviewServiceImpl implements UserReviewService {

    private final UserReviewRepo userReviewRepo;
    private final UserReviewMapper userReviewMapper;
    private final MessageService messageService;
    private final AccountService accountService;
    private final ProfileService profileService;

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

        if (messageService.wasConversation(reviewDto.getReviewerId(),
                profileService.findById(reviewDto.getProfileId()).getAccount().getId()) &&
                getReviewsForProfile(reviewDto.getProfileId()).stream()
                        .noneMatch(userReviewDto -> Objects.equals(userReviewDto.getReviewerId(), reviewDto.getId()))) {
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
