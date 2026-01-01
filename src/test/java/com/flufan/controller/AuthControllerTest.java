package com.flufan.controller;

import com.flufan.dto.LoginDto;
import com.flufan.entity.Account;
import com.flufan.service.AccountService;
import com.flufan.service.JWTService;
import com.flufan.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AccountService accountService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private JWTService jwtService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_Success() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@test.com");
        loginDto.setPassword("pass");

        Account account = new Account();
        account.setEmail("test@test.com");

        Map<String, Object> loginRes = new HashMap<>();
        loginRes.put("account", account);
        loginRes.put("token", "jwt-token");

        when(accountService.verify(loginDto)).thenReturn(loginRes);
        when(refreshTokenService.issueNew(account)).thenReturn("refresh-token");

        ResponseEntity<Map<String, String>> response = authController.login(loginDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt-token", response.getBody().get("accessToken"));
        assertEquals("refresh-token", response.getBody().get("refreshToken"));

        verify(accountService, times(1)).verify(loginDto);
        verify(refreshTokenService, times(1)).issueNew(account);
    }

    @Test
    void login_InvalidCredentials() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@test.com");
        loginDto.setPassword("wrong");

        when(accountService.verify(loginDto))
                .thenThrow(new IllegalArgumentException("Incorrect password"));

        ResponseEntity<Map<String, String>> response = authController.login(loginDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Incorrect password", response.getBody().get("message"));
    }

    @Test
    void login_InternalError() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@test.com");
        loginDto.setPassword("pass");

        when(accountService.verify(loginDto)).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<Map<String, String>> response = authController.login(loginDto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred. Please try again later", response.getBody().get("message"));
    }

    @Test
    void forgotPassword_Success() {
        Map<String, String> body = new HashMap<>();
        body.put("email", "test@test.com");

        doNothing().when(accountService).requestPasswordReset("test@test.com");

        ResponseEntity<Map<String, String>> response = authController.forgotPassword(body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("A password reset link has been sent to your email.",
                response.getBody().get("message"));

        verify(accountService, times(1)).requestPasswordReset("test@test.com");
    }

    @Test
    void refresh_Success() {
        Account account = new Account();
        account.setEmail("test@test.com");

        Map<String, String> req = new HashMap<>();
        req.put("refreshToken", "old-refresh-token");

        when(refreshTokenService.validateAndConsume("old-refresh-token")).thenReturn(account);
        when(jwtService.generateToken(account.getEmail())).thenReturn("new-access-token");
        when(refreshTokenService.issueNew(account)).thenReturn("new-refresh-token");

        ResponseEntity<Map<String, String>> response = authController.refresh(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("new-access-token", response.getBody().get("accessToken"));
        assertEquals("new-refresh-token", response.getBody().get("refreshToken"));
    }

    @Test
    void refresh_MissingToken() {
        Map<String, String> req = new HashMap<>(); // no refreshToken

        ResponseEntity<Map<String, String>> response = authController.refresh(req);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void refresh_InvalidToken() {
        Map<String, String> req = new HashMap<>();
        req.put("refreshToken", "bad-token");

        when(refreshTokenService.validateAndConsume("bad-token"))
                .thenThrow(new RuntimeException("invalid"));

        ResponseEntity<Map<String, String>> response = authController.refresh(req);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
