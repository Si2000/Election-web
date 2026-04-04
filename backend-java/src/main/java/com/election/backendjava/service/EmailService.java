package com.election.backendjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    // 1. Registratie: account activeren
    public void sendWelcomeEmail(String toEmail, String username, String token) {
        String verifyLink = "http://oege.ie.hva.nl:8889/verify-account?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(toEmail);
        message.setSubject("Activeer je account - Nederlandse Verkiezingen");

        message.setText(
                "Hallo " + username + ",\n\n" +
                        "Klik op de link om je account te activeren:\n\n" +
                        verifyLink + "\n\n" +
                        "Groet,\nHet Verkiezings Team"
        );

        mailSender.send(message);
    }

    // 2. E-mailwijziging: bevestiging naar OUDE e-mail (MET LINK)
    public void sendEmailChangeConfirmation(String oldEmail, String username, String token) {
        String link = "http://oege.ie.hva.nl:8889/confirm-email-change?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(oldEmail);
        message.setSubject("Bevestig wijziging van je e-mailadres");

        message.setText(
                "Hallo " + username + ",\n\n" +
                        "Er is een verzoek gedaan om je e-mailadres te wijzigen.\n\n" +
                        "Klik op de onderstaande link om deze wijziging te bevestigen:\n\n" +
                        link + "\n\n" +
                        "Heb jij dit niet aangevraagd? Negeer deze e-mail.\n\n" +
                        "Groet,\nHet Verkiezings Team"
        );

        mailSender.send(message);
    }

    // 3. E-mailwijziging: waarschuwing naar NIEUWE e-mail (ZONDER LINK)
    public void sendEmailChangeWarning(String newEmail, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(newEmail);
        message.setSubject("Je e-mailadres wordt gewijzigd");

        message.setText(
                "Hallo " + username + ",\n\n" +
                        "Dit e-mailadres wordt gekoppeld aan een bestaand account.\n\n" +
                        "De wijziging is pas definitief nadat deze is bevestigd via het oude e-mailadres.\n\n" +
                        "Heb jij dit niet aangevraagd? Dan hoef je niets te doen.\n\n" +
                        "Groet,\nHet Verkiezings Team"
        );

        mailSender.send(message);
    }

    // 4. Wachtwoord reset mail
    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink = "http://oege.ie.hva.nl:8889/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(toEmail);
        message.setSubject("Herstel je wachtwoord - Nederlandse Verkiezingen");

        message.setText(
                "Hallo,\n\n" +
                        "Er is een verzoek gedaan om je wachtwoord te herstellen.\n\n" +
                        "Klik op de onderstaande link om een nieuw wachtwoord in te stellen:\n\n" +
                        resetLink + "\n\n" +
                        "Als je dit niet hebt aangevraagd, kun je deze mail negeren.\n\n" +
                        "Groet,\nHet Verkiezings Team"
        );

        mailSender.send(message);
    }
}
