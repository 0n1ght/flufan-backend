package com.flufan.common.controller.web;

import com.flufan.modules.user.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordUpdateWebController {
    private final AccountService accountService;

    public PasswordUpdateWebController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/reset-password/{token}")
    public String getPasswordResetPage(@PathVariable String token, Model model) {
        if (accountService.verifyPasswordResetToken(token)) {
            model.addAttribute("token", token);
            return "password-reset";
        }
        return "password-reset-expired";
    }


    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String password) {
        try {
            accountService.resetPassword(token, password);
            return "password-reset-success";
        } catch (Exception e) {
            return "password-reset-failure";
        }
    }
}
