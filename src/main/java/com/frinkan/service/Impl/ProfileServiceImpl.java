package com.frinkan.service.Impl;

import com.frinkan.dto.ProfileDto;
import com.frinkan.entity.Account;
import com.frinkan.entity.Profile;
import com.frinkan.repo.AccountRepo;
import com.frinkan.repo.ProfileRepo;
import com.frinkan.service.ProfileService;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepo profileRepo;
    private final AccountRepo accountRepo;

    public ProfileServiceImpl(ProfileRepo profileRepo, AccountRepo accountRepo) {
        this.profileRepo = profileRepo;
        this.accountRepo = accountRepo;
    }

    @Override
    public void createProfile(ProfileDto profileDto) {
        if (profileRepo.findByNick(profileDto.getNick()).isPresent()) {
            throw new RuntimeException("Profile with this nickname already exists");
        }

        Account account = accountRepo.findById(profileDto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account does not exist"));

        Profile profile = new Profile();
        profile.setNick(profileDto.getNick());
        profile.setVerified(false); // Nowy profil nie jest zweryfikowany
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
    }

    @Override
    public void removeProfile(String nick) {
        Profile profile = profileRepo.findByNick(nick)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        profileRepo.delete(profile);
    }

    @Override
    public void editProfile(ProfileDto profileDto) {
        Profile profile = profileRepo.findByNick(profileDto.getNick())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

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
