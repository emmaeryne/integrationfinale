package edu.emmapi.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.emmapi.entities.security_questions;
import edu.emmapi.entities.user;
import edu.emmapi.services.SecurityQuestionDao;
import edu.emmapi.services.userService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignUpClient {

    @FXML
    private Label EmailErrorLabel;

    @FXML
    private PasswordField MotDePasse2Client;

    @FXML
    private PasswordField MotDePasseClient;

    @FXML
    private Label MotdepasseErrorLabel;

    @FXML
    private TextField emailClient;

    @FXML
    private Label errorLabel;

    @FXML
    private Label passwordErrorLabel;

    @FXML
    private Button sinscrireClientButton;

    @FXML
    private TextField userNameClient;

    @FXML
    private Label userNameErrorLabel;

    @FXML
    private ComboBox<String> SelectQ; // ComboBox pour les questions de sécurité
    private Map<String, Integer> questionMap = new HashMap<>(); // Map pour associer les questions à leurs ID

    @FXML
    private TextField Answer;

    private final userService userService = new userService();

    @FXML
    void initialize() {
        loadSecurityQuestions();

        // Add listeners for field validation
        userNameClient.textProperty().addListener((observable, oldValue, newValue) -> validateUserName());
        emailClient.textProperty().addListener((observable, oldValue, newValue) -> validateEmail());
        MotDePasseClient.textProperty().addListener((observable, oldValue, newValue) -> validatePassword());
        MotDePasse2Client.textProperty().addListener((observable, oldValue, newValue) -> validatePasswordConfirmation());

        // Remplir le ComboBox avec les questions de sécurité

        // Default text for error labels
        userNameErrorLabel.setText("Nom d'utilisateur invalide");
        EmailErrorLabel.setText("L'adresse email est obligatoire");
        MotdepasseErrorLabel.setText("Le mot de passe est obligatoire");
        passwordErrorLabel.setText("Le mot de passe doit avoir au moins 6 caractères");
    }

    private void loadSecurityQuestions() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hive2", "root", "")) {
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

        private boolean verifyEmailWithHunter(String email) {
        OkHttpClient client = new OkHttpClient();
        String apiKey = "51ebed6f136f3456dc8bb794c07d8270788706fa"; // Your API key

        String url = "https://api.hunter.io/v2/email-verifier";
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("email", email);
        urlBuilder.addQueryParameter("api_key", apiKey);
        String finalUrl = urlBuilder.build().toString();

        Request request = new Request.Builder().url(finalUrl).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                System.out.println("Réponse de l'API : " + responseBody); // For debugging

                JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                String status = jsonResponse.getAsJsonObject("data").get("status").getAsString();

                return "valid".equals(status) && "deliverable".equals(jsonResponse.getAsJsonObject("data").get("result").getAsString());
            } else {
                System.out.println("Erreur de l'API : " + response.code() + " - " + response.message());
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }



    private void validateUserName() {
        String userName = userNameClient.getText();
        if (userName.isEmpty()) {
            userNameErrorLabel.setText("Le nom d'utilisateur est obligatoire");
        } else {
            userNameErrorLabel.setText("Nom d'utilisateur valide ✓");
        }
    }

    private void validateEmail() {
        String email = emailClient.getText();
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (email.isEmpty() || !email.matches(emailRegex)) {
            EmailErrorLabel.setText("Email invalide");
            EmailErrorLabel.setStyle("-fx-text-fill: red");
        } else {
            EmailErrorLabel.setText("Email valide ✓");
            EmailErrorLabel.setStyle("-fx-text-fill: green");
        }
    }

    private void validatePassword() {
        String password = MotDePasseClient.getText();
        if (password.isEmpty() || password.length() < 6) {
            MotdepasseErrorLabel.setText("Le mot de passe doit avoir au moins 6 caractères");
            MotdepasseErrorLabel.setStyle("-fx-text-fill: red");
        } else {
            MotdepasseErrorLabel.setText("Mot de passe valide ✓");
            MotdepasseErrorLabel.setStyle("-fx-text-fill: green");
        }
    }

    private void validatePasswordConfirmation() {
        String password1 = MotDePasseClient.getText();
        String password2 = MotDePasse2Client.getText();

        if (password2.isEmpty()) {
            passwordErrorLabel.setText("La confirmation du mot de passe est obligatoire");
            passwordErrorLabel.setStyle("-fx-text-fill: red");
        } else if (!password1.equals(password2)) {
            passwordErrorLabel.setText("Les mots de passe ne correspondent pas");
            passwordErrorLabel.setStyle("-fx-text-fill: red");
        } else {
            passwordErrorLabel.setText("Les mots de passe correspondent ✓");
            passwordErrorLabel.setStyle("-fx-text-fill: green");
        }
    }

    @FXML
    void handleSignUp(ActionEvent event) throws SQLException, IOException {
        // Validate all fields
        if (EmailErrorLabel.getText().equals("Email valide ✓") &&
                userNameErrorLabel.getText().equals("Nom d'utilisateur valide ✓") &&
                MotdepasseErrorLabel.getText().equals("Mot de passe valide ✓") &&
                passwordErrorLabel.getText().equals("Les mots de passe correspondent ✓")) {

            String userName = userNameClient.getText();
            String email = emailClient.getText();
            String motDePasse = MotDePasseClient.getText();
            String securityAnswer = Answer.getText();
            String securityQuestion = SelectQ.getValue();

            // Validate security question and answer
            if (securityQuestion == null || securityQuestion.isEmpty()) {
                showAlert("Erreur", "Veuillez sélectionner une question de sécurité.");
                return;
            }
            if (securityAnswer == null || securityAnswer.isEmpty()) {
                showAlert("Erreur", "Veuillez fournir une réponse à la question de sécurité.");
                return;
            }

            // Récupérer l'ID de la question sélectionnée
            int securityQuestionId = questionMap.getOrDefault(securityQuestion, -1);
            if (securityQuestionId == -1) {
                showAlert("Erreur", "Question de sécurité invalide.");
                return;
            }

            boolean active = false;
            String role = "USER";
boolean x=verifyEmailWithHunter(email);
            // Check email validity via Hunter API
            if (x) {
                // If passwords match, proceed with registration
                if (motDePasse.equals(MotDePasse2Client.getText())) {
                    String mdpHache= BCrypt.hashpw(motDePasse, BCrypt.gensalt());
                    System.out.println(mdpHache);

                    user newUser = new user(userName, mdpHache,email, active, role, securityQuestionId, securityAnswer);
                    userService.addEntity(newUser);

                    showAlert("Inscription réussie !", "Votre inscription a été effectuée avec succès.");
                    logInBack();
                } else {
                    showAlert("Erreur", "Les mots de passe ne correspondent pas.");
                }
            } else {
                showAlert("Erreur", "L'email n'est pas valide selon la vérification Hunter.");
            }
        } else {
            showAlert("Erreur", "Veuillez corriger les erreurs avant de soumettre.");
        }
    }

    // Show an alert with a given title and message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void logInBack() throws IOException {
        // This should handle the action of navigating back to the login screen.
        Parent root = FXMLLoader.load(getClass().getResource("/logIn.fxml"));
        Scene currentScene = userNameClient.getScene();
        currentScene.setRoot(root);
        Stage stage = (Stage) currentScene.getWindow();
        stage.setWidth(457);
        stage.setHeight(1000);
    }

    public void connectWith(ActionEvent actionEvent) {
    }
}