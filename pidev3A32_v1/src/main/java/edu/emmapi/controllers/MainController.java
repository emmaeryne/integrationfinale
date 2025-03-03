package edu.pidev3a32.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainController {

    @FXML
    private void goBack(ActionEvent event) {
        loadScene(event, "/views/MainView.fxml");
    }

    @FXML
    private void goToProduits(ActionEvent event) {
        loadScene(event, "/views/ProduitView.fxml");
    }

    @FXML
    private void goToCommandes(ActionEvent event) {
        loadScene(event, "/views/CommandeView.fxml");
    }

    @FXML
    private void goToPaiements(ActionEvent event) {
        loadScene(event, "/views/PaiementView.fxml");
    }

    @FXML
    private void goToBackOffice(ActionEvent event) {
        loadScene(event, "/views/BackView.fxml");
    }

    private void loadScene(ActionEvent event, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Get the stage from the event source (button)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 500));  // Set new scene
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
