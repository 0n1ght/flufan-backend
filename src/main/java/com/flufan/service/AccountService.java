package com.flufan.service;

import com.flufan.dto.LoginDto;
import com.flufan.entity.Account;
import com.flufan.dto.RegisterDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService {
    Account saveAccount(RegisterDto accountDto);
    Account saveAccount(Account account);
    void deleteAccount(LoginDto loginDto);
    Account getAuthenticatedAccount();
    String verify(LoginDto loginDto);
    Account findById(Long id);
    Account findByUsername(String username);
    void updateUsername(String newUsername);
    void updatePassword(String oldPassword, String newPassword);
    void updateAccount(Account account);
    void verifyEmailUpdateRequest(String password, String newEmail);
    Account findAccountByEmail(String email);
    Account loadOrCreateGoogleUser(String email);
}
