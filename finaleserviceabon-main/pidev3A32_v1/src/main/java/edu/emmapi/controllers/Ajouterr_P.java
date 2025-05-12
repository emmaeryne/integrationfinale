/*package edu.pidev3a32.controllers;

import edu.pidev3a32.entities.Planning;
import edu.pidev3a32.entities.Cours;
import edu.pidev3a32.services.PlanningService;
import edu.pidev3a32.services.CoursService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class Ajouterr_P {

    @FXML
    private Button ajouter_planning;

    @FXML
    private RadioButton confirmeButton;

    @FXML
    private ComboBox<Cours> courstextfield;

    @FXML
    private DatePicker date_PlanningTextfield;

    @FXML
    private RadioButton enattentebutton;
    @FXML
    private Label labelcours;

    @FXML
    private Label labelparticipants;

    @FXML
    private TextField typeactivitetextfield;

    private PlanningService planningService;

    @FXML
    public void initialize() {
        planningService = new PlanningService();
        loadCourses();
    }

    private void loadCourses() {
        CoursService coursService = new CoursService();
        List<Cours> courses = coursService.getAllCourses(); // Assurez-vous que cette méthode existe
        courstextfield.getItems().addAll(courses);
    }

    @FXML
    void addplanning(ActionEvent event) {
        // Récupérer les valeurs des champs
        String typeActivite = typeactivitetextfield.getText().trim();
        Date datePlanning;

        // Vérifier si une date a été sélectionnée
        if (date_PlanningTextfield.getValue() == null) {
            showAlert("Veuillez sélectionner une date.");
            return;
        }

        // Convertir la date sélectionnée
        datePlanning = Date.valueOf(date_PlanningTextfield.getValue());
        Cours cours = courstextfield.getValue();
        String status = confirmeButton.isSelected() ? "Confirmé" : "En Attente";

        // Validation des entrées
        if (typeActivite.isEmpty()) {
            showAlert("Le champ 'Type d'Activité' est obligatoire.");
            return;
        }
        if (cours == null) {
            showAlert("Veuillez sélectionner un cours.");
            return;
        }
        if (datePlanning.before(Date.valueOf(java.time.LocalDate.now()))) {
            showAlert("La date de planning ne peut pas être dans le passé.");
            return;
        }

        // Créer l'objet Planning
        Planning planning = new Planning(typeActivite, datePlanning, status, cours.getId_Cours());

        // Ajouter le planning via le service
        try {
            planningService.addplanning(planning);
            showAlert("Planning ajouté avec succès !");

            // Réinitialiser les champs après l'ajout
            clearFields();

            // Charger la nouvelle scène
            switchToPlanningListScene();

            // Fermer l'interface d'ajout
            closeCurrentStage(event);
        } catch (SQLException e) {
            showAlert("Erreur lors de l'ajout du planning : " + e.getMessage());
        }
    }

    private void switchToPlanningListScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/list_planning.fxml")); // Chemin vers votre FXML
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Obtenir le stage actuel
            Stage stage = (Stage) ajouter_planning.getScene().getWindow();

            // Changer la scène
            stage.setScene(scene);
            stage.setTitle("Liste des Plannings");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur lors du chargement de la liste des plannings : " + e.getMessage());
        }
    }

    private void closeCurrentStage(ActionEvent event) {
        // Fermer la fenêtre actuelle
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }

    private void clearFields() {
        typeactivitetextfield.clear();
        date_PlanningTextfield.setValue(null);
        courstextfield.setValue(null);
        confirmeButton.setSelected(false);
        enattentebutton.setSelected(false);
    }
}*/
package edu.emmapi.controllers;

/*import edu.pidev3a32.entities.Planning;
import edu.pidev3a32.entities.Cours;
import edu.pidev3a32.services.PlanningService;
import edu.pidev3a32.services.CoursService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent; // Import pour les événements de souris

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class Ajouterr_P {

    @FXML
    private Button ajouter_planning;

    @FXML
    private RadioButton confirmeButton;

    @FXML
    private ComboBox<Cours> courstextfield;

    @FXML
    private DatePicker date_PlanningTextfield;

    @FXML
    private RadioButton enattentebutton;

    @FXML
    private Label labelcours;

    @FXML
    private Label labelparticipants;

    @FXML
    private TextField typeactivitetextfield;

    private PlanningService planningService;

    @FXML
    public void initialize() {
        planningService = new PlanningService();
        loadCourses();
    }

    private void loadCourses() {
        CoursService coursService = new CoursService();
        List<Cours> courses = coursService.getAllCourses(); // Assurez-vous que cette méthode existe
        courstextfield.getItems().addAll(courses);
    }

    @FXML
    void addplanning(ActionEvent event) {
        // Récupérer les valeurs des champs
        String typeActivite = typeactivitetextfield.getText().trim();
        Date datePlanning;

        // Vérifier si une date a été sélectionnée
        if (date_PlanningTextfield.getValue() == null) {
            showAlert("Veuillez sélectionner une date.");
            return;
        }

        // Convertir la date sélectionnée
        datePlanning = Date.valueOf(date_PlanningTextfield.getValue());
        Cours cours = courstextfield.getValue();
        String status = confirmeButton.isSelected() ? "Confirmé" : "En Attente";

        // Validation des entrées
        if (typeActivite.isEmpty()) {
            showAlert("Le champ 'Type d'Activité' est obligatoire.");
            return;
        }
        if (cours == null) {
            showAlert("Veuillez sélectionner un cours.");
            return;
        }
        if (datePlanning.before(Date.valueOf(java.time.LocalDate.now()))) {
            showAlert("La date de planning ne peut pas être dans le passé.");
            return;
        }

        // Créer l'objet Planning
        Planning planning = new Planning(typeActivite, datePlanning, status, cours.getId_Cours());

        // Ajouter le planning via le service
        try {
            planningService.addplanning(planning);
            showAlert("Planning ajouté avec succès !");

            // Réinitialiser les champs après l'ajout
            clearFields();

            // Charger la nouvelle scène
            switchToPlanningListScene();

            // Fermer l'interface d'ajout
            closeCurrentStage(event);
        } catch (SQLException e) {
            showAlert("Erreur lors de l'ajout du planning : " + e.getMessage());
        }
    }

    private void switchToPlanningListScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/list_plannning.fxml")); // Chemin vers votre FXML
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Obtenir le stage actuel
            Stage stage = (Stage) ajouter_planning.getScene().getWindow();

            // Changer la scène
            stage.setScene(scene);
            stage.setTitle("Liste des Plannings");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur lors du chargement de la liste des plannings : " + e.getMessage());
        }
    }

    private void closeCurrentStage(ActionEvent event) {
        // Fermer la fenêtre actuelle
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }

    private void clearFields() {
        typeactivitetextfield.clear();
        date_PlanningTextfield.setValue(null);
        courstextfield.setValue(null);
        confirmeButton.setSelected(false);
        enattentebutton.setSelected(false);
    }

    // Méthode pour gérer le clic sur le label des cours
    @FXML
    private void handleCoursLabelClick(MouseEvent event) {
        switchToScene("/Ajouter_Cours.fxml", event);  // Naviguer vers la page Ajouter Cours
    }

    // Méthode pour gérer le clic sur le label des participants
    @FXML
    private void handleParticipantsLabelClick(MouseEvent event) {
        switchToScene("/Ajouter_Participant.fxml", event);  // Naviguer vers la page Ajouter Participant
    }

    // Méthode générique pour changer de scène
    private void switchToScene(String fxmlFilePath, MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilePath));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur lors du changement de scène : " + e.getMessage());
        }
    }
}*/


/*import edu.pidev3a32.entities.Planning;
import edu.pidev3a32.entities.Cours;
import edu.pidev3a32.services.PlanningService;
import edu.pidev3a32.services.CoursService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class Ajouterr_P {

    @FXML
    private Button ajouter_planning;

    @FXML
    private RadioButton confirmeButton;

    @FXML
    private ComboBox<Cours> courstextfield;

    @FXML
    private DatePicker date_PlanningTextfield;

    @FXML
    private RadioButton enattentebutton;

    @FXML
    private Label labelcours;

    @FXML
    private Label labelparticipants;

    @FXML
    private TextField typeactivitetextfield;

    private PlanningService planningService;

    @FXML
    public void initialize() {
        planningService = new PlanningService();
        loadCourses();
    }

    private void loadCourses() {
        CoursService coursService = new CoursService();
        List<Cours> courses = coursService.getAllCourses(); // Assurez-vous que cette méthode existe
        courstextfield.getItems().addAll(courses);
    }

    @FXML
    void addplanning(ActionEvent event) {
        // Récupérer les valeurs des champs
        String typeActivite = typeactivitetextfield.getText().trim();
        Date datePlanning;

        // Vérifier si une date a été sélectionnée
        if (date_PlanningTextfield.getValue() == null) {
            showAlert("Veuillez sélectionner une date.");
            return;
        }

        // Convertir la date sélectionnée
        datePlanning = Date.valueOf(date_PlanningTextfield.getValue());
        Cours cours = courstextfield.getValue();
        String status = confirmeButton.isSelected() ? "Confirmé" : "En Attente";

        // Validation des entrées
        if (typeActivite.isEmpty()) {
            showAlert("Le champ 'Type d'Activité' est obligatoire.");
            return;
        }
        if (cours == null) {
            showAlert("Veuillez sélectionner un cours.");
            return;
        }
        if (datePlanning.before(Date.valueOf(java.time.LocalDate.now()))) {
            showAlert("La date de planning ne peut pas être dans le passé.");
            return;
        }

        // Créer l'objet Planning
        Planning planning = new Planning(typeActivite, datePlanning, status, cours.getId_Cours());

        // Ajouter le planning via le service
        try {
            planningService.addplanning(planning);
            showAlert("Planning ajouté avec succès !");

            // Réinitialiser les champs après l'ajout
            clearFields();

            // Charger la nouvelle scène de la liste des plannings
            switchToPlanningListScene();

            // Fermer l'interface actuelle (interface d'ajout)
            closeCurrentStage(event);
        } catch (SQLException e) {
            showAlert("Erreur lors de l'ajout du planning : " + e.getMessage());
        }
    }

    private void switchToPlanningListScene() {
        try {
            // Charger la scène de la liste des plannings
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/list_planning.fxml")); // Assurez-vous que le chemin est correct
            Parent root = loader.load();

            // Créer une nouvelle scène pour la liste des plannings
            Scene scene = new Scene(root);

            // Obtenir le stage actuel (interface d'ajout)
            Stage stage = (Stage) ajouter_planning.getScene().getWindow();

            // Changer la scène pour afficher la liste des plannings
            stage.setScene(scene);
            stage.setTitle("Liste des Plannings");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur lors du chargement de la liste des plannings : " + e.getMessage());
        }
    }

    private void closeCurrentStage(ActionEvent event) {
        // Fermer la fenêtre actuelle (interface d'ajout)
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message) {
        // Afficher une alerte avec le message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }

    private void clearFields() {
        // Réinitialiser les champs après un ajout réussi
        typeactivitetextfield.clear();
        date_PlanningTextfield.setValue(null);
        courstextfield.setValue(null);
        confirmeButton.setSelected(false);
        enattentebutton.setSelected(false);
    }

    // Méthode pour gérer le clic sur le label des cours
    @FXML
    private void handleCoursLabelClick(MouseEvent event) {
        // Naviguer vers la page Ajouter Cours
        switchToScene("/Ajouter_Cours.fxml", event);
    }

    // Méthode pour gérer le clic sur le label des participants
    @FXML
    private void handleParticipantsLabelClick(MouseEvent event) {
        // Naviguer vers la page Ajouter Participant
        switchToScene("/Ajouter_Participant.fxml", event);
    }

    // Méthode générique pour changer de scène
    private void switchToScene(String fxmlFilePath, MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/list_plannning.fxml"));
            Parent root = loader.load();

            // Obtenir le stage actuel et changer la scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur lors du changement de scène : " + e.getMessage());
        }
    }
}*/

/*import edu.pidev3a32.entities.Planning;
import edu.pidev3a32.entities.Cours;
import edu.pidev3a32.services.PlanningService;
import edu.pidev3a32.services.CoursService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class Ajouterr_P {

    @FXML
    private Button ajouter_planning;

    @FXML
    private RadioButton confirmeButton;

    @FXML
    private ComboBox<Cours> courstextfield;

    @FXML
    private DatePicker date_PlanningTextfield;

    @FXML
    private RadioButton enattentebutton;

    @FXML
    private Label labelcours;

    @FXML
    private Label labelparticipants;

    @FXML
    private TextField typeactivitetextfield;

    private PlanningService planningService;

    @FXML
    public void initialize() {
        planningService = new PlanningService();
        loadCourses();
    }

    private void loadCourses() {
        CoursService coursService = new CoursService();
        List<Cours> courses = coursService.getAllCourses(); // Assurez-vous que cette méthode existe
        courstextfield.getItems().addAll(courses);
    }

    @FXML
    void addplanning(ActionEvent event) {
        // Récupérer les valeurs des champs
        String typeActivite = typeactivitetextfield.getText().trim();
        Date datePlanning;

        // Vérifier si une date a été sélectionnée
        if (date_PlanningTextfield.getValue() == null) {
            showAlert("Veuillez sélectionner une date.");
            return;
        }

        // Convertir la date sélectionnée
        datePlanning = Date.valueOf(date_PlanningTextfield.getValue());
        Cours cours = courstextfield.getValue();
        String status = confirmeButton.isSelected() ? "Confirmé" : "En Attente";

        // Validation des entrées
        if (typeActivite.isEmpty()) {
            showAlert("Le champ 'Type d'Activité' est obligatoire.");
            return;
        }
        if (cours == null) {
            showAlert("Veuillez sélectionner un cours.");
            return;
        }
        if (datePlanning.before(Date.valueOf(java.time.LocalDate.now()))) {
            showAlert("La date de planning ne peut pas être dans le passé.");
            return;
        }

        // Créer l'objet Planning
        Planning planning = new Planning(typeActivite, datePlanning, status, cours.getId_Cours());

        // Ajouter le planning via le service
        try {
            planningService.addplanning(planning);
            showAlert("Planning ajouté avec succès !");

            // Réinitialiser les champs après l'ajout
            clearFields();

            // Charger la nouvelle scène de la liste des plannings
            switchToPlanningListScene();

            // Fermer l'interface actuelle (interface d'ajout)
            closeCurrentStage(event);
        } catch (SQLException e) {
            showAlert("Erreur lors de l'ajout du planning : " + e.getMessage());
        }
    }

    private void switchToPlanningListScene() {
        try {
            // Charger la scène de la liste des plannings
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/list_plannning.fxml")); // Assurez-vous que ce fichier existe bien
            Parent root = loader.load();

            // Créer une nouvelle scène pour la liste des plannings
            Scene scene = new Scene(root);

            // Obtenir le stage actuel (interface d'ajout)
            Stage stage = (Stage) ajouter_planning.getScene().getWindow();

            // Changer la scène pour afficher la liste des plannings
            stage.setScene(scene);
            stage.setTitle("Liste des Plannings");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur lors du chargement de la liste des plannings : " + e.getMessage());
        }
    }

    private void closeCurrentStage(ActionEvent event) {
        // Fermer la fenêtre actuelle (interface d'ajout)
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message) {
        // Afficher une alerte avec le message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }

    private void clearFields() {
        // Réinitialiser les champs après un ajout réussi
        typeactivitetextfield.clear();
        date_PlanningTextfield.setValue(null);
        courstextfield.setValue(null);
        confirmeButton.setSelected(false);
        enattentebutton.setSelected(false);
    }

    // Méthode pour gérer le clic sur le label des cours
    @FXML
    private void handleCoursLabelClick(MouseEvent event) {
        // Naviguer vers la page Ajouter Cours
        switchToScene("/Ajouter_Cours.fxml", event);
    }

    // Méthode pour gérer le clic sur le label des participants
    @FXML
    private void handleParticipantsLabelClick(MouseEvent event) {
        // Naviguer vers la page Ajouter Participant
        switchToScene("/Ajouter_Participant.fxml", event);
    }

    // Méthode générique pour changer de scène
    private void switchToScene(String fxmlFilePath, MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilePath)); // Utiliser le bon fichier FXML ici
            Parent root = loader.load();

            // Obtenir le stage actuel et changer la scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur lors du changement de scène : " + e.getMessage());
        }
    }
}*/
/*import edu.pidev3a32.entities.Planning;
import edu.pidev3a32.entities.Cours;
import edu.pidev3a32.services.PlanningService;
import edu.pidev3a32.services.CoursService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

public class Ajouterr_P {

    @FXML
    private Button ajouter_planning;

    @FXML
    private RadioButton confirmeButton;

    @FXML
    private ComboBox<Cours> courstextfield;

    @FXML
    private DatePicker date_PlanningTextfield;

    @FXML
    private RadioButton enattentebutton;

    @FXML
    private Label labelcours;

    @FXML
    private Label labelparticipants;

    @FXML
    private TextField typeactivitetextfield;

    private PlanningService planningService;

    @FXML
    public void initialize() {
        planningService = new PlanningService();
        loadCourses();
    }

    private void loadCourses() {
        CoursService coursService = new CoursService();
        List<Cours> courses = coursService.getAllCourses(); // Ensure this method is correct
        courstextfield.getItems().addAll(courses);
    }

    @FXML
    void addplanning(ActionEvent event) {
        String typeActivite = typeactivitetextfield.getText().trim();
        Date datePlanning;

        // Validate the date selection
        if (date_PlanningTextfield.getValue() == null) {
            showAlert("Veuillez sélectionner une date.");
            return;
        }

        // Convert selected date to Date object
        datePlanning = Date.valueOf(date_PlanningTextfield.getValue());
        Cours cours = courstextfield.getValue();
        String status = confirmeButton.isSelected() ? "Confirmé" : "En Attente";

        // Input validation
        if (typeActivite.isEmpty()) {
            showAlert("Le champ 'Type d'Activité' est obligatoire.");
            return;
        }
        if (cours == null) {
            showAlert("Veuillez sélectionner un cours.");
            return;
        }
        if (datePlanning.before(Date.valueOf(java.time.LocalDate.now()))) {
            showAlert("La date de planning ne peut pas être dans le passé.");
            return;
        }

        // Create planning object
        Planning planning = new Planning(typeActivite, datePlanning, status, cours.getId_Cours());

        // Add planning via service
        try {
            planningService.addplanning(planning);  // Ensure this method might not throw SQLException if not needed
            showAlert("Planning ajouté avec succès !");

            // Clear fields after successful addition
            clearFields();

            // Switch to planning list scene
            switchToPlanningListScene();

            // Close current stage
            closeCurrentStage(event);
        } catch (Exception e) {  // Catch general exception if SQLException is unnecessary
            showAlert("Erreur lors de l'ajout du planning : " + e.getMessage());
        }
    }

    private void switchToPlanningListScene() {
        try {
            // Correct the path to the list_planning.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/list_plannning.fxml")); // Ensure correct path
            Parent root = loader.load();

            // Create a new scene for planning list
            Scene scene = new Scene(root);

            // Get the current stage (the add planning window)
            Stage stage = (Stage) ajouter_planning.getScene().getWindow();

            // Change the scene to the planning list
            stage.setScene(scene);
            stage.setTitle("Liste des Plannings");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur lors du chargement de la liste des plannings : " + e.getMessage());
        }
    }

    private void closeCurrentStage(ActionEvent event) {
        // Close the current window (add planning interface)
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message) {
        // Display an informational alert with the given message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }

    private void clearFields() {
        // Reset fields after a successful add
        typeactivitetextfield.clear();
        date_PlanningTextfield.setValue(null);
        courstextfield.setValue(null);
        confirmeButton.setSelected(false);
        enattentebutton.setSelected(false);
    }

    // Handle click on the course label
    @FXML
    private void handleCoursLabelClick(MouseEvent event) {
        // Navigate to the "Ajouter Cours" page
        switchToScene("/Ajouter_Cours.fxml", event);
    }

    // Handle click on the participants label
    @FXML
    private void handleParticipantsLabelClick(MouseEvent event) {
        // Navigate to the "Ajouter Participant" page
        switchToScene("/Ajouter_Participant.fxml", event);
    }

    // Generic method to switch to different scenes
    private void switchToScene(String fxmlFilePath, MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilePath)); // Use the correct FXML file path
            Parent root = loader.load();

            // Get the current stage and switch the scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur lors du changement de scène : " + e.getMessage());
        }
    }
}*/


/*import edu.pidev3a32.entities.Cours;
import edu.pidev3a32.entities.Planning;
import edu.pidev3a32.services.CoursService;
import edu.pidev3a32.services.PlanningService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.mail.*;
import javax.mail.internet.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.sql.Date;

public class Ajouterr_P {

    @FXML
    private Button ajouter_planning;

    @FXML
    private RadioButton confirmeButton;

    @FXML
    private ComboBox<Cours> courstextfield;

    @FXML
    private DatePicker date_PlanningTextfield;

    @FXML
    private RadioButton enattentebutton;

    @FXML
    private Label labelcours;

    @FXML
    private Label labelparticipants;

    @FXML
    private TextField typeactivitetextfield;

    private PlanningService planningService;

    @FXML
    public void initialize() {
        planningService = new PlanningService();
        loadCourses();
    }

    private void loadCourses() {
        CoursService coursService = new CoursService();
        List<Cours> courses = coursService.getAllCourses(); // Ensure this method is correct
        courstextfield.getItems().addAll(courses);
    }

    @FXML
    void addplanning(ActionEvent event) {
        String typeActivite = typeactivitetextfield.getText().trim();
        Date datePlanning;

        // Validate the date selection
        if (date_PlanningTextfield.getValue() == null) {
            showAlert("Veuillez sélectionner une date.");
            return;
        }

        // Convert selected date to Date object
        datePlanning = Date.valueOf(date_PlanningTextfield.getValue());
        Cours cours = courstextfield.getValue();
        String status = confirmeButton.isSelected() ? "Confirmé" : "En Attente";

        // Input validation
        if (typeActivite.isEmpty()) {
            showAlert("Le champ 'Type d'Activité' est obligatoire.");
            return;
        }
        if (cours == null) {
            showAlert("Veuillez sélectionner un cours.");
            return;
        }
        if (datePlanning.before(Date.valueOf(java.time.LocalDate.now()))) {
            showAlert("La date de planning ne peut pas être dans le passé.");
            return;
        }

        // Create planning object
        Planning planning = new Planning(typeActivite, datePlanning, status, cours.getId_Cours());

        // Add planning via service
        try {
            planningService.addplanning(planning);  // Ensure this method might not throw SQLException if not needed
            showAlert("Planning ajouté avec succès !");

            // Send reminder email for planning
            sendReminderEmail(planning);

            // Clear fields after successful addition
            clearFields();

            // Switch to planning list scene
            switchToPlanningListScene();

            // Close current stage
            closeCurrentStage(event);
        } catch (Exception e) {  // Catch general exception if SQLException is unnecessary
            showAlert("Erreur lors de l'ajout du planning : " + e.getMessage());
        }
    }

    private void sendReminderEmail(Planning planning) {
        Timer timer = new Timer();
        Date planningDate = planning.getDate_planning();

        // Calculate the time difference between now and the planning date minus one day
        long reminderTime = planningDate.getTime() - (24 * 60 * 60 * 1000); // 24 hours in milliseconds

        // Schedule the task to send an email one day before the planning date
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Email setup
                String recipientEmail = "user-email@example.com"; // Replace with actual email of the participant
                String subject = "Rappel : Votre activité est prévue pour demain";
                String body = "Ceci est un rappel pour vous informer que votre activité est prévue pour demain, le " + planningDate.toString() + ".";

                sendEmail(recipientEmail, subject, body);
            }
        }, reminderTime);
    }

    private void sendEmail(String recipientEmail, String subject, String body) {
        // Email sending setup using JavaMail API
        String senderEmail = "manel.aloui@esprit.tn"; // Your email address
        String senderPassword = "Mannoula123456"; // Your email password

        // SMTP server configurations
        String smtpHost = "smtp.example.com"; // SMTP server address (e.g., smtp.gmail.com)
        String smtpPort = "587"; // SMTP port

        Properties properties = new Properties();
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Reminder email sent to " + recipientEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void switchToPlanningListScene() {
        try {
            // Correct the path to the list_planning.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/list_plannning.fxml")); // Ensure correct path
            Parent root = loader.load();

            // Create a new scene for planning list
            Scene scene = new Scene(root);

            // Get the current stage (the add planning window)
            Stage stage = (Stage) ajouter_planning.getScene().getWindow();

            // Change the scene to the planning list
            stage.setScene(scene);
            stage.setTitle("Liste des Plannings");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur lors du chargement de la liste des plannings : " + e.getMessage());
        }
    }

    private void closeCurrentStage(ActionEvent event) {
        // Close the current window (add planning interface)
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message) {
        // Display an informational alert with the given message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }

    private void clearFields() {
        // Reset fields after a successful add
        typeactivitetextfield.clear();
        date_PlanningTextfield.setValue(null);
        courstextfield.setValue(null);
        confirmeButton.setSelected(false);
        enattentebutton.setSelected(false);
    }

    // Handle click on the course label
    @FXML
    private void handleCoursLabelClick(MouseEvent event) {
        // Navigate to the "Ajouter Cours" page
        switchToScene("/Ajouter_Cours.fxml", event);
    }

    // Handle click on the participants label
    @FXML
    private void handleParticipantsLabelClick(MouseEvent event) {
        // Navigate to the "Ajouter Participant" page
        switchToScene("/Ajouter_Participant.fxml", event);
    }

    // Generic method to switch to different scenes
    private void switchToScene(String fxmlFilePath, MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilePath)); // Use the correct FXML file path
            Parent root = loader.load();

            // Get the current stage and switch the scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur lors du changement de scène : " + e.getMessage());
        }
    }
}*/
import edu.emmapi.entities.Cours;
import edu.emmapi.entities.Planning;
import edu.emmapi.services.CoursService;
import edu.emmapi.services.PlanningService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.mail.*;
import javax.mail.internet.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

public class Ajouterr_P {

    @FXML
    private Button ajouter_planning;

    @FXML
    private RadioButton confirmeButton;

    @FXML
    private ComboBox<Cours> courstextfield;

    @FXML
    private DatePicker date_PlanningTextfield;

    @FXML
    private RadioButton enattentebutton;

    @FXML
    private Label labelcours;

    @FXML
    private Label labelparticipants;

    @FXML
    private TextField typeactivitetextfield;

    private PlanningService planningService;

    @FXML
    public void initialize() {
        planningService = new PlanningService();
        loadCourses();
    }

    private void loadCourses() {
        CoursService coursService = new CoursService();
        List<Cours> courses = coursService.getAllCourses();
        courstextfield.getItems().addAll(courses);
    }

    @FXML
    void addplanning(ActionEvent event) {
        String typeActivite = typeactivitetextfield.getText().trim();
        Date datePlanning;

        // Validation de la sélection de la date
        if (date_PlanningTextfield.getValue() == null) {
            showAlert("Veuillez sélectionner une date.");
            return;
        }

        // Conversion de la date sélectionnée en objet Date
        datePlanning = Date.valueOf(date_PlanningTextfield.getValue());
        Cours cours = courstextfield.getValue();
        String status = confirmeButton.isSelected() ? "Confirmé" : "En Attente";

        // Validation des entrées
        if (typeActivite.isEmpty()) {
            showAlert("Le champ 'Type d'Activité' est obligatoire.");
            return;
        }
        if (cours == null) {
            showAlert("Veuillez sélectionner un cours.");
            return;
        }
        if (datePlanning.before(Date.valueOf(java.time.LocalDate.now()))) {
            showAlert("La date de planning ne peut pas être dans le passé.");
            return;
        }

        // Créer l'objet planning
        Planning planning = new Planning(typeActivite, datePlanning, status, cours.getId_Cours());

        // Ajouter le planning via le service
        try {
            planningService.addplanning(planning);
            showAlert("Planning ajouté avec succès !");

            // Envoyer l'email de rappel pour ce planning
            sendReminderEmail(planning);

            // Réinitialiser les champs après ajout
            clearFields();

            // Changer de scène vers la liste des plannings
            switchToPlanningListScene();

            // Fermer la fenêtre actuelle
            closeCurrentStage(event);
        } catch (Exception e) {
            showAlert("Erreur lors de l'ajout du planning : " + e.getMessage());
        }
    }

    private void sendReminderEmail(Planning planning) {
        LocalDate planningDate = planning.getDate_planning().toLocalDate();

        // Vérifiez si la planification est pour demain
        if (planningDate.isEqual(LocalDate.now().plusDays(1))) {
            // Si la planification est pour demain, programmer l'email pour aujourd'hui à 11h29
            LocalDateTime reminderTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 39)); // 11h29 aujourd'hui

            // Afficher des messages de débogage
            System.out.println("Planning Date: " + planningDate);  // Vérification de la date du planning
            System.out.println("Current Date: " + LocalDate.now()); // Vérification de la date actuelle
            System.out.println("Reminder Time: " + reminderTime);  // Vérification du temps de rappel

            // Vérifier si le temps d'envoi est dans le futur par rapport à l'heure actuelle
            if (reminderTime.isAfter(LocalDateTime.now())) {
                long delay = ChronoUnit.MILLIS.between(LocalDateTime.now(), reminderTime); // Délai en millisecondes

                // Afficher le délai calculé
                System.out.println("Délai en millisecondes avant l'envoi de l'email : " + delay);

                // Créer un timer pour envoyer l'email au moment prévu
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            // Envoi de l'email
                            String recipientEmail = "alouimanel776@gmail.com"; // Remplacez par l'email réel
                            String subject = "Rappel : Votre activité est prévue pour demain";
                            String body = "Ceci est un rappel pour vous informer que votre activité est prévue pour demain, le " + planningDate.toString() + ".";
                            sendEmail(recipientEmail, subject, body);
                        } catch (Exception e) {
                            System.out.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
                        }
                    }
                }, delay);  // Délai avant l'envoi de l'email
            } else {
                // Si l'heure de l'envoi est déjà passée (par exemple, il est déjà après 11h29), envoyez immédiatement l'email
                sendEmail("Manel.Aloui@esprit.tn", "Rappel : Votre activité est prévue pour demain", "Ceci est un rappel pour vous informer que votre activité est prévue pour demain, le " + planningDate.toString() + ".");
            }
        }
    }

    private void sendEmail(String recipientEmail, String subject, String body) {
        // Configuration de l'envoi d'email via JavaMail
        String senderEmail = "manel.aloui@esprit.tn"; // Votre email d'expédition
        String senderPassword = "221JFT3675"; // Votre mot de passe de l'email

        String smtpHost = "smtp.gmail.com"; // SMTP de Gmail
        String smtpPort = "587"; // Port SMTP

        Properties properties = new Properties();
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(body);

            // Envoi de l'email
            Transport.send(message);
            System.out.println("Email de rappel envoyé à " + recipientEmail);
        } catch (MessagingException e) {
            e.printStackTrace(); // Imprime l'exception dans la console
            System.out.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }

    private void switchToPlanningListScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/list_plannning.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène pour la liste des plannings
            Scene scene = new Scene(root);

            // Obtenez la fenêtre actuelle (la fenêtre d'ajout de planning)
            Stage stage = (Stage) ajouter_planning.getScene().getWindow();

            // Changer de scène vers la liste des plannings
            stage.setScene(scene);
            stage.setTitle("Liste des Plannings");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur lors du chargement de la liste des plannings : " + e.getMessage());
        }
    }

    private void closeCurrentStage(ActionEvent event) {
        // Fermer la fenêtre actuelle (interface d'ajout de planning)
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message) {
        // Afficher une alerte avec le message donné
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }

    private void clearFields() {
        // Réinitialiser les champs après un ajout réussi
        typeactivitetextfield.clear();
        date_PlanningTextfield.setValue(null);
        courstextfield.setValue(null);
        confirmeButton.setSelected(false);
        enattentebutton.setSelected(false);
    }

    // Gérer le clic sur le label du cours
    @FXML
    private void handleCoursLabelClick(MouseEvent event) {
        // Naviguer vers la page "Ajouter Cours"
        switchToScene("/Ajouter_Cours.fxml", event);
    }

    // Gérer le clic sur le label des participants
    @FXML
    private void handleParticipantsLabelClick(MouseEvent event) {
        // Naviguer vers la page "Ajouter Participant"
        switchToScene("/Ajouter_Participant.fxml", event);
    }

    // Méthode générique pour changer de scène
    private void switchToScene(String fxmlFilePath, MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilePath)); // Utiliser le bon chemin FXML
            Parent root = loader.load();

            // Obtenir la fenêtre actuelle et changer de scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur lors du changement de scène : " + e.getMessage());
        }
    }
}