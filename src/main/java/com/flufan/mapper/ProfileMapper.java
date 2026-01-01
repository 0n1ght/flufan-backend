package com.flufan.mapper;

import com.flufan.dto.ProfileDto;
import com.flufan.dto.ProfileResDto;
import com.flufan.entity.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileMapper {
    private final ServiceMapper serviceMapper;

    public Profile toProfile(ProfileDto profileDto) {
        Profile profile = new Profile();
        profile.setTitle(profileDto.getTitle());
        profile.setVerified(false);
        profile.setActive(profileDto.isActive());
        profile.setNick(profileDto.getNick());
        profile.setFirstName(profileDto.getFirstName());
        profile.setLastName(profileDto.getLastName());
        profile.setInteractionCounter(0);
        profile.setRating(0.0);
        profile.setRespondTime(profileDto.getRespondTime());
        profile.setMessagePrice(profileDto.getMessagePrice());
        profile.setCallPrice(profileDto.getCallPrice());
        profile.setLinkedAccounts(profileDto.getLinkedAccounts());
        profile.setMenu(
                profileDto.getMenu().stream()
                        .map(serviceMapper::toService)
                        .toList());

        return profile;
    }

    public ProfileResDto toProfileResDto(Profile profile) {

        if (profile == null) {
            return null;
        }

        return new ProfileResDto(
                profile.getPublicId(),
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

    public Profile updateProfileFromDto(Profile profile, ProfileDto profileDto) {
        profile.setTitle(profileDto.getTitle());
        profile.setVerified(false);
        profile.setActive(profileDto.isActive());
        profile.setNick(profileDto.getNick());
        profile.setFirstName(profileDto.getFirstName());
        profile.setLastName(profileDto.getLastName());
        profile.setRespondTime(profileDto.getRespondTime());
        profile.setMessagePrice(profileDto.getMessagePrice());
        profile.setCallPrice(profileDto.getCallPrice());
        profile.setLinkedAccounts(profileDto.getLinkedAccounts());

        profile.getMenu().clear();
        profile.getMenu().addAll(
                profileDto.getMenu().stream()
                        .map(serviceMapper::toService)
                        .toList()
        );

        return profile;
    }
}
