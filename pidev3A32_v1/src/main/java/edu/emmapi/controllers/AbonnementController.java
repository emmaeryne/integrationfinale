package edu.emmapi.controllers;

import edu.emmapi.entities.Abonnement;
import edu.emmapi.entities.Service;
import edu.emmapi.entities.TypeAbonnement;
import edu.emmapi.entities.Statut;
import edu.emmapi.services.AbonnementService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

import java.util.List;

public class AbonnementController {
    @FXML private TableView<Abonnement> abonnementTable;
    @FXML private TableColumn<Abonnement, String> serviceIdColumn;
    @FXML private TableColumn<Abonnement, String> typeAbonnementIdColumn;
    @FXML private TableColumn<Abonnement, Double> prixTotalColumn;
    @FXML private TableColumn<Abonnement, String> statutColumn;

    @FXML private ComboBox<String> sortComboBox;

    private AbonnementService abonnementService;
    private ObservableList<Abonnement> abonnements;

    @FXML
    public void initialize() {
        abonnementService = new AbonnementService();
        abonnements = FXCollections.observableArrayList();
        abonnementTable.setItems(abonnements);
        setupTableColumns();
        loadAbonnements();

        // Set up sorting options
        sortComboBox.setItems(FXCollections.observableArrayList("prix_asc", "prix_desc"));
        sortComboBox.setOnAction(event -> handleSort());
    }

    private void setupTableColumns() {
        serviceIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getService().getId())));
        typeAbonnementIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getTypeAbonnement().getId())));
        prixTotalColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrixTotal()).asObject());
        statutColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatut().toString()));
    }

    @FXML
    private void loadAbonnements() {
        try {
            List<Abonnement> loadedAbonnements = abonnementService.getAllAbonnements(null);
            abonnements.clear();
            abonnements.addAll(loadedAbonnements);
        } catch (Exception e) {
            showError("Erreur lors du chargement des abonnements", e.getMessage());
        }
    }

    @FXML
    private void handleSort() {
        String selectedSort = sortComboBox.getValue();
        if (selectedSort != null) {
            try {
                List<Abonnement> sortedAbonnements = abonnementService.getAllAbonnements(selectedSort);
                abonnements.clear();
                abonnements.addAll(sortedAbonnements);
            } catch (Exception e) {
                showError("Erreur lors du tri des abonnements", e.getMessage());
            }
        }
    }

    @FXML
    private void handleAddAbonnement() {
        Abonnement newAbonnement = showAbonnementDialog(null);
        if (newAbonnement != null) {
            try {
                abonnementService.ajouterAbonnement(newAbonnement);
                loadAbonnements(); // Reload the abonnements list
            } catch (Exception e) {
                showError("Erreur lors de l'ajout de l'abonnement", e.getMessage());
            }
        }
    }

    @FXML
    private void handleEditAbonnement() {
        Abonnement selectedAbonnement = abonnementTable.getSelectionModel().getSelectedItem();
        if (selectedAbonnement != null) {
            Abonnement updatedAbonnement = showAbonnementDialog(selectedAbonnement);
            if (updatedAbonnement != null) {
                try {
                    // Ensure the ID is set before proceeding
                    if (updatedAbonnement.getId() != null) {
                        abonnementService.updateAbonnement(updatedAbonnement);
                        loadAbonnements(); // Reload the abonnements list
                    } else {
                        showError("Erreur", "L'ID de l'abonnement est nul. Impossible de mettre à jour.");
                    }
                } catch (Exception e) {
                    showError("Erreur lors de la mise à jour de l'abonnement", e.getMessage());
                }
            }
        } else {
            showError("Sélectionnez un abonnement", "Veuillez sélectionner un abonnement à modifier.");
        }
    }

    @FXML
    private void handleDeleteAbonnement() {
        Abonnement selectedAbonnement = abonnementTable.getSelectionModel().getSelectedItem();
        if (selectedAbonnement != null) {
            try {
                abonnementService.deleteAbonnement(selectedAbonnement.getId());
                loadAbonnements(); // Reload the abonnements list
            } catch (Exception e) {
                showError("Erreur lors de la suppression de l'abonnement", e.getMessage());
            }
        } else {
            showError("Sélectionnez un abonnement", "Veuillez sélectionner un abonnement à supprimer.");
        }
    }

    private Abonnement showAbonnementDialog(Abonnement abonnement) {
        Dialog<Abonnement> dialog = new Dialog<>();
        dialog.setTitle(abonnement == null ? "Ajouter Abonnement" : "Modifier Abonnement");
        dialog.setHeaderText("Veuillez entrer les détails de l'abonnement");

        // Create input fields
        TextField serviceIdField = new TextField();
        serviceIdField.setPromptText("Service ID");
        TextField typeIdField = new TextField();
        typeIdField.setPromptText("Type Abonnement ID");
        TextField prixTotalField = new TextField();
        prixTotalField.setPromptText("Prix Total");

        // Create ComboBox for statut
        ComboBox<Statut> statutComboBox = new ComboBox<>();
        statutComboBox.getItems().addAll(Statut.values());
        statutComboBox.setPromptText("Sélectionner un statut");

        // Create date fields
        DatePicker dateDebutField = new DatePicker();
        dateDebutField.setPromptText("Date de Début");
        DatePicker dateFinField = new DatePicker();
        dateFinField.setPromptText("Date de Fin");

        // Populate fields if editing
        if (abonnement != null) {
            serviceIdField.setText(String.valueOf(abonnement.getService().getId()));
            typeIdField.setText(String.valueOf(abonnement.getTypeAbonnement().getId()));
            prixTotalField.setText(String.valueOf(abonnement.getPrixTotal()));
            statutComboBox.setValue(abonnement.getStatut());
            dateDebutField.setValue(abonnement.getDateDebut());
            dateFinField.setValue(abonnement.getDateFin());
        }

        // Create a grid pane to hold the fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("Service ID:"), 0, 0);
        grid.add(serviceIdField, 1, 0);
        grid.add(new Label("Type Abonnement ID:"), 0, 1);
        grid.add(typeIdField, 1, 1);
        grid.add(new Label("Prix Total:"), 0, 2);
        grid.add(prixTotalField, 1, 2);
        grid.add(new Label("Statut:"), 0, 3);
        grid.add(statutComboBox, 1, 3);
        grid.add(new Label("Date de Début:"), 0, 4);
        grid.add(dateDebutField, 1, 4);
        grid.add(new Label("Date de Fin:"), 0, 5);
        grid.add(dateFinField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Add buttons
        ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Convert the result to an Abonnement object when the add button is pressed
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                Abonnement newAbonnement = new Abonnement();
                // Set ID for the existing abonnement if editing
                if (abonnement != null) {
                    newAbonnement.setId(abonnement.getId());
                }
                Service service = new Service();
                service.setId(Long.valueOf(serviceIdField.getText()));
                newAbonnement.setService(service);

                TypeAbonnement typeAbonnement = new TypeAbonnement();
                typeAbonnement.setId(Long.valueOf(typeIdField.getText()));
                newAbonnement.setTypeAbonnement(typeAbonnement);

                newAbonnement.setPrixTotal(Double.valueOf(prixTotalField.getText()));
                newAbonnement.setStatut(statutComboBox.getValue());
                newAbonnement.setDateDebut(dateDebutField.getValue());
                newAbonnement.setDateFin(dateFinField.getValue());

                return newAbonnement;
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleBackToHome(javafx.event.ActionEvent actionEvent) {
        // Implement navigation back to home if needed
    }
}