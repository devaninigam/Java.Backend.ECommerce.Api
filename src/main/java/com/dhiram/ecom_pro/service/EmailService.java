package com.dhiram.ecom_pro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @SuppressWarnings("UseSpecificCatch")
    public boolean sendSimpleEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("nigamdevani2029@gmail.com");
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            
            mailSender.send(message);
            System.out.println("Mail Sent successfully to " + toEmail);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to send email to " + toEmail);
            System.err.println("Error: " + e.getMessage());
            return false;
        }

    }
}
