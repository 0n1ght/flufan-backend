package com.frinkan.controller;

import com.frinkan.dto.LoginDto;
import com.frinkan.dto.RegisterDto;
import com.frinkan.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping(value = "/req/signup", consumes = "application/json")
    public void register(@RequestBody RegisterDto registerDto) {
        registerDto.setPassword(registerDto.getPassword());
        accountService.saveAccount(registerDto);
    }

    @PostMapping("/update-login-data")
    public ResponseEntity<String> updateLoginData(@RequestBody LoginDto loginDto) {
        try {
            accountService.changeLoginData(loginDto);
            return ResponseEntity.ok("Login data updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to update login data: " + e.getMessage());
        }
    }
}
