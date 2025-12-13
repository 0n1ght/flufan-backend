package com.flufan.service;

import com.flufan.dto.LoginDto;
import com.flufan.entity.Account;
import com.flufan.dto.RegisterDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService {
    void saveAccount(RegisterDto accountDto);
    void deleteAccount(LoginDto loginDto);
    Account getAuthenticatedAccount();
    String verify(LoginDto loginDto);
    Account findById(Long id);
    Account findByUsername(String username);
    void changeUsername(String newUsername);
    void changeEmail(String password, String newEmail);
    void changePassword(String oldPassword, String newPassword);
    void updateAccount(Account account);
    void verifyAccountEmail(String email);
    Account findAccountByEmail(String email);
    Account loadOrCreateGoogleUser(String email);
}
