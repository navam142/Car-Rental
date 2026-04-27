package com.example.carrental.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationMailService {

    private final JavaMailSender mailSender;

    @Value("${application.mail.from}")
    private String fromAddress;

    @Value("${application.mail.verification-url}")
    private String verificationUrl;

    public void sendVerificationEmail(String to, String token) {
        String verifyLink = verificationUrl + "?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject("Verify your car-rental account");
        message.setText("Welcome to Car Rental! Please verify your email by clicking the link below:\n\n"
                + verifyLink
                + "\n\nIf you did not create this account, please ignore this email.");

        mailSender.send(message);
    }
}

