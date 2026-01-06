package com.flufan.modules.user.controller;

import com.flufan.modules.user.controller.AccountController;
import com.flufan.modules.user.entity.Account;
import com.flufan.common.mapper.AccountMapper;
import com.flufan.modules.user.dto.AccountDto;
import com.flufan.modules.user.dto.RegisterDto;
import com.flufan.modules.user.service.AccountService;
import com.flufan.modules.user.service.FileStorageService;
import com.flufan.modules.notification.service.MailSenderService;
import com.flufan.modules.user.service.VerificationTokenService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountControllerTest {

    @InjectMocks
    private AccountController controller;

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
    void register_success() throws MessagingException {
        RegisterDto dto = new RegisterDto();
        dto.setUsername("user");
        dto.setPassword("pass");
        dto.setEmail("user@example.com");
        Account account = new Account();
        when(accountService.saveAccount(dto)).thenReturn(account);
        when(tokenService.generateToken(dto.getEmail(), account)).thenReturn("token");

        ResponseEntity<String> response = controller.register(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Registration successful. Verification email sent.", response.getBody());
        verify(mailSender).sendVerificationEmail(dto.getEmail(), dto.getUsername(), "token");
    }

    @Test
    void register_illegalArgument() throws MessagingException {
        RegisterDto dto = new RegisterDto();
        dto.setUsername("user");
        dto.setPassword("pass");
        dto.setEmail("user@example.com");
        when(accountService.saveAccount(dto)).thenThrow(new IllegalArgumentException("Invalid data"));

        ResponseEntity<String> response = controller.register(dto);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid data", response.getBody());
    }

    @Test
    void register_messagingException() throws MessagingException {
        RegisterDto dto = new RegisterDto();
        dto.setUsername("user");
        dto.setPassword("pass");
        dto.setEmail("user@example.com");
        Account account = new Account();
        when(accountService.saveAccount(dto)).thenReturn(account);
        when(tokenService.generateToken(dto.getEmail(), account)).thenReturn("token");
        doThrow(new MessagingException("Mail failed")).when(mailSender)
                .sendVerificationEmail(dto.getEmail(), dto.getUsername(), "token");

        ResponseEntity<String> response = controller.register(dto);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Failed to send verification email", response.getBody());
    }

    @Test
    void getAuthenticatedAccount_success() {
        Account account = new Account();
        AccountDto dto = new AccountDto();
        when(accountService.getAuthenticatedAccount()).thenReturn(account);
        when(accountMapper.toAccountDto(account)).thenReturn(dto);

        ResponseEntity<AccountDto> response = controller.getAuthenticatedAccount();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void getAuthenticatedAccount_unauthorized() {
        when(accountService.getAuthenticatedAccount()).thenReturn(null);

        ResponseEntity<AccountDto> response = controller.getAuthenticatedAccount();

        assertEquals(401, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void updatePfp_success() throws IOException {
        Account account = new Account();
        account.setId(1L);
        when(accountService.getAuthenticatedAccount()).thenReturn(account);

        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.io.ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();
        javax.imageio.ImageIO.write(img, "png", os);
        byte[] imageBytes = os.toByteArray();

        MockMultipartFile file = new MockMultipartFile("file", "img.png", "image/png", imageBytes);

        when(fileStorageService.save(file, "1_pfp", "profile-pictures")).thenReturn("1_pfp.png");

        ResponseEntity<String> response = controller.updatePfp(file);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("1_pfp.png", response.getBody());
    }

    @Test
    void removePfp_deleted() throws IOException {
        Account account = new Account();
        account.setId(1L);
        when(accountService.getAuthenticatedAccount()).thenReturn(account);
        when(fileStorageService.delete("profile-pictures", "1_pfp")).thenReturn(true);

        ResponseEntity<Void> response = controller.removePfp();

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void removePfp_notFound() throws IOException {
        Account account = new Account();
        account.setId(1L);
        when(accountService.getAuthenticatedAccount()).thenReturn(account);
        when(fileStorageService.delete("profile-pictures", "1_pfp")).thenReturn(false);

        ResponseEntity<Void> response = controller.removePfp();

        assertEquals(404, response.getStatusCodeValue());
    }

}
