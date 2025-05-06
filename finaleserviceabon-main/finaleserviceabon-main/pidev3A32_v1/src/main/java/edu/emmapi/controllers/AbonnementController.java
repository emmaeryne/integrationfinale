


/*package edu.emmapi.controllers;

import edu.emmapi.entities.Abonnement;
import edu.emmapi.entities.Service;
import edu.emmapi.entities.TypeAbonnement;
import edu.emmapi.entities.Statut;
import edu.emmapi.services.AbonnementService;
import edu.emmapi.services.ServiceService;
import edu.emmapi.services.TypeAbonnementService;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import org.eclipse.paho.client.mqttv3.*;

import java.time.LocalDate;
import java.util.List;

public class AbonnementController {
    @FXML private TableView<Abonnement> abonnementTable;
    @FXML private TableColumn<Abonnement, String> serviceIdColumn;
    @FXML private TableColumn<Abonnement, String> typeAbonnementIdColumn;
    @FXML private TableColumn<Abonnement, Double> prixTotalColumn;
    @FXML private TableColumn<Abonnement, String> statutColumn;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private TextArea iotNotifications;

    private AbonnementService abonnementService;
    private ServiceService serviceService;
    private TypeAbonnementService typeAbonnementService;
    private ObservableList<Abonnement> abonnements;
    private ObservableList<Service> services;
    private ObservableList<TypeAbonnement> typeAbonnements;
    private MqttClient mqttClient;

    @FXML
    public void initialize() {
        abonnementService = new AbonnementService();
        serviceService = new ServiceService();
        typeAbonnementService = new TypeAbonnementService();
        abonnements = FXCollections.observableArrayList();
        services = FXCollections.observableArrayList();
        typeAbonnements = FXCollections.observableArrayList();
        abonnementTable.setItems(abonnements);

        setupTableColumns();

        try {
            mqttClient = new MqttClient("tcp://172.20.10.7:1883", MqttClient.generateClientId(), null);
            mqttClient.connect();
            logToIoT("Connecté au broker MQTT local sur 172.20.10.7:1883");
        } catch (MqttException e) {
            logToIoT("Erreur connexion MQTT: " + e.getMessage());
            mqttClient = null;
        }

        loadServices();
        loadTypeAbonnements();
        loadAbonnements();

        sortComboBox.setItems(FXCollections.observableArrayList("prix_asc", "prix_desc"));
        sortComboBox.setOnAction(event -> handleSort());

        iotNotifications.clear();
        logToIoT("Contrôleur initialisé");
    }

    private void setupTableColumns() {
        serviceIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getService().getNom()));
        typeAbonnementIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTypeAbonnement().getNom()));
        prixTotalColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrixTotal()).asObject());
        statutColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatut().toString()));
    }

    private void loadServices() {
        try {
            List<Service> serviceList = serviceService.getAllServices();
            services.clear();
            services.addAll(serviceList);
            logToIoT("Services chargés: " + services.size() + " trouvés");
        } catch (Exception e) {
            showError("Erreur lors du chargement des services", e.getMessage());
            logToIoT("Erreur chargement services: " + e.getMessage());
        }
    }

    private void loadTypeAbonnements() {
        try {
            List<TypeAbonnement> typeList = typeAbonnementService.getAllTypeAbonnements(null);
            typeAbonnements.clear();
            typeAbonnements.addAll(typeList);
            logToIoT("Types d'abonnements chargés: " + typeAbonnements.size() + " trouvés");
        } catch (Exception e) {
            showError("Erreur lors du chargement des types d'abonnements", e.getMessage());
            logToIoT("Erreur chargement types abonnements: " + e.getMessage());
        }
    }

    @FXML
    private void loadAbonnements() {
        try {
            List<Abonnement> loadedAbonnements = abonnementService.getAllAbonnements(null);
            abonnements.clear();
            abonnements.addAll(loadedAbonnements);
            logToIoT("Abonnements chargés: " + abonnements.size() + " trouvés");
        } catch (Exception e) {
            showError("Erreur lors du chargement des abonnements", e.getMessage());
            logToIoT("Erreur chargement abonnements: " + e.getMessage());
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
                logToIoT("Abonnements triés par: " + selectedSort);
            } catch (Exception e) {
                showError("Erreur lors du tri des abonnements", e.getMessage());
                logToIoT("Erreur tri abonnements: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleAddAbonnement() {
        logToIoT("Bouton Ajouter cliqué");
        Abonnement newAbonnement = showAbonnementDialog(null);
        if (newAbonnement != null) {
            try {
                abonnementService.ajouterAbonnement(newAbonnement);
                loadAbonnements();
                if (newAbonnement.getPrixTotal() == 0) {
                    sendFreeAbonnementIoTNotification("Abonnement gratuit ajouté: " + newAbonnement.getService().getNom());
                } else {
                    sendIoTNotification("Abonnement ajouté: " + newAbonnement.getService().getNom());
                }
            } catch (Exception e) {
                showError("Erreur lors de l'ajout de l'abonnement", e.getMessage());
                logToIoT("Erreur ajout abonnement: " + e.getMessage());
            }
        } else {
            logToIoT("Ajout annulé ou dialogue fermé sans données");
        }
    }

    @FXML
    private void handleEditAbonnement() {
        Abonnement selectedAbonnement = abonnementTable.getSelectionModel().getSelectedItem();
        if (selectedAbonnement != null) {
            Abonnement updatedAbonnement = showAbonnementDialog(selectedAbonnement);
            if (updatedAbonnement != null) {
                try {
                    if (updatedAbonnement.getId() != null) {
                        abonnementService.updateAbonnement(updatedAbonnement);
                        loadAbonnements();
                        if (updatedAbonnement.getPrixTotal() == 0) {
                            sendFreeAbonnementIoTNotification("Abonnement gratuit modifié: " + updatedAbonnement.getService().getNom());
                        } else {
                            sendIoTNotification("Abonnement modifié: " + updatedAbonnement.getService().getNom());
                        }
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
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Confirmer la suppression ?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        abonnementService.deleteAbonnement(selectedAbonnement.getId());
                        loadAbonnements();
                        sendIoTNotification("Abonnement supprimé: " + selectedAbonnement.getService().getNom());
                    } catch (Exception e) {
                        showError("Erreur lors de la suppression de l'abonnement", e.getMessage());
                    }
                }
            });
        } else {
            showError("Sélectionnez un abonnement", "Veuillez sélectionner un abonnement à supprimer.");
        }
    }

    private Abonnement showAbonnementDialog(Abonnement abonnement) {
        Dialog<Abonnement> dialog = new Dialog<>();
        dialog.setTitle(abonnement == null ? "Ajouter Abonnement" : "Modifier Abonnement");
        dialog.setHeaderText("Entrez les détails de l'abonnement");

        ComboBox<Service> serviceComboBox = new ComboBox<>(services);
        serviceComboBox.setPromptText("Sélectionner un service");
        serviceComboBox.setCellFactory(lv -> new ListCell<Service>() {
            @Override
            protected void updateItem(Service item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom());
            }
        });
        serviceComboBox.setButtonCell(serviceComboBox.getCellFactory().call(null));

        ComboBox<TypeAbonnement> typeComboBox = new ComboBox<>(typeAbonnements);
        typeComboBox.setPromptText("Sélectionner un type d'abonnement");
        typeComboBox.setCellFactory(lv -> new ListCell<TypeAbonnement>() {
            @Override
            protected void updateItem(TypeAbonnement item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom());
            }
        });
        typeComboBox.setButtonCell(typeComboBox.getCellFactory().call(null));

        TextField prixTotalField = new TextField();
        prixTotalField.setPromptText("Prix Total");
        CheckBox freeCheckBox = new CheckBox("Gratuit");
        ComboBox<Statut> statutComboBox = new ComboBox<>(FXCollections.observableArrayList(Statut.values()));
        statutComboBox.setPromptText("Sélectionner un statut");
        DatePicker dateDebutField = new DatePicker();
        dateDebutField.setPromptText("Date de Début");
        DatePicker dateFinField = new DatePicker();
        dateFinField.setPromptText("Date de Fin");

        freeCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                prixTotalField.setText("0");
                prixTotalField.setDisable(true);
            } else {
                prixTotalField.setText("");
                prixTotalField.setDisable(false);
            }
        });

        if (abonnement != null) {
            serviceComboBox.setValue(abonnement.getService());
            typeComboBox.setValue(abonnement.getTypeAbonnement());
            prixTotalField.setText(String.valueOf(abonnement.getPrixTotal()));
            freeCheckBox.setSelected(abonnement.getPrixTotal() == 0);
            statutComboBox.setValue(abonnement.getStatut());
            dateDebutField.setValue(abonnement.getDateDebut());
            dateFinField.setValue(abonnement.getDateFin());
        } else {
            dateDebutField.setValue(LocalDate.now());
            dateFinField.setValue(LocalDate.now().plusMonths(1));
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("Service:"), 0, 0);
        grid.add(serviceComboBox, 1, 0);
        grid.add(new Label("Type Abonnement:"), 0, 1);
        grid.add(typeComboBox, 1, 1);
        grid.add(new Label("Prix Total:"), 0, 2);
        grid.add(prixTotalField, 1, 2);
        grid.add(new Label("Gratuit:"), 0, 3);
        grid.add(freeCheckBox, 1, 3);
        grid.add(new Label("Statut:"), 0, 4);
        grid.add(statutComboBox, 1, 4);
        grid.add(new Label("Date de Début:"), 0, 5);
        grid.add(dateDebutField, 1, 5);
        grid.add(new Label("Date de Fin:"), 0, 6);
        grid.add(dateFinField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType(abonnement == null ? "Ajouter" : "Mettre à jour", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        Runnable updateButtonState = () -> {
            boolean isValid = serviceComboBox.getValue() != null &&
                    typeComboBox.getValue() != null &&
                    !prixTotalField.getText().trim().isEmpty() &&
                    statutComboBox.getValue() != null &&
                    dateDebutField.getValue() != null &&
                    dateFinField.getValue() != null;
            saveButton.setDisable(!isValid);
        };

        serviceComboBox.valueProperty().addListener((obs, old, newVal) -> updateButtonState.run());
        typeComboBox.valueProperty().addListener((obs, old, newVal) -> updateButtonState.run());
        prixTotalField.textProperty().addListener((obs, old, newVal) -> updateButtonState.run());
        statutComboBox.valueProperty().addListener((obs, old, newVal) -> updateButtonState.run());
        dateDebutField.valueProperty().addListener((obs, old, newVal) -> updateButtonState.run());
        dateFinField.valueProperty().addListener((obs, old, newVal) -> updateButtonState.run());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    Abonnement newAbonnement = new Abonnement();
                    if (abonnement != null) {
                        newAbonnement.setId(abonnement.getId());
                    }
                    newAbonnement.setService(serviceComboBox.getValue());
                    newAbonnement.setTypeAbonnement(typeComboBox.getValue());
                    newAbonnement.setPrixTotal(Double.parseDouble(prixTotalField.getText().trim()));
                    newAbonnement.setStatut(statutComboBox.getValue());
                    newAbonnement.setDateDebut(dateDebutField.getValue());
                    newAbonnement.setDateFin(dateFinField.getValue());

                    if (newAbonnement.getDateDebut().isAfter(newAbonnement.getDateFin())) {
                        showError("Erreur de validation", "La date de début doit être avant la date de fin.");
                        return null;
                    }
                    logToIoT("Abonnement créé dans le dialogue: " + newAbonnement.getService().getNom());
                    return newAbonnement;
                } catch (NumberFormatException e) {
                    showError("Erreur de validation", "Veuillez entrer une valeur numérique valide pour le prix.");
                    logToIoT("Erreur validation numérique: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    private void sendIoTNotification(String message) {
        String timestampedMessage = LocalDate.now() + " " + java.time.LocalTime.now().toString().substring(0, 8) + ": " + message + "\n";
        System.out.println("[IoT Notification] " + timestampedMessage);
        Platform.runLater(() -> iotNotifications.appendText(timestampedMessage));
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                mqttClient.publish("abonnements/general", new MqttMessage(timestampedMessage.getBytes()));
            } catch (MqttException e) {
                logToIoT("Erreur envoi MQTT: " + e.getMessage());
            }
        } else {
            logToIoT("MQTT client non connecté, notification non envoyée");
        }
    }

    private void sendFreeAbonnementIoTNotification(String message) {
        String timestampedMessage = LocalDate.now() + " " + java.time.LocalTime.now().toString().substring(0, 8) + ": PROMO - " + message + "\n";
        System.out.println("[IoT Promo Notification] " + timestampedMessage);
        Platform.runLater(() -> iotNotifications.appendText(timestampedMessage));
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                mqttClient.publish("abonnements/promo", new MqttMessage(timestampedMessage.getBytes()));
            } catch (MqttException e) {
                logToIoT("Erreur envoi MQTT promo: " + e.getMessage());
            }
        } else {
            logToIoT("MQTT client non connecté, notification promo non envoyée");
        }
    }

    private void logToIoT(String message) {
        sendIoTNotification("[DEBUG] " + message);
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
}*/
/*package edu.emmapi.controllers;

import edu.emmapi.entities.Abonnement;
import edu.emmapi.entities.Service;
import edu.emmapi.entities.TypeAbonnement;
import edu.emmapi.entities.Statut;
import edu.emmapi.services.AbonnementService;
import edu.emmapi.services.ServiceService;
import edu.emmapi.services.TypeAbonnementService;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.time.LocalDate;
import java.util.List;

public class AbonnementController {
    @FXML private TableView<Abonnement> abonnementTable;
    @FXML private TableColumn<Abonnement, String> serviceIdColumn;
    @FXML private TableColumn<Abonnement, String> typeAbonnementIdColumn;
    @FXML private TableColumn<Abonnement, Double> prixTotalColumn;
    @FXML private TableColumn<Abonnement, String> statutColumn;
    @FXML private TableColumn<Abonnement, Void> actionsColumn;
    @FXML private ComboBox<String> sortComboBox;

    private AbonnementService abonnementService;
    private ServiceService serviceService;
    private TypeAbonnementService typeAbonnementService;
    private ObservableList<Abonnement> abonnements;
    private ObservableList<Service> services;
    private ObservableList<TypeAbonnement> typeAbonnements;

    @FXML
    public void initialize() {
        abonnementService = new AbonnementService();
        serviceService = new ServiceService();
        typeAbonnementService = new TypeAbonnementService();
        abonnements = FXCollections.observableArrayList();
        services = FXCollections.observableArrayList();
        typeAbonnements = FXCollections.observableArrayList();
        abonnementTable.setItems(abonnements);

        setupTableColumns();
        setupSortComboBox();

        loadServices();
        loadTypeAbonnements();
        loadAbonnements();
    }

    private void setupTableColumns() {
        serviceIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getService().getNom()));
        typeAbonnementIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTypeAbonnement().getNom()));
        prixTotalColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrixTotal()).asObject());
        statutColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatut().toString()));

        // Actions column with Edit, Delete, and Reserve buttons
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final Button reserveButton = new Button("Réserver");

            {
                editButton.setOnAction(event -> {
                    Abonnement abonnement = getTableView().getItems().get(getIndex());
                    handleEditAbonnement(abonnement);
                });
                deleteButton.setOnAction(event -> {
                    Abonnement abonnement = getTableView().getItems().get(getIndex());
                    handleDeleteAbonnement(abonnement);
                });
                reserveButton.setOnAction(event -> {
                    Abonnement abonnement = getTableView().getItems().get(getIndex());
                    handleReservation(abonnement);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new javafx.scene.layout.HBox(5, editButton, deleteButton, reserveButton));
                }
            }
        });
    }

    private void setupSortComboBox() {
        sortComboBox.setItems(FXCollections.observableArrayList("prix_asc", "prix_desc"));
        sortComboBox.setValue("prix_asc");
        sortComboBox.setOnAction(event -> handleSort());
    }

    private void loadServices() {
        try {
            List<Service> serviceList = serviceService.getAllServices();
            services.clear();
            services.addAll(serviceList);
        } catch (Exception e) {
            showError("Erreur lors du chargement des services", e.getMessage());
        }
    }

    private void loadTypeAbonnements() {
        try {
            List<TypeAbonnement> typeList = typeAbonnementService.getAllTypeAbonnements(null);
            typeAbonnements.clear();
            typeAbonnements.addAll(typeList);
        } catch (Exception e) {
            showError("Erreur lors du chargement des types d'abonnements", e.getMessage());
        }
    }

    @FXML
    private void loadAbonnements() {
        try {
            String sort = sortComboBox.getValue();
            List<Abonnement> loadedAbonnements = abonnementService.getAllAbonnements(sort);
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
                showSuccess("Succès", "Abonnements triés par " + selectedSort);
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
                loadAbonnements();
                showSuccess("Succès", "Abonnement créé avec succès.");
            } catch (Exception e) {
                showError("Erreur lors de l'ajout de l'abonnement", e.getMessage());
            }
        }
    }

    private void handleEditAbonnement(Abonnement selectedAbonnement) {
        if (selectedAbonnement != null) {
            Abonnement updatedAbonnement = showAbonnementDialog(selectedAbonnement);
            if (updatedAbonnement != null) {
                try {
                    if (updatedAbonnement.getId() != null) {
                        abonnementService.updateAbonnement(updatedAbonnement);
                        loadAbonnements();
                        showSuccess("Succès", "Abonnement modifié avec succès.");
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

    private void handleDeleteAbonnement(Abonnement selectedAbonnement) {
        if (selectedAbonnement != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Confirmer la suppression ?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        abonnementService.deleteAbonnement(Long.valueOf(selectedAbonnement.getId()));
                        loadAbonnements();
                        showSuccess("Succès", "Abonnement supprimé avec succès.");
                    } catch (Exception e) {
                        showError("Erreur lors de la suppression de l'abonnement", e.getMessage());
                    }
                }
            });
        } else {
            showError("Sélectionnez un abonnement", "Veuillez sélectionner un abonnement à supprimer.");
        }
    }

    private void handleReservation(Abonnement selectedAbonnement) {
        if (selectedAbonnement != null) {
            try {
                if (selectedAbonnement.isAutoRenouvellement()) {
                    selectedAbonnement.prolongerAbonnement();
                    abonnementService.updateAbonnement(selectedAbonnement);
                    loadAbonnements();
                    showSuccess("Succès", "Réservation effectuée. Abonnement prolongé automatiquement.");
                } else {
                    LocalDate now = LocalDate.now();
                    if (selectedAbonnement.getDateFin().isBefore(now)) {
                        showError("Erreur", "L'abonnement est expiré. Veuillez le renouveler manuellement.");
                    } else {
                        showSuccess("Succès", "Réservation effectuée.");
                    }
                }
            } catch (Exception e) {
                showError("Erreur lors de la réservation", e.getMessage());
            }
        } else {
            showError("Sélectionnez un abonnement", "Veuillez sélectionner un abonnement pour la réservation.");
        }
    }

    private Abonnement showAbonnementDialog(Abonnement abonnement) {
        Dialog<Abonnement> dialog = new Dialog<>();
        dialog.setTitle(abonnement == null ? "Ajouter Abonnement" : "Modifier Abonnement");
        dialog.setHeaderText("Entrez les détails de l'abonnement");

        ComboBox<Service> serviceComboBox = new ComboBox<>(services);
        serviceComboBox.setPromptText("Sélectionner un service");
        serviceComboBox.setCellFactory(lv -> new ListCell<Service>() {
            @Override
            protected void updateItem(Service item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom());
            }
        });
        serviceComboBox.setButtonCell(serviceComboBox.getCellFactory().call(null));

        ComboBox<TypeAbonnement> typeComboBox = new ComboBox<>(typeAbonnements);
        typeComboBox.setPromptText("Sélectionner un type d'abonnement");
        typeComboBox.setCellFactory(lv -> new ListCell<TypeAbonnement>() {
            @Override
            protected void updateItem(TypeAbonnement item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom());
            }
        });
        typeComboBox.setButtonCell(typeComboBox.getCellFactory().call(null));

        TextField prixTotalField = new TextField();
        prixTotalField.setPromptText("Prix Total");
        CheckBox freeCheckBox = new CheckBox("Gratuit");
        ComboBox<Statut> statutComboBox = new ComboBox<>(FXCollections.observableArrayList(Statut.values()));
        statutComboBox.setPromptText("Sélectionner un statut");
        DatePicker dateDebutField = new DatePicker();
        dateDebutField.setPromptText("Date de Début");
        DatePicker dateFinField = new DatePicker();
        dateFinField.setPromptText("Date de Fin");
        CheckBox autoRenouvellementCheckBox = new CheckBox("Renouvellement Automatique");

        freeCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                prixTotalField.setText("0");
                prixTotalField.setDisable(true);
            } else {
                prixTotalField.setText("");
                prixTotalField.setDisable(false);
            }
        });

        if (abonnement != null) {
            serviceComboBox.setValue(abonnement.getService());
            typeComboBox.setValue(abonnement.getTypeAbonnement());
            prixTotalField.setText(String.valueOf(abonnement.getPrixTotal()));
            freeCheckBox.setSelected(abonnement.getPrixTotal() == 0);
            statutComboBox.setValue(abonnement.getStatut());
            dateDebutField.setValue(abonnement.getDateDebut());
            dateFinField.setValue(abonnement.getDateFin());
            autoRenouvellementCheckBox.setSelected(abonnement.isAutoRenouvellement());
        } else {
            dateDebutField.setValue(LocalDate.now());
            dateFinField.setValue(LocalDate.now().plusMonths(1));
            statutComboBox.setValue(Statut.ACTIF);
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("Service:"), 0, 0);
        grid.add(serviceComboBox, 1, 0);
        grid.add(new Label("Type Abonnement:"), 0, 1);
        grid.add(typeComboBox, 1, 1);
        grid.add(new Label("Prix Total:"), 0, 2);
        grid.add(prixTotalField, 1, 2);
        grid.add(new Label("Gratuit:"), 0, 3);
        grid.add(freeCheckBox, 1, 3);
        grid.add(new Label("Statut:"), 0, 4);
        grid.add(statutComboBox, 1, 4);
        grid.add(new Label("Date de Début:"), 0, 5);
        grid.add(dateDebutField, 1, 5);
        grid.add(new Label("Date de Fin:"), 0, 6);
        grid.add(dateFinField, 1, 6);
        grid.add(new Label("Renouvellement Automatique:"), 0, 7);
        grid.add(autoRenouvellementCheckBox, 1, 7);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType(abonnement == null ? "Ajouter" : "Mettre à jour", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        Runnable updateButtonState = () -> {
            boolean isValid = serviceComboBox.getValue() != null &&
                    typeComboBox.getValue() != null &&
                    !prixTotalField.getText().trim().isEmpty() &&
                    statutComboBox.getValue() != null &&
                    dateDebutField.getValue() != null &&
                    dateFinField.getValue() != null;
            saveButton.setDisable(!isValid);
        };

        serviceComboBox.valueProperty().addListener((obs, old, newVal) -> updateButtonState.run());
        typeComboBox.valueProperty().addListener((obs, old, newVal) -> updateButtonState.run());
        prixTotalField.textProperty().addListener((obs, old, newVal) -> updateButtonState.run());
        statutComboBox.valueProperty().addListener((obs, old, newVal) -> updateButtonState.run());
        dateDebutField.valueProperty().addListener((obs, old, newVal) -> updateButtonState.run());
        dateFinField.valueProperty().addListener((obs, old, newVal) -> updateButtonState.run());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    Abonnement newAbonnement = new Abonnement();
                    if (abonnement != null) {
                        newAbonnement.setId(abonnement.getId());
                    }
                    newAbonnement.setService(serviceComboBox.getValue());
                    newAbonnement.setTypeAbonnement(typeComboBox.getValue());
                    newAbonnement.setPrixTotal(Double.parseDouble(prixTotalField.getText().trim()));
                    newAbonnement.setStatut(statutComboBox.getValue());
                    newAbonnement.setDateDebut(dateDebutField.getValue());
                    newAbonnement.setDateFin(dateFinField.getValue());
                    newAbonnement.setAutoRenouvellement(autoRenouvellementCheckBox.isSelected());

                    if (newAbonnement.getDateDebut().isAfter(newAbonnement.getDateFin())) {
                        showError("Erreur de validation", "La date de début doit être avant la date de fin.");
                        return null;
                    }
                    return newAbonnement;
                } catch (NumberFormatException e) {
                    showError("Erreur de validation", "Veuillez entrer une valeur numérique valide pour le prix.");
                    return null;
                }
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

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleBackToHome(javafx.event.ActionEvent actionEvent) {
        // Implement navigation back to home if needed
    }
}*/


package edu.emmapi.controllers;

import edu.emmapi.entities.Abonnement;
import edu.emmapi.entities.Service;
import edu.emmapi.entities.TypeAbonnement;
import edu.emmapi.entities.Statut;
import edu.emmapi.services.AbonnementService;
import edu.emmapi.services.ServiceService;
import edu.emmapi.services.TypeAbonnementService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.util.List;

public class AbonnementController {
    @FXML private TableView<Abonnement> abonnementTable;
    @FXML private TableColumn<Abonnement, String> serviceIdColumn;
    @FXML private TableColumn<Abonnement, String> typeAbonnementIdColumn;
    @FXML private TableColumn<Abonnement, LocalDate> dateDebutColumn;
    @FXML private TableColumn<Abonnement, LocalDate> dateFinColumn;
    @FXML private TableColumn<Abonnement, Boolean> estActifColumn;
    @FXML private TableColumn<Abonnement, Double> prixTotalColumn;
    @FXML private TableColumn<Abonnement, Boolean> estGratuitColumn;
    @FXML private TableColumn<Abonnement, String> statutColumn;
    @FXML private TableColumn<Abonnement, Integer> nombreSeancesRestantesColumn;
    @FXML private TableColumn<Abonnement, Boolean> autoRenouvellementColumn;
    @FXML private TableColumn<Abonnement, Integer> dureeMoisColumn;
    @FXML private TableColumn<Abonnement, String> modePaiementColumn;
    @FXML private TableColumn<Abonnement, Void> actionsColumn;
    @FXML private ComboBox<String> sortComboBox;

    private AbonnementService abonnementService;
    private ServiceService serviceService;
    private TypeAbonnementService typeAbonnementService;
    private ObservableList<Abonnement> abonnements;
    private ObservableList<Service> services;
    private ObservableList<TypeAbonnement> typeAbonnements;

    @FXML
    public void initialize() {
        abonnementService = new AbonnementService();
        serviceService = new ServiceService();
        typeAbonnementService = new TypeAbonnementService();
        abonnements = FXCollections.observableArrayList();
        services = FXCollections.observableArrayList();
        typeAbonnements = FXCollections.observableArrayList();
        abonnementTable.setItems(abonnements);

        setupTableColumns();
        setupSortComboBox();

        loadServices();
        loadTypeAbonnements();
        loadAbonnements();
    }

    private void setupTableColumns() {
        serviceIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getService().getNom()));
        typeAbonnementIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTypeAbonnement().getNom()));
        dateDebutColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        dateFinColumn.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        estActifColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isEstActif()).asObject());
        prixTotalColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrixTotal()).asObject());
        estGratuitColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isEstGratuit()).asObject());
        statutColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatut().toString()));
        nombreSeancesRestantesColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getNombreSeancesRestantes()).asObject());
        autoRenouvellementColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isAutoRenouvellement()).asObject());
        dureeMoisColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDureeMois() != null ? cellData.getValue().getDureeMois() : 0).asObject());
        modePaiementColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getModePaiement() != null ? cellData.getValue().getModePaiement() : ""));

        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final Button reserveButton = new Button("Réserver");

            {
                editButton.setOnAction(event -> {
                    Abonnement abonnement = getTableView().getItems().get(getIndex());
                    handleEditAbonnement(abonnement);
                });
                deleteButton.setOnAction(event -> {
                    Abonnement abonnement = getTableView().getItems().get(getIndex());
                    handleDeleteAbonnement(abonnement);
                });
                reserveButton.setOnAction(event -> {
                    Abonnement abonnement = getTableView().getItems().get(getIndex());
                    handleReservation(abonnement);
                });
                editButton.setStyle("-fx-background-color: #FF7043; -fx-text-fill: #FFFFFF; -fx-font-size: 12px;");
                deleteButton.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: #FFFFFF; -fx-font-size: 12px;");
                reserveButton.setStyle("-fx-background-color: #66C2D9; -fx-text-fill: #FFFFFF; -fx-font-size: 12px;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new javafx.scene.layout.HBox(5, editButton, deleteButton, reserveButton));
                }
            }
        });
    }

    private void setupSortComboBox() {
        sortComboBox.setItems(FXCollections.observableArrayList("prix_asc", "prix_desc"));
        sortComboBox.setValue("prix_asc");
        sortComboBox.setOnAction(event -> handleSort());
    }

    private void loadServices() {
        try {
            List<Service> serviceList = serviceService.getAllServices();
            services.clear();
            services.addAll(serviceList);
        } catch (Exception e) {
            showError("Erreur lors du chargement des services", e.getMessage());
        }
    }

    private void loadTypeAbonnements() {
        try {
            List<TypeAbonnement> typeList = typeAbonnementService.getAllTypeAbonnements();
            typeAbonnements.clear();
            typeAbonnements.addAll(typeList);
        } catch (Exception e) {
            showError("Erreur lors du chargement des types d'abonnements", e.getMessage());
        }
    }

    @FXML
    private void loadAbonnements() {
        try {
            String sort = sortComboBox.getValue();
            List<Abonnement> loadedAbonnements = abonnementService.getAllAbonnements(sort);
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
                showSuccess("Succès", "Abonnements triés par " + selectedSort);
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
                loadAbonnements();
                showSuccess("Succès", "Abonnement créé avec succès.");
            } catch (Exception e) {
                showError("Erreur lors de l'ajout de l'abonnement", e.getMessage());
            }
        }
    }

    private void handleEditAbonnement(Abonnement selectedAbonnement) {
        if (selectedAbonnement != null) {
            Abonnement updatedAbonnement = showAbonnementDialog(selectedAbonnement);
            if (updatedAbonnement != null) {
                try {
                    abonnementService.updateAbonnement(updatedAbonnement);
                    loadAbonnements();
                    showSuccess("Succès", "Abonnement modifié avec succès.");
                } catch (Exception e) {
                    showError("Erreur lors de la mise à jour de l'abonnement", e.getMessage());
                }
            }
        } else {
            showError("Sélectionnez un abonnement", "Veuillez sélectionner un abonnement à modifier.");
        }
    }

    private void handleDeleteAbonnement(Abonnement selectedAbonnement) {
        if (selectedAbonnement != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Confirmer la suppression ?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        abonnementService.deleteAbonnement(selectedAbonnement.getId());
                        loadAbonnements();
                        showSuccess("Succès", "Abonnement supprimé avec succès.");
                    } catch (Exception e) {
                        showError("Erreur lors de la suppression de l'abonnement", e.getMessage());
                    }
                }
            });
        } else {
            showError("Sélectionnez un abonnement", "Veuillez sélectionner un abonnement à supprimer.");
        }
    }

    private void handleReservation(Abonnement selectedAbonnement) {
        if (selectedAbonnement != null) {
            try {
                if (selectedAbonnement.isAutoRenouvellement()) {
                    selectedAbonnement.prolongerAbonnement();
                    abonnementService.updateAbonnement(selectedAbonnement);
                    loadAbonnements();
                    showSuccess("Succès", "Réservation effectuée. Abonnement prolongé automatiquement.");
                } else {
                    LocalDate now = LocalDate.now();
                    if (selectedAbonnement.getDateFin().isBefore(now)) {
                        showError("Erreur", "L'abonnement est expiré. Veuillez le renouveler manuellement.");
                    } else {
                        showSuccess("Succès", "Réservation effectuée.");
                    }
                }
            } catch (Exception e) {
                showError("Erreur lors de la réservation", e.getMessage());
            }
        } else {
            showError("Sélectionnez un abonnement", "Veuillez sélectionner un abonnement pour la réservation.");
        }
    }

    private Abonnement showAbonnementDialog(Abonnement abonnement) {
        Dialog<Abonnement> dialog = new Dialog<>();
        dialog.setTitle(abonnement == null ? "Ajouter Abonnement" : "Modifier Abonnement");
        dialog.setHeaderText("Entrez les détails de l'abonnement");

        ComboBox<Service> serviceComboBox = new ComboBox<>(services);
        serviceComboBox.setPromptText("Sélectionner un service");
        serviceComboBox.setCellFactory(lv -> new ListCell<Service>() {
            @Override
            protected void updateItem(Service item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom());
            }
        });
        serviceComboBox.setButtonCell(serviceComboBox.getCellFactory().call(null));

        ComboBox<TypeAbonnement> typeComboBox = new ComboBox<>(typeAbonnements);
        typeComboBox.setPromptText("Sélectionner un type d'abonnement");
        typeComboBox.setCellFactory(lv -> new ListCell<TypeAbonnement>() {
            @Override
            protected void updateItem(TypeAbonnement item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom());
            }
        });
        typeComboBox.setButtonCell(typeComboBox.getCellFactory().call(null));

        DatePicker dateDebutField = new DatePicker();
        dateDebutField.setPromptText("Date de Début");
        DatePicker dateFinField = new DatePicker();
        dateFinField.setPromptText("Date de Fin");
        CheckBox estActifCheckBox = new CheckBox("Actif");
        TextField prixTotalField = new TextField();
        prixTotalField.setPromptText("Prix Total");
        CheckBox estGratuitCheckBox = new CheckBox("Gratuit");
        ComboBox<Statut> statutComboBox = new ComboBox<>(FXCollections.observableArrayList(Statut.values()));
        statutComboBox.setPromptText("Sélectionner un statut");
        TextField nombreSeancesField = new TextField();
        nombreSeancesField.setPromptText("Séances Restantes");
        CheckBox autoRenouvellementCheckBox = new CheckBox("Renouvellement Automatique");
        TextField dureeMoisField = new TextField();
        dureeMoisField.setPromptText("Durée (mois)");
        ComboBox<String> modePaiementComboBox = new ComboBox<>(FXCollections.observableArrayList("Carte bancaire", "Espèces"));
        modePaiementComboBox.setPromptText("Mode de Paiement");

        estGratuitCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                prixTotalField.setText("0.00");
                prixTotalField.setDisable(true);
            } else {
                prixTotalField.setText("");
                prixTotalField.setDisable(false);
            }
        });

        autoRenouvellementCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            dureeMoisField.setDisable(newVal);
            if (newVal) {
                dureeMoisField.setText("");
            }
        });

        if (abonnement != null) {
            serviceComboBox.setValue(abonnement.getService());
            typeComboBox.setValue(abonnement.getTypeAbonnement());
            dateDebutField.setValue(abonnement.getDateDebut());
            dateFinField.setValue(abonnement.getDateFin());
            estActifCheckBox.setSelected(abonnement.isEstActif());
            prixTotalField.setText(String.valueOf(abonnement.getPrixTotal()));
            estGratuitCheckBox.setSelected(abonnement.isEstGratuit());
            statutComboBox.setValue(abonnement.getStatut());
            nombreSeancesField.setText(String.valueOf(abonnement.getNombreSeancesRestantes()));
            autoRenouvellementCheckBox.setSelected(abonnement.isAutoRenouvellement());
            dureeMoisField.setText(abonnement.getDureeMois() != null ? String.valueOf(abonnement.getDureeMois()) : "");
            modePaiementComboBox.setValue(abonnement.getModePaiement());
        } else {
            dateDebutField.setValue(LocalDate.now());
            dateFinField.setValue(LocalDate.now().plusMonths(1));
            estActifCheckBox.setSelected(true);
            statutComboBox.setValue(Statut.ACTIF);
            nombreSeancesField.setText("0");
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("Service:"), 0, 0);
        grid.add(serviceComboBox, 1, 0);
        grid.add(new Label("Type Abonnement:"), 0, 1);
        grid.add(typeComboBox, 1, 1);
        grid.add(new Label("Date de Début:"), 0, 2);
        grid.add(dateDebutField, 1, 2);
        grid.add(new Label("Date de Fin:"), 0, 3);
        grid.add(dateFinField, 1, 3);
        grid.add(new Label("Actif:"), 0, 4);
        grid.add(estActifCheckBox, 1, 4);
        grid.add(new Label("Prix Total:"), 0, 5);
        grid.add(prixTotalField, 1, 5);
        grid.add(new Label("Gratuit:"), 0, 6);
        grid.add(estGratuitCheckBox, 1, 6);
        grid.add(new Label("Statut:"), 0, 7);
        grid.add(statutComboBox, 1, 7);
        grid.add(new Label("Séances Restantes:"), 0, 8);
        grid.add(nombreSeancesField, 1, 8);
        grid.add(new Label("Renouvellement Automatique:"), 0, 9);
        grid.add(autoRenouvellementCheckBox, 1, 9);
        grid.add(new Label("Durée (mois):"), 0, 10);
        grid.add(dureeMoisField, 1, 10);
        grid.add(new Label("Mode de Paiement:"), 0, 11);
        grid.add(modePaiementComboBox, 1, 11);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType(abonnement == null ? "Ajouter" : "Mettre à jour", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        Runnable updateButtonState = () -> {
            boolean isValid = serviceComboBox.getValue() != null &&
                    typeComboBox.getValue() != null &&
                    dateDebutField.getValue() != null &&
                    dateFinField.getValue() != null &&
                    !prixTotalField.getText().trim().isEmpty() &&
                    statutComboBox.getValue() != null &&
                    !nombreSeancesField.getText().trim().isEmpty() &&
                    (!autoRenouvellementCheckBox.isSelected() || dureeMoisField.getText().trim().isEmpty());
            saveButton.setDisable(!isValid);
        };

        serviceComboBox.valueProperty().addListener((obs, old, newVal) -> updateButtonState.run());
        typeComboBox.valueProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && !estGratuitCheckBox.isSelected()) {
                prixTotalField.setText(newVal.getPrix());
            }
            updateButtonState.run();
        });
        dateDebutField.valueProperty().addListener((obs, old, newVal) -> updateButtonState.run());
        dateFinField.valueProperty().addListener((obs, old, newVal) -> updateButtonState.run());
        prixTotalField.textProperty().addListener((obs, old, newVal) -> updateButtonState.run());
        statutComboBox.valueProperty().addListener((obs, old, newVal) -> updateButtonState.run());
        nombreSeancesField.textProperty().addListener((obs, old, newVal) -> updateButtonState.run());
        autoRenouvellementCheckBox.selectedProperty().addListener((obs, old, newVal) -> updateButtonState.run());
        estGratuitCheckBox.selectedProperty().addListener((obs, old, newVal) -> updateButtonState.run());
        dureeMoisField.textProperty().addListener((obs, old, newVal) -> updateButtonState.run());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    Abonnement newAbonnement = new Abonnement();
                    if (abonnement != null) {
                        newAbonnement.setId(abonnement.getId());
                    }
                    newAbonnement.setService(serviceComboBox.getValue());
                    newAbonnement.setTypeAbonnement(typeComboBox.getValue());
                    newAbonnement.setDateDebut(dateDebutField.getValue());
                    newAbonnement.setDateFin(dateFinField.getValue());
                    newAbonnement.setEstActif(estActifCheckBox.isSelected());
                    newAbonnement.setPrixTotal(Double.parseDouble(prixTotalField.getText().trim()));
                    newAbonnement.setEstGratuit(estGratuitCheckBox.isSelected());
                    newAbonnement.setStatut(statutComboBox.getValue());
                    newAbonnement.setNombreSeancesRestantes(Integer.parseInt(nombreSeancesField.getText().trim()));
                    newAbonnement.setAutoRenouvellement(autoRenouvellementCheckBox.isSelected());
                    String dureeMoisText = dureeMoisField.getText().trim();
                    newAbonnement.setDureeMois(dureeMoisText.isEmpty() ? null : Integer.parseInt(dureeMoisText));
                    newAbonnement.setModePaiement(modePaiementComboBox.getValue());

                    if (newAbonnement.getDateDebut().isAfter(newAbonnement.getDateFin())) {
                        showError("Erreur de validation", "La date de début doit être avant la date de fin.");
                        return null;
                    }
                    return newAbonnement;
                } catch (NumberFormatException e) {
                    showError("Erreur de validation", "Veuillez entrer des valeurs numériques valides pour le prix, les séances ou la durée.");
                    return null;
                }
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

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleBackToHome(javafx.event.ActionEvent actionEvent) {
        // Implement navigation back to home if needed
    }
}