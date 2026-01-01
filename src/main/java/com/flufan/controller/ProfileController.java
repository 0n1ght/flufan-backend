package com.flufan.controller;

import com.flufan.dto.DeleteProfileDto;
import com.flufan.dto.ProfileDto;
import com.flufan.dto.ProfileResDto;
import com.flufan.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/market-profiles")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping("/create")
    public ResponseEntity<String> createProfile(@RequestBody ProfileDto profileDto) {
        profileService.createProfile(profileDto);
        return ResponseEntity.ok("Profile created successfully");
    }

    @PutMapping("/edit")
    public ResponseEntity<String> editProfile(@RequestBody ProfileDto profileDto) {
        profileService.editProfile(profileDto);
        return ResponseEntity.ok("Profile updated successfully");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteProfile(@RequestBody DeleteProfileDto deleteProfileDto) {
        profileService.removeProfile(deleteProfileDto.password());
        return ResponseEntity.ok("Profile deleted successfully");
    }

    @GetMapping("/search/{searchVal}")
    public List<ProfileResDto> searchProfiles(@PathVariable String searchVal) {
        return profileService.searchProfiles(searchVal);
    }
}
