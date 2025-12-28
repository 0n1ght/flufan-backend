package com.flufan.controller;

import com.flufan.dto.LoginDto;
import com.flufan.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {
    @Mock
    private AccountService accountService;

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

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password change link sent to your email", response.getBody());

        verify(accountService, times(1)).requestPasswordReset(email);
    }
}
