/*package edu.emmapi.controllers;

import edu.emmapi.entities.Service;
import edu.emmapi.entities.TypeAbonnement;
import edu.emmapi.services.ServiceService;
import edu.emmapi.services.TypeAbonnementService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.util.List;

public class ClientController {

    @FXML
    private ComboBox<Service> serviceComboBox;
    @FXML
    private ComboBox<TypeAbonnement> abonnementComboBox;
    @FXML
    private Label serviceDetailsLabel;
    @FXML
    private Label abonnementDetailsLabel;

    private ServiceService serviceService = new ServiceService();
    private TypeAbonnementService typeAbonnementService = new TypeAbonnementService();

    @FXML
    public void initialize() {
        loadServices();
    }

    private void loadServices() {
        List<Service> services = serviceService.getAllServices();
        serviceComboBox.getItems().addAll(services);

        // Set a StringConverter to display only the service name
        serviceComboBox.setConverter(new StringConverter<Service>() {
            @Override
            public String toString(Service service) {
                return service != null ? service.getNom() : ""; // Display the service name
            }

            @Override
            public Service fromString(String string) {
                return null; // Not needed for this use case
            }
        });

        serviceComboBox.setOnAction(e -> showServiceDetails());
    }

    @FXML
    private void showServiceDetails() {
        Service selectedService = serviceComboBox.getValue();
        if (selectedService != null) {
            serviceDetailsLabel.setText("Nom: " + selectedService.getNom() +
                    ", Prix: " + selectedService.getPrix() +
                    ", Réservations: " + selectedService.getNombreReservations());
            selectedService.setNombreReservations(selectedService.getNombreReservations() + 1); // Increment reservations
            loadAbonnements();
        }
    }

    private void loadAbonnements() {
        abonnementComboBox.getItems().clear(); // Clear previous items
        List<TypeAbonnement> abonnements = typeAbonnementService.getAllTypeAbonnements();

        if (abonnements.isEmpty()) {
            abonnementDetailsLabel.setText("Aucun abonnement disponible."); // Feedback for empty list
        } else {
            abonnementComboBox.getItems().addAll(abonnements); // Populate ComboBox
            abonnementComboBox.setOnAction(e -> showAbonnementDetails());
        }
    }

    @FXML
    private void showAbonnementDetails() {
        TypeAbonnement selectedAbonnement = abonnementComboBox.getValue();
        if (selectedAbonnement != null) {
            abonnementDetailsLabel.setText("Nom: " + selectedAbonnement.getNom() +
                    ", Prix: " + selectedAbonnement.getPrix() +
                    ", Statut: " + (selectedAbonnement.getIsPremium() ? "Premium" : "Standard"));
        }
    }

    @FXML
    private void submitOffer() {
        // Handle the submission of the selected service and abonnement
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Offre soumise avec succès!");
        alert.showAndWait();
    }
}*/
package edu.emmapi.controllers;

import edu.emmapi.entities.Service;
import edu.emmapi.entities.TypeAbonnement;
import edu.emmapi.services.ServiceService;
import edu.emmapi.services.TypeAbonnementService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.util.List;

public class ClientController {

    @FXML
    private ComboBox<Service> serviceComboBox;
    @FXML
    private ComboBox<TypeAbonnement> abonnementComboBox;
    @FXML
    private Label serviceDetailsLabel;
    @FXML
    private Label abonnementDetailsLabel;

    private ServiceService serviceService = new ServiceService();
    private TypeAbonnementService typeAbonnementService = new TypeAbonnementService();

    @FXML
    public void initialize() {
        loadServices();
    }

    private void loadServices() {
        List<Service> services = serviceService.getAllServices();
        serviceComboBox.getItems().addAll(services);

        // Set a StringConverter to display only the service name
        serviceComboBox.setConverter(new StringConverter<Service>() {
            @Override
            public String toString(Service service) {
                return service != null ? service.getNom() : ""; // Display the service name
            }

            @Override
            public Service fromString(String string) {
                return null; // Not needed for this use case
            }
        });

        serviceComboBox.setOnAction(e -> showServiceDetails());
    }

    @FXML
    private void showServiceDetails() {
        Service selectedService = serviceComboBox.getValue();
        if (selectedService != null) {
            // Increment the number of reservations
            selectedService.setNombreReservations(selectedService.getNombreReservations() + 1);

            // Update the details label
            serviceDetailsLabel.setText("Nom: " + selectedService.getNom() +
                    ", Prix: " + selectedService.getPrix() +
                    ", Réservations: " + selectedService.getNombreReservations());

            // Optionally, load abonnements after updating the service
            loadAbonnements();
        }
    }

    private void loadAbonnements() {
        abonnementComboBox.getItems().clear(); // Clear previous items
        List<TypeAbonnement> abonnements = typeAbonnementService.getAllTypeAbonnements();

        if (abonnements.isEmpty()) {
            abonnementDetailsLabel.setText("Aucun abonnement disponible."); // Feedback for empty list
        } else {
            abonnementComboBox.getItems().addAll(abonnements); // Populate ComboBox
            abonnementComboBox.setOnAction(e -> showAbonnementDetails());
        }
    }

    @FXML
    private void showAbonnementDetails() {
        TypeAbonnement selectedAbonnement = abonnementComboBox.getValue();
        if (selectedAbonnement != null) {
            abonnementDetailsLabel.setText("Nom: " + selectedAbonnement.getNom() +
                    ", Prix: " + selectedAbonnement.getPrix() +
                    ", Statut: " + (selectedAbonnement.getIsPremium() ? "Premium" : "Standard"));
        }
    }

    @FXML
    private void submitOffer() {
        // Handle the submission of the selected service and abonnement
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Offre soumise avec succès!");
        alert.showAndWait();
    }
}