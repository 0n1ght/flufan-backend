package com.flufan.mapper;

import com.flufan.dto.ProfileDto;
import com.flufan.dto.ProfileResDto;
import com.flufan.entity.Profile;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {

    public Profile toProfile(ProfileDto profileDto) {
        Profile profile = new Profile();
        profile.setTitle(profileDto.getTitle());
        profile.setVerified(false);
        profile.setActive(profileDto.getActive());
        profile.setNick(profileDto.getNick());
        profile.setFirstName(profileDto.getFirstName());
        profile.setLastName(profileDto.getLastName());
        profile.setInteractionCounter(0);
        profile.setRating(0.0);
        profile.setRespondTime(profileDto.getRespondTime());
        profile.setMessagePrice(profileDto.getMessagePrice());
        profile.setCallPrice(profileDto.getCallPrice());
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
                profile.getLinkedAccounts(),
                profile.getMenu(),
                profile.getAccount().getId()
                );
    }
}
