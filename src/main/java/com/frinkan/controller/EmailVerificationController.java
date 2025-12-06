package com.frinkan.controller;

import com.frinkan.service.AccountService;
import com.frinkan.service.MailSenderService;
import com.frinkan.service.VerificationTokenService;
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

    // -------------------------------------------
    // 1) STRONA verify-loader.html
    // -------------------------------------------
    @GetMapping("/verify-loader")
    public String verifyLoaderPage() {
        return "verify-loader";
    }


    // -------------------------------------------
    // 2) BACKEND do którego uderza verify-loader (POST)
    // -------------------------------------------
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


    // -------------------------------------------
    // 3) Twój endpoint regeneracji tokenu
    // -------------------------------------------
    @GetMapping("/regenerateToken")
    @ResponseBody
    public void regenerateToken(@RequestParam String email) {

        if (!accountService.findAccountByEmail(email).isVerifiedEmail()) {

            String newToken = verificationTokenService.generateToken(email);

            // UWAGA: poprawny link musi zawierać parametry
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
        return "verified"; // Thymeleaf szuka templates/verified.html
    }

    @GetMapping("/expired")
    public String expiredPage() {
        return "expired";  // Thymeleaf szuka templates/expired.html
    }
}
