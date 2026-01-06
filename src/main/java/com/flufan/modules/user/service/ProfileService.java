package com.flufan.modules.user.service;

import com.flufan.modules.user.dto.ProfileDto;
import com.flufan.modules.user.dto.ProfileResDto;
import com.flufan.modules.user.entity.Profile;

import java.util.List;
import java.util.UUID;

public interface ProfileService {
    void createProfile(ProfileDto profileDto);
    void removeProfile(String password);
    void editProfile(ProfileDto profileDto);
    List<ProfileResDto> searchProfiles(String searchVal);
    Profile findByPublicId(UUID publicProfileId);
}
