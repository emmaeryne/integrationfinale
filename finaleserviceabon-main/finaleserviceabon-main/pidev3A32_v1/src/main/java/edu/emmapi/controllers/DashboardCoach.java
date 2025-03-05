package edu.emmapi.controllers;

import edu.emmapi.entities.profile;
import edu.emmapi.services.ProfileService;
import edu.emmapi.tools.MyConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

public class DashboardCoach {

    @FXML
    private Label bioLabel;
    @FXML
    private Label dateOfBirthLabel;
    @FXML
    private Label firstNameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label locationLabel;
    @FXML
    private Label phoneNumberLabel;
    @FXML
    private AnchorPane profileDetailsPane;
    @FXML
    private ImageView profilePictureView;
    @FXML
    private Label socialMediaLinksLabel;
    @FXML
    private Label websiteLabel;


    private int idReived;
    private Connection cnx;

    public DashboardCoach() {
        cnx= MyConnection.getInstance().getCnx();
    }

    ProfileService profileService = new ProfileService();

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

            if (currentProfile.getProfilePicture() != null && !currentProfile.getProfilePicture().isEmpty()) {
                Image profileImage = new Image("file:" + currentProfile.getProfilePicture());
                profilePictureView.setImage(profileImage);
            }
        }
    }

    // Méthode pour éditer le profil
    public void editProfile(ActionEvent actionEvent) throws IOException {
//profileService.deleteEntity(idReived);
    }

    public void supprimerProfile(ActionEvent actionEvent) {
        profileService.deleteEntity(idReived);

    }
}