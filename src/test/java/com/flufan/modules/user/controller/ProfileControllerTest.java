package com.flufan.modules.user.controller;

import com.flufan.modules.user.controller.ProfileController;
import com.flufan.modules.user.dto.DeleteProfileDto;
import com.flufan.modules.user.dto.ProfileDto;
import com.flufan.modules.user.dto.ProfileResDto;
import com.flufan.modules.user.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileControllerTest {
    @Mock
    private ProfileService profileService;

    @InjectMocks
    private ProfileController profileController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProfile_Success() {
        ProfileDto dto = new ProfileDto();

        ResponseEntity<String> response = profileController.createProfile(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Profile created successfully"));
        verify(profileService).createProfile(dto);
    }

    @Test
    void testEditProfile_Success() {
        ProfileDto dto = new ProfileDto();

        ResponseEntity<String> response = profileController.editProfile(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Profile updated successfully"));
        verify(profileService).editProfile(dto);
    }

    @Test
    void testDeleteProfile_Success() {
        DeleteProfileDto request = new DeleteProfileDto("");

        ResponseEntity<String> response = profileController.deleteProfile(request);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Profile deleted successfully"));
        verify(profileService).removeProfile("");
    }

    @Test
    void testSearchProfiles_Success() {
        String searchVal = "nick";
        ProfileResDto dto1 = new ProfileResDto();
        ProfileResDto dto2 = new ProfileResDto();

        when(profileService.searchProfiles(searchVal)).thenReturn(List.of(dto1, dto2));

        List<ProfileResDto> results = profileController.searchProfiles(searchVal);

        assertEquals(2, results.size());
        verify(profileService).searchProfiles(searchVal);
    }
}
