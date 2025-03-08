package com.frinkan.service.Impl;

import com.frinkan.dto.ProfileDto;
import com.frinkan.dto.ProfileResDto;
import com.frinkan.entity.Account;
import com.frinkan.entity.Profile;
import com.frinkan.mapper.ProfileMapper;
import com.frinkan.repo.AccountRepo;
import com.frinkan.repo.ProfileRepo;
import com.frinkan.service.AccountService;
import com.frinkan.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepo profileRepo;
    private final AccountRepo accountRepo;
    private final AccountService authService;
    private final ProfileMapper profileMapper;

    @Autowired
    public ProfileServiceImpl(ProfileRepo profileRepo, AccountRepo accountRepo,
                              AccountService authService, ProfileMapper profileMapper) {
        this.profileRepo = profileRepo;
        this.accountRepo = accountRepo;
        this.authService = authService;
        this.profileMapper = profileMapper;
    }

    @Override
    public void createProfile(ProfileDto profileDto) {
        // Pobieramy zalogowanego użytkownika
        Account account = authService.getAuthenticatedAccount();

        // Sprawdzamy, czy konto już ma profil
        if (account.getProfile() != null) {
            throw new RuntimeException("Your profile is already created");
        }

        profileDto.setAccountId(account.getId());
        Profile profile = profileMapper.toProfile(profileDto);
//        profile.setAccount(account);

        profileRepo.save(profile);
        account.setProfile(profile);
        accountRepo.save(account);
    }

    @Override
    public void removeProfile(String nick) {
        Account account = authService.getAuthenticatedAccount();
        Profile profile = profileRepo.findByNick(nick)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        // Sprawdzamy, czy użytkownik chce usunąć SWÓJ profil
        if (!profile.getAccount().equals(account)) {
            throw new RuntimeException("You can not delete this profile");
        }

        profileRepo.delete(profile);
        account.setProfile(null);
        accountRepo.save(account);
    }

    @Override
    public void editProfile(ProfileDto profileDto) {
        Account account = authService.getAuthenticatedAccount();
        Profile profile = profileRepo.findByNick(profileDto.getNick())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        if (!profile.getAccount().equals(account)) {
            throw new RuntimeException("You can not edit this profile");
        }

        profile.setFirstName(profileDto.getFirstName());
        profile.setLastName(profileDto.getLastName());
        profile.setRespondTime(profileDto.getRespondTime());
        profile.setMessagePrice(profileDto.getMessagePrice());
        profile.setCallPrice(profileDto.getCallPrice());
        profile.setProfilePicturePath(profileDto.getProfilePicturePath());
        profile.setLinkedAccounts(profileDto.getLinkedAccounts());

        profileRepo.save(profile);
    }

    @Override
    public List<ProfileResDto> searchProfiles(String searchVal) {
        return profileRepo.searchByNickOrName(searchVal).stream()
                .map(profileMapper::toProfileResDto).toList();
    }
}
