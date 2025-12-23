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
    void register_shouldReturnOk() {
        RegisterDto dto = new RegisterDto();
        dto.setEmail("test@test.com");
        dto.setUsername("user");
        when(accountService.saveAccount(dto)).thenReturn(new Account());
        when(tokenService.generateToken(anyString(), any())).thenReturn("token");

        ResponseEntity<String> response = controller.register(dto);

        assertEquals(200, response.getStatusCodeValue());
        verify(mailSender).sendEmail(eq("test@test.com"), anyString(), contains("token"));
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
        ResponseEntity<String> response = controller.updateUsername("newName");
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void updateUsername_failure() {
        doThrow(new RuntimeException("error")).when(accountService).updateUsername(anyString());
        ResponseEntity<String> response = controller.updateUsername("newName");
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("error"));
    }

    @Test
    void updatePassword_success() {
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setOldPassword("old");
        req.setNewPassword("new");

        ResponseEntity<String> response = controller.updatePassword(req);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void updatePassword_failure() {
        ChangePasswordRequest req = new ChangePasswordRequest();
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
    void verifyEmail_invalidToken() {
        ResponseEntity<String> response = controller.verifyEmail("");
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void verifyEmail_notVerified() {
        when(tokenService.useToken("token")).thenReturn(false);
        ResponseEntity<String> response = controller.verifyEmail("token");
        assertEquals(410, response.getStatusCodeValue());
    }

    @Test
    void verifyEmail_success() {
        when(tokenService.useToken("token")).thenReturn(true);
        ResponseEntity<String> response = controller.verifyEmail("token");
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void regenerateToken_success() {
        Account account = new Account();
        account.setEmail("a@a.com");
        account.setUsername("user");
        account.setVerifiedEmail(false);
        when(accountService.getAuthenticatedAccount()).thenReturn(account);
        when(tokenService.generateToken("a@a.com", account)).thenReturn("token");

        ResponseEntity<String> response = controller.regenerateToken();
        assertEquals(200, response.getStatusCodeValue());
        verify(mailSender).sendEmail(eq("a@a.com"), anyString(), contains("token"));
    }

    @Test
    void regenerateToken_unauthorized() {
        when(accountService.getAuthenticatedAccount()).thenReturn(null);
        ResponseEntity<String> response = controller.regenerateToken();
        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void regenerateToken_alreadyVerified() {
        Account account = new Account();
        account.setVerifiedEmail(true);
        when(accountService.getAuthenticatedAccount()).thenReturn(account);

        ResponseEntity<String> response = controller.regenerateToken();
        assertEquals(400, response.getStatusCodeValue());
    }
}
