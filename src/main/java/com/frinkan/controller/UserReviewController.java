package com.frinkan.controller;

import com.frinkan.dto.UserReviewDto;
import com.frinkan.service.UserReviewService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class UserReviewController {
    private final UserReviewService userReviewService;

    public UserReviewController(UserReviewService userReviewService) {
        this.userReviewService = userReviewService;
    }

    @GetMapping("/{profileId}")
    public List<UserReviewDto> getUserReviewsOf(@PathVariable int profileId) {
        return userReviewService.get3Reviews();
    }

//    @PostMapping("/create")
//    public ResponseEntity<String> createProfile(@RequestBody ProfileDto profileDto) {
//        profileService.createProfile(profileDto);
//        return ResponseEntity.ok("Profile created successfully");
//    }
//
//    @PutMapping("/edit")
//    public ResponseEntity<String> editProfile(@RequestBody ProfileDto profileDto) {
//        profileService.editProfile(profileDto);
//        return ResponseEntity.ok("Profile updated successfully");
//    }
//
//    @DeleteMapping("/remove/{nick}")
//    public ResponseEntity<String> removeProfile(@PathVariable String nick) {
//        profileService.removeProfile(nick);
//        return ResponseEntity.ok("Profile deleted successfully");
//    }
//
//    @GetMapping("/search/{searchVal}")
//    public List<ProfileResDto> searchProfiles(@PathVariable String searchVal) {
//        return profileService.searchProfiles(searchVal);
//    }
}
