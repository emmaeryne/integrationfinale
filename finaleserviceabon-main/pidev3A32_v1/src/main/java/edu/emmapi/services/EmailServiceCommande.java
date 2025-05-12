package edu.emmapi.services;


import com.google.zxing.WriterException;

import javax.activation.DataHandler;
import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Properties;

public class EmailServiceCommande {

    private final String username = "salmahaouari6@gmail.com"; // Votre adresse Gmail
    private final String password = "loxt sszi beyl uqan\n"; // Mot de passe d'application

    public void sendEmail(String toEmail, String subject, String body, String qrText, LocalDate dateDeCommande, int idUtilisateur) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.debug", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            String logoPath = "src/main/resources/hjk-removebg-preview.png";

            // Générer le QR code avec logo
            BufferedImage qrImage = QRCodeGenerator.generateQRCodeWithLogo(
                    qrText,
                    200,
                    200,
                    logoPath
            );

            // Convertir en tableau de bytes
            ByteArrayOutputStream qrOutputStream = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", qrOutputStream);
            byte[] qrCodeBytes = qrOutputStream.toByteArray();

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);

            MimeBodyPart textPart = new MimeBodyPart();
            String htmlContent = "<html><body>"
                    + "<p>Bonjour,</p>"
                    + "<p>Nous sommes ravis de vous informer qu'une nouvelle commande a été passée avec succès ! 🎉</p>"
                    + "<p><strong>📅 Date de la commande :</strong> " + dateDeCommande + "</p>"
                    + "<p><strong>👤 ID Utilisateur :</strong> " + idUtilisateur + "</p>"
                    + "<p>Cette commande marque une nouvelle étape dans notre collaboration. Nous nous engageons à vous fournir un service de qualité et à répondre à vos attentes avec professionnalisme.</p>"
                    + "<p>Cordialement,</p>"
                    + "<p>L'équipe de Hive 🌟</p>"
                    + "<hr>"
                    + "<p><strong>💡 Conseil du jour :</strong> Profitez de nos offres spéciales et restez connecté pour ne rien manquer de nos nouveautés !</p>"
                    + "<p>Voici votre code QR :</p>"
                    + "<img src='cid:qrCodeImage' style='width:200px;height:200px;'/>"
                    + "</body></html>";

            textPart.setContent(htmlContent, "text/html; charset=UTF-8");

            MimeBodyPart imagePart = new MimeBodyPart();
            ByteArrayDataSource qrDataSource = new ByteArrayDataSource(qrCodeBytes, "image/png");
            imagePart.setDataHandler(new DataHandler(qrDataSource));
            imagePart.setHeader("Content-ID", "<qrCodeImage>");

            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(imagePart);

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("E-mail envoyé avec succès à " + toEmail);
        } catch (MessagingException | IOException | WriterException e) {
            e.printStackTrace();
            System.out.println("Échec de l'envoi de l'e-mail.");
        }
    }
}