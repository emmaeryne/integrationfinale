package edu.emmapi.controllers;

import edu.emmapi.entities.SessionManager;
import edu.emmapi.entities.profile;
import edu.emmapi.entities.user;
import edu.emmapi.services.ProfileService;
import edu.emmapi.tools.MyConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

public class DashboardClient {
    @FXML
    private Button ReserverB;
    @FXML
    private Button GestionCoach;
    @FXML
    private Button GestionUser;
    @FXML
    private Label bioLabel;

    @FXML
    private Label dateOfBirthLabel;

    @FXML
    private Label firstNameLabel;

    @FXML
    private Button gestionOwnerB;

    @FXML
    private Label lastNameLabel;

    @FXML
    private Label locationLabel;

    @FXML
    private Label phoneNumberLabel;

    @FXML
    private Button planningB;

    @FXML
    private AnchorPane profileDetailsPane;

    @FXML
    private ImageView profilePictureView;

    @FXML
    private Label socialMediaLinksLabel;

    @FXML
    private Label websiteLabel;



    @FXML
    void GoToGestionCoach(ActionEvent event) {

    }

    @FXML
    void GoToGestionOwner(ActionEvent event) {

    }


    @FXML
    void nextPageUser(ActionEvent event) {


    }




    private int idReived;
    private Connection cnx;

    public DashboardClient() {
        cnx= MyConnection.getInstance().getCnx();
    }

    ProfileService profileService = new ProfileService();
    user u;
    public void setId(int id) {
        idReived = id;
        loadProfileDetails();
    }

    @FXML
    void initialize() {

    }

    private void loadProfileDetails() {

        ProfileService profileService = new ProfileService();
        List<profile> profiles = profileService.Data(idReived);

        //appelle session dans une classe
        u= SessionManager.getInstance().getUser();
        if(u.getRole().equals("COACH"))
        {
            GestionCoach.setVisible(true);
            GestionUser.setVisible(false);
        } else if (u.getRole().equals("USER")) {
            GestionCoach.setVisible(false);
            GestionUser.setVisible(true);
        }


        if (!profiles.isEmpty()) {
            profile currentProfile = profiles.get(0);
            firstNameLabel.setText(currentProfile.getFirstName());
            lastNameLabel.setText(currentProfile.getLastName());
            dateOfBirthLabel.setText(currentProfile.getDateOfBirth());
            bioLabel.setText(currentProfile.getBio());
            locationLabel.setText(currentProfile.getLocation());
            phoneNumberLabel.setText(currentProfile.getPhoneNumber());
            websiteLabel.setText(currentProfile.getWebsite());
            socialMediaLinksLabel.setText(currentProfile.getSocialMediaLink());

            // Charger l'image depuis l'URL
            String imageUrl = currentProfile.getProfilePicture();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                try {
                    Image profileImage = new Image(imageUrl); // Charger l'image depuis l'URL
                    profilePictureView.setImage(profileImage); // Afficher l'image dans l'ImageView
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Erreur lors du chargement de l'image : " + e.getMessage());
                }
            } else {
                System.out.println("Aucune image trouvée pour ce profil.");
            }
        }

    }

    // Méthode pour éditer le profil
    public void editProfile(ActionEvent actionEvent) throws IOException {
//profileService.deleteEntity(idReived);
    }


    public void supprimerProfile(ActionEvent actionEvent) throws IOException {
        profileService.deleteEntity(u.getId());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/logIn.fxml"));
        Parent parent = loader.load();


        Scene currentScene = firstNameLabel.getScene();  // Supposons que TFadresse est un TextField dans l'ancienne scène
        Stage currentStage = (Stage) currentScene.getWindow();
        currentStage.setWidth(850);  // Définir la largeur de la scène
        currentStage.setHeight(600);  // Définir la hauteur de la scène
        currentScene.setRoot(parent);


    }

    public void nextPage(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/homepage.fxml"));
        Parent parent = loader.load();


        Scene currentScene = firstNameLabel.getScene();  // Supposons que TFadresse est un TextField dans l'ancienne scène
        Stage currentStage = (Stage) currentScene.getWindow();
        currentStage.setWidth(850);  // Définir la largeur de la scène
        currentStage.setHeight(600);  // Définir la hauteur de la scène
        currentScene.setRoot(parent);
    }
}
