package edu.emmapi.controllers;

import edu.emmapi.entities.Cours;
import edu.emmapi.services.CoursService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;
import java.util.stream.Collectors;

public class CoursesCardsController extends CoursC {

    @FXML
    private FlowPane coursesFlowPane; // FlowPane to hold course cards
    @FXML
    private TextField searchField; // Search field for filtering by course name
    @FXML
    private ComboBox<String> etatFilter; // ComboBox for filtering by course status

    private ObservableList<Cours> data = FXCollections.observableArrayList();
    private CoursService coursService = new CoursService(); // Service class for database operations

    @FXML
    public void initialize() {
        // Populate the ComboBox with status options
        etatFilter.getItems().addAll("Tous", "Actif", "Inactif", "En attente");
        etatFilter.setValue("Tous");

        // Add listeners for search and filter
        searchField.textProperty().addListener((obs, oldValue, newValue) -> filterCourses());
        etatFilter.valueProperty().addListener((obs, oldValue, newValue) -> filterCourses());

        // Populate the cards
        show();
    }

    public void show() {
        data.clear(); // Clear previous data
        List<Cours> courses = coursService.getAllDatacour(); // Get all courses
        data.addAll(courses); // Add to observable list
        displayCourses(data); // Display all courses
    }

    private void filterCourses() {
        String searchText = searchField.getText().toLowerCase();
        String selectedEtat = etatFilter.getValue();

        List<Cours> filteredCourses = data.stream()
                .filter(course -> course.getNom_Cours().toLowerCase().contains(searchText))
                .filter(course -> selectedEtat.equals("Tous") || course.getEtat_Cours().equals(selectedEtat))
                .collect(Collectors.toList());

        displayCourses(FXCollections.observableArrayList(filteredCourses));
    }

    private void displayCourses(ObservableList<Cours> courses) {
        coursesFlowPane.getChildren().clear(); // Clear existing cards

        for (Cours course : courses) {
            // Create a card (VBox) for each course
            VBox card = new VBox(10);
            card.setStyle("-fx-background-color: #3B4252; -fx-border-color: #4C566A; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 15;");
            card.setPrefWidth(300);
            card.setPrefHeight(150);

            // Course details
            Label nameLabel = new Label("Nom: " + course.getNom_Cours());
            nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #D8DEE9;");

            Label durationLabel = new Label("Durée: " + course.getDuree() + " heures");
            durationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #D8DEE9;");

            Label statusLabel = new Label("État: " + course.getEtat_Cours());
            statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #D8DEE9;");

            card.getChildren().addAll(nameLabel, durationLabel, statusLabel);

            // Make card clickable to open details
            card.setOnMouseClicked((MouseEvent event) -> openDetailView(course));

            coursesFlowPane.getChildren().add(card);
        }
    }

    private void openDetailView(Cours selectedCourse) {
        try {
            // Load the Detail_C FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/detail_c.fxml"));
            Parent root = loader.load();

            // Get the controller and set the course details
            Detail_C detailController = loader.getController();
            detailController.setCourseDetails(selectedCourse, this);

            // Create a new stage for the details view
            Stage stage = new Stage();
            stage.setTitle("Course Details");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshCourseList() {
        System.out.println("Refreshing course list");
        show();
    }
}

