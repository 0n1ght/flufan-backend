package com.flufan.service.impl;

import com.flufan.dto.ProfileDto;
import com.flufan.dto.ProfileResDto;
import com.flufan.entity.Account;
import com.flufan.entity.Profile;
import com.flufan.exception.ProfileNotFoundException;
import com.flufan.mapper.ProfileMapper;
import com.flufan.mapper.ServiceMapper;
import com.flufan.repo.AccountRepo;
import com.flufan.repo.ProfileRepo;
import com.flufan.service.AccountService;
import com.flufan.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepo profileRepo;
    private final AccountRepo accountRepo;
    private final AccountService authService;
    private final ProfileMapper profileMapper;
    private final ServiceMapper serviceMapper;

    @Override
    @Transactional
    public void createProfile(ProfileDto profileDto) {
        Account account = authService.getAuthenticatedAccount();

        if (account.getProfile() != null) {
            throw new RuntimeException("Your profile is already created");
        }

        Profile profile = profileMapper.toProfile(profileDto);
        profile.setAccount(account);
        profile.setNick(profileDto.getNick());

        profileRepo.save(profile);
        account.setProfile(profile);
        accountRepo.save(account);
    }

    @Override
    public void removeProfile() {
        Account account = authService.getAuthenticatedAccount();
        profileRepo.delete(account.getProfile());
    }

    @Override
    public void editProfile(ProfileDto profileDto) {
        Account account = authService.getAuthenticatedAccount();
        Profile profile = account.getProfile();

        profile.setTitle(profileDto.getTitle());
        profile.setFirstName(profileDto.getFirstName());
        profile.setLastName(profileDto.getLastName());
        profile.setRespondTime(profileDto.getRespondTime());
        profile.setMessagePrice(profileDto.getMessagePrice());
        profile.setCallPrice(profileDto.getCallPrice());
        profile.setLinkedAccounts(profileDto.getLinkedAccounts());
        profile.setMenu(
                profileDto.getMenu().stream()
                        .map(serviceMapper::toService)
                        .toList());

        profileRepo.save(profile);
    }

    @Override
    public List<ProfileResDto> searchProfiles(String searchVal) {
        return profileRepo.searchByNickOrName(searchVal).stream()
                .map(profileMapper::toProfileResDto).toList();
    }

    @Override
    public Profile findById(Long profileId) {
        Optional<Profile> profile = profileRepo.findById(profileId);
        if (profile.isEmpty()) {
            throw new ProfileNotFoundException("Profile not found");
        }
        return profile.get();
    }
}
