package edu.emmapi.controllers;

import edu.emmapi.services.OwnerService;
import edu.emmapi.services.userService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class firstInterface {

    @FXML
    private Button ClientButton;

    @FXML
    private Button adminButton;

    @FXML
    private Button ownerButton;

    @FXML
    private Text title;
    final private userService userService = new userService();
    final private OwnerService ownerService = new OwnerService();

    @FXML
    void sinscrireOwner(ActionEvent event) {

        try {

            Parent root = FXMLLoader.load(getClass().getResource("/SignUpOwner.fxml"));

            Scene currentScene = title.getScene();

            currentScene.setRoot(root);

            Stage stage = (Stage) currentScene.getWindow();
            stage.setWidth(600);
            stage.setHeight(720);  //
            stage.setResizable(false);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }


    public void sinscrireUser(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/SignUpClient.fxml"));

            Scene currentScene = title.getScene();

            currentScene.setRoot(root);

            Stage stage = (Stage) currentScene.getWindow();
            stage.setWidth(600);
            stage.setHeight(1000);  //
            stage.setResizable(false);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    public void sinscrireCoach(ActionEvent actionEvent) throws IOException {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/OwnerAddCoach.fxml"));

            Scene currentScene = title.getScene();

            currentScene.setRoot(root);

            Stage stage = (Stage) currentScene.getWindow();
            stage.setWidth(600);
            stage.setHeight(1000);  //
            stage.setResizable(false);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}



