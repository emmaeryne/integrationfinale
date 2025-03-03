package edu.emmapi.controllers;

import edu.emmapi.entities.Cours;
import edu.emmapi.services.CoursService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class Modifier_C {

    @FXML
    private TextField Dureecoursmodif; // TextField for course duration

    @FXML
    private Button buttonokmodif; // Button to confirm modifications

    @FXML
    private TextField etatcoursmodif; // TextField for course status

    @FXML
    private TextField nomcoursmodif; // TextField for course name

    private Cours currentCourse; // To hold the course being modified
    private CoursC coursCController; // Reference to the course list controller
    private CoursService coursService = new CoursService(); // Create service instance

    // Method to set the current course details
    public void setCourseDetails(Cours course, CoursC controller) {
        this.currentCourse = course; // Store the current course
        this.coursCController = controller; // Set the course list controller reference
        nomcoursmodif.setText(course.getNom_Cours());
        Dureecoursmodif.setText(String.valueOf(course.getDuree()));
        etatcoursmodif.setText(course.getEtat_Cours());
    }

    @FXML
    private void handleModifier() {
        try {
            // Update course details from text fields
            currentCourse.setNom_Cours(nomcoursmodif.getText());
            currentCourse.setDuree(Integer.parseInt(Dureecoursmodif.getText()));
            currentCourse.setEtat_Cours(etatcoursmodif.getText());

            // Call the service to update the course in the database
            coursService.updatecour(currentCourse);

            // Refresh the course list in the previous controller
            coursCController.refreshCourseList();

            // Show success message
            showAlert("Success", "Course updated successfully.");

            // Close all open windows (like detail view and modifier view)
            closeAllWindows();

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid duration.");
        } catch (Exception e) {
            showAlert("Error", "Failed to update course: " + e.getMessage());
        }
    }

    private void closeAllWindows() {
        Stage stage = (Stage) buttonokmodif.getScene().getWindow();
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