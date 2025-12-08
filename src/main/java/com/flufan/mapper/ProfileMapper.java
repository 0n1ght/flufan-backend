package com.flufan.mapper;

import com.flufan.dto.ProfileDto;
import com.flufan.dto.ProfileResDto;
import com.flufan.entity.Profile;
import com.flufan.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {
    @Autowired
    private AccountService accountService;

    public Profile toProfile(ProfileDto profileDto) {
        Profile profile = new Profile();
        profile.setNick(profileDto.getNick());
        profile.setTitle(profileDto.getTitle());
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
        profile.setMenu(profileDto.getMenu());

        return profile;
    }

    public ProfileResDto toProfileResDto(Profile profile) {

        if (profile == null) {
            return null;
        }

        return new ProfileResDto(
                profile.getId(),
                profile.getNick(),
                profile.getTitle(),
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
