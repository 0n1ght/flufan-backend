package com.flufan.service.impl;

import com.flufan.dto.LoginDto;
import com.flufan.entity.Account;
import com.flufan.entity.PasswordResetToken;
import com.flufan.exception.*;
import com.flufan.repo.AccountRepo;
import com.flufan.repo.PasswordResetTokenRepo;
import com.flufan.repo.SuspendedAccountRepo;
import com.flufan.service.JWTService;
import com.flufan.service.MailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private AccountRepo accountRepo;

    @Mock
    private SuspendedAccountRepo suspendedAccountRepo;

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

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_existingEmail_returnsUserDetails() {
        Account account = new Account();
        account.setEmail("test@example.com");
        account.setPassword("encoded");
        account.setVerifiedEmail(true);

        when(accountRepo.findByEmailIgnoreCase("test@example.com"))
                .thenReturn(Optional.of(account));

        var userDetails = accountService.loadUserByUsername("test@example.com");

        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("encoded", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_VERIFIED_USER")));
    }

    @Test
    void loadUserByUsername_notFound_throwsException() {
        when(accountRepo.findByEmailIgnoreCase("test@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> accountService.loadUserByUsername("test@example.com"));
    }

    @Test
    void verify_withEmail_success() {
        Account account = new Account();
        account.setEmail("test@example.com");
        account.setPassword("pwd");
        account.setDeletedAt(null);

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("pwd");

        when(accountRepo.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(account));
        when(authManager.authenticate(any())).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(jwtService.generateToken("test@example.com")).thenReturn("token123");

        var result = accountService.verify(loginDto);

        assertEquals(account, result.get("account"));
        assertEquals("token123", result.get("token"));
    }

    @Test
    void verify_incorrectPassword_throwsIncorrectPasswordException() {
        Account account = new Account();
        account.setEmail("test@example.com");
        account.setPassword("pwd");

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("wrong");

        when(accountRepo.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(account));
        when(authManager.authenticate(any())).thenThrow(BadCredentialsException.class);

        assertThrows(IncorrectPasswordException.class, () -> accountService.verify(loginDto));
    }

    @Test
    void verify_accountNotFound_throwsAccountNotFoundException() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");

        when(accountRepo.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.verify(loginDto));
    }

    @Test
    void findByPublicId_found() {
        Account account = new Account();
        UUID id = UUID.randomUUID();

        when(accountRepo.findByPublicId(id)).thenReturn(Optional.of(account));

        assertEquals(account, accountService.findByPublicId(id));
    }

    @Test
    void findByPublicId_notFound_throws() {
        UUID id = UUID.randomUUID();

        when(accountRepo.findByPublicId(id)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.findByPublicId(id));
    }

    @Test
    void getAuthenticatedAccount_found() {
        Account account = new Account();
        account.setEmail("test@example.com");

        when(accountRepo.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(account));

        try (var mocked = mockStatic(org.springframework.security.core.context.SecurityContextHolder.class)) {
            var context = mock(org.springframework.security.core.context.SecurityContext.class);
            when(context.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("test@example.com");
            mocked.when(org.springframework.security.core.context.SecurityContextHolder::getContext).thenReturn(context);

            Account result = accountService.getAuthenticatedAccount();
            assertEquals(account, result);
        }
    }

    @Test
    void getAuthenticatedAccount_notFound_throws() {
        when(accountRepo.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.empty());

        try (var mocked = mockStatic(org.springframework.security.core.context.SecurityContextHolder.class)) {
            var context = mock(org.springframework.security.core.context.SecurityContext.class);
            when(context.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("test@example.com");
            mocked.when(org.springframework.security.core.context.SecurityContextHolder::getContext).thenReturn(context);

            assertThrows(AuthenticatedAccountNotFoundException.class,
                    () -> accountService.getAuthenticatedAccount());
        }
    }

    @Test
    void resetPassword_success() {
        Account account = new Account();
        PasswordResetToken token = new PasswordResetToken();
        token.setAccount(account);

        when(tokenRepo.findByToken("token123")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newPwd")).thenReturn("encodedPwd");

        accountService.resetPassword("token123", "newPwd");

        verify(accountRepo).save(account);
        verify(tokenRepo).delete(token);
    }

    @Test
    void resetPassword_invalidToken_throws() {
        when(tokenRepo.findByToken("token123")).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> accountService.resetPassword("token123", "newPwd"));
    }

    @Test
    void resetPassword_expiredToken_throws() {
        Account account = new Account();
        PasswordResetToken token = new PasswordResetToken();
        token.setAccount(account);
        token.setExpiryDate(Instant.now().minusSeconds(3600));

        when(tokenRepo.findByToken("token123")).thenReturn(Optional.of(token));

        assertThrows(TokenExpiredException.class, () ->
                accountService.resetPassword("token123", "newPwd")
        );
    }


    @Test
    void authenticatePassword_correct() {
        Account account = new Account();
        account.setPassword("encoded");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@example.com");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        try (var mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(accountRepo.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(account));
            when(passwordEncoder.matches("pwd", "encoded")).thenReturn(true);

            accountService.authenticatePassword("pwd");
        }
    }

    @Test
    void authenticatePassword_incorrect_throws() {
        Account account = new Account();
        account.setPassword("encoded");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@example.com");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        try (var mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(accountRepo.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(account));
            when(passwordEncoder.matches("pwd", "encoded")).thenReturn(false);

            assertThrows(IncorrectPasswordException.class,
                    () -> accountService.authenticatePassword("pwd"));
        }
    }
}
