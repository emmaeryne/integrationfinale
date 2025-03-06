package edu.emmapi.controllers;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
public class ServiceMail {


        private String host = "smtp.gmail.com";
        private String port = "587";
        private String username = "bochriff.21@gmail.com";
        private String password = "yosa jnid xppp qosf"; // App Password, not your regular password

        public void sendEmail(String to, String subject, String content) {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);

            // Optional: Add debugging to see detailed SMTP conversation
            // props.put("mail.debug", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
                message.setSubject(subject);
                message.setText(content);

                Transport.send(message);

                System.out.println("Email envoyé avec succès.");

            } catch (MessagingException e) {
                System.err.println("Erreur d'envoi d'email: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

