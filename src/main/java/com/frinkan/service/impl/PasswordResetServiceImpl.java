package com.frinkan.service.impl;

import com.frinkan.entity.Account;
import com.frinkan.entity.PasswordResetToken;
import com.frinkan.repo.AccountRepo;
import com.frinkan.repo.PasswordResetTokenRepo;
import com.frinkan.service.PasswordResetService;
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
        message.setSubject("Frinkan – Resetowanie hasła");
        message.setText("Kliknij poniższy link, aby zresetować hasło: \n" +
                resetUrl + "\n\n" +  // Dodanie pustych linii dla lepszej czytelności
                "Jeśli to nie Ty wysłałeś tę prośbę, zignoruj ten e-mail. Twoje konto pozostaje bezpieczne.\n" +
                "\n" +
                "Pozdrawiamy,\n" +
                "Zespół Frinkan");
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

        tokenRepo.delete(resetToken); // Usuwamy token po użyciu
    }
}
