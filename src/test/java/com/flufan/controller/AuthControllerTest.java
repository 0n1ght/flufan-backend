package com.flufan.controller;

import com.flufan.dto.LoginDto;
import com.flufan.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

        ResponseEntity<Map<String, String>> response = authController.login(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("JWT-TOKEN", response.getBody().get("token"));

        verify(accountService).verify(dto);
    }

    @Test
    void testForgotPassword_Success() {
        String email = "test@test.com";
        Map<String, String> body = new HashMap<>();
        body.put("email", email);

        ResponseEntity<Map<String, String>> response = authController.forgotPassword(body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(
                "A password reset link has been sent to your email.",
                Objects.requireNonNull(response.getBody()).get("message")
        );

        verify(accountService, times(1)).requestPasswordReset(email);
    }
}
