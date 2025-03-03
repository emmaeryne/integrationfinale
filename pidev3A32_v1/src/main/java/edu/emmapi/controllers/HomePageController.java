
package edu.emmapi.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

import java.io.IOException;

public class HomePageController {
    @FXML
    private Hyperlink courslab;

    @FXML
    private Hyperlink detaillab;

    @FXML
    private Hyperlink participantlab;

    @FXML
    private Hyperlink planninglab;

    @FXML
    private Hyperlink serviceid;

    @FXML
    private Hyperlink abonnementid;

    @FXML
    private void openServiceDashboard(ActionEvent event) {
        openDashboard("/dashboard.fxml", "Service Dashboard");
    }

    @FXML
    private void openAbonnementDashboard(ActionEvent event) {
        openDashboard("/type_abonnement.fxml", "Abonnement Dashboard");
    }



    private void openDashboard(String fxmlPath, String title) {
        try {
            System.out.println("FXML Location: " + getClass().getResource(fxmlPath));

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
            // TODO: Afficher une alerte à l'utilisateur
        }
    }

    @FXML
    public void openServiceDashboard2(ActionEvent actionEvent) {
        openDashboard("/service_view.fxml", "Service Dashboard");
    }
    @FXML
    public void opentypeDashboard2(ActionEvent actionEvent) {
        openDashboard("/AbonnementView.fxml", "Service Dashboard");
    }
}