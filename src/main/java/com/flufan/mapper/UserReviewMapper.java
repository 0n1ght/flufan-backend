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
                review.getProfile().getPublicId(),
                review.getReviewer().getPublicId(),
                review.getRating(),
                review.getComment()
        );
    }

    public UserReview toUserReview(UserReviewDto reviewDto) {
        Profile profile = profileRepo.findByPublicId(reviewDto.getProfilePublicId())
                .orElseThrow(() -> new IllegalArgumentException("Profile not found with ID: " + reviewDto.getProfilePublicId()));

        Account reviewer = accountRepo.findByPublicId(reviewDto.getReviewerPublicId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + reviewDto.getReviewerPublicId()));

        return new UserReview(
                profile,
                reviewer,
                reviewDto.getRating(),
                reviewDto.getComment()
        );
    }
}
