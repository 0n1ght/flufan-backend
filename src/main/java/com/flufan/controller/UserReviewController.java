package com.flufan.controller;

import com.flufan.dto.UserReviewDto;
import com.flufan.service.UserReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class UserReviewController {

    private final UserReviewService userReviewService;

    public UserReviewController(UserReviewService userReviewService) {
        this.userReviewService = userReviewService;
    }

    @GetMapping("/get-profile-reviews/{profileId}")
    public ResponseEntity<List<UserReviewDto>> getReviewsForProfile(@PathVariable Long profileId) {
        return ResponseEntity.ok(new ArrayList<>(userReviewService.getReviewsForProfile(profileId)));
    }

    @GetMapping("/get-review/{id}")
    public ResponseEntity<UserReviewDto> getReviewById(@PathVariable Long id) {
        Optional<UserReviewDto> reviewOpt = userReviewService.getReviewById(id);
        return ResponseEntity.of(reviewOpt);
    }

    @PostMapping("/add-review")
    public ResponseEntity<UserReviewDto> addReview(@RequestBody UserReviewDto userReviewDto) {
        userReviewService.saveReview(userReviewDto);
        return ResponseEntity.ok(userReviewDto);
    }

    @DeleteMapping("/delete-review/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        userReviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
