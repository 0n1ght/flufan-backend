package com.flufan.service.impl;

import com.flufan.dto.LoginDto;
import com.flufan.dto.RegisterDto;
import com.flufan.entity.Account;
import com.flufan.entity.PasswordResetToken;
import com.flufan.repo.*;
import com.flufan.service.JWTService;
import com.flufan.service.MailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {

    @Mock private AccountRepo accountRepo;
    @Mock private BannedAccountRepo bannedAccountRepo;
    @Mock private SuspendedAccountRepo suspendedAccountRepo;
    @Mock private AuthenticationManager authManager;
    @Mock private JWTService jwtService;
    @Mock private MailSenderService mailService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private PasswordResetTokenRepo tokenRepo;

    @InjectMocks private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_existingUser_shouldReturnUserDetails() {
        Account account = new Account("user1", "user1@email.com", "pwd");
        account.setVerifiedEmail(true);
        when(accountRepo.findByEmailIgnoreCase("user1@email.com")).thenReturn(Optional.of(account));

        var userDetails = accountService.loadUserByUsername("user1@email.com");

        assertEquals("user1@email.com", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_VERIFIED_USER")));
    }

    @Test
    void loadUserByUsername_notFound_shouldThrow() {
        when(accountRepo.findByEmailIgnoreCase("notfound@email.com")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> accountService.loadUserByUsername("notfound@email.com"));
    }

    @Test
    void saveAccount_newAccount_shouldCreateAndReturn() {
        RegisterDto dto = new RegisterDto();
        dto.setEmail("a@b.com");
        dto.setUsername("user");
        dto.setPassword("pwd");

        when(accountRepo.findByEmailIgnoreCase("a@b.com")).thenReturn(Optional.empty());
        when(accountRepo.findByUsernameIgnoreCase("user")).thenReturn(Optional.empty());
        when(bannedAccountRepo.findByEmailIgnoreCase("a@b.com")).thenReturn(Optional.empty());
        when(suspendedAccountRepo.findByEmailIgnoreCase("a@b.com")).thenReturn(Optional.empty());
        when(suspendedAccountRepo.findByUsernameIgnoreCase("user")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pwd")).thenReturn("encodedPwd");
        when(accountRepo.save(any(Account.class))).thenAnswer(i -> i.getArguments()[0]);

        Account saved = accountService.saveAccount(dto);

        assertEquals("user", saved.getUsername());
        assertEquals("a@b.com", saved.getEmail());
        assertEquals("encodedPwd", saved.getPassword());
    }

    @Test
    void verify_validLogin_shouldReturnToken() {
        LoginDto dto = new LoginDto();
        dto.setEmail("email@test.com");
        dto.setPassword("pwd");
        Account account = new Account("user", "email@test.com", "pwd");

        when(accountRepo.findByEmailIgnoreCase("email@test.com")).thenReturn(Optional.of(account));
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtService.generateToken("email@test.com")).thenReturn("jwtToken");

        String token = accountService.verify(dto);

        assertEquals("jwtToken", token);
    }

    @Test
    void loadOrCreateGoogleUser_existingUser_shouldReturnAccount() {
        Account existing = new Account();
        existing.setEmail("a@b.com");
        when(accountRepo.findByEmailIgnoreCase("a@b.com")).thenReturn(Optional.of(existing));

        Account result = accountService.loadOrCreateGoogleUser("a@b.com");

        assertEquals(existing, result);
        verify(accountRepo, never()).save(any());
    }

    @Test
    void loadOrCreateGoogleUser_newUser_shouldCreateAndSave() {
        when(accountRepo.findByEmailIgnoreCase("new@b.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedRandom");
        when(accountRepo.save(any(Account.class))).thenAnswer(i -> i.getArguments()[0]);

        Account result = accountService.loadOrCreateGoogleUser("new@b.com");

        assertEquals("new", result.getUsername());
        assertEquals("new@b.com", result.getEmail());
        assertTrue(result.isVerifiedEmail());
    }

    @Test
    void requestPasswordReset_existingAccount_shouldCreateTokenAndSendEmail() throws Exception {
        Account account = new Account();
        account.setEmail("a@b.com");
        account.setUsername("user");
        when(accountRepo.findByEmailIgnoreCase("a@b.com")).thenReturn(Optional.of(account));
        when(tokenRepo.existsByToken(anyString())).thenReturn(false);
        doNothing().when(mailService).sendPasswordResetEmail(anyString(), anyString(), anyString());
        when(tokenRepo.save(any(PasswordResetToken.class))).thenAnswer(i -> i.getArguments()[0]);

        assertDoesNotThrow(() -> accountService.requestPasswordReset("a@b.com"));
        verify(mailService, times(1)).sendPasswordResetEmail(eq("a@b.com"), eq("user"), anyString());
    }
}
