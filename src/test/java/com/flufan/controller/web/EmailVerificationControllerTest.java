package com.flufan.controller.web;

import com.flufan.service.VerificationTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EmailVerificationControllerTest {

    private VerificationTokenService tokenService;
    private EmailVerificationController controller;

    @BeforeEach
    void setUp() {
        tokenService = mock(VerificationTokenService.class);
        controller = new EmailVerificationController(tokenService);
    }

    @Test
    void verifyEmail_tokenIsNull_shouldReturnVerifiedPage() {
        String result = controller.verifyEmail(null);
        assertEquals("email-verified.html", result);
        verifyNoInteractions(tokenService);
    }

    @Test
    void verifyEmail_tokenIsBlank_shouldReturnVerifiedPage() {
        String result = controller.verifyEmail("  ");
        assertEquals("email-verified.html", result);
        verifyNoInteractions(tokenService);
    }

    @Test
    void verifyEmail_tokenInvalid_shouldReturnExpiredPage() {
        String token = "invalidToken";
        when(tokenService.useToken(token)).thenReturn(false);

        String result = controller.verifyEmail(token);

        assertEquals("email-verification-expired", result);
        verify(tokenService, times(1)).useToken(token);
    }

    @Test
    void verifyEmail_tokenValid_shouldReturnVerifiedPage() {
        String token = "validToken";
        when(tokenService.useToken(token)).thenReturn(true);

        String result = controller.verifyEmail(token);

        assertEquals("email-verified.html", result);
        verify(tokenService, times(1)).useToken(token);
    }
}
