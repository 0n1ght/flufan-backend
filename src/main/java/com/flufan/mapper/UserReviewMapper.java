package com.flufan.mapper;

import com.flufan.dto.UserReviewDto;
import com.flufan.entity.UserReview;
import com.flufan.entity.Profile;
import com.flufan.entity.Account;
import com.flufan.repo.ProfileRepo;
import com.flufan.repo.AccountRepo;
import org.springframework.stereotype.Component;

@Component
public class UserReviewMapper {

    private final ProfileRepo profileRepo;
    private final AccountRepo accountRepo;

    public UserReviewMapper(ProfileRepo profileRepo, AccountRepo accountRepo) {
        this.profileRepo = profileRepo;
        this.accountRepo = accountRepo;
    }

    public UserReviewDto toUserReviewDto(UserReview review) {
        return new UserReviewDto(
                review.getId(),
                review.getProfile().getId(),
                review.getReviewer().getId(),
                review.getRating(),
                review.getComment()
        );
    }

    public UserReview toUserReview(UserReviewDto reviewDto) {
        Profile profile = profileRepo.findById(reviewDto.getProfileId())
                .orElseThrow(() -> new IllegalArgumentException("Profile not found with ID: " + reviewDto.getProfileId()));

        Account reviewer = accountRepo.findById(reviewDto.getReviewerId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + reviewDto.getReviewerId()));

        return new UserReview(
                profile,
                reviewer,
                reviewDto.getRating(),
                reviewDto.getComment()
        );
    }
}
