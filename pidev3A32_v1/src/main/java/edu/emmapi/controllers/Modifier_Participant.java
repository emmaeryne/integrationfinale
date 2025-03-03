package edu.emmapi.controllers;

import edu.emmapi.entities.Participant;
import edu.emmapi.services.ParticipantService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class Modifier_Participant {

    @FXML
    private TextField adresserextfieldmodif; // TextField for address

    @FXML
    private TextField agetextfieldmodif; // TextField for age

    @FXML
    private TextField nomtextfielmodif; // TextField for first name

    @FXML
    private Button okmodif; // Button to confirm modifications

    @FXML
    private TextField prenomtextfieldmodif; // TextField for last name

    @FXML
    private TextField teltextfieldmodif; // TextField for phone number

    private Participant currentParticipant; // To hold the participant being modified
    private Liste_Participant listeParticipantController; // Reference to the participant list controller
    private ParticipantService participantService = new ParticipantService(); // Create service instance

    // Method to set the current participant details
    public void setParticipantDetails(Participant participant, Liste_Participant controller) {
        this.currentParticipant = participant; // Store the current participant
        this.listeParticipantController = controller; // Set the participant list controller reference
        nomtextfielmodif.setText(participant.getNom()); // Set first name
        prenomtextfieldmodif.setText(participant.getPrenom()); // Set last name
        agetextfieldmodif.setText(String.valueOf(participant.getAge())); // Set age
        adresserextfieldmodif.setText(participant.getAdresse()); // Set address
        teltextfieldmodif.setText(String.valueOf(participant.getTel())); // Set phone number
    }

    @FXML
    private void handleModifier() {
        try {
            // Update participant details from text fields
            currentParticipant.setNom(nomtextfielmodif.getText());
            currentParticipant.setPrenom(prenomtextfieldmodif.getText());
            currentParticipant.setAge(Integer.parseInt(agetextfieldmodif.getText()));
            currentParticipant.setAdresse(adresserextfieldmodif.getText());
            currentParticipant.setTel(String.valueOf(Integer.parseInt(teltextfieldmodif.getText())));

            // Call the service to update the participant in the database
            participantService.updateparticipant(currentParticipant);

            // Refresh the participant list in the previous controller
            //listeParticipantController.refreshParticipantList();

            // Show success message
            showAlert("Success", "Participant updated successfully.");

            // Close all open windows (like detail view and modifier view)
            closeAllWindows();

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid age.");
        } catch (Exception e) {
            showAlert("Error", "Failed to update participant: " + e.getMessage());
        }
    }

    private void closeAllWindows() {
        Stage stage = (Stage) okmodif.getScene().getWindow();
        stage.close(); // Close the modifier window
        // You may need to implement logic to close other open windows if necessary
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
