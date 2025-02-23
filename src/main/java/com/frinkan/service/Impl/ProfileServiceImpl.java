package com.frinkan.service.Impl;

import com.frinkan.dto.ProfileDto;
import com.frinkan.repo.ProfileRepo;
import com.frinkan.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private ProfileRepo profileRepo;

    @Override
    public void createProfile(ProfileDto profileDto) {

    }

    @Override
    public void removeProfile(String nick) {

    }

    @Override
    public void editProfile(ProfileDto profileDto) {

    }
}
