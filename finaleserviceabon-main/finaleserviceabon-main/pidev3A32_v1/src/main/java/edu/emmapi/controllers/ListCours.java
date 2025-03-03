/*package edu.pidev3a32.controllers;


import edu.pidev3a32.entities.Cours;
import edu.pidev3a32.services.CoursService;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.List;

public class ListCours {

    @FXML
    private VBox coursVBox;  // VBox où les informations des cours seront affichées

    private CoursService coursService = new CoursService();
    @FXML
    private Label courslabel;

    @FXML
    private Label participantlabel;

    @FXML
    private Label planninglabel;
    @FXML
    private Label Detaillabel;
    public void initialize() {
        try {
            // Charger les cours depuis la base de données
            loadCourses();
        } catch (SQLException e) {
            e.printStackTrace();  // Gérer l'exception si l'accès à la base échoue
        }

        // Animation de fade-in pour la VBox
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), coursVBox);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setCycleCount(1);
        fadeIn.play();
    }

    // Charger les cours depuis la base de données et les afficher dans la VBox
    private void loadCourses() throws SQLException {
        List<Cours> coursList = coursService.getAllCourses();  // Cette méthode peut potentiellement lancer une SQLException

        // Vider le VBox avant de remplir avec de nouveaux éléments
        coursVBox.getChildren().clear();

        // Ajouter chaque cours sous forme de "carte" dans le VBox
        for (Cours cours : coursList) {
            // Créer une boîte contenant les informations du cours
            VBox courseBox = new VBox();
            courseBox.setStyle("-fx-border-color: #888888; -fx-padding: 10; -fx-spacing: 10; -fx-background-color: #f4f4f4;");

            // Nom du cours
            Text nameText = new Text("Nom du cours: " + cours.getNom_Cours());
            nameText.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

            // Durée du cours
            Text durationText = new Text("Durée: " + cours.getDuree() + " heures");

            // État du cours avec une couleur spécifique
            Text stateText = new Text("État: " + cours.getEtat_Cours());
            if ("Actif".equals(cours.getEtat_Cours())) {
                stateText.setStyle("-fx-fill: green;");
            } else {
                stateText.setStyle("-fx-fill: red;");
            }

            // Ajouter les informations du cours à la boîte
            courseBox.getChildren().addAll(nameText, durationText, stateText);

            // Ajouter la boîte à la VBox principale
            coursVBox.getChildren().add(courseBox);
        }
    }
}*/
package edu.emmapi.controllers;

import edu.emmapi.entities.Cours;
import edu.emmapi.services.CoursService;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ListCours {

    @FXML
    private VBox coursVBox;  // VBox où les informations des cours seront affichées

    private CoursService coursService = new CoursService();

    @FXML
    private Label courslabel;

    @FXML
    private Label participantlabel;

    @FXML
    private Label planninglabel;

    @FXML
    private Label Detaillabel;

    public void initialize() {
        try {
            // Charger les cours depuis la base de données
            loadCourses();
        } catch (SQLException e) {
            e.printStackTrace();  // Gérer l'exception si l'accès à la base échoue
        }

        // Animation de fade-in pour la VBox
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), coursVBox);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setCycleCount(1);
        fadeIn.play();

        // Ajouter l'événement de clic pour chaque label
        setupLabelClickActions();
    }

    // Charger les cours depuis la base de données et les afficher dans la VBox
    private void loadCourses() throws SQLException {
        List<Cours> coursList = coursService.getAllCourses();  // Cette méthode peut potentiellement lancer une SQLException

        // Vider le VBox avant de remplir avec de nouveaux éléments
        coursVBox.getChildren().clear();

        // Ajouter chaque cours sous forme de "carte" dans le VBox
        for (Cours cours : coursList) {
            // Créer une boîte contenant les informations du cours
            VBox courseBox = new VBox();
            courseBox.setStyle("-fx-border-color: #888888; -fx-padding: 10; -fx-spacing: 10; -fx-background-color: #f4f4f4;");

            // Nom du cours
            Text nameText = new Text("Nom du cours: " + cours.getNom_Cours());
            nameText.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

            // Durée du cours
            Text durationText = new Text("Durée: " + cours.getDuree() + " heures");

            // État du cours avec une couleur spécifique
            Text stateText = new Text("État: " + cours.getEtat_Cours());
            if ("Actif".equals(cours.getEtat_Cours())) {
                stateText.setStyle("-fx-fill: green;");
            } else {
                stateText.setStyle("-fx-fill: red;");
            }

            // Ajouter les informations du cours à la boîte
            courseBox.getChildren().addAll(nameText, durationText, stateText);

            // Ajouter la boîte à la VBox principale
            coursVBox.getChildren().add(courseBox);
        }
    }

    // Méthode pour configurer l'événement de clic sur les labels
    private void setupLabelClickActions() {
        courslabel.setOnMouseClicked(event -> navigateToPage("/listcours.fxml"));
        participantlabel.setOnMouseClicked(event -> navigateToPage("/Paticipantt.fxml"));
        planninglabel.setOnMouseClicked(event -> navigateToPage("/planninguser.fxml"));
        Detaillabel.setOnMouseClicked(event -> navigateToPage("/chemin/vers/votrePageDetail.fxml"));
    }

    // Méthode pour naviguer vers une autre interface
    private void navigateToPage(String fxmlPath) {
        try {
            // Charger le fichier FXML de la page cible
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Obtenir la scène actuelle et changer le contenu de la fenêtre
            Stage stage = (Stage) courslabel.getScene().getWindow(); // Utilise n'importe quel composant comme source pour obtenir la fenêtre
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'ouverture de la page : " + e.getMessage());
        }
    }
}



