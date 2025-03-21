package com.frinkan.mapper;

import com.frinkan.dto.ProfileDto;
import com.frinkan.dto.ProfileResDto;
import com.frinkan.entity.Profile;
import com.frinkan.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {
    @Autowired
    private AccountService accountService;

    public Profile toProfile(ProfileDto profileDto) {
        Profile profile = new Profile();
        profile.setNick(profileDto.getNick());
        profile.setVerified(false);
        profile.setActive(profileDto.getActive());
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

    public ProfileResDto toProfileResDto(Profile profile) {

        if (profile == null) {
            return null;
        }

        return new ProfileResDto(
                profile.getId(),
                profile.getNick(),
                profile.isVerified(),
                profile.isActive(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getInteractionCounter(),
                profile.getRating(),
                profile.getRespondTime(),
                profile.getMessagePrice(),
                profile.getCallPrice(),
                profile.getProfilePicturePath(),
                profile.getLinkedAccounts(),
                profile.getMenu(),
                profile.getAccount().getId()
                );
    }
}
