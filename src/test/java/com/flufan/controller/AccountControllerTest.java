package com.flufan.controller;

import com.flufan.dto.ChangeEmailRequest;
import com.flufan.dto.ChangePasswordRequest;
import com.flufan.dto.LoginDto;
import com.flufan.dto.RegisterDto;
import com.flufan.service.AccountService;
import com.flufan.service.MailSenderService;
import com.flufan.service.VerificationTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @Mock
    private VerificationTokenService tokenService;

    @Mock
    private MailSenderService mailService;

    @InjectMocks
    private AccountController accountController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister() {
        RegisterDto dto = new RegisterDto();
        dto.setEmail("test@example.com");
        dto.setUsername("user123");
        when(tokenService.generateToken(dto.getEmail())).thenReturn("token123");

        ResponseEntity<String> response = accountController.register(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Registration successful"));

        verify(accountService).saveAccount(dto);

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(mailService).sendEmail(emailCaptor.capture(), subjectCaptor.capture(), messageCaptor.capture());

        assertEquals(dto.getEmail(), emailCaptor.getValue());
        assertTrue(messageCaptor.getValue().contains("token123"));
    }

    @Test
    void testUpdateUsernameSuccess() {
        ResponseEntity<String> response = accountController.updateUsername("newUser");
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Login data updated successfully"));
        verify(accountService).updateUsername("newUser");
    }

    @Test
    void testUpdateUsernameFailure() {
        doThrow(new RuntimeException("Error")).when(accountService).updateUsername("badUser");

        ResponseEntity<String> response = accountController.updateUsername("badUser");
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Failed to update login data"));
    }

    @Test
    void testUpdateEmailSuccess() {
        ChangeEmailRequest req = new ChangeEmailRequest();
        req.setNewEmail("new@example.com");
        req.setPassword("pass123");

        ResponseEntity<String> response = accountController.updateEmail(req);
        assertEquals(200, response.getStatusCodeValue());
        verify(accountService).verifyEmailUpdateRequest("pass123", "new@example.com");
    }

    @Test
    void testUpdatePasswordSuccess() {
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setOldPassword("old");
        req.setNewPassword("new");

        ResponseEntity<String> response = accountController.updatePassword(req);
        assertEquals(200, response.getStatusCodeValue());
        verify(accountService).updatePassword("old", "new");
    }

    @Test
    void testDeleteAccount() {
        LoginDto dto = new LoginDto();
        dto.setEmail("user@example.com");
        dto.setPassword("pass");

        ResponseEntity<String> response = accountController.deleteAccount(dto);
        assertEquals(200, response.getStatusCodeValue());
        verify(accountService).deleteAccount(dto);
    }
}
