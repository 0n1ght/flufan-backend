package com.flufan.modules.notification.service;

import jakarta.mail.MessagingException;

public interface MailSenderService {
    void sendEmail(String receiver, String subject, String message);
    void sendVerificationEmail(String email, String username, String token) throws MessagingException;
    void sendPasswordResetEmail(String email, String username, String token) throws MessagingException;
    void sendAccountDelInfo(String email, String username);
}
