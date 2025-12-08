package com.flufan.controller;

import com.flufan.dto.LoginDto;
import com.flufan.dto.RegisterDto;
import com.flufan.service.AccountService;
import com.flufan.service.MailSenderService;
import com.flufan.service.VerificationTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;
    private final VerificationTokenService tokenService;
    private final MailSenderService mailService;

    public AccountController(AccountService accountService,
                             VerificationTokenService tokenService,
                             MailSenderService mailService) {
        this.accountService = accountService;
        this.tokenService = tokenService;
        this.mailService = mailService;
    }

    @PostMapping(value = "/signup", consumes = "application/json")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        accountService.saveAccount(registerDto);
        String token = tokenService.generateToken(registerDto.getEmail());
        String link = buildVerificationLink(registerDto.getEmail(), token);
        sendVerificationEmail(registerDto.getEmail(), registerDto.getUsername(), link);
        return ResponseEntity.ok("Registration successful. Verification email sent.");
    }

    @PostMapping("/login/update")
    public ResponseEntity<String> updateLoginData(@RequestBody LoginDto loginDto) {
        try {
            accountService.changeLoginData(loginDto);
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

    private String buildVerificationLink(String email, String token) {
        return String.format("http://localhost:8080/email-auth/verify-loader?email=%s&token=%s", email, token);
    }

    private void sendVerificationEmail(String email, String username, String link) {
        String subject = "Account Verification";
        String message = String.format("To verify your email for account %s, please click the link below:\n%s", username, link);
        mailService.sendEmail(email, subject, message);
    }
}
