package com.flufan.service;

import jakarta.mail.MessagingException;

public interface MailSenderService {
    void sendEmail(String receiver, String subject, String message);
    void sendVerificationEmail(String email, String username, String token) throws MessagingException;
}
