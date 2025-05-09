package edu.emmapi.controllers.users;

import edu.emmapi.entities.users.Users;
import edu.emmapi.interfaces.users.UserService;
import edu.emmapi.services.users.UserServiceImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private ChoiceBox<String> roleChoiceBox;

    @FXML
    private Button registerButton;

    private final UserService userService = new UserServiceImpl();

    @FXML
    private void initialize() {
        roleChoiceBox.getItems().addAll("CLIENT", "COACH", "ADMIN");
        roleChoiceBox.setValue("CLIENT");
    }

    @FXML
    void handleRegister(ActionEvent event) {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String role = roleChoiceBox.getValue();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Erreur", "Les mots de passe ne correspondent pas.");
            return;
        }

        if (userService.findByEmail(email).isPresent()) {
            showAlert("Erreur", "Cet email est déjà utilisé.");
            return;
        }

        Users user = new Users();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password); // Updated to password_hash
        user.setRole(role);
        user.setIsActive(true);

        try {
            userService.saveUser(user);
            showAlert("Succès", "Inscription réussie ! Vous pouvez maintenant vous connecter.");
            redirectToLogin();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'inscription : " + e.getMessage());
        }
    }

    @FXML
    void handleSignUp(ActionEvent event) throws IOException {
        redirectToLogin();
    }

    private void redirectToLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(scene);
        stage.setWidth(600);
        stage.setHeight(500);
        stage.setResizable(false);
        stage.show();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(title.equals("Succès") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}