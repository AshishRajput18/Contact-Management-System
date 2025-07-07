package com.smart.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public boolean sendEmail(String to, String subject, String messageText) {
        boolean sent = false;

        String from = "ashishrajput6768@gmail.com"; // sender email
        String password = "pepqpkdimerfigur"; // App password

        // Setup mail server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Create Authenticator
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        };

        // Create a mail session
        Session session = Session.getInstance(properties, auth);

        try {
            // Compose the message
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            message.setSubject(subject);
            message.setText(messageText);

            // Send the message
            Transport.send(message);
            sent = true;
            System.out.println("Email sent successfully to " + to);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sent;
    }
}
