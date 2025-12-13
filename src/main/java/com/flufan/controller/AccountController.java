package com.flufan.controller;

import com.flufan.dto.*;
import com.flufan.entity.Account;
import com.flufan.mapper.AccountMapper;
import com.flufan.service.AccountService;
import com.flufan.service.FileStorageService;
import com.flufan.service.MailSenderService;
import com.flufan.service.VerificationTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final AccountService accountService;
    private final VerificationTokenService tokenService;
    private final MailSenderService mailService;
    private final AccountMapper accountMapper;
    private final FileStorageService fileStorageService;

    public AccountController(AccountService accountService,
                             VerificationTokenService tokenService,
                             MailSenderService mailService,
                             AccountMapper accountMapper,
                             FileStorageService fileStorageService) {
        this.accountService = accountService;
        this.tokenService = tokenService;
        this.mailService = mailService;
        this.accountMapper = accountMapper;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(value = "/signup", consumes = "application/json")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        accountService.saveAccount(registerDto);
        String token = tokenService.generateToken(registerDto.getEmail());
        String link = buildVerificationLink(registerDto.getEmail(), token);
        sendVerificationEmail(registerDto.getEmail(), registerDto.getUsername(), link);
        return ResponseEntity.ok("Registration successful. Verification email sent.");
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
    public ResponseEntity<String> changeUsername(@RequestBody String newUsername) {
        try {
            accountService.changeUsername(newUsername);
            return ResponseEntity.ok("Login data updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Failed to update login data: " + e.getMessage());
        }
    }

    @PostMapping("/update/email")
    public ResponseEntity<String> changeEmail(@RequestBody ChangeEmailRequest req) {
        try {
            accountService.changeEmail(req.getPassword(), req.getNewEmail());
            return ResponseEntity.ok("Login data updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Failed to update login data: " + e.getMessage());
        }
    }

    @PostMapping("/update/password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest req) {
        try {
            accountService.changePassword(req.getOldPassword(), req.getNewPassword());
            return ResponseEntity.ok("Login data updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Failed to update login data: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAccount(@RequestBody LoginDto loginDto) {
        accountService.deleteAccount(loginDto);
        return ResponseEntity.ok("Account deleted successfully.");
    }

    @PutMapping("/update-pfp")
    public ResponseEntity<String> updatePfp(@RequestParam("file") MultipartFile file) throws IOException {
        Account account = accountService.getAuthenticatedAccount();

        validateImage(file);

        String filename = fileStorageService.save(file, account.getUsername() + "_pfp", "profile-pictures");
        return ResponseEntity.ok(filename);
    }

    @DeleteMapping("/remove-pfp")
    public ResponseEntity<Void> removePfp() throws IOException {
        Account account = accountService.getAuthenticatedAccount();
        boolean deleted = fileStorageService.delete("profile-pictures", account.getUsername() + "_pfp");
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    private String buildVerificationLink(String email, String token) {
        return String.format("http://localhost:8080/email-auth/verify-loader?email=%s&token=%s", email, token);
    }

    private void sendVerificationEmail(String email, String username, String link) {
        String subject = "Account Verification";
        String message = String.format("To verify your email for account %s, please click the link below:\n%s", username, link);
        mailService.sendEmail(email, subject, message);
    }

    private void validateImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String original = Objects.requireNonNull(file.getOriginalFilename());
        String ext = original.contains(".")
                ? original.substring(original.lastIndexOf(".") + 1).toLowerCase()
                : "";
        if (!Set.of("png", "jpg", "jpeg").contains(ext)) {
            throw new IllegalArgumentException("Invalid file type. Only png, jpg, jpeg allowed");
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
