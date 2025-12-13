package com.flufan.controller;

import com.flufan.dto.ChangeEmailRequest;
import com.flufan.dto.ChangePasswordRequest;
import com.flufan.dto.LoginDto;
import com.flufan.dto.RegisterDto;
import com.flufan.entity.Account;
import com.flufan.service.AccountService;
import com.flufan.service.MailSenderService;
import com.flufan.service.VerificationTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
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

//    todo
//    @GetMapping("/this-account")
//    public ResponseEntity<AccountDto> getAuthenticatedAccount() {
//        Account account = accountService.getAuthenticatedAccount();
//        if (account == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//        AccountDto dto = new AccountDto(account.getUsername(), account.getEmail());
//        return ResponseEntity.ok(dto);
//    }

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

    private String buildVerificationLink(String email, String token) {
        return String.format("http://localhost:8080/email-auth/verify-loader?email=%s&token=%s", email, token);
    }

    private void sendVerificationEmail(String email, String username, String link) {
        String subject = "Account Verification";
        String message = String.format("To verify your email for account %s, please click the link below:\n%s", username, link);
        mailService.sendEmail(email, subject, message);
    }
}
