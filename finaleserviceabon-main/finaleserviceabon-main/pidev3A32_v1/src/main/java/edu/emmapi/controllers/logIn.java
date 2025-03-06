package edu.emmapi.controllers;

import edu.emmapi.entities.SessionManager;
import edu.emmapi.entities.SessionProfile;
import edu.emmapi.entities.profile;
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
    private void initialize() {
        showTF.setVisible(false);
        showTF.textProperty().bindBidirectional(Tfmdp.textProperty());

        showPwd.setOnAction(event -> {
            if (showPwd.isSelected()) {
                showTF.setVisible(true);
                Tfmdp.setVisible(false);
            } else {
                showTF.setVisible(false);
                Tfmdp.setVisible(true);
            }
        });
    }
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    public static boolean verifyPassword(String inputPassword, String storedHash) {
        return BCrypt.checkpw(inputPassword, storedHash);
    }
    private final OwnerService ownerService=new OwnerService();
    private final ProfileService profileService=new ProfileService();
    private final userService userservice=new userService();

    @FXML
    void sautentifier(ActionEvent event) throws IOException {
        String adresse=TFadresse.getText();


        String password=Tfmdp.getText();
        boolean check;
        String mdp=ownerService.PassSelect(adresse,password);
        System.out.println(mdp);
        if(password.isEmpty()|| adresse.isEmpty() || mdp==null) {
            check=false;
        }else
        {
            check =verifyPassword(password,mdp);
        }
        if(check){
            String role=ownerService.RoleSelect(adresse,mdp);
            System.out.println(role+"le role est");
            boolean isvalide=ownerService.ValiditeSelect(adresse,mdp);
            int id=ownerService.IDselect(adresse,mdp);
            System.out.println(id+"le id est");
            int testOwnerIDInPorfile= profileService.ProfileSelect(id);
            System.out.println(testOwnerIDInPorfile);
            EmailApp emailService = new EmailApp();
               // emailService.sendEmail(TFadresse.getText(), "Password reset","salemo alaykom");
            user u =userservice.getAllForSession(adresse,mdp);





        boolean status=true;


        System.out.println(isvalide);

        if(isvalide){

            SessionManager.getInstance().setUser(u);
            if(role.equals("OWNER")){

                if(testOwnerIDInPorfile==0){
                    System.out.println("hello");
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddProfile.fxml"));
                    Parent root = loader.load();
                    createProfileOwner controller = loader.getController();
                    controller.setId(id,role,status);
                    Scene scene = new Scene(root);
                    Stage newStage = new Stage();
                    newStage.setScene(scene);
                    newStage.setTitle("");
                    newStage.setWidth(800);
                    newStage.setHeight(607);
                    newStage.setResizable(false);
                    newStage.show();
                }else  {

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardClient.fxml"));
                    Parent parent = loader.load();
                    DashboardClient dashboardClient = loader.getController();
                    dashboardClient.setId(id);

                    Scene currentScene = TFadresse.getScene();  // Supposons que TFadresse est un TextField dans l'ancienne scène
                    Stage currentStage = (Stage) currentScene.getWindow();
                    currentStage.setWidth(850);  // Définir la largeur de la scène
                    currentStage.setHeight(600);  // Définir la hauteur de la scène
                    currentScene.setRoot(parent);
                }

            }else if(role.equals("ADMIN")){
                Parent root = FXMLLoader.load(getClass().getResource("/AfficherClients.fxml"));

                Scene currentScene = TFadresse.getScene();

                currentScene.setRoot(root);

                Stage stage = (Stage) currentScene.getWindow();
                stage.setWidth(1000);
                stage.setHeight(600);  //
                stage.setResizable(false);

            }else if(role.equals("COACH")){
                if(testOwnerIDInPorfile==0){
                    System.out.println("hello");
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddProfile.fxml"));
                    Parent root = loader.load();
                    createProfileOwner controller = loader.getController();
                    controller.setId(id,role,status);
                    Scene scene = new Scene(root);
                    Stage newStage = new Stage();
                    newStage.setScene(scene);
                    newStage.setTitle("");
                    newStage.setWidth(950);
                    newStage.setHeight(607);
                    newStage.setResizable(false);
                    newStage.show();
                }else  {

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardClient.fxml"));

                    Parent parent = loader.load();
                    DashboardClient dashboardClient = loader.getController();
                    dashboardClient.setId(id);

                    Scene currentScene = TFadresse.getScene();  // Supposons que TFadresse est un TextField dans l'ancienne scène
                    Stage currentStage = (Stage) currentScene.getWindow();
                    currentStage.setWidth(850);  // Définir la largeur de la scène
                    currentStage.setHeight(600);  // Définir la hauteur de la scène
                    currentScene.setRoot(parent);
                }


            }else if(role.equals("USER")){
                if(testOwnerIDInPorfile==0){
                    System.out.println("hello");
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddProfile.fxml"));
                    Parent root = loader.load();
                    createProfileOwner controller = loader.getController();
                    controller.setId(id,role,status);
                    Scene scene = new Scene(root);
                    Stage newStage = new Stage();
                    newStage.setScene(scene);
                    newStage.setTitle("");
                    newStage.setWidth(950);
                    newStage.setHeight(607);
                    newStage.setResizable(false);
                    newStage.show();
                }else  {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardClient.fxml"));

                    Parent parent = loader.load();
                    DashboardClient dashboardClient = loader.getController();
                    dashboardClient.setId(id);

                    Scene currentScene = TFadresse.getScene();  // Supposons que TFadresse est un TextField dans l'ancienne scène
                    Stage currentStage = (Stage) currentScene.getWindow();
                    currentStage.setWidth(850);  // Définir la largeur de la scène
                    currentStage.setHeight(600);  // Définir la hauteur de la scène
                    currentScene.setRoot(parent);

                }

            }

        }
        else  {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Code.fxml"));
            Parent root = loader.load();
            code controller = loader.getController();
            controller.setEmail(TFadresse.getText());
            Scene scene = new Scene(root);
            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle("");
            newStage.setWidth(800);
            newStage.setHeight(607);
            newStage.setResizable(false);
            newStage.show();


        }
        }else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Sur Compte");
            alert.setContentText("verifier les donnes ");
            alert.show();
        }

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
    private void GoTo(int id,String role) throws IOException {

    }
    @FXML
    void forgetPwdOnClick(ActionEvent event) {
        forgetPwd.setOnAction(e -> {  // Renommer 'e' ici
            try {
                // Load the ForgotPwd view
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



}


