package edu.emmapi.controllers;

import edu.emmapi.entities.Participant;
import edu.emmapi.services.ParticipantService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Detail_Participant {

    @FXML
    private Label Agelabel;

    @FXML
    private Label NOMlabel;

    @FXML
    private Label adresse_label;

    @FXML
    private Button modifierbuttdetail;

    @FXML
    private Label prenomlabel;

    @FXML
    private Button supprimerbuttondetail;

    @FXML
    private Label tellabel;

    private Participant currentParticipant; // To hold the current participant details
    private ParticipantService participantService = new ParticipantService(); // Create an instance of your service
    private Liste_Participant listeParticipantController; // Reference to the participant list controller

    public void setParticipantDetails(Participant participant, Liste_Participant controller) {
        this.currentParticipant = participant; // Store the current participant
        this.listeParticipantController = controller; // Set the participant list controller reference
        NOMlabel.setText(participant.getNom()); // Set name
        prenomlabel.setText(participant.getPrenom()); // Set surname
        Agelabel.setText(String.valueOf(participant.getAge())); // Set age
        adresse_label.setText(participant.getAdresse()); // Set address
        tellabel.setText(String.valueOf(participant.getTel())); // Set telephone number
    }

    @FXML
    private void deleteParticipant(ActionEvent event) {
        // Confirmation dialog before deletion
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Delete Participant");
        alert.setContentText("Are you sure you want to delete this participant?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Attempt to delete the participant
                    participantService.deleteparticipant(currentParticipant); // Call the delete method on the service
                    showAlert("Success", "Participant deleted successfully."); // Show success message
                    listeParticipantController.refreshParticipantList(); // Refresh the participant list
                    closeCurrentWindow(event); // Close the current detail window
                } catch (Exception e) {
                    showAlert("Error", "Participant deletion failed: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void openModifierView(ActionEvent event) {
        try {
            // Close the current Detail_Participant window
            closeCurrentWindow(event);

            // Load the Modifier_Participant FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifier_participant.fxml"));
            Parent root = loader.load();

            // Get the controller and set the participant details
            Modifier_Participant modifierController = loader.getController();
            modifierController.setParticipantDetails(currentParticipant, listeParticipantController); // Pass the current participant and controller

            // Create a new stage for the modifier view
            Stage stage = new Stage();
            stage.setTitle("Modifier Participant");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            showAlert("Error", "Failed to open modifier view: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeCurrentWindow(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close(); // Close the current detail window
    }
}
