package edu.emmapi.controllers;

import edu.emmapi.entities.security_questions;
import edu.emmapi.services.SecurityQuestionDao;
import edu.emmapi.services.userService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.emmapi.tools.MyConnection;
public class ForgotPwd {

    @FXML
    private TextField Answer;
    private Map<String, Integer> questionMap = new HashMap<>(); // Map pour associer les questions à leurs ID

    @FXML
    private Button Back;

    @FXML
    private Button Procceed;

    @FXML
    private ComboBox<String> SelectQ;

    @FXML
    private TextField email;

 private final userService userService = new userService();
    @FXML
    void initialize() {
        loadSecurityQuestions();
        // Remplir le ComboBox avec des questions de sécurité en français

    }
    private void loadSecurityQuestions() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hive1", "root", "")) {
            SecurityQuestionDao securityQuestionDao = new SecurityQuestionDao(connection);
            List<security_questions> questions = securityQuestionDao.getAllSecurityQuestions();

            // Ajouter les questions et leurs ID à la Map
            for (security_questions question : questions) {
                questionMap.put(question.getQuestionText(), question.getId());
            }

            // Remplir le ComboBox avec les questions
            SelectQ.getItems().addAll(questionMap.keySet());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les questions de sécurité depuis la base de données. Vérifiez votre connexion.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void BackOnClick(ActionEvent event) {
        // Fermer la fenêtre actuelle
        Stage stage = (Stage) Back.getScene().getWindow();
        stage.close();
    }

    @FXML
    void ProcceedOnClick(ActionEvent event) throws IOException {
        String selectedQuestion = SelectQ.getValue();
        String answer = Answer.getText();
        String emailText = email.getText();

        if (selectedQuestion == null || selectedQuestion.isEmpty()) {
            showError("Veuillez sélectionner une question de sécurité.");
            return;
        }
        if (answer == null || answer.isEmpty()) {
            showError("Veuillez fournir une réponse à la question de sécurité.");
            return;
        }
        if (emailText == null || emailText.isEmpty()) {
            showError("Veuillez entrer un nom d'utilisateur.");
            return;
        }

        int securityQuestionId = questionMap.get(selectedQuestion);
        String passwd =userService.ChercherPassword(emailText,answer,selectedQuestion);
        if(passwd == null){
            showError("Question Reponse Incorrect ");
        }else {
            try {
                // Charger le fichier FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FordetPwdProceed.fxml"));
                Parent root = loader.load(); // Charger la vue

                // Récupérer le contrôleur de la nouvelle vue
                FordetPwdProceed controller = loader.getController();

                // Passer la variable au contrôleur
                controller.setEmail(emailText);

                // Récupérer la scène actuelle
                Scene currentScene = email.getScene();

                // Changer la racine de la scène
                currentScene.setRoot(root);

                // Redimensionner la fenêtre (optionnel)
                Stage stage = (Stage) currentScene.getWindow();
                stage.setWidth(485);
                stage.setHeight(662);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }



    private void showError(String message) {
        // Créer une alerte pour afficher un message d'erreur
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    // Show an alert with a given title and message
}
