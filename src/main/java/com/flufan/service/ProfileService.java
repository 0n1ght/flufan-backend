package com.flufan.service;

import com.flufan.dto.ProfileDto;
import com.flufan.dto.ProfileResDto;
import com.flufan.entity.Profile;

import java.util.List;

public interface ProfileService {
    void createProfile(ProfileDto profileDto);
    void removeProfile(String password);
    void editProfile(ProfileDto profileDto);
    List<ProfileResDto> searchProfiles(String searchVal);
    Profile findById(Long profileId);
}
