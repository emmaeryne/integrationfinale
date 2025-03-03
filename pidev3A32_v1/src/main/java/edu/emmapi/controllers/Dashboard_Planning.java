package edu.emmapi.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Dashboard_Planning {

    @FXML
    private Button planningbutton;

    @FXML
    private Button coursbutton;

    @FXML
    private Button partcipantbutton;

    @FXML
    private Button detailbutton;

    // Méthode d'initialisation appelée après le chargement du fichier FXML
    @FXML
    private void initialize() {
        // Vérifiez que les boutons sont bien initialisés
        if (planningbutton == null) {
            System.out.println("Le bouton 'planningbutton' n'est pas initialisé.");
        } else {
            System.out.println("Le bouton 'planningbutton' est initialisé.");
        }

        if (coursbutton == null) {
            System.out.println("Le bouton 'coursbutton' n'est pas initialisé.");
        } else {
            System.out.println("Le bouton 'coursbutton' est initialisé.");
        }

        if (partcipantbutton == null) {
            System.out.println("Le bouton 'partcipantbutton' n'est pas initialisé.");
        } else {
            System.out.println("Le bouton 'partcipantbutton' est initialisé.");
        }

        if (detailbutton == null) {
            System.out.println("Le bouton 'detailbutton' n'est pas initialisé.");
        } else {
            System.out.println("Le bouton 'detailbutton' est initialisé.");
        }
    }

    @FXML
    private void handleCoursButton() {
        System.out.println("Bouton 'Cours' cliqué.");
        navigateToPage("/listcours.fxml");
    }

    @FXML
    private void handleDetailButton() {
        System.out.println("Bouton 'Association' cliqué.");
        navigateToPage("/path/to/your/detail.fxml");
    }

    @FXML
    private void handleParticipantButton() {
        System.out.println("Bouton 'Participant' cliqué.");
        navigateToPage("/paticipantt.fxml");
    }

    @FXML
    private void handlePlanningButton() {
        System.out.println("Bouton 'Planning' cliqué.");
        navigateToPage("/planninguser.fxml");
    }

    private void navigateToPage(String fxmlPath) {
        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Vérification si le bouton est null avant d'utiliser getScene()
            if (coursbutton == null) {
                System.out.println("Le bouton coursButton est null dans navigateToPage.");
            } else {
                // Obtenir la scène actuelle du Stage
                Stage stage = (Stage) coursbutton.getScene().getWindow();
                // Créer une nouvelle scène et la charger dans la fenêtre existante
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'ouverture de la fenêtre: " + e.getMessage());
        }
    }
}
