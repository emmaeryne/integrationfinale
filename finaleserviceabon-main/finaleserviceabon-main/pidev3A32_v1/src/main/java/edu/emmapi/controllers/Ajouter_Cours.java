/*package edu.emmapi.controllers;
import edu.emmapi.entities.Cours;
import edu.emmapi.services.CoursService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class Ajouter_Cours {

    @FXML
    private Button buttonajoutercours;

    @FXML
    private TextField dureecourstextfield;

    @FXML
    private TextField etatcourstextfield;

    @FXML
    private TextField nomcourstextfield;

    @FXML
    private Label deatillabel;

    @FXML
    private Label labelcours;

    @FXML
    private Label labelparticipant;

    @FXML
    private Label labelplanning;

    // Méthode pour ajouter un cours
    @FXML
    void addcours(ActionEvent event) {
        if (!isInputValid()) {
            return;
        }

        String nomCours = nomcourstextfield.getText().trim();
        Integer dureeCours = validateDuree();
        if (dureeCours == null) return;

        String etatCours = etatcourstextfield.getText().trim();
        if (!isValidEtatCours(etatCours)) {
            showAlert("L'état du cours doit être 'Ouvert' ou 'Fermé'.");
            return;
        }

        Cours cours = new Cours();
        cours.setNom_Cours(nomCours);
        cours.setDuree(dureeCours);
        cours.setEtat_Cours(etatCours);

        CoursService coursService = new CoursService();
        try {
            coursService.addcours(cours);
            showAlert("Cours ajouté avec succès !");

            // Passer à la scène "List_Cours" et fermer la scène actuelle
            switchToCoursListScene(event);
            closeCurrentWindow(event);

        } catch (Exception e) {
            showAlert("Erreur lors de l'ajout du cours : " + e.getMessage());
        }
    }

    // Méthode pour changer de scène (List_Cours)
    private void switchToCoursListScene(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cours.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();  // Affiche l'erreur dans la console pour le débogage
            showAlert("Erreur lors de la navigation vers la liste des cours : " + e.getMessage());
        }
    }

    // Fermer la scène actuelle
    private void closeCurrentWindow(javafx.event.Event event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }

    // Gestion du clic sur le label "Planning"
    @FXML
    private void handleLabelPlanningClick(MouseEvent event) {
        switchToScene("/Ajouter_Planning.fxml", event);
    }

    // Gestion du clic sur le label "Participant"
    @FXML
    private void handleLabelParticipantClick(MouseEvent event) {
        switchToScene("/Ajouter_Participant.fxml", event);
    }

    // Gestion du clic sur le label "Cours"
    @FXML
    private void handleLabelCoursClick(MouseEvent event) {
        switchToScene("/Ajouter_Cours.fxml", event);
    }

    // Gestion du clic sur le label "Detail"
    @FXML
    private void handleLabeldetailClick(MouseEvent event) {
        switchToScene("/Ajouter_Cours_Participant.fxml", event);
    }

    // Méthode commune pour changer de scène
    private void switchToScene(String fxmlFilePath, javafx.event.Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilePath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();  // Affiche l'erreur dans la console pour le débogage
            showAlert("Erreur lors du changement de scène : " + e.getMessage());
        }
    }

    // Validation des champs de saisie
    private boolean isInputValid() {
        if (nomcourstextfield.getText().trim().isEmpty() ||
                dureecourstextfield.getText().trim().isEmpty() ||
                etatcourstextfield.getText().trim().isEmpty()) {
            showAlert("Veuillez remplir tous les champs obligatoires.");
            return false;
        }

        String nomCours = nomcourstextfield.getText().trim();
        if (!isValidNomCours(nomCours)) {
            showAlert("Le nom du cours ne doit pas contenir de chiffres ou de caractères spéciaux.");
            return false;
        }

        return true;
    }

    // Validation du nom du cours
    private boolean isValidNomCours(String nom) {
        return nom.matches("^[a-zA-Z\\s]+$");
    }

    // Validation de la durée du cours
    private Integer validateDuree() {
        try {
            Integer dureeCours = Integer.valueOf(dureecourstextfield.getText().trim());
            if (dureeCours <= 0) {
                showAlert("La durée doit être un nombre entier positif.");
                return null;
            }
            return dureeCours;
        } catch (NumberFormatException e) {
            showAlert("La durée doit être un nombre entier.");
            return null;
        }
    }

    // Validation de l'état du cours
    private boolean isValidEtatCours(String etat) {
        return etat.equalsIgnoreCase("Ouvert") || etat.equalsIgnoreCase("Fermé");
    }

    // Affichage d'un message d'alerte
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }
}*/







package edu.emmapi.controllers;

import edu.emmapi.entities.Cours;
import edu.emmapi.services.CoursService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class Ajouter_Cours {

    @FXML
    private Button buttonajoutercours;

    @FXML
    private TextField dureecourstextfield;

    @FXML
    private TextField etatcourstextfield;

    @FXML
    private TextField nomcourstextfield;

    @FXML
    private Label deatillabel;

    @FXML
    private Label labelcours;

    @FXML
    private Label labelparticipant;

    @FXML
    private Label labelplanning;

    // Méthode pour ajouter un cours
    @FXML
    void addcours(ActionEvent event) {
        if (!isInputValid()) {
            return;
        }

        String nomCours = nomcourstextfield.getText().trim();
        Integer dureeCours = validateDuree();
        if (dureeCours == null) return;

        String etatCours = etatcourstextfield.getText().trim();
        if (!isValidEtatCours(etatCours)) {
            showAlert("L'état du cours doit être 'Ouvert' ou 'Fermé'.");
            return;
        }

        Cours cours = new Cours();
        cours.setNom_Cours(nomCours);
        cours.setDuree(dureeCours);
        cours.setEtat_Cours(etatCours);

        CoursService coursService = new CoursService();
        try {
            coursService.addcours(cours);
            showAlert("Cours ajouté avec succès !");

            // Passer à la scène "List_Cours" et fermer la scène actuelle
            switchToCoursListScene(event);
            closeCurrentWindow(event);

        } catch (Exception e) {
            showAlert("Erreur lors de l'ajout du cours : " + e.getMessage());
        }
    }

    // Méthode pour changer de scène (List_Cours)
    private void switchToCoursListScene(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cours.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();  // Affiche l'erreur dans la console pour le débogage
            showAlert("Erreur lors de la navigation vers la liste des cours : " + e.getMessage());
        }
    }

    // Fermer la scène actuelle
    private void closeCurrentWindow(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }

    // Gestion du clic sur le label "Planning"
    @FXML
    private void handleLabelPlanningClick(MouseEvent event) {
        switchToScene("/Ajouter_Planning.fxml", event);
    }

    // Gestion du clic sur le label "Participant"
    @FXML
    private void handleLabelParticipantClick(MouseEvent event) {
        switchToScene("/Ajouter_Participant.fxml", event);
    }

    // Gestion du clic sur le label "Cours"
    @FXML
    private void handleLabelCoursClick(MouseEvent event) {
        switchToScene("/Ajouter_Cours.fxml", event);
    }

    // Gestion du clic sur le label "Detail"
    @FXML
    private void handleLabeldetailClick(MouseEvent event) {
        switchToScene("/Ajouter_Cours_Participant.fxml", event);
    }

    // Méthode commune pour changer de scène
    private void switchToScene(String fxmlFilePath, MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilePath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();  // Affiche l'erreur dans la console pour le débogage
            showAlert("Erreur lors du changement de scène : " + e.getMessage());
        }
    }

    // Validation des champs de saisie
    private boolean isInputValid() {
        if (nomcourstextfield.getText().trim().isEmpty() ||
                dureecourstextfield.getText().trim().isEmpty() ||
                etatcourstextfield.getText().trim().isEmpty()) {
            showAlert("Veuillez remplir tous les champs obligatoires.");
            return false;
        }

        String nomCours = nomcourstextfield.getText().trim();
        if (!isValidNomCours(nomCours)) {
            showAlert("Le nom du cours ne doit pas contenir de chiffres ou de caractères spéciaux.");
            return false;
        }

        return true;
    }

    // Validation du nom du cours
    private boolean isValidNomCours(String nom) {
        return nom.matches("^[a-zA-Z\\s]+$");
    }

    // Validation de la durée du cours
    private Integer validateDuree() {
        try {
            Integer dureeCours = Integer.valueOf(dureecourstextfield.getText().trim());
            if (dureeCours <= 0) {
                showAlert("La durée doit être un nombre entier positif.");
                return null;
            }
            return dureeCours;
        } catch (NumberFormatException e) {
            showAlert("La durée doit être un nombre entier.");
            return null;
        }
    }

    // Validation de l'état du cours
    private boolean isValidEtatCours(String etat) {
        return etat.equalsIgnoreCase("Ouvert") || etat.equalsIgnoreCase("Fermé");
    }

    // Affichage d'un message d'alerte
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }
}

