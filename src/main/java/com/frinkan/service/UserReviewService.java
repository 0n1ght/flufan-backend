package com.frinkan.service;

import com.frinkan.dto.UserReviewDto;

import java.util.List;
import java.util.Optional;

public interface UserReviewService {
    List<UserReviewDto> getReviewsForProfile(Long profileId);
    Optional<UserReviewDto> getReviewById(Long id);
    void saveReview(UserReviewDto reviewDto);
    void deleteReview(Long id);
}
