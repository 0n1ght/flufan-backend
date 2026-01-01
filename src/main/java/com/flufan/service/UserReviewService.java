package com.flufan.service;

import com.flufan.dto.UserReviewDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserReviewService {
    List<UserReviewDto> getReviewsForProfile(UUID publicProfileId);
    Optional<UserReviewDto> getReviewById(Long id);
    void saveReview(UserReviewDto reviewDto);
    void deleteReview(Long id);
}
