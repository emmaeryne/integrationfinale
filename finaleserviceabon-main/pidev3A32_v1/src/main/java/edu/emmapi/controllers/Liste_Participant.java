


/*package edu.pidev3a32.controllers;

import edu.pidev3a32.entities.Participant;
import edu.pidev3a32.services.ParticipantService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

public class Liste_Participant {

    @FXML
    private TableColumn<Participant, String> adresep;

    @FXML
    private TableColumn<Participant, Integer> agep;

    @FXML
    private Label courslabel;

    @FXML
    private Label detaillabel;

    @FXML
    private TableView<Participant> listpart;

    @FXML
    private TableColumn<Participant, String> nomp;

    @FXML
    private Label participantlabel;

    @FXML
    private Label planninglabel;

    @FXML
    private TableColumn<Participant, String> prenomp;

    @FXML
    private TableColumn<Participant, String> telp;

    private ParticipantService participantService; // Service for obtaining participants

    @FXML
    public void initialize() throws SQLException {
        participantService = new ParticipantService(); // Initialize the service

        // Configure the columns of the TableView
        nomp.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomp.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        agep.setCellValueFactory(new PropertyValueFactory<>("age"));
        adresep.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        telp.setCellValueFactory(new PropertyValueFactory<>("tel"));

        // Load the data into the TableView
        loadParticipantData();
    }

    private void loadParticipantData() throws SQLException {
        // Create an observable list for the participants
        ObservableList<Participant> participantList = FXCollections.observableArrayList(participantService.getAllDataparticipant());

        // Assign the list to the TableView
        listpart.setItems(participantList);
    }

    // Method to refresh the participant list
    public void refreshParticipantList() {
        try {
            // Reload the participant data and update the TableView
            loadParticipantData();
        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally, show an alert to the user in case of an error
        }
    }
}*/
package edu.emmapi.controllers;

import edu.emmapi.entities.Participant;
import edu.emmapi.services.ParticipantService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import java.sql.SQLException;

public class Liste_Participant {

    @FXML
    private TableColumn<Participant, String> adresep;

    @FXML
    private TableColumn<Participant, Integer> agep;

    @FXML
    private Label courslabel;

    @FXML
    private Label detaillabel;

    @FXML
    private TableView<Participant> listpart;

    @FXML
    private TableColumn<Participant, String> nomp;

    @FXML
    private Label participantlabel;

    @FXML
    private Label planninglabel;

    @FXML
    private TableColumn<Participant, String> prenomp;

    @FXML
    private TableColumn<Participant, String> telp;

    private ParticipantService participantService; // Service for obtaining participants

    @FXML
    public void initialize() throws SQLException {
        participantService = new ParticipantService(); // Initialize the service

        // Configure the columns of the TableView
        nomp.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomp.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        agep.setCellValueFactory(new PropertyValueFactory<>("age"));
        adresep.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        telp.setCellValueFactory(new PropertyValueFactory<>("tel"));

        // Load the data into the TableView
        loadParticipantData();

        // Set a listener on the TableView selection
        listpart.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                openDetailView(newValue);  // Open the detail view when a participant is selected
            }
        });
    }

    private void loadParticipantData() throws SQLException {
        // Create an observable list for the participants
        ObservableList<Participant> participantList = FXCollections.observableArrayList(participantService.getAllDataparticipant());

        // Assign the list to the TableView
        listpart.setItems(participantList);
    }

    // Method to open the Detail_Participant view and pass selected participant data
    private void openDetailView(Participant selectedParticipant) {
        try {
            // Load the Detail_Participant FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Detail_Participant.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the participant details
            Detail_Participant detailController = loader.getController();
            detailController.setParticipantDetails(selectedParticipant, this);  // Pass selected participant and the current controller

            // Create a new stage for the Detail_Participant view
            Stage stage = new Stage();
            stage.setTitle("Participant Details");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();  // Optionally handle the exception with an alert or logging
        }
    }

    // Method to refresh the participant list
    public void refreshParticipantList() {
        try {
            // Reload the participant data and update the TableView
            loadParticipantData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


