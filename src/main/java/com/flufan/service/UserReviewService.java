package com.flufan.service;

import com.flufan.dto.UserReviewDto;

import java.util.List;
import java.util.Optional;

public interface UserReviewService {
    List<UserReviewDto> getReviewsForProfile(Long profileId);
    Optional<UserReviewDto> getReviewById(Long id);
    void saveReview(UserReviewDto reviewDto);
    void deleteReview(Long id);
}
