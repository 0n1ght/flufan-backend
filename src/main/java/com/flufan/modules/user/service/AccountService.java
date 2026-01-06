package com.flufan.modules.user.service;

import com.flufan.modules.user.dto.LoginDto;
import com.flufan.modules.user.entity.Account;
import com.flufan.modules.user.dto.RegisterDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Map;
import java.util.UUID;

public interface AccountService extends UserDetailsService {
    Account saveAccount(RegisterDto accountDto);
    void deleteAccount(String password);
    Map<String, Object> verify(LoginDto loginDto);
    Account findByPublicId(UUID publicId);
    Account findByUsername(String username);
    void updateUsername(String newUsername);
    void updatePassword(String oldPassword, String newPassword);
    void updateAccount(Account account);
    void verifyEmailUpdateRequest(String password, String newEmail);
    Account loadOrCreateGoogleUser(String email);
    String requestPasswordReset(String login);
    void resetPassword(String token, String newPassword);
    boolean verifyPasswordResetToken(String token);
    Account getAuthenticatedAccount();
    void authenticatePassword(String password);
}
