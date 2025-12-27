package com.flufan.controller;

import com.flufan.dto.LoginDto;
import com.flufan.service.AccountService;
import com.flufan.service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AccountService accountService;
    private final PasswordResetService passwordResetService;

    public AuthController(AccountService accountService, PasswordResetService passwordResetService) {
        this.accountService = accountService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginDto loginDto) {
        return accountService.verify(loginDto);
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        passwordResetService.requestPasswordReset(email);
        return ResponseEntity.ok("Password change link sent to your email");
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody String token, @RequestBody String newPassword) {
        passwordResetService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password has been changed");
    }
}
