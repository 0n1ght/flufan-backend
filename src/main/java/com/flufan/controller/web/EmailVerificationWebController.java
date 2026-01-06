package com.flufan.controller.web;

import com.flufan.modules.user.service.VerificationTokenService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/email-auth")
public class EmailVerificationWebController {
    private final VerificationTokenService tokenService;

    public EmailVerificationWebController(VerificationTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/verify/{token}")
    public String verifyEmail(@PathVariable String token) {
        if (token == null || token.isBlank()) {
            return "email-verified.html";
        }

        boolean verified = tokenService.useToken(token);

        if (!verified) {
            return "email-verification-expired";
        }

        return "email-verified.html";
    }
}
