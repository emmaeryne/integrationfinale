package edu.emmapi.controllers;

import edu.emmapi.services.userService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class FordetPwdProceed {

    @FXML
    private Button Back;

    @FXML
    private Button Procceed;

    @FXML
    private TextField pwd;

    @FXML
    private TextField pwdConfirm;

    @FXML
    private Label errorLabel; // Label pour afficher les erreurs

    String email;
    private final userService userService = new userService();

    @FXML
    public void initialize() {
        // Listener pour vérifier la longueur du mot de passe
        pwd.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.length() < 8) {
                    pwd.setStyle("-fx-border-color: red;"); // Bordure rouge si moins de 8 caractères
                    errorLabel.setText("Le mot de passe doit contenir au moins 8 caractères.");
                } else {
                    pwd.setStyle("-fx-border-color: green;"); // Bordure verte si valide
                    errorLabel.setText(""); // Effacer le message d'erreur
                }
            }
        });

        // Listener pour vérifier la correspondance des mots de passe
        pwdConfirm.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.equals(pwd.getText())) {
                    pwdConfirm.setStyle("-fx-border-color: red;"); // Bordure rouge si les mots de passe ne correspondent pas
                    errorLabel.setText("Les mots de passe ne correspondent pas.");
                } else {
                    pwdConfirm.setStyle("-fx-border-color: green;"); // Bordure verte si valide
                    errorLabel.setText(""); // Effacer le message d'erreur
                }
            }
        });
    }

    @FXML
    void BackOnClick(ActionEvent event) {
        // Code pour revenir à la vue précédente
    }

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt()); // Hacher le mot de passe
    }

    @FXML
    void ProcceedOnClick(ActionEvent event) throws SQLException {
        String pwdConfirmText = this.pwdConfirm.getText();
        String pwdText = this.pwd.getText();

        if (pwdText.isEmpty() || pwdConfirmText.isEmpty()) {
            errorLabel.setText("Les champs ne peuvent pas être vides.");
        } else if (pwdText.length() < 8) {
            errorLabel.setText("Le mot de passe doit contenir au moins 8 caractères.");
        } else if (!pwdText.equals(pwdConfirmText)) {
            errorLabel.setText("Les mots de passe ne correspondent pas.");
        } else {
            String hashedPassword = hashPassword(pwdText);
            userService.updatePwd(email, hashedPassword); // Mettre à jour le mot de passe dans la base de données
           // errorLabel.setText("Mot de passe mis à jour avec succès !");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Mot de passe mis à jour avec succès !");
            errorLabel.setTextFill(javafx.scene.paint.Color.GREEN); // Changer la couleur du texte en vert
            System.out.println("Mot de passe haché : " + hashedPassword);
        }
    }

    public void setEmail(String emailText) {
        email = emailText;
        System.out.println("Email reçu : " + email);
    }
}