package com.frinkan.controller;

import com.frinkan.dto.ProfileDto;
import com.frinkan.dto.ProfileResDto;
import com.frinkan.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

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

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeProfile() {
        profileService.removeProfile();
        return ResponseEntity.ok("Profile deleted successfully");
    }

    @GetMapping("/search/{searchVal}")
    public List<ProfileResDto> searchProfiles(@PathVariable String searchVal) {
        return profileService.searchProfiles(searchVal);
    }
}
