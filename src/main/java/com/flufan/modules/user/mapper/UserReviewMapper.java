package com.flufan.modules.user.mapper;

import com.flufan.modules.user.dto.UserReviewDto;
import com.flufan.modules.user.entity.UserReview;
import com.flufan.modules.user.entity.Profile;
import com.flufan.modules.user.entity.Account;
import com.flufan.modules.user.repo.ProfileRepo;
import com.flufan.modules.user.repo.AccountRepo;
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
