package com.frinkan.controller;

import com.frinkan.dto.LoginDto;
import com.frinkan.dto.RegisterDto;
import com.frinkan.service.AccountService;
import com.frinkan.service.MailSenderService;
import com.frinkan.service.VerificationTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
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
    void testRegister_Success() {
        RegisterDto dto = new RegisterDto();
        dto.setEmail("test@test.com");
        dto.setUsername("user");

        when(tokenService.generateToken(dto.getEmail())).thenReturn("TOKEN");

        ResponseEntity<String> response = accountController.register(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Registration successful"));
        verify(accountService).saveAccount(dto);
        verify(tokenService).generateToken(dto.getEmail());
        verify(mailService).sendEmail(eq("test@test.com"), anyString(), contains("http://localhost:8080/email-auth/verify-loader"));
    }

    @Test
    void testUpdateLoginData_Success() {
        LoginDto dto = new LoginDto();
        dto.setEmail("test@test.com");
        dto.setPassword("pwd");

        ResponseEntity<String> response = accountController.updateLoginData(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Login data updated successfully"));
        verify(accountService).changeLoginData(dto);
    }

    @Test
    void testUpdateLoginData_Failure() {
        LoginDto dto = new LoginDto();
        dto.setEmail("test@test.com");

        doThrow(new RuntimeException("Error")).when(accountService).changeLoginData(dto);

        ResponseEntity<String> response = accountController.updateLoginData(dto);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Failed to update login data"));
    }

    @Test
    void testDeleteAccount_Success() {
        LoginDto dto = new LoginDto();
        dto.setEmail("test@test.com");

        ResponseEntity<String> response = accountController.deleteAccount(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Account deleted successfully"));
        verify(accountService).deleteAccount(dto);
    }
}
