package edu.emmapi.controllers;

import edu.emmapi.entities.SessionManager;
import edu.emmapi.entities.user;
import edu.emmapi.services.OwnerService;
import edu.emmapi.services.ProfileService;
import edu.emmapi.services.userService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

public class logIn {

    @FXML
    private Button SignUpClicked;

    @FXML
    private Hyperlink forgetPwd;

    @FXML
    private TextField TFadresse;

    @FXML
    private PasswordField Tfmdp;

    @FXML
    private Text labelAdresse;

    @FXML
    private Button sinscrireButton;

    @FXML
    private CheckBox showPwd;

    @FXML
    private TextField showTF;
    @FXML
    private static user loggedInUser;

    private final OwnerService ownerService = new OwnerService();
    private final ProfileService profileService = new ProfileService();
    private final userService userservice = new userService();

    @FXML
    private void initialize() {
        showTF.managedProperty().bind(showTF.visibleProperty());

        showPwd.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                showTF.setText(Tfmdp.getText());
                showTF.setVisible(true);
                Tfmdp.setVisible(false);
            } else {
                Tfmdp.setText(showTF.getText());
                Tfmdp.setVisible(true);
                showTF.setVisible(false);
            }
        });

        showTF.textProperty().addListener((obs, oldVal, newVal) -> {
            if (showTF.isVisible()) {
                Tfmdp.setText(newVal);
            }
        });
    }
    public static user getLoggedInUser() {
        return loggedInUser;
    }

    public static String getLoggedInUserEmail() {
        return loggedInUser != null ? loggedInUser.getEmail() : null;
    }

    // ✅ Génération du hash compatible avec PHP (à utiliser si tu crées de nouveaux comptes en Java)
    public static String hashPassword(String password) {
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(13)); // 13 = coût
        // PHP utilise $2y$, Java utilise $2a$ => tu peux le convertir manuellement si tu veux l’envoyer à PHP
        return hash.replace("$2a$", "$2y$");
    }

    // ✅ Vérification compatible avec PHP ($2y$ -> $2a$)
    public static boolean verifyPassword(String inputPassword, String storedHash) {
        if (storedHash.startsWith("$2y$")) {
            storedHash = "$2a$" + storedHash.substring(4);
        }
        return BCrypt.checkpw(inputPassword, storedHash);
    }


    @FXML
    void sautentifier(ActionEvent event) throws IOException {
        String adresse = TFadresse.getText();
        String password = Tfmdp.getText();
        boolean check;

        String mdp = ownerService.PassSelect(adresse, password);
        System.out.println("Mot de passe hashé récupéré : " + mdp);

        if (password.isEmpty() || adresse.isEmpty() || mdp == null) {
            check = false;
        } else {
            check = verifyPassword(password, mdp);
        }

        if (check) {
            String role = ownerService.RoleSelect(adresse, mdp);
            boolean isValide = ownerService.ValiditeSelect(adresse, mdp);
            int id = ownerService.IDselect(adresse, mdp);
            int profileExists = profileService.ProfileSelect(id);

            user u = userservice.getAllForSession(adresse, mdp);
            SessionManager.getInstance().setUser(u);

            if (isValide) {
                if (role.equals("OWNER") || role.equals("COACH") || role.equals("USER")) {
                    if (profileExists == 0) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddProfile.fxml"));
                        Parent root = loader.load();
                        createProfileOwner controller = loader.getController();
                        controller.setId(id, role, true);
                        showNewStage(root, 900, 900);
                    } else {
                        loadDashboardClient(id);
                    }
                } else if (role.equals("ADMIN")) {
                    Parent root = FXMLLoader.load(getClass().getResource("/AfficherClients.fxml"));
                    Scene currentScene = TFadresse.getScene();
                    currentScene.setRoot(root);
                    Stage stage = (Stage) currentScene.getWindow();
                    stage.setWidth(1000);
                    stage.setHeight(600);
                    stage.setResizable(false);
                }
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Code.fxml"));
                Parent root = loader.load();
                code controller = loader.getController();
                controller.setEmail(adresse);
                showNewStage(root, 800, 607);
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Sur Compte");
            alert.setContentText("Vérifiez les données.");
            alert.show();
        }
    }






    private void loadDashboardClient(int id) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardClient.fxml"));
        Parent parent = loader.load();
        DashboardClient controller = loader.getController();
        controller.setId(id);

        Scene currentScene = TFadresse.getScene();
        Stage currentStage = (Stage) currentScene.getWindow();
        currentStage.setWidth(850);
        currentStage.setHeight(600);
        currentScene.setRoot(parent);
    }

    private void showNewStage(Parent root, int width, int height) {
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setTitle("");
        newStage.setWidth(width);
        newStage.setHeight(height);
        newStage.setResizable(false);
        newStage.show();
    }

    @FXML
    void SignUpClickedOnAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/first-interface.fxml"));
        Scene currentScene = TFadresse.getScene();
        currentScene.setRoot(root);
        Stage stage = (Stage) currentScene.getWindow();
        stage.setWidth(485);
        stage.setHeight(662);
    }

    @FXML
    void forgetPwdOnClick(ActionEvent event) {
        forgetPwd.setOnAction(e -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/ForgotPwd.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Forgot Password");
                stage.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void hoverGlow(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof Button button) {
            button.setStyle(button.getStyle() +
                    "-fx-effect: dropshadow(gaussian, #00ACC1, 10, 0.3, 0, 0);");
        }
    }

    public void removeGlow(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof Button button) {
            // Supprime l'effet en rétablissant le style de base
            button.setStyle(button.getStyle().replaceAll("-fx-effect:.*?;", ""));
        }
    }

    public void hoverBorder(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof Button button) {
            button.setStyle(button.getStyle() +
                    "-fx-border-color: #00ACC1; -fx-border-width: 2px;");
        }
    }

    public void removeBorder(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof Button button) {
            // Supprime uniquement les modifications de bordure
            button.setStyle(button.getStyle().replaceAll("-fx-border-color:.*?;", "")
                    .replaceAll("-fx-border-width:.*?;", ""));
        }
    }
}

