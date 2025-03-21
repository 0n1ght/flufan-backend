package com.frinkan.service;

import com.frinkan.dto.UserReviewDto;
import com.frinkan.entity.UserReview;
import com.frinkan.mapper.UserReviewMapper;
import com.frinkan.repo.UserReviewRepo;

import java.util.List;
import java.util.Optional;

public interface UserReviewService {
    List<UserReviewDto> getReviewsForProfile(Long profileId);
    Optional<UserReviewDto> getReviewById(Long id);
    UserReviewDto saveReview(UserReviewDto reviewDto);
    void deleteReview(Long id);
}
