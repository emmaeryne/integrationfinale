package edu.emmapi.controllers;

import edu.emmapi.controllers.logIn;
import edu.emmapi.services.OwnerService;
import edu.emmapi.entities.Adresse;
import edu.emmapi.entities.user;
import edu.emmapi.services.AdresseService;
import edu.emmapi.services.userService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountAdressController {
    private static final Logger LOGGER = Logger.getLogger(AccountAdressController.class.getName());
    private final AdresseService adresseService = new AdresseService();
    private final userService userService = new userService();
    private user currentUser;

    @FXML private ListView<Adresse> adressesListView;
    @FXML private VBox formContainer;
    @FXML private TextField nameField;
    @FXML private TextField firstnameField;
    @FXML private TextField lastnameField;
    @FXML private TextField companyField;
    @FXML private TextField adressField;
    @FXML private TextField postalField;
    @FXML private TextField cityField;
    @FXML private ComboBox<String> countryComboBox;
    @FXML private TextField phoneField;
    @FXML private Button saveButton;
    @FXML private Button addButton;
    @FXML private Button deleteButton;

    private Adresse currentAdresse;

    public void initialize() {
        try {
            // Récupérer l'email de l'utilisateur connecté depuis le LoginController
            String userEmail = logIn.getLoggedInUserEmail();

            if (userEmail == null || userEmail.isEmpty()) {
                showAlert("Erreur", "Aucun utilisateur connecté. Veuillez vous connecter.");
                return;
            }

            currentUser = userService.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email: " + userEmail));

            LOGGER.info("Current user initialized with ID: " + currentUser.getId());

            setupForm();
            setupListView();
            loadAdresses();

            // Initialize countries
           //countryComboBox.getItems().addAll("France", "Belgique", "Suisse", "Canada", "Autre");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize current user", e);
            showAlert("Error", "Failed to initialize user: " + e.getMessage());
            return;
        }
    }

    private void loadAdresses() {
        adressesListView.getItems().clear();
        if (currentUser == null) {
            LOGGER.warning("Cannot load addresses: currentUser is null or has no ID");
            showAlert("Error", "User not properly initialized. Please log in again.");
            return;
        }

        try {
            adressesListView.getItems().addAll(adresseService.findByUser(currentUser));
            LOGGER.info("Loaded addresses for user ID: " + currentUser.getId());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading addresses", e);
            showAlert("Error", "Failed to load addresses: " + e.getMessage());
        }
    }

    private void setupListView() {
        adressesListView.setCellFactory(param -> new ListCell<Adresse>() {
            @Override
            protected void updateItem(Adresse adresse, boolean empty) {
                super.updateItem(adresse, empty);
                if (empty || adresse == null) {
                    setText(null);
                } else {
                    setText(adresse.toString());
                }
            }
        });

        adressesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showAdresseDetails(newVal);
            }
        });
    }

    private void setupForm() {
        formContainer.setVisible(false);
    }

    @FXML
    private void handleAddAdresse() {
        if (currentUser == null) {
            showAlert("Error", "User not initialized. Please log in again.");
            return;
        }
        currentAdresse = new Adresse();
        currentAdresse.setuser(currentUser);
        showAdresseDetails(currentAdresse);
    }

    private void showAdresseDetails(Adresse adresse) {
        formContainer.setVisible(true);
        currentAdresse = adresse;

        nameField.setText(adresse.getName() != null ? adresse.getName() : "");
        firstnameField.setText(adresse.getFirstname() != null ? adresse.getFirstname() : "");
        lastnameField.setText(adresse.getLastname() != null ? adresse.getLastname() : "");
        companyField.setText(adresse.getCompany() != null ? adresse.getCompany() : "");
        adressField.setText(adresse.getAdress() != null ? adresse.getAdress() : "");
        postalField.setText(adresse.getPostal() != null ? adresse.getPostal() : "");
        cityField.setText(adresse.getCity() != null ? adresse.getCity() : "");
        //countryComboBox.setValue(adresse.getCountry() != null ? adresse.getCountry() : "France");
        phoneField.setText(adresse.getPhone() != null ? adresse.getPhone() : "");
    }

    @FXML
    private void handleSaveAdresse() {
        // Basic validation
        if (nameField.getText().isEmpty() || firstnameField.getText().isEmpty() || lastnameField.getText().isEmpty() ||
                adressField.getText().isEmpty() || postalField.getText().isEmpty() || cityField.getText().isEmpty());
                //countryComboBox.getValue() == null || phoneField.getText().isEmpty()) {
            //showAlert("Error", "All fields except company are required.");



        currentAdresse.setName(nameField.getText());
        currentAdresse.setFirstname(firstnameField.getText());
        currentAdresse.setLastname(lastnameField.getText());
        currentAdresse.setCompany(companyField.getText().isEmpty() ? null : companyField.getText());
        currentAdresse.setAdress(adressField.getText());
        currentAdresse.setPostal(postalField.getText());
        currentAdresse.setCity(cityField.getText());
        //currentAdresse.setCountry(countryComboBox.getValue());
        currentAdresse.setPhone(phoneField.getText());

        try {
            if (currentAdresse.getId() == null) {
                adresseService.saveAdresse(currentAdresse);
                LOGGER.info("Saved new address for user ID: " + currentUser.getId());
            } else {
                adresseService.updateAdresse(currentAdresse);
                LOGGER.info("Updated address ID: " + currentAdresse.getId());
            }
            formContainer.setVisible(false);
            loadAdresses();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving/updating address", e);
            showAlert("Error", "Failed to save address: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        formContainer.setVisible(false);
    }

    @FXML
    private void handleDeleteAdresse() {
        Adresse selected = adressesListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select an address to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer l'adresse");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette adresse ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    adresseService.deleteAdresse(selected.getId());
                    LOGGER.info("Deleted address ID: " + selected.getId());
                    loadAdresses();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error deleting address", e);
                    showAlert("Error", "Failed to delete address: " + e.getMessage());
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
