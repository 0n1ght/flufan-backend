package com.frinkan.service.impl;

import com.frinkan.dto.LoginDto;
import com.frinkan.dto.RegisterDto;
import com.frinkan.entity.Account;
import com.frinkan.repo.AccountRepo;
import com.frinkan.repo.BannedAccountRepo;
import com.frinkan.repo.SuspendedAccountRepo;
import com.frinkan.service.JWTService;
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

    @Mock
    private AccountRepo accountRepo;
    @Mock
    private BannedAccountRepo bannedAccountRepo;
    @Mock
    private SuspendedAccountRepo suspendedAccountRepo;
    @Mock
    private AuthenticationManager authManager;
    @Mock
    private JWTService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveAccount_EmailAlreadyExists_ThrowsException() {
        RegisterDto dto = new RegisterDto();
        dto.setEmail("test@test.com");
        dto.setUsername("user");
        dto.setPassword("pwd");

        when(accountRepo.findByEmail("test@test.com")).thenReturn(Optional.of(new Account()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> accountService.saveAccount(dto));
        assertEquals("Email is already in use", exception.getMessage());
    }

    @Test
    void testGetAuthenticatedAccount_ThrowsWhenNotFound() {
        try (var mockStatic = Mockito.mockStatic(org.springframework.security.core.context.SecurityContextHolder.class)) {
            var securityContext = mock(org.springframework.security.core.context.SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            mockStatic.when(() -> org.springframework.security.core.context.SecurityContextHolder.getContext())
                    .thenReturn(securityContext);
            when(authentication.getName()).thenReturn("unknown@test.com");

            when(accountRepo.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> accountService.getAuthenticatedAccount());
        }
    }

    @Test
    void testVerify_ReturnsTokenWhenAuthenticated() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@test.com");
        loginDto.setPassword("pwd");

        Authentication auth = mock(Authentication.class);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(true);
        when(jwtService.generateToken("test@test.com")).thenReturn("JWT-TOKEN");

        String token = accountService.verify(loginDto);
        assertEquals("JWT-TOKEN", token);
    }
}
