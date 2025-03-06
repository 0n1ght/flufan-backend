package com.frinkan.mapper;

import com.frinkan.dto.ProfileDto;
import com.frinkan.entity.Profile;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {

    public Profile toProfile(ProfileDto profileDto) {
        Profile profile = new Profile();
        profile.setNick(profileDto.getNick());
        profile.setVerified(false);
        profile.setActive(true);
        profile.setFirstName(profileDto.getFirstName());
        profile.setLastName(profileDto.getLastName());
        profile.setInteractionCounter(0);
        profile.setRating(0.0);
        profile.setRespondTime(profileDto.getRespondTime());
        profile.setMessagePrice(profileDto.getMessagePrice());
        profile.setCallPrice(profileDto.getCallPrice());
        profile.setProfilePicturePath(profileDto.getProfilePicturePath());
        profile.setLinkedAccounts(profileDto.getLinkedAccounts());

        return profile;
    }
}
