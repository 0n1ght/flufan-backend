package com.flufan.service.impl;

import com.flufan.dto.ProfileDto;
import com.flufan.dto.ProfileResDto;
import com.flufan.entity.Account;
import com.flufan.entity.Profile;
import com.flufan.exception.ProfileNotFoundException;
import com.flufan.mapper.ProfileMapper;
import com.flufan.repo.AccountRepo;
import com.flufan.repo.ProfileRepo;
import com.flufan.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileServiceImplTest {
    @Mock
    private ProfileRepo profileRepo;
    @Mock
    private AccountRepo accountRepo;
    @Mock
    private AccountService authService;
    @Mock
    private ProfileMapper profileMapper;

    @InjectMocks
    private ProfileServiceImpl profileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProfile_Success() {
        ProfileDto dto = new ProfileDto();
        Account account = new Account();
        when(authService.getAuthenticatedAccount()).thenReturn(account);

        Profile profile = new Profile();
        when(profileMapper.toProfile(dto)).thenReturn(profile);

        profileService.createProfile(dto);

        verify(profileRepo).save(profile);
        verify(accountRepo).save(account);
        assertEquals(profile, account.getProfile());
    }

    @Test
    void testCreateProfile_AlreadyExists_Throws() {
        ProfileDto dto = new ProfileDto();
        Account account = new Account();
        account.setProfile(new Profile());
        when(authService.getAuthenticatedAccount()).thenReturn(account);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> profileService.createProfile(dto));
        assertEquals("Your profile is already created", ex.getMessage());
    }

    @Test
    void testRemoveProfile_Success() {
        Account account = new Account();
        Profile profile = new Profile();
        account.setProfile(profile);
        when(authService.getAuthenticatedAccount()).thenReturn(account);

        profileService.removeProfile("");

        verify(profileRepo).delete(profile);
        verify(accountRepo).save(account);
        assertNull(account.getProfile());
    }

    @Test
    void testSearchProfiles() {
        Profile profile = new Profile();
        ProfileResDto resDto = new ProfileResDto();

        when(profileRepo.searchByNickOrName("search")).thenReturn(List.of(profile));
        when(profileMapper.toProfileResDto(profile)).thenReturn(resDto);

        List<ProfileResDto> results = profileService.searchProfiles("search");
        assertEquals(1, results.size());
        assertEquals(resDto, results.get(0));
    }

    @Test
    void testFindById_Success() {
        Profile profile = new Profile();
        when(profileRepo.findById(1L)).thenReturn(Optional.of(profile));

        Profile result = profileService.findById(1L);
        assertEquals(profile, result);
    }

    @Test
    void testFindById_NotFound_Throws() {
        when(profileRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProfileNotFoundException.class, () -> profileService.findById(1L));
    }
}
