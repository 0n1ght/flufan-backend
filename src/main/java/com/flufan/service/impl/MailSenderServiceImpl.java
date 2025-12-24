package com.flufan.service.impl;

import com.flufan.service.MailSenderService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailSenderServiceImpl implements MailSenderService {

    private final JavaMailSender mailSender;

    public MailSenderServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(String receiver, String subject, String message) {
        SimpleMailMessage msg = new SimpleMailMessage();

        msg.setTo(receiver);
        msg.setSubject("FluFan- " + subject);
        msg.setText(message);

        mailSender.send(msg);
    }

    @Override
    public void sendVerificationEmail(String email, String username, String token) throws MessagingException {
        String link = buildVerificationLink(token);

        String htmlContent = "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f5f5f5; margin:0; padding:0;'>" +
                "<table width='100%' cellpadding='0' cellspacing='0'>" +
                "<tr><td align='center' style='padding:40px 0;'>" +
                "<table width='600' cellpadding='0' cellspacing='0' style='background:#fff; border-radius:10px; overflow:hidden;'>" +
                "<tr><td align='center' style='background-color:#6a1b9a; padding:30px;'>" +
                "<h1 style='color:#fff; margin:0; font-size:32px;'>Flufan</h1>" +
                "</td></tr>" +
                "<tr><td style='padding:40px; color:#333;'>" +
                "<h2 style='margin-top:0;'>Verify Your Email Address</h2>" +
                "<p>Hello <strong>" + username + "</strong>,</p>" +
                "<p>Please verify your email by clicking the button below:</p>" +
                "<p style='text-align:center; margin:30px 0;'>" +
                "<a href='" + link + "' style='background-color:#6a1b9a; color:#fff; padding:15px 25px; text-decoration:none; border-radius:5px; display:inline-block;'>Verify Email</a>" +
                "</p>" +
                "<p>This verification link will expire in <strong>24 hours</strong>.</p>" +
                "<p>If you did not sign up, ignore this email.</p>" +
                "<p>Need help? Contact support: <a href='mailto:support@flufan.com'>support@flufan.com</a></p>" +
                "</td></tr>" +
                "<tr><td style='background-color:#f0f0f0; text-align:center; padding:20px; font-size:12px; color:#888;'>" +
                "&copy; 2025 Flufan. All rights reserved.<br>123 Main Street, Your City" +
                "</td></tr>" +
                "</table></td></tr></table></body></html>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(email);
        helper.setSubject("Flufan - Email Verification");
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    private String buildVerificationLink(String token) {
        return String.format("http://localhost:8080/email-auth/verify/%s", token);
    }
}
