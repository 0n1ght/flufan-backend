package com.flufan.service;

import com.flufan.dto.LoginDto;
import com.flufan.entity.Account;
import com.flufan.dto.RegisterDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Map;
import java.util.UUID;

public interface AccountService extends UserDetailsService {
    Account saveAccount(RegisterDto accountDto);
    void saveAccount(Account account);
    void deleteAccount(String password);
    Map<String, Object> verify(LoginDto loginDto);
    Account findById(Long id);
    Account findByPublicId(UUID publicId);
    Account findByUsername(String username);
    void updateUsername(String newUsername);
    void updatePassword(String oldPassword, String newPassword);
    void updateAccount(Account account);
    void verifyEmailUpdateRequest(String password, String newEmail);
    Account loadOrCreateGoogleUser(String email);
    void requestPasswordReset(String email);
    void resetPassword(String token, String newPassword);
    boolean verifyPasswordResetToken(String token);
    Account getAuthenticatedAccount();
    void authenticatePassword(String password);
}
