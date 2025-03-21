package com.frinkan.mapper;

import com.frinkan.dto.UserReviewDto;
import com.frinkan.entity.UserReview;
import com.frinkan.entity.Profile;
import com.frinkan.entity.Account;
import com.frinkan.repo.ProfileRepo;
import com.frinkan.repo.AccountRepo;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
