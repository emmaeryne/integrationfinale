package edu.emmapi.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.emmapi.entities.profile;
import edu.emmapi.services.ProfileService;
import edu.emmapi.tools.MyConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;

public class createProfileOwner {

    @FXML
    private TextField DateOfBirthOwnerTF;
    @FXML
    private TextField FirstNameOwnerTF;
    @FXML
    private TextField LastNameOwnerTF;
    @FXML
    private TextField LocationOwnerTF;
    @FXML
    private TextField PhoneOwnerTF;
    @FXML
    private TextField ProfilePicOwnerTF; // Champ caché pour stocker l'URL de l'image
    @FXML
    private TextField WebsiteOwnerTF;
    @FXML
    private Button addCoach;
    @FXML
    private TextField bioOwnerTF;
    @FXML
    private Button deletecoach;
    @FXML
    private Button reloadCreateOwner;
    @FXML
    private TextField socialMediaLinksOwnerTF;
    @FXML
    private Button updateCoach;
    @FXML
    private Button selectImageButton; // Bouton pour sélectionner une image
    @FXML
    private ImageView selectedImageView; // ImageView pour afficher l'image sélectionnée

    @FXML
    private Label errorFirstName;
    @FXML
    private Label errorLastName;
    @FXML
    private Label errorDateOfBirth;
    @FXML
    private Label errorLocation;
    @FXML
    private Label errorPhone;
    @FXML
    private Label errorBio;
    @FXML
    private Label errorSocialLinks;

    private int idRecicved;
    private String RoleRecived;
    private boolean statusRecicved; // true pour modification, false pour ajout
    public Connection cnx;
    private final ProfileService profileService = new ProfileService();
    private File selectedImageFile; // Fichier image sélectionné

    public createProfileOwner() {
        cnx = MyConnection.getInstance().getCnx();
    }

    public void setId(int id, String role, boolean isEditMode) {
        idRecicved = id;
        RoleRecived = role;
        statusRecicved = isEditMode;
    }

    // Input validation method with ChangeListeners
    private void setupValidationListeners() {
        // Validate First Name
        FirstNameOwnerTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                errorFirstName.setText("Le prénom est requis");
            } else {
                errorFirstName.setText("");
            }
        });

        // Validate Last Name
        LastNameOwnerTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                errorLastName.setText("Le nom est requis");
            } else {
                errorLastName.setText("");
            }
        });

        // Validate Date of Birth
        DateOfBirthOwnerTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                errorDateOfBirth.setText("La date de naissance est requise");
            } else {
                errorDateOfBirth.setText("");
            }
        });

        // Validate Location
        LocationOwnerTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                errorLocation.setText("La localisation est requise");
            } else {
                errorLocation.setText("");
            }
        });

        // Validate Phone Number
        PhoneOwnerTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                errorPhone.setText("Le numéro de téléphone est requis");
            } else {
                errorPhone.setText("");
            }
        });

        // Validate Bio
        bioOwnerTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                errorBio.setText("La bio est requise");
            } else {
                errorBio.setText("");
            }
        });

        // Validate Social Media Links
        socialMediaLinksOwnerTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                errorSocialLinks.setText("Les liens des réseaux sociaux sont requis");
            } else {
                errorSocialLinks.setText("");
            }
        });
    }

    // Adding the profile to the database
    @FXML
    public void AjouterProfile(ActionEvent actionEvent) throws SQLException {
        if (!validateInputs()) {
            return; // Return early if validation fails
        }

        String dateOfBirth = DateOfBirthOwnerTF.getText();
        String firstName = FirstNameOwnerTF.getText();
        String lastName = LastNameOwnerTF.getText();
        String location = LocationOwnerTF.getText();
        String phone = PhoneOwnerTF.getText();
        String website = WebsiteOwnerTF.getText();
        String bio = bioOwnerTF.getText();
        String socialMediaLinks = socialMediaLinksOwnerTF.getText();

        profile OwnerProfile = new profile(firstName, lastName, dateOfBirth, bio, location, phone, website, socialMediaLinks);
        profileService.addEntity(OwnerProfile, idRecicved);

        showAlert("Succès", "Le profil a été créé avec succès!");

        Stage currentStage = (Stage) PhoneOwnerTF.getScene().getWindow();
        currentStage.close();
    }


    // Input validation method
    private boolean validateInputs() {
        boolean isValid = true;

        if (FirstNameOwnerTF.getText().isEmpty()) {
            errorFirstName.setText("Le prénom est requis");
            isValid = false;
        }
        if (LastNameOwnerTF.getText().isEmpty()) {
            errorLastName.setText("Le nom est requis");
            isValid = false;
        }
        if (DateOfBirthOwnerTF.getText().isEmpty()) {
            errorDateOfBirth.setText("La date de naissance est requise");
            isValid = false;
        }
        if (LocationOwnerTF.getText().isEmpty()) {
            errorLocation.setText("La localisation est requise");
            isValid = false;
        }
        if (PhoneOwnerTF.getText().isEmpty()) {
            errorPhone.setText("Le numéro de téléphone est requis");
            isValid = false;
        }
        if (bioOwnerTF.getText().isEmpty()) {
            errorBio.setText("La bio est requise");
            isValid = false;
        }
        if (socialMediaLinksOwnerTF.getText().isEmpty()) {
            errorSocialLinks.setText("Les liens des réseaux sociaux sont requis");
            isValid = false;
        }

        return isValid;
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // This method is a placeholder if needed
    public void uploadImage(ActionEvent actionEvent) {
    }

    @FXML
    public void initialize() {
        setupValidationListeners();  // Set up listeners for validation
    }

    public void toListeProprietaire(ActionEvent actionEvent) {
    }

    public void handleSearch(ActionEvent actionEvent) {
    }

    public void ToListCoach(ActionEvent actionEvent) {
    }

    public void GoToHomePage(MouseEvent mouseEvent) {
    }
}
