package com.flufan.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MailSenderServiceImplTest {

    @InjectMocks
    private MailSenderServiceImpl service;

    @Mock
    private JavaMailSender mailSender;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendEmail_sendsSimpleMail() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        service.sendEmail("test@example.com", "Subject", "Message body");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendVerificationEmail_sendsMimeMail() throws MessagingException {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        service.sendVerificationEmail("user@example.com", "username", "token123");

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void sendPasswordResetEmail_sendsMimeMail() throws MessagingException {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        service.sendPasswordResetEmail("user@example.com", "username", "resetToken");

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void sendAccountDelInfo_sendsMimeMail() throws MessagingException {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        service.sendAccountDelInfo("user@example.com", "username");

        verify(mailSender, times(1)).send(mimeMessage);
    }
}
