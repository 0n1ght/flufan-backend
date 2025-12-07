package com.frinkan.service;

import com.frinkan.dto.ProfileDto;
import com.frinkan.dto.ProfileResDto;
import com.frinkan.entity.Profile;

import java.util.List;

public interface ProfileService {
    void createProfile(ProfileDto profileDto);
    void removeProfile();
    void editProfile(ProfileDto profileDto);
    List<ProfileResDto> searchProfiles(String searchVal);
    Profile findById(Long profileId);
}
