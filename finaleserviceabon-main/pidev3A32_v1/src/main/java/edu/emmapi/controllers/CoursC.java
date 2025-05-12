package edu.emmapi.controllers;

import java.util.List;

import edu.emmapi.entities.Cours;
import edu.emmapi.services.CoursService; // Import your service class
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CoursC {

    @FXML
    private TableColumn<Cours, String> courc;   // Column for course name
    @FXML
    private TableColumn<Cours, Integer> dureec; // Column for course duration
    @FXML
    private TableColumn<Cours, String> etatc;   // Column for course status
    @FXML
    private TableView<Cours> tablec;            // Table view for courses

    private ObservableList<Cours> data = FXCollections.observableArrayList();
    private CoursService coursService = new CoursService(); // Use a service class for database operations

    @FXML
    public void initialize() {
        show(); // Populate the table on initialization

        // Add a listener for row selection
        tablec.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click to open details
                Cours selectedCourse = tablec.getSelectionModel().getSelectedItem();
                if (selectedCourse != null) {
                    openDetailView(selectedCourse);
                }
            }
        });
    }

    public void show() {
        data.clear(); // Clear previous data to avoid duplicates
        List<Cours> courses = coursService.getAllDatacour(); // Get all courses
        data.addAll(courses); // Add all courses to the observable list

        // Set cell value factories for each column
        courc.setCellValueFactory(new PropertyValueFactory<>("nomCours")); // Ensure property names match
        dureec.setCellValueFactory(new PropertyValueFactory<>("duree"));
        etatc.setCellValueFactory(new PropertyValueFactory<>("etatCours"));

        // Set the items in the TableView
        tablec.setItems(data);
    }

    public void refreshCourseList() {
        System.out.println("Refreshing course list"); // Debugging statement
        show(); // Call the show method to refresh the TableView
    }

    private void openDetailView(Cours selectedCourse) {
        try {
            // Load the Detail_C FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/detail_c.fxml"));
            Parent root = loader.load();

            // Get the controller and set the course details
            Detail_C detailController = loader.getController();
            detailController.setCourseDetails(selectedCourse, this); // Pass the current controller instance

            // Create a new stage for the details view
            Stage stage = new Stage();
            stage.setTitle("Course Details");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}