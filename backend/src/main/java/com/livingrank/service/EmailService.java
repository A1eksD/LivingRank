package com.livingrank.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAdminMail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject("[LivingRank Admin] " + subject);
        message.setText(body + "\n\n---\nDiese Nachricht wurde vom LivingRank Admin-Team versendet.");
        mailSender.send(message);
    }

    public void sendVerificationEmail(String to, String displayName, String verifyUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject("LivingRank - E-Mail-Adresse bestätigen");
        message.setText(
            "Hallo " + displayName + ",\n\n" +
            "vielen Dank für Ihre Registrierung bei LivingRank.\n\n" +
            "Bitte bestätigen Sie Ihre E-Mail-Adresse, indem Sie auf folgenden Link klicken:\n\n" +
            verifyUrl + "\n\n" +
            "Dieser Link ist 24 Stunden gültig.\n\n" +
            "Falls Sie sich nicht registriert haben, können Sie diese E-Mail ignorieren.\n\n" +
            "Mit freundlichen Grüßen,\n" +
            "Ihr LivingRank Team"
        );
        mailSender.send(message);
    }
}
