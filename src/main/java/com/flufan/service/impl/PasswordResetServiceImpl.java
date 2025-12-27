package com.flufan.service.impl;

import com.flufan.entity.Account;
import com.flufan.entity.PasswordResetToken;
import com.flufan.repo.AccountRepo;
import com.flufan.repo.PasswordResetTokenRepo;
import com.flufan.service.PasswordResetService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {
    private final AccountRepo accountRepo;
    private final PasswordResetTokenRepo tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    public PasswordResetServiceImpl(AccountRepo accountRepo, PasswordResetTokenRepo tokenRepo,
                                PasswordEncoder passwordEncoder, JavaMailSender mailSender) {
        this.accountRepo = accountRepo;
        this.tokenRepo = tokenRepo;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    public void requestPasswordReset(String email) {
        Account account = accountRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setAccount(account);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1)); // Ważny 1h

        tokenRepo.save(resetToken);

        sendResetEmail(account.getEmail(), token);
    }

    private void sendResetEmail(String email, String token) {
        String resetUrl = "http://localhost:8080/api/auth/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Flufan – PasswordReset");
        message.setText("Click the link below to reset your password:\n" +
                resetUrl + "\n\n" +
                "If you did not request this, please ignore this email. Your account remains secure.\n" +
                "\n" +
                "Best regards,\n" +
                "The Flufan Team");
        mailSender.send(message);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.isExpired()) {
            throw new RuntimeException("Token expired");
        }

        Account account = resetToken.getAccount();
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepo.save(account);

        tokenRepo.delete(resetToken);
    }
}
