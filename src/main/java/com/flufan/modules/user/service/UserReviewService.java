package com.flufan.modules.user.service;

import com.flufan.modules.user.dto.UserReviewDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserReviewService {
    List<UserReviewDto> getReviewsForProfile(UUID publicProfileId);
    Optional<UserReviewDto> getReviewById(Long id);
    void saveReview(UserReviewDto reviewDto);
    void deleteReview(Long id);
}
