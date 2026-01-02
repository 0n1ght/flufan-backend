package com.flufan.controller;

import com.flufan.dto.*;
import com.flufan.entity.Account;
import com.flufan.service.AccountService;
import com.flufan.service.JWTService;
import com.flufan.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    void testLoginSuccess() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("user@example.com");
        loginDto.setPassword("password");
        Account account = new Account();
        Map<String, Object> serviceResponse = Map.of(
                "account", account,
                "token", "access-token"
        );

        when(accountService.verify(loginDto)).thenReturn(serviceResponse);
        when(refreshTokenService.issueNew(account)).thenReturn("refresh-token");

        ResponseEntity<Map<String, String>> response = authController.login(loginDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("access-token", response.getBody().get("accessToken"));
        assertEquals("refresh-token", response.getBody().get("refreshToken"));
    }

    @Test
    void testLoginBadRequest() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("user@example.com");
        loginDto.setPassword("wrong-password");

        when(accountService.verify(loginDto)).thenThrow(new IllegalArgumentException("Invalid credentials"));

        ResponseEntity<Map<String, String>> response = authController.login(loginDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody().get("message"));
    }

    @Test
    void testLogoutSuccess() {
        LogoutDto request = new LogoutDto("refresh-token");

        when(refreshTokenService.invalidateToken("refresh-token")).thenReturn(true);

        ResponseEntity<String> response = authController.logout(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logged out successfully", response.getBody());
    }

    @Test
    void testLogoutInvalidToken() {
        LogoutDto request = new LogoutDto("bad-token");

        when(refreshTokenService.invalidateToken("bad-token")).thenReturn(false);

        ResponseEntity<String> response = authController.logout(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid or already used refresh token", response.getBody());
    }

    @Test
    void testForgotPassword() {

        when(accountService.requestPasswordReset("user@example.com")).thenReturn("user@example.com");
    }

    @Test
    void testRefreshSuccess() {
        RefreshTokenDto request = new RefreshTokenDto("refresh-token");
        Account account = new Account();
        account.setEmail("user@example.com");

        when(refreshTokenService.validateAndConsume("refresh-token")).thenReturn(account);
        when(jwtService.generateToken("user@example.com")).thenReturn("new-access-token");
        when(refreshTokenService.issueNew(account)).thenReturn("new-refresh-token");

        ResponseEntity<Map<String, String>> response = authController.refresh(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("new-access-token", response.getBody().get("accessToken"));
        assertEquals("new-refresh-token", response.getBody().get("refreshToken"));
    }

    @Test
    void testRefreshInvalidToken() {
        RefreshTokenDto request = new RefreshTokenDto("invalid-token");

        when(refreshTokenService.validateAndConsume("invalid-token")).thenReturn(null);

        ResponseEntity<Map<String, String>> response = authController.refresh(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody().get("message"));
    }

    @Test
    void testRefreshUnauthorized() {
        RefreshTokenDto request = new RefreshTokenDto("token");

        when(refreshTokenService.validateAndConsume("token")).thenThrow(new RuntimeException());

        ResponseEntity<Map<String, String>> response = authController.refresh(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
