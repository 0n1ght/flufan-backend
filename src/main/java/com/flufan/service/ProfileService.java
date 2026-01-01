package com.flufan.service;

import com.flufan.dto.ProfileDto;
import com.flufan.dto.ProfileResDto;
import com.flufan.entity.Profile;

import java.util.List;
import java.util.UUID;

public interface ProfileService {
    void createProfile(ProfileDto profileDto);
    void removeProfile(String password);
    void editProfile(ProfileDto profileDto);
    List<ProfileResDto> searchProfiles(String searchVal);
    Profile findByPublicId(UUID publicProfileId);
}
