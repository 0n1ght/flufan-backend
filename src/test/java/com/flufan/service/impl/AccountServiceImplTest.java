package com.flufan.service.impl;

import com.flufan.dto.LoginDto;
import com.flufan.dto.RegisterDto;
import com.flufan.entity.Account;
import com.flufan.entity.PasswordResetToken;
import com.flufan.repo.AccountRepo;
import com.flufan.repo.PasswordResetTokenRepo;
import com.flufan.repo.SuspendedAccountRepo;
import com.flufan.service.JWTService;
import com.flufan.service.MailSenderService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private AccountRepo accountRepo;

    @Mock
    private SuspendedAccountRepo suspendedRepo;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JWTService jwtService;

    @Mock
    private MailSenderService mailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordResetTokenRepo tokenRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void verify_validLogin_shouldReturnTokenAndAccount() {
        LoginDto dto = new LoginDto();
        dto.setEmail("email@test.com");
        dto.setPassword("pwd");

        Account account = new Account("user", "email@test.com", "pwd");

        when(accountRepo.findByEmailIgnoreCase("email@test.com")).thenReturn(Optional.of(account));
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(authManager.authenticate(any())).thenReturn(auth);
        when(jwtService.generateToken("email@test.com")).thenReturn("jwtToken");

        Map<String, Object> result = accountService.verify(dto);

        assertEquals("jwtToken", result.get("token"));
        assertEquals(account, result.get("account"));

        verify(accountRepo, times(1)).findByEmailIgnoreCase("email@test.com");
        verify(authManager, times(1)).authenticate(any());
        verify(jwtService, times(1)).generateToken("email@test.com");
    }

    @Test
    void verify_invalidEmail_shouldThrow() {
        LoginDto dto = new LoginDto();
        dto.setEmail("missing@test.com");
        dto.setPassword("pwd");

        when(accountRepo.findByEmailIgnoreCase("missing@test.com")).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> accountService.verify(dto));
        assertEquals("Account with this email does not exist", ex.getMessage());
    }

    @Test
    void verify_badPassword_shouldThrow() {
        LoginDto dto = new LoginDto();
        dto.setEmail("email@test.com");
        dto.setPassword("pwd");

        Account account = new Account("user", "email@test.com", "pwd");

        when(accountRepo.findByEmailIgnoreCase("email@test.com")).thenReturn(Optional.of(account));
        when(authManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> accountService.verify(dto));
        assertEquals("Incorrect password", ex.getMessage());
    }

    @Test
    void saveAccount_validDto_shouldSave() {
        RegisterDto dto = new RegisterDto();
        dto.setUsername("user");
        dto.setEmail("email@test.com");
        dto.setPassword("pwd");

        when(accountRepo.findByEmailIgnoreCase("email@test.com")).thenReturn(Optional.empty());
        when(accountRepo.findByUsernameIgnoreCase("user")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pwd")).thenReturn("encoded");
        when(accountRepo.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        Account saved = accountService.saveAccount(dto);

        assertEquals("user", saved.getUsername());
        assertEquals("email@test.com", saved.getEmail());
        assertEquals("encoded", saved.getPassword());
    }

    @Test
    void saveAccount_duplicateEmail_shouldThrow() {
        RegisterDto dto = new RegisterDto();
        dto.setUsername("user");
        dto.setEmail("email@test.com");
        dto.setPassword("pwd");

        when(accountRepo.findByEmailIgnoreCase("email@test.com")).thenReturn(Optional.of(new Account()));

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> accountService.saveAccount(dto));
        assertEquals("Email is already in use", ex.getMessage());
    }

    @Test
    void requestPasswordReset_existingAccount_shouldSendEmail() throws MessagingException {
        Account account = new Account("user", "email@test.com", "pwd");
        when(accountRepo.findByEmailIgnoreCase("email@test.com")).thenReturn(Optional.of(account));
        when(tokenRepo.existsByToken(anyString())).thenReturn(false);

        doNothing().when(mailService).sendPasswordResetEmail(anyString(), anyString(), anyString());
        doNothing().when(tokenRepo).deleteByAccount(account);
        doAnswer(invocation -> null).when(tokenRepo).save(any(PasswordResetToken.class));

        accountService.requestPasswordReset("email@test.com");

        verify(tokenRepo, times(1)).deleteByAccount(account);
        verify(tokenRepo, times(1)).save(any(PasswordResetToken.class));
        verify(mailService, times(1)).sendPasswordResetEmail(eq("email@test.com"), eq("user"), anyString());
    }

    @Test
    void requestPasswordReset_nonExistingAccount_shouldDoNothing() throws MessagingException {
        when(accountRepo.findByEmailIgnoreCase("missing@test.com")).thenReturn(Optional.empty());

        accountService.requestPasswordReset("missing@test.com");

        verify(tokenRepo, never()).deleteByAccount(any());
        verify(tokenRepo, never()).save(any());
        verify(mailService, never()).sendPasswordResetEmail(anyString(), anyString(), anyString());
    }

    @Test
    void resetPassword_validToken_shouldUpdatePassword() {
        Account account = new Account("user", "email@test.com", "old");
        PasswordResetToken token = new PasswordResetToken();
        token.setToken("token123");
        token.setAccount(account);
        token.setExpiryDate(LocalDateTime.now().plusHours(1));

        when(tokenRepo.findByToken("token123")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("new")).thenReturn("encoded-new");

        accountService.resetPassword("token123", "new");

        assertEquals("encoded-new", account.getPassword());
        verify(accountRepo, times(1)).save(account);
        verify(tokenRepo, times(1)).delete(token);
    }

    @Test
    void resetPassword_expiredToken_shouldThrow() {
        Account account = new Account("user", "email@test.com", "old");
        PasswordResetToken token = new PasswordResetToken();
        token.setToken("token123");
        token.setAccount(account);
        token.setExpiryDate(LocalDateTime.now().minusHours(1));

        when(tokenRepo.findByToken("token123")).thenReturn(Optional.of(token));

        Exception ex = assertThrows(RuntimeException.class,
                () -> accountService.resetPassword("token123", "new"));
        assertEquals("Token expired", ex.getMessage());
    }

    @Test
    void loadOrCreateGoogleUser_existing_shouldReturn() {
        Account account = new Account("user", "email@test.com", "pwd");
        when(accountRepo.findByEmailIgnoreCase("email@test.com")).thenReturn(Optional.of(account));

        Account result = accountService.loadOrCreateGoogleUser("email@test.com");

        assertEquals(account, result);
        verify(accountRepo, never()).save(any());
    }

    @Test
    void loadOrCreateGoogleUser_new_shouldCreateAndSave() {
        when(accountRepo.findByEmailIgnoreCase("new@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-random");
        doAnswer(invocation -> invocation.getArgument(0)).when(accountRepo).save(any(Account.class));

        Account result = accountService.loadOrCreateGoogleUser("new@test.com");

        assertEquals("new", result.getUsername());
        assertEquals("new@test.com", result.getEmail());
        assertEquals(true, result.isVerifiedEmail());
        assertEquals("encoded-random", result.getPassword());

        verify(accountRepo, times(1)).save(result);
    }
}
