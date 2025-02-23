package com.frinkan.service;


import com.frinkan.dto.ProfileDto;

public interface ProfileService {
    void createProfile(ProfileDto profileDto);
    void removeProfile(String nick);
    void editProfile(ProfileDto profileDto);
}
