package com.flufan.service.impl;

import com.flufan.service.MailSenderService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
}
