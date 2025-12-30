package com.flufan.controller;

import com.flufan.dto.LoginDto;
import com.flufan.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AccountService accountService;

    public AuthController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginDto loginDto) {
        String token = accountService.verify(loginDto);
        return Collections.singletonMap("token", token);
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        accountService.requestPasswordReset(email);
        return ResponseEntity.ok("Password change link sent to your email");
    }
}
