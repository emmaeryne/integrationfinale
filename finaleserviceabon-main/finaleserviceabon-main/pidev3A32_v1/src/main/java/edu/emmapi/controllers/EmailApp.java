package edu.emmapi.controllers;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EmailApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mindhive/tn/gui/email.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Envoyer un Email");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to send emails directly
    public void sendEmail(String to, String subject, String content) {
        // Create an instance of ServiceMail (your actual email service)
        ServiceMail emailService = new ServiceMail();
        // Call the sendEmail method on the ServiceMail instance
        emailService.sendEmail(to, subject, content);
    }

    public static void main(String[] args) {
        launch(args);
    }
}