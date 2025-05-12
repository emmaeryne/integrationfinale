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

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField showPasswordField;

    @FXML
    private CheckBox showPasswordCheckBox;

    @FXML
    private Button loginButton;

    private final UserService userService = new UserServiceImpl();

    @FXML
    private void initialize() {
        showPasswordField.setVisible(false);
        showPasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        showPasswordCheckBox.setOnAction(event -> {
            showPasswordField.setVisible(showPasswordCheckBox.isSelected());
            passwordField.setVisible(!showPasswordCheckBox.isSelected());
        });
    }

    @FXML
    void handleLogin(ActionEvent event) throws IOException {
        String email = emailField.getText();
        String password_hash = passwordField.getText();

        if (email.isEmpty() || password_hash.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        userService.findByEmail(email).ifPresentOrElse(user -> {
            if (!user.getIsActive()) {
                showAlert("Erreur", "Ce compte est désactivé.");
                return;
            }

            if (userService.verifyPassword(password_hash, user.getPassword())) { // Updated to password_hash
                try {
                    redirectBasedOnRole(user);
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Erreur lors de la redirection.");
                }
            } else {
                showAlert("Erreur", "Email ou mot de passe incorrect.");
            }
        }, () -> showAlert("Erreur", "Utilisateur non trouvé."));
    }

    private void redirectBasedOnRole(Users user) throws IOException {
        String fxmlPath;
        String role = user.getRole();
        switch (role) {
            case "ADMIN":
                fxmlPath = "/homepage.fxml";
                break;
            case "COACH":
                fxmlPath = "/homepagecoach.fxml";
                break;
            case "CLIENT":
                fxmlPath = "/homepageclient.fxml";
                break;
            default:
                fxmlPath = "/homepage.fxml";
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.setScene(scene);
        stage.setWidth(850);
        stage.setHeight(600);
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    void handleSignUp(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.setScene(scene);
        stage.setWidth(600);
        stage.setHeight(700);
        stage.setResizable(false);
        stage.show();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}