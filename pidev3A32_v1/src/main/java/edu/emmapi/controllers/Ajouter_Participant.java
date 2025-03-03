/*package edu.pidev3a32.controllers;

import edu.pidev3a32.entities.Participant;
import edu.pidev3a32.services.ParticipantService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.io.IOException;

public class Ajouter_Participant {

    @FXML
    private TextField Adressetextfield;

    @FXML
    private TextField Agetextfield;

    @FXML
    private TextField Nom_textfield;

    @FXML
    private Button buttonajouterparticipant;

    @FXML
    private Label courslabel;

    @FXML
    private Label participerlabel;

    @FXML
    private Label planninglabel;

    @FXML
    private TextField prenomtextfield;

    @FXML
    private TextField teltextfield;

    private ParticipantService participantService = new ParticipantService(); // Create service instance

    // Handle the add participant button click
    @FXML
    void handleAjouter(ActionEvent event) {
        try {
            // Get values from TextFields
            String nom = Nom_textfield.getText().trim();
            String prenom = prenomtextfield.getText().trim();
            String ageText = Agetextfield.getText().trim();
            String adresse = Adressetextfield.getText().trim();
            String tel = teltextfield.getText().trim();

            // Check if any field is empty
            if (nom.isEmpty() || prenom.isEmpty() || ageText.isEmpty() || adresse.isEmpty() || tel.isEmpty()) {
                showAlert("Error", "Please fill all the fields.");
                return;
            }

            // Validate age input
            int age;
            try {
                age = Integer.parseInt(ageText);
            } catch (NumberFormatException e) {
                showAlert("Error", "Please enter a valid age.");
                return;
            }

            // Create a new participant object
            Participant participant = new Participant(nom, prenom, age, adresse, tel);

            // Call the service to add the participant to the database
            participantService.addparticipant(participant);

            // Show success message
            showAlert("Success", "Participant added successfully.");

            // Optionally clear the fields after adding
            clearFields();

            // Switch to the "List_Participant" interface and close the current window
            switchToParticipantListScene(event);
            closeCurrentWindow(event);

        } catch (Exception e) {
            showAlert("Error", "Failed to add participant: " + e.getMessage());
        }
    }

    // Show an alert with the specified title and message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Clear all text fields in the form
    private void clearFields() {
        Nom_textfield.clear();
        prenomtextfield.clear();
        Agetextfield.clear();
        Adressetextfield.clear();
        teltextfield.clear();
    }

    // Switch to the "List_Participant" scene
    private void switchToParticipantListScene(ActionEvent event) {
        try {
            // Load the "List_Participant" scene (replace with your actual FXML file path)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/list_participant.fxml"));
            Parent root = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show(); // Display the new scene
        } catch (IOException e) {
            showAlert("Error", "Failed to navigate to the participant list: " + e.getMessage());
        }
    }

    // Close the current window
    private void closeCurrentWindow(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close(); // Close the current window
    }

    @FXML
    void handleCoursLabelClick(MouseEvent event) {
        // Handle click on the courslabel if needed
    }

    @FXML
    void handleParticipantLabelClick(MouseEvent event) {
        // Handle click on the participerlabel if needed
    }

    @FXML
    void handlePlanningLabelClick(MouseEvent event) {
        // Handle click on the planninglabel if needed
    }
}*/
/*package edu.pidev3a32.controllers;

import edu.pidev3a32.entities.Participant;
import edu.pidev3a32.services.ParticipantService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class Ajouter_Participant {

    @FXML
    private TextField Adressetextfield;

    @FXML
    private TextField Agetextfield;

    @FXML
    private TextField Nom_textfield;

    @FXML
    private Button buttonajouterparticipant;

    @FXML
    private Label courslabel;

    @FXML
    private Label participerlabel;

    @FXML
    private Label planninglabel;

    @FXML
    private TextField prenomtextfield;

    @FXML
    private TextField teltextfield;

    private ParticipantService participantService = new ParticipantService(); // Create service instance

    // Handle the add participant button click
    @FXML
    void handleAjouter(ActionEvent event) {
        try {
            // Get values from TextFields
            String nom = Nom_textfield.getText().trim();
            String prenom = prenomtextfield.getText().trim();
            String ageText = Agetextfield.getText().trim();
            String adresse = Adressetextfield.getText().trim();
            String tel = teltextfield.getText().trim();

            // Check if any field is empty
            if (nom.isEmpty() || prenom.isEmpty() || ageText.isEmpty() || adresse.isEmpty() || tel.isEmpty()) {
                showAlert("Error", "Please fill all the fields.");
                return;
            }

            // Validate age input
            int age;
            try {
                age = Integer.parseInt(ageText);
            } catch (NumberFormatException e) {
                showAlert("Error", "Please enter a valid age.");
                return;
            }

            // Create a new participant object
            Participant participant = new Participant(nom, prenom, age, adresse, tel);

            // Call the service to add the participant to the database
            participantService.addparticipant(participant);

            // Show success message
            showAlert("Success", "Participant added successfully.");

            // Optionally clear the fields after adding
            clearFields();

            // Switch to the "List_Participant" interface
            switchToParticipantListScene(event);

        } catch (Exception e) {
            showAlert("Error", "Failed to add participant: " + e.getMessage());
        }
    }

    // Show an alert with the specified title and message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Clear all text fields in the form
    private void clearFields() {
        Nom_textfield.clear();
        prenomtextfield.clear();
        Agetextfield.clear();
        Adressetextfield.clear();
        teltextfield.clear();
    }

    // Switch to the "List_Participant" scene
    private void switchToParticipantListScene(ActionEvent event) {
        try {
            // Load the "List_Participant" scene (replace with your actual FXML file path)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/list_participant.fxml"));
            Parent root = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show(); // Display the new scene
        } catch (IOException e) {
            showAlert("Error", "Failed to navigate to the participant list: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
        }
    }
}*/
package edu.emmapi.controllers;

import edu.emmapi.entities.Participant;
import edu.emmapi.services.ParticipantService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;  // Import MouseEvent

import java.io.IOException;

public class Ajouter_Participant {

    @FXML
    private TextField Adressetextfield;

    @FXML
    private TextField Agetextfield;

    @FXML
    private TextField Nom_textfield;

    @FXML
    private Button buttonajouterparticipant;

    @FXML
    private Label courslabel;

    @FXML
    private Label participerlabel;

    @FXML
    private Label planninglabel;

    @FXML
    private TextField prenomtextfield;

    @FXML
    private TextField teltextfield;

    private ParticipantService participantService = new ParticipantService(); // Create service instance

    // Handle the add participant button click
    @FXML
    void handleAjouter(ActionEvent event) {
        try {
            // Get values from TextFields
            String nom = Nom_textfield.getText().trim();
            String prenom = prenomtextfield.getText().trim();
            String ageText = Agetextfield.getText().trim();
            String adresse = Adressetextfield.getText().trim();
            String tel = teltextfield.getText().trim();

            // Check if any field is empty
            if (nom.isEmpty() || prenom.isEmpty() || ageText.isEmpty() || adresse.isEmpty() || tel.isEmpty()) {
                showAlert("Error", "Please fill all the fields.");
                return;
            }

            // Validate age input
            int age;
            try {
                age = Integer.parseInt(ageText);
            } catch (NumberFormatException e) {
                showAlert("Error", "Please enter a valid age.");
                return;
            }

            // Create a new participant object
            Participant participant = new Participant(nom, prenom, age, adresse, tel);

            // Call the service to add the participant to the database
            participantService.addparticipant(participant);

            // Show success message
            showAlert("Success", "Participant added successfully.");

            // Optionally clear the fields after adding
            clearFields();

            // Switch to the "List_Participant" interface
            switchToParticipantListScene(event);

        } catch (Exception e) {
            showAlert("Error", "Failed to add participant: " + e.getMessage());
        }
    }

    // Show an alert with the specified title and message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Clear all text fields in the form
    private void clearFields() {
        Nom_textfield.clear();
        prenomtextfield.clear();
        Agetextfield.clear();
        Adressetextfield.clear();
        teltextfield.clear();
    }

    // Switch to the "List_Participant" scene
    private void switchToParticipantListScene(ActionEvent event) {
        try {
            // Load the "List_Participant" scene (replace with your actual FXML file path)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/list_participant.fxml"));
            Parent root = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show(); // Display the new scene
        } catch (IOException e) {
            showAlert("Error", "Failed to navigate to the participant list: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
        }
    }

    // Event handler for the 'courslabel' click (Navigate to course page)
    @FXML
    void handleCoursLabelClick(MouseEvent event) {
        navigateToPage("/Ajouter_Cours.fxml", event);
    }

    // Event handler for the 'participerlabel' click (Navigate to participant page)
    @FXML
    void handleParticipantLabelClick(MouseEvent event) {
        navigateToPage("/Ajouter_Cours_Participant.fxml", event);
    }

    // Event handler for the 'planninglabel' click (Navigate to planning page)
    @FXML
    void handlePlanningLabelClick(MouseEvent event) {
        navigateToPage("/Ajouter_Planning.fxml", event);
    }

    // General method to load and navigate to a specific page
    private void navigateToPage(String fxmlFilePath, MouseEvent event) {
        try {
            // Load the FXML page
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilePath));
            Parent root = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show(); // Display the new scene
        } catch (IOException e) {
            showAlert("Error", "Failed to navigate to the page: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
        }
    }
}

