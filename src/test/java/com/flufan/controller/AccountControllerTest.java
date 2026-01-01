package com.flufan.controller;

import com.flufan.dto.*;
import com.flufan.entity.Account;
import com.flufan.mapper.AccountMapper;
import com.flufan.service.AccountService;
import com.flufan.service.FileStorageService;
import com.flufan.service.MailSenderService;
import com.flufan.service.VerificationTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountControllerTest {

    @Mock private AccountService accountService;
    @Mock private VerificationTokenService tokenService;
    @Mock private MailSenderService mailSender;
    @Mock private AccountMapper accountMapper;
    @Mock private FileStorageService fileStorageService;

    @InjectMocks private AccountController controller;


    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        Field field = AccountController.class.getDeclaredField("baseUrl");
        field.setAccessible(true);
        field.set(controller, "http://localhost:8080");
    }

    @Test
    void register_shouldReturnOk() throws Exception {
        RegisterDto dto = new RegisterDto();
        dto.setEmail("test@test.com");
        dto.setUsername("user");

        when(accountService.saveAccount(dto)).thenReturn(new Account());
        when(tokenService.generateToken(eq("test@test.com"), any())).thenReturn("token");

        ResponseEntity<String> response = controller.register(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Registration successful. Verification email sent.", response.getBody());

        verify(mailSender, times(1))
                .sendVerificationEmail(eq("test@test.com"), eq("user"), eq("token"));
    }

    @Test
    void getAuthenticatedAccount_shouldReturnAccountDto() {
        Account account = new Account();
        AccountDto dto = new AccountDto();
        when(accountService.getAuthenticatedAccount()).thenReturn(account);
        when(accountMapper.toAccountDto(account)).thenReturn(dto);

        ResponseEntity<AccountDto> response = controller.getAuthenticatedAccount();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void getAuthenticatedAccount_whenNull_shouldReturnUnauthorized() {
        when(accountService.getAuthenticatedAccount()).thenReturn(null);

        ResponseEntity<AccountDto> response = controller.getAuthenticatedAccount();

        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void updateUsername_success() {
        UpdateUsernameDto request = new UpdateUsernameDto("newName");

        doNothing().when(accountService).updateUsername("newName");

        ResponseEntity<String> response = controller.updateUsername(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Login data updated successfully.", response.getBody());

        verify(accountService).updateUsername("newName");
        verifyNoMoreInteractions(accountService);
    }

    @Test
    void updateUsername_failure() {
        doThrow(new RuntimeException("error"))
                .when(accountService).updateUsername(anyString());

        UpdateUsernameDto request = new UpdateUsernameDto("newName");

        ResponseEntity<String> response = controller.updateUsername(request);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().startsWith("Failed to update login data"));
        assertTrue(response.getBody().contains("error"));
    }

    @Test
    void updatePassword_success() {
        ChangePasswordDto req = new ChangePasswordDto();
        req.setOldPassword("old");
        req.setNewPassword("new");

        ResponseEntity<String> response = controller.updatePassword(req);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void updatePassword_failure() {
        ChangePasswordDto req = new ChangePasswordDto();
        req.setOldPassword("old");
        req.setNewPassword("new");
        doThrow(new RuntimeException("fail")).when(accountService).updatePassword(anyString(), anyString());

        ResponseEntity<String> response = controller.updatePassword(req);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("fail"));
    }

    @Test
    void removePfp_deleted() throws Exception {
        Account account = new Account();
        account.setId(1L);
        when(accountService.getAuthenticatedAccount()).thenReturn(account);
        when(fileStorageService.delete("profile-pictures", "1_pfp")).thenReturn(true);

        ResponseEntity<Void> response = controller.removePfp();
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void removePfp_notFound() throws Exception {
        Account account = new Account();
        account.setId(1L);
        when(accountService.getAuthenticatedAccount()).thenReturn(account);
        when(fileStorageService.delete("profile-pictures", "1_pfp")).thenReturn(false);

        ResponseEntity<Void> response = controller.removePfp();
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void regenerateToken_success() throws Exception {
        Account account = new Account();
        account.setEmail("a@a.com");
        account.setUsername("user");
        account.setVerifiedEmail(false);

        when(accountService.getAuthenticatedAccount()).thenReturn(account);
        when(tokenService.generateToken("a@a.com", account)).thenReturn("token");

        ResponseEntity<String> response = controller.regenerateToken();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Verification email sent", response.getBody());
        verify(mailSender, times(1))
                .sendVerificationEmail(eq("a@a.com"), eq("user"), contains("token"));
    }

    @Test
    void regenerateToken_unauthorized() throws Exception {
        when(accountService.getAuthenticatedAccount()).thenReturn(null);

        ResponseEntity<String> response = controller.regenerateToken();

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Unauthorized", response.getBody());
        verifyNoInteractions(mailSender, tokenService);
    }

    @Test
    void regenerateToken_alreadyVerified() throws Exception {
        Account account = new Account();
        account.setEmail("a@a.com");
        account.setUsername("user");
        account.setVerifiedEmail(true);

        when(accountService.getAuthenticatedAccount()).thenReturn(account);

        ResponseEntity<String> response = controller.regenerateToken();

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Email already verified", response.getBody());
        verifyNoInteractions(mailSender, tokenService);
    }
}
