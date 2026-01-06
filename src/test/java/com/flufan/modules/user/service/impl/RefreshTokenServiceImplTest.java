package com.flufan.modules.user.service.impl;

import com.flufan.config.util.TokenHashUtil;
import com.flufan.modules.user.entity.Account;
import com.flufan.modules.user.entity.RefreshToken;
import com.flufan.modules.user.repo.RefreshTokenRepo;
import com.flufan.modules.user.service.impl.RefreshTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefreshTokenServiceImplTest {

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    @Mock
    private RefreshTokenRepo refreshTokenRepo;

    @Mock
    private TokenHashUtil tokenHashUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void issueNew_shouldReturnTokenAndSave() {
        Account account = new Account();
        when(tokenHashUtil.hash(anyString())).thenAnswer(invocation -> "hashed");

        when(refreshTokenRepo.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String token = refreshTokenService.issueNew(account);

        assertNotNull(token);
        verify(refreshTokenRepo, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void issueNew_shouldRetryOnDataIntegrityViolation() {
        Account account = new Account();
        when(tokenHashUtil.hash(anyString())).thenReturn("hashed");
        when(refreshTokenRepo.save(any(RefreshToken.class)))
                .thenThrow(DataIntegrityViolationException.class)
                .thenAnswer(invocation -> invocation.getArgument(0));

        String token = refreshTokenService.issueNew(account);

        assertNotNull(token);
        verify(refreshTokenRepo, times(2)).save(any(RefreshToken.class));
    }

    @Test
    void validateAndConsume_validToken_shouldReturnAccount() {
        Account account = new Account();
        RefreshToken rt = new RefreshToken();
        rt.setAccount(account);
        rt.setExpirationDate(Instant.now().plusSeconds(60));
        rt.setUsed(false);

        when(tokenHashUtil.hash("token")).thenReturn("hashed");
        when(refreshTokenRepo.findByTokenHashAndUsedFalse("hashed")).thenReturn(Optional.of(rt));

        Account result = refreshTokenService.validateAndConsume("token");

        assertEquals(account, result);
        assertTrue(rt.isUsed());
        verify(refreshTokenRepo, times(1)).findByTokenHashAndUsedFalse("hashed");
    }

    @Test
    void validateAndConsume_invalidToken_shouldThrow() {
        when(tokenHashUtil.hash("token")).thenReturn("hashed");
        when(refreshTokenRepo.findByTokenHashAndUsedFalse("hashed")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> refreshTokenService.validateAndConsume("token"));
        assertEquals("Invalid refresh token", ex.getMessage());
    }

    @Test
    void validateAndConsume_expiredToken_shouldThrow() {
        Account account = new Account();
        RefreshToken rt = new RefreshToken();
        rt.setAccount(account);
        rt.setExpirationDate(Instant.now().minusSeconds(10));
        rt.setUsed(false);

        when(tokenHashUtil.hash("token")).thenReturn("hashed");
        when(refreshTokenRepo.findByTokenHashAndUsedFalse("hashed")).thenReturn(Optional.of(rt));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> refreshTokenService.validateAndConsume("token"));
        assertEquals("Refresh token expired", ex.getMessage());
        assertTrue(rt.isUsed());
    }

    @Test
    void cleanup_shouldCallRepoDelete() {
        refreshTokenService.cleanup();
        verify(refreshTokenRepo, times(1)).deleteAllByExpirationDateBefore(any());
    }
}
