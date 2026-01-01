package com.flufan.service.impl;

import com.flufan.dto.ProfileDto;
import com.flufan.dto.ProfileResDto;
import com.flufan.entity.Account;
import com.flufan.entity.Profile;
import com.flufan.exception.ProfileNotFoundException;
import com.flufan.mapper.ProfileMapper;
import com.flufan.repo.ProfileRepo;
import com.flufan.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileServiceImplTest {

    @Mock
    private ProfileRepo profileRepo;

    @Mock
    private AccountService accountService;

    @Mock
    private ProfileMapper profileMapper;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private Account account;
    private ProfileDto profileDto;
    private Profile profile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        account = new Account();
        profileDto = new ProfileDto();
        profile = new Profile();
    }

    @Test
    void createProfile_success() {
        when(accountService.getAuthenticatedAccount()).thenReturn(account);
        when(profileMapper.toProfile(profileDto)).thenReturn(profile);

        profileService.createProfile(profileDto);

        assertEquals(profile, account.getProfile());
        assertEquals(account, profile.getAccount());
        verify(accountService).saveAccount(account);
    }

    @Test
    void createProfile_alreadyExists_throwsException() {
        account.setProfile(new Profile());
        when(accountService.getAuthenticatedAccount()).thenReturn(account);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> profileService.createProfile(profileDto));

        assertEquals("Your profile is already created", exception.getMessage());
        verify(accountService, never()).saveAccount(any(Account.class));
    }

    @Test
    void removeProfile_success() {
        when(accountService.getAuthenticatedAccount()).thenReturn(account);
        doNothing().when(accountService).authenticatePassword("pass");
        account.setProfile(profile);

        profileService.removeProfile("pass");

        verify(accountService).authenticatePassword("pass");
        verify(profileRepo).delete(profile);
    }

    @Test
    void editProfile_success() {
        account.setProfile(profile);
        when(accountService.getAuthenticatedAccount()).thenReturn(account);
        when(profileMapper.updateProfileFromDto(profile, profileDto)).thenReturn(profile);

        profileService.editProfile(profileDto);

        assertEquals(profile, account.getProfile());
        assertEquals(account, profile.getAccount());
        verify(accountService).saveAccount(account);
    }

    @Test
    void searchProfiles_returnsMappedDtos() {
        Profile profile1 = new Profile();
        Profile profile2 = new Profile();
        ProfileResDto dto1 = new ProfileResDto();
        ProfileResDto dto2 = new ProfileResDto();

        when(profileRepo.searchByNickOrName("search")).thenReturn(List.of(profile1, profile2));
        when(profileMapper.toProfileResDto(profile1)).thenReturn(dto1);
        when(profileMapper.toProfileResDto(profile2)).thenReturn(dto2);

        List<ProfileResDto> results = profileService.searchProfiles("search");

        assertEquals(2, results.size());
        assertTrue(results.contains(dto1));
        assertTrue(results.contains(dto2));
    }

    @Test
    void findById_existingProfile_returnsProfile() {
        when(profileRepo.findById(1L)).thenReturn(Optional.of(profile));

        Profile found = profileService.findById(1L);

        assertEquals(profile, found);
    }

    @Test
    void findById_nonExistingProfile_throwsException() {
        when(profileRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProfileNotFoundException.class, () -> profileService.findById(1L));
    }
}
