package com.frinkan.controller;

import com.frinkan.dto.LoginDto;
import com.frinkan.service.AccountService;
import com.frinkan.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/login")
    public String login(@RequestBody LoginDto loginDto) {
        return accountService.verify(loginDto);
    }

    @Autowired
    public AuthController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        passwordResetService.requestPasswordReset(email);
        return ResponseEntity.ok("Reset link sent to your email");
    }

    @PutMapping("/reset-password") // Bylo postmapping
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        passwordResetService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password successfully reset");
    }
}
