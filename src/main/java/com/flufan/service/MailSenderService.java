package com.flufan.service;

public interface MailSenderService {
    void sendEmail(String receiver, String subject, String message);
}
