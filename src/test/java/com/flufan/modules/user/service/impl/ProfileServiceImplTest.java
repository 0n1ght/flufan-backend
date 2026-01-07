package com.flufan.modules.user.service.impl;

import com.flufan.modules.user.dto.ProfileDto;
import com.flufan.modules.user.dto.ProfileResDto;
import com.flufan.modules.user.entity.Account;
import com.flufan.modules.user.entity.Profile;
import com.flufan.common.exception.ProfileNotFoundException;
import com.flufan.modules.user.mapper.ProfileMapper;
import com.flufan.modules.user.repo.ProfileRepo;
import com.flufan.modules.user.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProfile_shouldCreateProfile_whenNoExistingProfile() {
        Account account = new Account();
        when(accountService.getAuthenticatedAccount()).thenReturn(account);

        ProfileDto dto = new ProfileDto();
        Profile profile = new Profile();
        when(profileMapper.toProfile(dto)).thenReturn(profile);

        profileService.createProfile(dto);

        assertEquals(profile, account.getProfile());
        verify(accountService).updateAccount(account);
    }

    @Test
    void createProfile_shouldThrowException_whenProfileAlreadyExists() {
        Account account = new Account();
        account.setProfile(new Profile());
        when(accountService.getAuthenticatedAccount()).thenReturn(account);

        ProfileDto dto = new ProfileDto();

        RuntimeException ex = assertThrows(RuntimeException.class, () -> profileService.createProfile(dto));
        assertEquals("Your profile is already created", ex.getMessage());
    }

    @Test
    void removeProfile_shouldDeleteProfile() {
        Account account = new Account();
        Profile profile = new Profile();
        account.setProfile(profile);
        when(accountService.getAuthenticatedAccount()).thenReturn(account);

        String password = "secret";
        doNothing().when(accountService).authenticatePassword(password);

        profileService.removeProfile(password);

        verify(profileRepo).delete(profile);
    }

    @Test
    void editProfile_shouldUpdateProfile() {
        Account account = new Account();
        Profile existingProfile = new Profile();
        account.setProfile(existingProfile);
        when(accountService.getAuthenticatedAccount()).thenReturn(account);

        ProfileDto dto = new ProfileDto();
        Profile updatedProfile = new Profile();
        when(profileMapper.updateProfileFromDto(existingProfile, dto)).thenReturn(updatedProfile);

        profileService.editProfile(dto);

        assertEquals(updatedProfile, account.getProfile());
        verify(accountService).updateAccount(account);
    }

    @Test
    void searchProfiles_shouldReturnMappedResults() {
        Profile profile1 = new Profile();
        Profile profile2 = new Profile();
        when(profileRepo.searchByNickOrName("search")).thenReturn(List.of(profile1, profile2));

        ProfileResDto res1 = new ProfileResDto();
        ProfileResDto res2 = new ProfileResDto();
        when(profileMapper.toProfileResDto(profile1)).thenReturn(res1);
        when(profileMapper.toProfileResDto(profile2)).thenReturn(res2);

        List<ProfileResDto> result = profileService.searchProfiles("search");

        assertEquals(List.of(res1, res2), result);
    }

    @Test
    void findByPublicId_shouldReturnProfile_whenFound() {
        UUID id = UUID.randomUUID();
        Profile profile = new Profile();
        when(profileRepo.findByPublicId(id)).thenReturn(Optional.of(profile));

        Profile result = profileService.findByPublicId(id);

        assertEquals(profile, result);
    }

    @Test
    void findByPublicId_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(profileRepo.findByPublicId(id)).thenReturn(Optional.empty());

        assertThrows(ProfileNotFoundException.class, () -> profileService.findByPublicId(id));
    }
}
