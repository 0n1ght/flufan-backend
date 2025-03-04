package com.frinkan.controller;

import com.frinkan.dto.ProfileDto;
import com.frinkan.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/remove/{nick}")
    public ResponseEntity<String> removeProfile(@PathVariable String nick) {
        profileService.removeProfile(nick);
        return ResponseEntity.ok("Profile deleted successfully");
    }
}
