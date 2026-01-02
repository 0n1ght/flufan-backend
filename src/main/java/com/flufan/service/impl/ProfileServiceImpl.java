package com.flufan.service.impl;

import com.flufan.dto.ProfileDto;
import com.flufan.dto.ProfileResDto;
import com.flufan.entity.Account;
import com.flufan.entity.Profile;
import com.flufan.exception.ProfileAlreadyExistsException;
import com.flufan.exception.ProfileNotFoundException;
import com.flufan.mapper.ProfileMapper;
import com.flufan.repo.ProfileRepo;
import com.flufan.service.AccountService;
import com.flufan.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepo profileRepo;
    private final AccountService accountService;
    private final ProfileMapper profileMapper;

    @Override
    @Transactional
    public void createProfile(ProfileDto profileDto) {
        Account account = accountService.getAuthenticatedAccount();

        if (account.getProfile() != null) {
            throw new ProfileAlreadyExistsException();
        }

        Profile profile = profileMapper.toProfile(profileDto);
        profile.setAccount(account);

        account.setProfile(profile);
        accountService.updateAccount(account);
    }

    @Override
    public void removeProfile(String password) {
        accountService.authenticatePassword(password);
        Account account = accountService.getAuthenticatedAccount();
        profileRepo.delete(account.getProfile());
    }

    @Override
    public void editProfile(ProfileDto profileDto) {
        Account account = accountService.getAuthenticatedAccount();
        Profile profile = profileMapper.updateProfileFromDto(account.getProfile(), profileDto);

        profile.setAccount(account);
        account.setProfile(profile);
        accountService.updateAccount(account);
    }

    @Override
    public List<ProfileResDto> searchProfiles(String searchVal) {
        return profileRepo.searchByNickOrName(searchVal).stream()
                .map(profileMapper::toProfileResDto).toList();
    }

    @Override
    public Profile findByPublicId(UUID publicProfileId) {
        Optional<Profile> profile = profileRepo.findByPublicId(publicProfileId);
        if (profile.isEmpty()) {
            throw new ProfileNotFoundException("Profile not found");
        }
        return profile.get();
    }
}
