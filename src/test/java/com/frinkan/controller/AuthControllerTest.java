package com.frinkan.controller;

import com.frinkan.dto.LoginDto;
import com.frinkan.service.AccountService;
import com.frinkan.service.PasswordResetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {
    @Mock
    private AccountService accountService;
    @Mock
    private PasswordResetService passwordResetService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin_Success() {
        LoginDto dto = new LoginDto();
        dto.setEmail("test@test.com");
        dto.setPassword("pwd");

        when(accountService.verify(dto)).thenReturn("JWT-TOKEN");

        String token = authController.login(dto);

        assertEquals("JWT-TOKEN", token);
        verify(accountService).verify(dto);
    }

    @Test
    void testForgotPassword_Success() {
        String email = "test@test.com";

        ResponseEntity<String> response = authController.forgotPassword(email);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Reset link sent"));
        verify(passwordResetService).requestPasswordReset(email);
    }

    @Test
    void testResetPassword_Success() {
        String token = "TOKEN";
        String newPassword = "newPwd";

        ResponseEntity<String> response = authController.resetPassword(token, newPassword);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Password successfully reset"));
        verify(passwordResetService).resetPassword(token, newPassword);
    }
}
