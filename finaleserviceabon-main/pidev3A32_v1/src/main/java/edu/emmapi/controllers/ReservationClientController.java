package edu.emmapi.controllers;

import edu.emmapi.entities.Reservation;
import edu.emmapi.entities.TypeAbonnement;
import edu.emmapi.services.ReservationService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationClientController {

    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, String> typeAbonnementIdColumn;
    @FXML private TableColumn<Reservation, String> dateReservationColumn;
    @FXML private TableColumn<Reservation, String> dateDebutColumn;
    @FXML private TableColumn<Reservation, String> dateFinColumn;
    @FXML private TableColumn<Reservation, String> statutColumn;
    @FXML private TableColumn<Reservation, Void> actionsColumn;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;

    private ReservationService service;
    private ObservableList<Reservation> reservations;
    private boolean isAdmin = false; // Interface client uniquement
    private Map<Integer, TypeAbonnement> abonnementIdToAbonnementMap; // Mapping ID -> TypeAbonnement

    @FXML
    public void initialize() {
        service = new ReservationService();
        reservations = FXCollections.observableArrayList();
        reservationTable.setItems(reservations);
        reservationTable.setPlaceholder(new Label("Aucune réservation trouvée."));

        // Initialisation des mappings pour les abonnements
        initializeAbonnementMappings();

        setupTableColumns();
        setupSortComboBox();
        loadReservations();

        searchField.textProperty().addListener((obs, oldValue, newValue) -> handleSearch());
    }

    private void initializeAbonnementMappings() {
        abonnementIdToAbonnementMap = new HashMap<>();
        List<TypeAbonnement> abonnements = service.getAllTypeAbonnements();
        for (TypeAbonnement abonnement : abonnements) {
            abonnementIdToAbonnementMap.put(Math.toIntExact(abonnement.getId()), abonnement);
        }
    }

    private void setupTableColumns() {
        typeAbonnementIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                mapTypeAbonnementIdToName(cellData.getValue().getTypeAbonnementId())));
        dateReservationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDateReservation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        dateDebutColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDateDebut() != null ?
                        cellData.getValue().getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-"));
        dateFinColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDateFin() != null ?
                        cellData.getValue().getDateFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-"));
        statutColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatut()));

        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");

            {
                deleteButton.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: #FFFFFF; -fx-font-size: 12px;");
                deleteButton.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    handleDeleteReservation(reservation);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(5, deleteButton));
            }
        });
    }

    private void setupSortComboBox() {
        sortComboBox.setItems(FXCollections.observableArrayList("date_asc", "date_desc"));
        sortComboBox.setValue("date_asc");
        sortComboBox.setOnAction(event -> loadReservations());
    }

    private void loadReservations() {
        try {
            service.deleteExpiredReservations();
            String sort = sortComboBox.getValue();
            String search = searchField.getText().trim();
            List<Reservation> loadedReservations = search.isEmpty() ?
                    service.getAllReservations(sort) :
                    service.searchReservations(search);
            reservations.clear();
            reservations.addAll(loadedReservations);
            if (loadedReservations.isEmpty()) {
                showInfo("Information", "Aucune réservation trouvée.");
            }
        } catch (Exception e) {
            showError("Erreur", "Erreur lors du chargement des réservations: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        loadReservations();
    }

    @FXML
    private void handleAddReservation() {
        Reservation newReservation = showReservationDialog(null);
        if (newReservation != null) {
            try {
                newReservation.setStatut("en attente"); // Statut par défaut pour le client
                newReservation.setDateReservation(LocalDateTime.now()); // Date de création
                service.addReservation(newReservation);
                loadReservations();
                showSuccess("Succès", "Réservation ajoutée avec succès.");
            } catch (Exception e) {
                showError("Erreur", "Erreur lors de l'ajout de la réservation: " + e.getMessage());
            }
        }
    }

    private void handleDeleteReservation(Reservation reservation) {
        if (reservation != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Confirmer la suppression ?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        service.deleteReservation(reservation.getId());
                        loadReservations();
                        showSuccess("Succès", "Réservation supprimée avec succès.");
                    } catch (Exception e) {
                        showError("Erreur", "Erreur lors de la suppression de la réservation: " + e.getMessage());
                    }
                }
            });
        } else {
            showError("Erreur", "Veuillez sélectionner une réservation à supprimer.");
        }
    }

    private Reservation showReservationDialog(Reservation reservation) {
        Dialog<Reservation> dialog = new Dialog<>();
        dialog.setTitle("Réserver");
        dialog.setHeaderText("Entrez les détails de la réservation");

        // Récupérer dynamiquement les types d'abonnements depuis la base de données
        List<TypeAbonnement> abonnements = service.getAllTypeAbonnements();
        ObservableList<String> abonnementOptions = FXCollections.observableArrayList(
                abonnements.stream().map(TypeAbonnement::getNom).toList());
        ComboBox<String> typeAbonnementCombo = new ComboBox<>(abonnementOptions);
        typeAbonnementCombo.setPromptText("Choisir un type d'abonnement");
        typeAbonnementCombo.setStyle("-fx-pref-width: 200px;");

        DatePicker dateDebutPicker = new DatePicker();
        dateDebutPicker.setPromptText("jj/mm/aaaa");
        dateDebutPicker.setStyle("-fx-pref-width: 200px;");

        DatePicker dateFinPicker = new DatePicker();
        dateFinPicker.setPromptText("jj/mm/aaaa");
        dateFinPicker.setDisable(true); // Désactivé car calculé automatiquement
        dateFinPicker.setStyle("-fx-pref-width: 200px;");

        // Ajouter un listener pour calculer dateFin en fonction de dateDebut et de la durée
        typeAbonnementCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (dateDebutPicker.getValue() != null && newVal != null) {
                TypeAbonnement abonnement = abonnements.stream()
                        .filter(a -> a.getNom().equals(newVal))
                        .findFirst()
                        .orElse(null);
                if (abonnement != null) {
                    LocalDate dateDebut = dateDebutPicker.getValue();
                    LocalDate dateFin = dateDebut.plusMonths(abonnement.getDureeEnMois());
                    dateFinPicker.setValue(dateFin);
                }
            }
        });

        dateDebutPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && typeAbonnementCombo.getValue() != null) {
                TypeAbonnement abonnement = abonnements.stream()
                        .filter(a -> a.getNom().equals(typeAbonnementCombo.getValue()))
                        .findFirst()
                        .orElse(null);
                if (abonnement != null) {
                    LocalDate dateFin = newVal.plusMonths(abonnement.getDureeEnMois());
                    dateFinPicker.setValue(dateFin);
                }
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Type d'abonnement:"), 0, 0);
        grid.add(typeAbonnementCombo, 1, 0);
        grid.add(new Label("Date de début:"), 0, 1);
        grid.add(dateDebutPicker, 1, 1);
        grid.add(new Label("Date de fin:"), 0, 2);
        grid.add(dateFinPicker, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("SAVE", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String selectedAbonnement = typeAbonnementCombo.getValue();
                if (selectedAbonnement == null || dateDebutPicker.getValue() == null) {
                    showError("Erreur", "Veuillez choisir un type d'abonnement et une date de début.");
                    return null;
                }
                TypeAbonnement abonnement = abonnements.stream()
                        .filter(a -> a.getNom().equals(selectedAbonnement))
                        .findFirst()
                        .orElse(null);
                if (abonnement == null) {
                    showError("Erreur", "Type d'abonnement invalide.");
                    return null;
                }
                Reservation newReservation = new Reservation();
                newReservation.setTypeAbonnementId(Math.toIntExact(abonnement.getId()));
                newReservation.setDateDebut(dateDebutPicker.getValue().atStartOfDay());
                newReservation.setDateFin(dateFinPicker.getValue() != null ? dateFinPicker.getValue().atStartOfDay() : null);
                newReservation.setStatut("en attente");
                return newReservation;
            }
            return null;
        });

        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("dialog-pane");
        dialog.getDialogPane().lookupButton(saveButtonType).getStyleClass().add("save-button");

        return dialog.showAndWait().orElse(null);
    }

    private String mapTypeAbonnementIdToName(int id) {
        TypeAbonnement abonnement = abonnementIdToAbonnementMap.get(id);
        return abonnement != null ? abonnement.getNom() : "Inconnu";
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

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setAdminMode(boolean adminMode) {
        this.isAdmin = adminMode;
        loadReservations();
    }
}