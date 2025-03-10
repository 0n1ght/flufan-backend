package com.frinkan.service.Impl;

import com.frinkan.dto.UserReviewDto;
import com.frinkan.service.UserReviewService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserReviewServiceImpl implements UserReviewService {
    @Override
    public List<UserReviewDto> get3Reviews() {
        return List.of();
    }
}
