package com.flufan.modules.user.controller;

import com.flufan.modules.user.dto.ForgotPasswordDto;
import com.flufan.modules.user.dto.LoginDto;
import com.flufan.modules.user.dto.LogoutDto;
import com.flufan.modules.user.dto.RefreshTokenDto;
import com.flufan.modules.user.entity.Account;
import com.flufan.modules.user.service.AccountService;
import com.flufan.modules.user.service.JWTService;
import com.flufan.modules.user.service.RefreshTokenService;
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

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LogoutDto logoutDto) {
        String refreshToken = logoutDto.refreshToken();

        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("refreshToken is required");
        }

        boolean invalidated = refreshTokenService.invalidateToken(refreshToken);

        if (invalidated) {
            return ResponseEntity.ok("Logged out successfully");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid or already used refresh token");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto) {
        String email = accountService.requestPasswordReset(forgotPasswordDto.login());

        int prefixLength = Math.min(2, email.indexOf("@"));
        String emailSubstring = email.substring(0, prefixLength) + "..." + email.substring(email.indexOf("@") + 1);

        return ResponseEntity.ok("A password reset link has been sent to " + emailSubstring);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody RefreshTokenDto refreshTokenDto) {
        String refreshToken = refreshTokenDto.refreshToken();
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
