package edu.emmapi.controllers;

import edu.emmapi.services.OwnerService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.Random;

public class code {

    @FXML
    private Label time;

    @FXML
    private TextField TFcode;

    @FXML
    private Button sinscrireButton;

    String email;
    String x; // Cette variable contiendra le code aléatoire (chiffres et lettres)
    private final OwnerService ownerService = new OwnerService();
    private int countdownTime = 60; // Temps initial en secondes
    private Timeline timeline; // Pour gérer le décompte

    // Méthode pour générer une chaîne aléatoire alphanumérique
    private String generateRandomAlphanumericCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"; // Caractères possibles
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length()); // Choisir un index aléatoire
            code.append(characters.charAt(index)); // Ajouter le caractère correspondant
        }

        return code.toString();
    }

    public void setEmail(String text) {
        System.out.println(text);
        EmailApp emailService = new EmailApp();

        x = generateRandomAlphanumericCode(8); // Générer un code aléatoire de 8 caractères
        emailService.sendEmail(text, "Code de vérification", x);
        email = text;

        // Démarrer le minuteur
        startCountdown();
    }

    // Méthode pour démarrer le décompte
    private void startCountdown() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            countdownTime--; // Décrémenter le temps
            time.setText(String.format("%02d:%02d", countdownTime / 60, countdownTime % 60)); // Mettre à jour le label

            if (countdownTime <= 0) { // Si le temps est écoulé
                timeline.stop(); // Arrêter le minuteur
                Stage currentStage = (Stage) time.getScene().getWindow(); // Fermer la fenêtre
                currentStage.close();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE); // Répéter indéfiniment
        timeline.play();

    }

    @FXML
    void verifier(ActionEvent event) throws SQLException {
        if (TFcode.getText().equals(x)) { // Vérifier si le code saisi correspond à x
            timeline.stop(); // Arrêter le minuteur
            Stage currentStage = (Stage) TFcode.getScene().getWindow();
            ownerService.ActiverAccount(email, true);
            currentStage.close();
        }
    }
}