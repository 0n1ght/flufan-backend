package com.frinkan.service;


import com.frinkan.dto.ProfileDto;
import com.frinkan.dto.ProfileResDto;

import java.util.List;

public interface ProfileService {
    void createProfile(ProfileDto profileDto);
    void removeProfile(String nick);
    void editProfile(ProfileDto profileDto);
    List<ProfileResDto> searchProfiles(String searchVal);
}
