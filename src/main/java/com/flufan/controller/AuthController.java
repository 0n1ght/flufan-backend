package com.flufan.controller;

import com.flufan.dto.LoginDto;
import com.flufan.entity.Account;
import com.flufan.service.AccountService;
import com.flufan.service.JWTService;
import com.flufan.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AccountService accountService;
    private final RefreshTokenService refreshTokenService;
    private final JWTService jwtService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDto loginDto) {
        try {
            Map<String, Object> loginRes = accountService.verify(loginDto);
            Account account = (Account) loginRes.get("account");

            String accessToken = (String) loginRes.get("token");
            String refreshToken = refreshTokenService.issueNew(account);

            return ResponseEntity.ok(Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "An error occurred. Please try again later"));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        accountService.requestPasswordReset(email);
        return ResponseEntity.ok(Collections.singletonMap("message",
                "A password reset link has been sent to your email.")
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> req) {
        String refreshToken = req.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Account account = refreshTokenService.validateAndConsume(refreshToken);

            if (account == null) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Invalid credentials"));
            }

            return ResponseEntity.ok(Map.of(
                    "accessToken", jwtService.generateToken(account.getEmail()),
                    "refreshToken", refreshTokenService.issueNew(account)
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
