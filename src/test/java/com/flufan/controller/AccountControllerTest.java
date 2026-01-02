package com.flufan.controller;

import com.flufan.dto.*;
import com.flufan.entity.Account;
import com.flufan.mapper.AccountMapper;
import com.flufan.service.AccountService;
import com.flufan.service.FileStorageService;
import com.flufan.service.MailSenderService;
import com.flufan.service.VerificationTokenService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountService accountService;

    @Mock
    private VerificationTokenService tokenService;

    @Mock
    private MailSenderService mailSender;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_success() throws Exception {
        RegisterDto dto = new RegisterDto();
        dto.setEmail("test@example.com");
        dto.setUsername("user1");
        dto.setPassword("pwd");

        Account account = new Account();
        account.setEmail("test@example.com");
        account.setUsername("user1");

        when(accountService.saveAccount(dto)).thenReturn(account);
        when(tokenService.generateToken(dto.getEmail(), account)).thenReturn("token123");

        ResponseEntity<String> resp = accountController.register(dto);

        assertEquals(200, resp.getStatusCodeValue());
        assertTrue(resp.getBody().contains("Registration successful"));
        verify(mailSender).sendVerificationEmail("test@example.com", "user1", "token123");
    }

    @Test
    void register_emailExists_returnsBadRequest() throws Exception {
        RegisterDto dto = new RegisterDto();
        dto.setEmail("test@example.com");
        dto.setUsername("user1");

        when(accountService.saveAccount(dto)).thenThrow(new IllegalArgumentException("Email is already in use"));

        ResponseEntity<String> resp = accountController.register(dto);
        assertEquals(400, resp.getStatusCodeValue());
        assertTrue(resp.getBody().contains("Email is already in use"));
    }

    @Test
    void register_messagingException_returnsInternalServerError() throws Exception {
        RegisterDto dto = new RegisterDto();
        dto.setEmail("test@example.com");
        dto.setUsername("user1");

        Account account = new Account();
        when(accountService.saveAccount(dto)).thenReturn(account);
        when(tokenService.generateToken(dto.getEmail(), account)).thenReturn("token123");
        doThrow(MessagingException.class).when(mailSender).sendVerificationEmail(anyString(), anyString(), anyString());

        ResponseEntity<String> resp = accountController.register(dto);
        assertEquals(500, resp.getStatusCodeValue());
        assertTrue(resp.getBody().contains("Failed to send verification email"));
    }

    @Test
    void getAuthenticatedAccount_success() {
        Account account = new Account();
        AccountDto accountDto = new AccountDto();
        when(accountService.getAuthenticatedAccount()).thenReturn(account);
        when(accountMapper.toAccountDto(account)).thenReturn(accountDto);

        ResponseEntity<AccountDto> resp = accountController.getAuthenticatedAccount();
        assertEquals(200, resp.getStatusCodeValue());
        assertEquals(accountDto, resp.getBody());
    }

    @Test
    void getAuthenticatedAccount_null_returnsUnauthorized() {
        when(accountService.getAuthenticatedAccount()).thenReturn(null);
        ResponseEntity<AccountDto> resp = accountController.getAuthenticatedAccount();
        assertEquals(401, resp.getStatusCodeValue());
    }

    @Test
    void updateUsername_success() {
        UpdateUsernameDto dto = new UpdateUsernameDto("newUser");
        doNothing().when(accountService).updateUsername("newUser");

        ResponseEntity<String> resp = accountController.updateUsername(dto);
        assertEquals(200, resp.getStatusCodeValue());
        assertTrue(resp.getBody().contains("Login data updated successfully"));
    }

    @Test
    void updateUsername_failure() {
        UpdateUsernameDto dto = new UpdateUsernameDto("newUser");
        doThrow(new IllegalArgumentException("Username is taken")).when(accountService).updateUsername("newUser");

        ResponseEntity<String> resp = accountController.updateUsername(dto);
        assertEquals(400, resp.getStatusCodeValue());
        assertTrue(resp.getBody().contains("Failed to update login data"));
    }

    @Test
    void updatePassword_success() {
        ChangePasswordDto dto = new ChangePasswordDto("oldPwd", "newPwd");
        doNothing().when(accountService).updatePassword("oldPwd", "newPwd");

        ResponseEntity<String> resp = accountController.updatePassword(dto);
        assertEquals(200, resp.getStatusCodeValue());
    }

    @Test
    void updatePassword_failure() {
        ChangePasswordDto dto = new ChangePasswordDto("oldPwd", "newPwd");
        doThrow(new IllegalArgumentException("Incorrect password")).when(accountService)
                .updatePassword("oldPwd", "newPwd");

        ResponseEntity<String> resp = accountController.updatePassword(dto);
        assertEquals(400, resp.getStatusCodeValue());
        assertTrue(resp.getBody().contains("Failed to update login data"));
    }

    @Test
    void updateEmail_success() throws Exception {
        ChangeEmailDto dto = new ChangeEmailDto("pwd", "new@example.com");
        Account account = new Account();
        account.setUsername("user1");

        when(accountService.getAuthenticatedAccount()).thenReturn(account);
        doNothing().when(accountService).verifyEmailUpdateRequest(dto.password(), dto.newEmail());
        when(tokenService.generateToken(dto.newEmail(), account)).thenReturn("token123");

        ResponseEntity<String> resp = accountController.updateEmail(dto);
        assertEquals(200, resp.getStatusCodeValue());
        verify(mailSender).sendVerificationEmail(dto.newEmail(), "user1", "token123");
    }

    @Test
    void updateEmail_failure() throws Exception {
        ChangeEmailDto dto = new ChangeEmailDto("pwd", "new@example.com");
        doThrow(new IllegalArgumentException("Email is in use")).when(accountService)
                .verifyEmailUpdateRequest(dto.password(), dto.newEmail());

        ResponseEntity<String> resp = accountController.updateEmail(dto);
        assertEquals(400, resp.getStatusCodeValue());
        assertTrue(resp.getBody().contains("Failed to update login data"));
    }

    @Test
    void updatePfp_success() throws Exception {
        BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", os);
        byte[] imageBytes = os.toByteArray();

        MultipartFile file = new MockMultipartFile("file", "img.png", "image/png", imageBytes);

        Account account = new Account();
        account.setId(1L);

        when(accountService.getAuthenticatedAccount()).thenReturn(account);
        when(fileStorageService.save(file, "1_pfp", "profile-pictures")).thenReturn("1_pfp.png");

        ResponseEntity<String> resp = accountController.updatePfp(file);

        assertEquals(200, resp.getStatusCodeValue());
        assertEquals("1_pfp.png", resp.getBody());
    }

    @Test
    void removePfp_success() throws Exception {
        Account account = new Account();
        account.setId(1L);

        when(accountService.getAuthenticatedAccount()).thenReturn(account);
        when(fileStorageService.delete("profile-pictures", "1_pfp")).thenReturn(true);

        ResponseEntity<Void> resp = accountController.removePfp();
        assertEquals(200, resp.getStatusCodeValue());
    }

}
