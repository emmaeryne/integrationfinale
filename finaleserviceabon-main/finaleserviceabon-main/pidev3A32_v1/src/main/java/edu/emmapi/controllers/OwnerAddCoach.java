package edu.emmapi.controllers;

import edu.emmapi.entities.Coach;
import edu.emmapi.entities.security_questions;
import edu.emmapi.services.SecurityQuestionDao;
import edu.emmapi.services.userService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OwnerAddCoach {

    @FXML
    private TextField CertificationField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField password2Field;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextField specialityField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField yearOfExperienceField;

    @FXML
    private Label usernameErrorLabel;

    @FXML
    private Label emailErrorLabel;

    @FXML
    private Label passwordErrorLabel;

    @FXML
    private Label password2ErrorLabel;

    @FXML
    private Label phoneNumberErrorLabel;

    @FXML
    private Label specialityErrorLabel;

    @FXML
    private Label yearOfExperienceErrorLabel;

    @FXML
    private Label certificationErrorLabel;

    @FXML
    private ComboBox<String> SelectQ;

    @FXML
    private TextField Answer;

    private final userService userservice = new userService();
    private final Map<String, Integer> questionMap = new HashMap<>();

    @FXML
    public void initialize() {
        loadSecurityQuestions();

        // Add listeners for field validation
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> validateUsername(newValue));
        emailField.textProperty().addListener((observable, oldValue, newValue) -> validateEmail(newValue));
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> validatePassword(newValue));
        password2Field.textProperty().addListener((observable, oldValue, newValue) -> validatePasswordConfirmation(newValue));
        phoneNumberField.textProperty().addListener((observable, oldValue, newValue) -> validatePhoneNumber(newValue));
        specialityField.textProperty().addListener((observable, oldValue, newValue) -> validateSpeciality(newValue));
        yearOfExperienceField.textProperty().addListener((observable, oldValue, newValue) -> validateYearOfExperience(newValue));
        CertificationField.textProperty().addListener((observable, oldValue, newValue) -> validateCertification(newValue));
    }

    private void validateUsername(String username) {
        if (username.isEmpty()) {
            setError(usernameErrorLabel, "Le nom d'utilisateur ne peut pas être vide.");
        } else {
            setSuccess(usernameErrorLabel, "Nom d'utilisateur valide.");
        }
    }

    private void validateEmail(String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            setError(emailErrorLabel, "L'email n'est pas valide.");
        } else {
            setSuccess(emailErrorLabel, "Email valide.");
        }
    }

    private void validatePassword(String password) {
        if (password.length() < 8) {
            setError(passwordErrorLabel, "Le mot de passe doit contenir au moins 8 caractères.");
        } else {
            setSuccess(passwordErrorLabel, "Mot de passe valide.");
        }
    }

    private void validatePasswordConfirmation(String passwordConfirmation) {
        if (!passwordConfirmation.equals(passwordField.getText())) {
            setError(password2ErrorLabel, "Les mots de passe ne correspondent pas.");
        } else {
            setSuccess(password2ErrorLabel, "Les mots de passe correspondent.");
        }
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (!phoneNumber.matches("^[0-9]{8}$")) {
            setError(phoneNumberErrorLabel, "Le numéro de téléphone doit contenir 8 chiffres.");
        } else {
            setSuccess(phoneNumberErrorLabel, "Numéro valide.");
        }
    }

    private void validateSpeciality(String speciality) {
        if (speciality.isEmpty()) {
            setError(specialityErrorLabel, "La spécialité ne peut pas être vide.");
        } else {
            setSuccess(specialityErrorLabel, "Spécialité valide.");
        }
    }

    private void validateYearOfExperience(String yearOfExperience) {
        try {
            int years = Integer.parseInt(yearOfExperience);
            if (years < 0 || years > 20) {
                setError(yearOfExperienceErrorLabel, "L'année d'expérience doit être entre 0 et 20.");
            } else {
                setSuccess(yearOfExperienceErrorLabel, "Année d'expérience valide.");
            }
        } catch (NumberFormatException e) {
            setError(yearOfExperienceErrorLabel, "L'année d'expérience doit être un nombre.");
        }
    }

    private void validateCertification(String certification) {
        if (certification.isEmpty()) {
            setError(certificationErrorLabel, "La certification ne peut pas être vide.");
        } else {
            setSuccess(certificationErrorLabel, "Certification valide.");
        }
    }

    private void setError(Label label, String message) {
        label.setText(message);
        label.setStyle("-fx-text-fill: red");
    }

    private void setSuccess(Label label, String message) {
        label.setText(message);
        label.setStyle("-fx-text-fill: green");
    }

//    private boolean verifyEmailWithHunter(String email) {
//        OkHttpClient client = new OkHttpClient();
//        String apiKey = "51ebed6f136f3456dc8bb794c07d8270788706fa"; // Replace with environment variable
//
//        String url = "https://api.hunter.io/v2/email-verifier";
//        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
//        urlBuilder.addQueryParameter("email", email);
//        urlBuilder.addQueryParameter("api_key", apiKey);
//        String finalUrl = urlBuilder.build().toString();
//
//        Request request = new Request.Builder().url(finalUrl).build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (response.isSuccessful()) {
//                String responseBody = response.body().string();
//                System.out.println("Réponse de l'API : " + responseBody);
//
//                JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
//                String status = jsonResponse.getAsJsonObject("data").get("status").getAsString();
//
//                return "valid".equals(status) && "deliverable".equals(jsonResponse.getAsJsonObject("data").get("result").getAsString());
//            } else {
//                System.out.println("Erreur de l'API : " + response.code() + " - " + response.message());
//                return false;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    private void loadSecurityQuestions() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hive1", "root", "")) {
            SecurityQuestionDao securityQuestionDao = new SecurityQuestionDao(connection);
            List<security_questions> questions = securityQuestionDao.getAllSecurityQuestions();

            questionMap.clear();
            SelectQ.getItems().clear();

            for (security_questions question : questions) {
                questionMap.put(question.getQuestionText(), question.getId());
            }

            SelectQ.getItems().addAll(questionMap.keySet());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les questions de sécurité depuis la base de données. Vérifiez votre connexion.");
        }
    }

    @FXML
    public void ajouterCoach(ActionEvent actionEvent) throws SQLException {
        // Validate all fields
        validateUsername(usernameField.getText());
        validateEmail(emailField.getText());
        validatePassword(passwordField.getText());
        validatePasswordConfirmation(password2Field.getText());
        validatePhoneNumber(phoneNumberField.getText());
        validateSpeciality(specialityField.getText());
        validateYearOfExperience(yearOfExperienceField.getText());
        validateCertification(CertificationField.getText());

        // If all fields are valid, proceed with adding the coach
        if (usernameErrorLabel.getStyle().equals("-fx-text-fill: green") &&
                emailErrorLabel.getStyle().equals("-fx-text-fill: green") &&
                passwordErrorLabel.getStyle().equals("-fx-text-fill: green") &&
                password2ErrorLabel.getStyle().equals("-fx-text-fill: green") &&
                phoneNumberErrorLabel.getStyle().equals("-fx-text-fill: green") &&
                specialityErrorLabel.getStyle().equals("-fx-text-fill: green") &&
                yearOfExperienceErrorLabel.getStyle().equals("-fx-text-fill: green") &&
                certificationErrorLabel.getStyle().equals("-fx-text-fill: green")) {
            boolean passage=true;

            if (passage) {
                String email = emailField.getText();
                String password = passwordField.getText();
                String speciality = specialityField.getText();
                String username = usernameField.getText();
                int yearOfExperience = Integer.parseInt(yearOfExperienceField.getText());
                String certification = CertificationField.getText();

                // Récupérer la question de sécurité et la réponse
                String selectedQuestion = (String) SelectQ.getValue();
                if (selectedQuestion == null || selectedQuestion.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner une question de sécurité.");
                    return;
                }
                int securityQuestionId = questionMap.get(selectedQuestion);
                String securityAnswer = Answer.getText();
                String mdpHache= BCrypt.hashpw(password, BCrypt.gensalt());

                boolean is_Active = true;
                String role = "COACH";
                Coach coach = new Coach(username, email, mdpHache, is_Active, role, securityQuestionId, securityAnswer, speciality, yearOfExperience, certification);
                userservice.addEntityCoach(coach);

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Coach ajouté avec succès.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "L'email n'est pas valide selon la vérification Hunter.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez corriger les erreurs avant de soumettre.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null); // Optional: Can leave this empty
        alert.showAndWait();
    }

    @FXML
    public void loginBack(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/logIn.fxml"));
        Scene currentScene = emailField.getScene();
        currentScene.setRoot(root);
        Stage stage = (Stage) currentScene.getWindow();
        stage.setWidth(475);
        stage.setHeight(1000);
        stage.setResizable(false);
    }
}