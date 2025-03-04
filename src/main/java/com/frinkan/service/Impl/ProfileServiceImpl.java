package com.frinkan.service.Impl;

import com.frinkan.dto.ProfileDto;
import com.frinkan.entity.Account;
import com.frinkan.entity.Profile;
import com.frinkan.repo.AccountRepo;
import com.frinkan.repo.ProfileRepo;
import com.frinkan.service.AccountService;
import com.frinkan.service.ProfileService;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepo profileRepo;
    private final AccountRepo accountRepo;
    private final AccountService authService; // Serwis do pobierania zalogowanego użytkownika

    public ProfileServiceImpl(ProfileRepo profileRepo, AccountRepo accountRepo, AccountService authService) {
        this.profileRepo = profileRepo;
        this.accountRepo = accountRepo;
        this.authService = authService;
    }

    @Override
    public void createProfile(ProfileDto profileDto) {
        // Pobieramy zalogowanego użytkownika
        Account account = authService.getAuthenticatedAccount();

        // Sprawdzamy, czy konto już ma profil
        if (account.getProfile() != null) {
            throw new RuntimeException("Masz już utworzony profil!");
        }

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
        profile.setAccount(account);

        profileRepo.save(profile);
        account.setProfile(profile); // Przypisujemy profil do konta
        accountRepo.save(account);
    }

    @Override
    public void removeProfile(String nick) {
        Account account = authService.getAuthenticatedAccount();
        Profile profile = profileRepo.findByNick(nick)
                .orElseThrow(() -> new RuntimeException("Profil nie znaleziony"));

        // Sprawdzamy, czy użytkownik chce usunąć SWÓJ profil
        if (!profile.getAccount().equals(account)) {
            throw new RuntimeException("Nie masz uprawnień do usunięcia tego profilu!");
        }

        profileRepo.delete(profile);
        account.setProfile(null);
        accountRepo.save(account);
    }

    @Override
    public void editProfile(ProfileDto profileDto) {
        Account account = authService.getAuthenticatedAccount();
        Profile profile = profileRepo.findByNick(profileDto.getNick())
                .orElseThrow(() -> new RuntimeException("Profil nie znaleziony"));

        // Sprawdzamy, czy użytkownik edytuje SWÓJ profil
        if (!profile.getAccount().equals(account)) {
            throw new RuntimeException("Nie masz uprawnień do edycji tego profilu!");
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
}
