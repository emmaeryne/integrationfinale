package edu.emmapi.controllers;

import edu.emmapi.entities.Cours;
import edu.emmapi.services.CoursService;
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

public class Detail_C {

    @FXML
    private Label dureelabel;

    @FXML
    private Label etatlabel;

    @FXML
    private Button modifiercoursbutton;

    @FXML
    private Label nom_courslabel;

    @FXML
    private Button supprimercoursbutton;

    private Cours currentCourse; // To hold the current course details
    private CoursService coursService = new CoursService(); // Create an instance of your service
    private CoursC coursCController; // Reference to the course list controller

    public void setCourseDetails(Cours course, CoursC controller) {
        this.currentCourse = course; // Store the current course
        this.coursCController = controller; // Set the course list controller reference
        nom_courslabel.setText(course.getNom_Cours());
        dureelabel.setText(String.valueOf(course.getDuree()));
        etatlabel.setText(course.getEtat_Cours());
    }

    @FXML
    private void deleteCourse(ActionEvent event) {
        // Confirmation dialog before deletion
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Delete Course");
        alert.setContentText("Are you sure you want to delete this course?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Attempt to delete the course
                    coursService.deletecour(currentCourse); // Call the delete method on the service
                    showAlert("Success", "Course deleted successfully."); // Show success message
                    coursCController.refreshCourseList(); // Refresh the course list
                    closeCurrentWindow(event); // Close the current detail window
                } catch (Exception e) {
                    showAlert("Error", "Course deletion failed: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void openModifierView(ActionEvent event) {
        try {
            // Close the current Detail_C window
            closeCurrentWindow(event);

            // Load the Modifier_C FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Modifiercours.fxml"));
            Parent root = loader.load();

            // Get the controller and set the course details
            Modifier_C modifierController = loader.getController();
            modifierController.setCourseDetails(currentCourse, coursCController); // Pass the current course and controller

            // Create a new stage for the modifier view
            Stage stage = new Stage();
            stage.setTitle("Modifier Course");
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