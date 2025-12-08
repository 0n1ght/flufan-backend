package com.flufan.controller;

import com.flufan.service.AccountService;
import com.flufan.service.MailSenderService;
import com.flufan.service.VerificationTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/email-auth")
public class EmailVerificationController {

    private final VerificationTokenService verificationTokenService;
    private final AccountService accountService;
    private final MailSenderService mailSender;

    public EmailVerificationController(VerificationTokenService verificationTokenService,
                                       AccountService accountService,
                                       MailSenderService mailSender) {
        this.verificationTokenService = verificationTokenService;
        this.accountService = accountService;
        this.mailSender = mailSender;
    }

    @GetMapping("/verify-loader")
    public String verifyLoaderPage() {
        return "verify-loader";
    }

    @PostMapping("/verify")
    @ResponseBody
    public ResponseEntity<?> verifyEmail(
            @RequestParam String email,
            @RequestParam String token
    ) {
        try {
            verificationTokenService.useToken(email, token);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/regenerateToken")
    @ResponseBody
    public void regenerateToken(@RequestParam String email) {

        if (!accountService.findAccountByEmail(email).isVerifiedEmail()) {

            String newToken = verificationTokenService.generateToken(email);

            String link = "http://localhost:8080/email-auth/verify-loader?email="
                    + email + "&token=" + newToken;

            mailSender.sendEmail(
                    email,
                    "Account Verification",
                    "To verify your email, please click the link:\n" + link
            );
        }
    }

    @GetMapping("/verified")
    public String verifiedPage() {
        return "verified";
    }

    @GetMapping("/expired")
    public String expiredPage() {
        return "expired";
    }
}
