package edu.emmapi.controllers;

import edu.emmapi.entities.profile;
import edu.emmapi.services.ProfileService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditProfileController {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField dateOfBirthField;
    @FXML
    private TextField bioField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField websiteField;
    @FXML
    private TextField socialMediaLinksField;

    private ProfileService profileService = new ProfileService();
    private profile currentProfile;

    public void setProfile(profile profile) {
        this.currentProfile = profile;
        firstNameField.setText(profile.getFirstName());
        lastNameField.setText(profile.getLastName());
        dateOfBirthField.setText(profile.getDateOfBirth());
        bioField.setText(profile.getBio());
        locationField.setText(profile.getLocation());
        phoneNumberField.setText(profile.getPhoneNumber());
        websiteField.setText(profile.getWebsite());
        socialMediaLinksField.setText(profile.getSocialMediaLink());
    }

    @FXML
    private void saveProfile() {
        // Mettre à jour les champs de l'objet profile
        currentProfile.setFirstName(firstNameField.getText());
        currentProfile.setLastName(lastNameField.getText());
        currentProfile.setDateOfBirth(dateOfBirthField.getText());
        currentProfile.setBio(bioField.getText());
        currentProfile.setLocation(locationField.getText());
        currentProfile.setPhoneNumber(phoneNumberField.getText());
        currentProfile.setWebsite(websiteField.getText());
        currentProfile.setSocialMediaLink(socialMediaLinksField.getText());

        // Appeler la méthode de mise à jour
        profileService.updateEntity(currentProfile);

        // Fermer la fenêtre après la mise à jour
        Stage stage = (Stage) firstNameField.getScene().getWindow();
        stage.close();
    }
}