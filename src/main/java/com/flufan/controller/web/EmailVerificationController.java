package com.flufan.controller.web;

import com.flufan.service.VerificationTokenService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/email-auth")
public class EmailVerificationController {
    private final VerificationTokenService tokenService;

    public EmailVerificationController(VerificationTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/verify/{token}")
    public String verifyEmail(@PathVariable String token) {
        if (token == null || token.isBlank()) {
            return "expired";
        }

        boolean verified = tokenService.useToken(token);

        if (!verified) {
            return "expired";
        }

        return "verified";
    }
}
