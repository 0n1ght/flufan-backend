package com.flufan.controller;

import com.flufan.dto.*;
import com.flufan.entity.Account;
import com.flufan.mapper.AccountMapper;
import com.flufan.service.AccountService;
import com.flufan.service.FileStorageService;
import com.flufan.service.MailSenderService;
import com.flufan.service.VerificationTokenService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final AccountService accountService;
    private final VerificationTokenService tokenService;
    private final MailSenderService mailSender;
    private final AccountMapper accountMapper;
    private final FileStorageService fileStorageService;

    @Value("${app.base-url}")
    private String baseUrl;

    public AccountController(AccountService accountService, VerificationTokenService tokenService, MailSenderService mailSender, AccountMapper accountMapper, FileStorageService fileStorageService) {
        this.accountService = accountService;
        this.tokenService = tokenService;
        this.mailSender = mailSender;
        this.accountMapper = accountMapper;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(value = "/signup", consumes = "application/json")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        try {
            Account savedAccount = accountService.saveAccount(registerDto);
            String token = tokenService.generateToken(registerDto.getEmail(), savedAccount);
            mailSender.sendVerificationEmail(registerDto.getEmail(), registerDto.getUsername(), token);
            return ResponseEntity.ok("Registration successful. Verification email sent.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send verification email");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred. Please try again later");
        }
    }

    @GetMapping("/this-account")
    public ResponseEntity<AccountDto> getAuthenticatedAccount() {
        Account account = accountService.getAuthenticatedAccount();
        if (account == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(accountMapper.toAccountDto(account));
    }

    @PostMapping("/update/username")
    public ResponseEntity<String> updateUsername(@RequestBody UpdateUsernameRequest usernameRequest) {
        try {
            accountService.updateUsername(usernameRequest.username());
            return ResponseEntity.ok("Login data updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Failed to update login data: " + e.getMessage());
        }
    }

    @PostMapping("/update/password")
    public ResponseEntity<String> updatePassword(@RequestBody ChangePasswordRequest req) {
        try {
            accountService.updatePassword(req.getOldPassword(), req.getNewPassword());
            return ResponseEntity.ok("Login data updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Failed to update login data: " + e.getMessage());
        }
    }

    @PostMapping("/request-email-update")
    public ResponseEntity<String> updateEmail(@RequestBody ChangeEmailRequest req) {
        try {
            accountService.verifyEmailUpdateRequest(req.getPassword(), req.getNewEmail());
            String token = tokenService.generateToken(req.getNewEmail(), accountService.getAuthenticatedAccount());
            mailSender.sendVerificationEmail(
                    req.getNewEmail(),
                    accountService.getAuthenticatedAccount().getUsername(),
                    token
            );
            return ResponseEntity.ok("Verification email has been sent.");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Failed to update login data: " + e.getMessage());
        }
    }

    @PutMapping("/update-pfp")
    public ResponseEntity<String> updatePfp(@RequestParam("file") MultipartFile file) throws IOException {
        Account account = accountService.getAuthenticatedAccount();

        validateImage(file);

        String filename = fileStorageService.save(file, account.getId() + "_pfp", "profile-pictures");
        return ResponseEntity.ok(filename);
    }

    @DeleteMapping("/remove-pfp")
    public ResponseEntity<Void> removePfp() throws IOException {
        Account account = accountService.getAuthenticatedAccount();
        boolean deleted = fileStorageService.delete("profile-pictures", account.getId() + "_pfp");
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/profile-pictures/{username}")
    public ResponseEntity<Resource> getAccountPfp(@PathVariable String username) throws IOException {

        if (!username.matches("[a-zA-Z0-9_]+")) {
            return ResponseEntity.badRequest().build();
        }

        Path path = fileStorageService.load("profile-pictures",
                accountService.findByUsername(username).getId()+ "_pfp.png");

        if (!Files.exists(path)) {
            path = fileStorageService.load("profile-pictures", "pfp.png");
            if (!Files.exists(path)) {
                return ResponseEntity.notFound().build();
            }
        }

        Resource resource = new UrlResource(path.toUri());
        String contentType = Files.probeContentType(path);

        return ResponseEntity.ok()
                .cacheControl(org.springframework.http.CacheControl.maxAge(30, java.util.concurrent.TimeUnit.DAYS))
                .contentType(MediaType.parseMediaType(contentType == null ? "application/octet-stream" : contentType))
                .body(resource);
    }

    @PostMapping("/regenerate-verification")
    public ResponseEntity<String> regenerateToken() throws MessagingException {
        Account account = accountService.getAuthenticatedAccount();
        if (account == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        if (account.isVerifiedEmail()) {
            return ResponseEntity.badRequest().body("Email already verified");
        }

        String token = tokenService.generateToken(account.getEmail(), account);

        mailSender.sendVerificationEmail(
                account.getEmail(),
                account.getUsername(),
                String.format("%s/email-auth/verify/%s", baseUrl, token)
        );

        return ResponseEntity.ok("Verification email sent");
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<String> deleteAccount(@RequestBody Map<String, String> request) {

        accountService.deleteAccount(request.get("password"));

        Account account = accountService.getAuthenticatedAccount();
        mailSender.sendAccountDelInfo(account.getEmail(), account.getUsername());

        return ResponseEntity.ok(
                "Your account has been scheduled for deletion. " +
                        "It will be permanently deleted in 30 days. " +
                        "You can log in anytime before that to reactivate it."
        );
    }

    private void validateImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("File too large");
        }

        try (var is = file.getInputStream()) {
            var img = javax.imageio.ImageIO.read(is);
            if (img == null) throw new IllegalArgumentException("File is not a valid image");
        }
    }
}
