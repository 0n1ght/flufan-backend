package com.frinkan.controller;

import com.frinkan.dto.LoginDto;
import com.frinkan.dto.RegisterDto;
import com.frinkan.service.AccountService;
import com.frinkan.service.MailSenderService;
import com.frinkan.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {
    private final AccountService accountService;
    private final VerificationTokenService verificationTokenService;
    private final MailSenderService mailSender;

    public AccountController(AccountService accountService, VerificationTokenService verificationTokenService, MailSenderService mailSender) {
        this.accountService = accountService;
        this.verificationTokenService = verificationTokenService;
        this.mailSender = mailSender;
    }

    @PostMapping(value = "/req/signup", consumes = "application/json")
    public void register(@RequestBody RegisterDto registerDto) {

        accountService.saveAccount(registerDto);
        String verificationToken = verificationTokenService.generateToken(registerDto.getEmail());

        String link = "http://localhost:8080/email-auth/verify-loader?email="
                + registerDto.getEmail() + "&token=" + verificationToken;

        mailSender.sendEmail(registerDto.getEmail(),
                "Account Verification",
                "To verify your email for account " + registerDto.getUsername() + ", please click the link below:\n" + link);
    }

    @PostMapping("/update-login-data")
    public ResponseEntity<String> updateLoginData(@RequestBody LoginDto loginDto) {
        try {
            accountService.changeLoginData(loginDto);
            return ResponseEntity.ok("Login data updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to update login data: " + e.getMessage());
        }
    }

    @DeleteMapping("/req/delete-account")
    public ResponseEntity<String> deleteAccount(@RequestBody LoginDto loginDto) {
        accountService.deleteAccount(loginDto);
        return ResponseEntity.ok("Account deleted successfully");
    }
}
