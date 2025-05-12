
/*package edu.emmapi.controllers;

import edu.emmapi.entities.Reservation;
import edu.emmapi.services.ReservationService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReservationController {

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
    private boolean isAdmin = false; // Simule l'état admin (à remplacer par une authentification réelle)

    @FXML
    public void initialize() {
        service = new ReservationService();
        reservations = FXCollections.observableArrayList();
        reservationTable.setItems(reservations);
        reservationTable.setPlaceholder(new Label("Aucune réservation trouvée."));

        setupTableColumns();
        setupSortComboBox();
        loadReservations();

        searchField.textProperty().addListener((obs, oldValue, newValue) -> handleSearch());
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
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");

            {
                editButton.setStyle("-fx-background-color: #FF7043; -fx-text-fill: #FFFFFF; -fx-font-size: 12px;");
                deleteButton.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: #FFFFFF; -fx-font-size: 12px;");

                if (!isAdmin) {
                    editButton.setVisible(false);
                    editButton.setManaged(false);
                } else {
                    editButton.setOnAction(event -> {
                        Reservation reservation = getTableView().getItems().get(getIndex());
                        handleEditReservation(reservation);
                    });
                }

                deleteButton.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    handleDeleteReservation(reservation);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, isAdmin ? editButton : new Button(), deleteButton);
                    setGraphic(buttons);
                }
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
                service.addReservation(newReservation);
                loadReservations();
                showSuccess("Succès", "Réservation ajoutée avec succès.");
            } catch (Exception e) {
                showError("Erreur", "Erreur lors de l'ajout de la réservation: " + e.getMessage());
            }
        }
    }

    private void handleEditReservation(Reservation reservation) {
        if (isAdmin && reservation != null) {
            Reservation updatedReservation = showReservationDialog(reservation);
            if (updatedReservation != null) {
                try {
                    reservation.setStatut(updatedReservation.getStatut());
                    service.updateReservation(reservation);
                    loadReservations();
                    showSuccess("Succès", "Statut mis à jour avec succès.");
                } catch (Exception e) {
                    showError("Erreur", "Erreur lors de la mise à jour du statut: " + e.getMessage());
                }
            }
        } else if (!isAdmin) {
            showError("Erreur", "Seul un administrateur peut modifier les réservations.");
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
        dialog.setTitle(reservation == null ? "Réserver" : "Modifier Réservation");
        dialog.setHeaderText("Entrez les détails de la réservation");

        ObservableList<String> abonnementOptions = FXCollections.observableArrayList(
                "Abonnement Basic", "Abonnement Starter", "EMMALIVAR");
        ComboBox<String> typeAbonnementCombo = new ComboBox<>(abonnementOptions);
        typeAbonnementCombo.setPromptText("Choisir un type d'abonnement");
        typeAbonnementCombo.setStyle("-fx-pref-width: 200px;");

        DatePicker dateDebutPicker = new DatePicker();
        dateDebutPicker.setPromptText("jj/mm/aaaa --:--");
        dateDebutPicker.setStyle("-fx-pref-width: 200px;");

        DatePicker dateFinPicker = new DatePicker();
        dateFinPicker.setPromptText("jj/mm/aaaa --:--");
        dateFinPicker.setStyle("-fx-pref-width: 200px;");

        ComboBox<String> statutCombo = new ComboBox<>(FXCollections.observableArrayList("en attente", "en cours"));
        statutCombo.setPromptText("Statut");
        statutCombo.setStyle("-fx-pref-width: 200px;");

        if (reservation != null) {
            String abonnementName = mapTypeAbonnementIdToName(reservation.getTypeAbonnementId());
            typeAbonnementCombo.setValue(abonnementName);
            dateDebutPicker.setValue(reservation.getDateDebut() != null ? reservation.getDateDebut().toLocalDate() : null);
            dateFinPicker.setValue(reservation.getDateFin() != null ? reservation.getDateFin().toLocalDate() : null);
            statutCombo.setValue(reservation.getStatut());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Type d'abonnement:"), 0, 0);
        grid.add(typeAbonnementCombo, 1, 0);
        grid.add(new Label("Date de début:"), 0, 1);
        grid.add(dateDebutPicker, 1, 1);
        grid.add(new Label("Date de fin:"), 0, 2);
        grid.add(dateFinPicker, 1, 2);
        if (isAdmin) {
            grid.add(new Label("Statut:"), 0, 3);
            grid.add(statutCombo, 1, 3);
        }

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("SAVE", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String selectedAbonnement = typeAbonnementCombo.getValue();
                if (selectedAbonnement == null) {
                    showError("Erreur", "Veuillez choisir un type d'abonnement.");
                    return null;
                }
                Reservation newReservation = reservation == null ? new Reservation() : reservation;
                newReservation.setTypeAbonnementId(mapNameToTypeAbonnementId(selectedAbonnement));
                newReservation.setDateDebut(dateDebutPicker.getValue() != null ?
                        dateDebutPicker.getValue().atStartOfDay() : null);
                newReservation.setDateFin(dateFinPicker.getValue() != null ?
                        dateFinPicker.getValue().atStartOfDay() : null);
                if (isAdmin && statutCombo.getValue() != null) {
                    newReservation.setStatut(statutCombo.getValue());
                } else if (reservation == null) {
                    newReservation.setStatut("en attente");
                }
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
        return switch (id) {
            case 50 -> "Abonnement Basic";
            case 105 -> "Abonnement Starter";
            case 78 -> "EMMALIVAR";
            default -> "Inconnu";
        };
    }

    private int mapNameToTypeAbonnementId(String name) {
        return switch (name) {
            case "Abonnement Basic" -> 50;
            case "Abonnement Starter" -> 105;
            case "EMMALIVAR" -> 78;
            default -> 0;
        };
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
}*/
package edu.emmapi.controllers;

import edu.emmapi.entities.Reservation;
import edu.emmapi.services.ReservationService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReservationController {

    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, String> typeAbonnementIdColumn;
    @FXML private TableColumn<Reservation, String> dateReservationColumn;
    @FXML private TableColumn<Reservation, String> dateDebutColumn;
    @FXML private TableColumn<Reservation, String> dateFinColumn;
    @FXML private TableColumn<Reservation, String> statutColumn;
    @FXML private TableColumn<Reservation, Void> actionsColumn;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private Label reservationCountLabel; // Ajout du Label pour le nombre total

    private ReservationService service;
    private ObservableList<Reservation> reservations;
    private boolean isAdmin = true; // Interface admin uniquement

    @FXML
    public void initialize() {
        service = new ReservationService();
        reservations = FXCollections.observableArrayList();
        reservationTable.setItems(reservations);
        reservationTable.setPlaceholder(new Label("Aucune réservation trouvée."));

        setupTableColumns();
        setupSortComboBox();
        loadReservations();

        searchField.textProperty().addListener((obs, oldValue, newValue) -> handleSearch());
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
            private final Button showButton = new Button("Show");
            private final Button editButton = new Button("Modifier Statut");
            private final Button deleteButton = new Button("Supprimer");

            {
                showButton.setStyle("-fx-background-color: #66C2D9; -fx-text-fill: #FFFFFF; -fx-font-size: 12px;");
                editButton.setStyle("-fx-background-color: #FF7043; -fx-text-fill: #FFFFFF; -fx-font-size: 12px;");
                deleteButton.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: #FFFFFF; -fx-font-size: 12px;");

                showButton.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    handleShowReservation(reservation);
                });

                editButton.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    handleEditReservation(reservation);
                });

                deleteButton.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    handleDeleteReservation(reservation);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(5, showButton, editButton, deleteButton));
                }
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

            // Mise à jour du nombre total de réservations
            reservationCountLabel.setText("Nombre total de réservations : " + loadedReservations.size());

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

    private void handleShowReservation(Reservation reservation) {
        if (reservation != null) {
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Détails de la Réservation");
            dialog.setHeaderText("Réservation ID: " + reservation.getId());

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setStyle("-fx-padding: 10;");

            // Ajout des labels et valeurs
            grid.add(new Label("Abonnement:"), 0, 0);
            grid.add(new Label(mapTypeAbonnementIdToName(reservation.getTypeAbonnementId())), 1, 0);

            grid.add(new Label("Date de création:"), 0, 1);
            grid.add(new Label(reservation.getDateReservation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))), 1, 1);

            grid.add(new Label("Date de début:"), 0, 2);
            grid.add(new Label(reservation.getDateDebut() != null ?
                    reservation.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-"), 1, 2);

            grid.add(new Label("Date de fin:"), 0, 3);
            grid.add(new Label(reservation.getDateFin() != null ?
                    reservation.getDateFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-"), 1, 3);

            grid.add(new Label("Statut:"), 0, 4);
            grid.add(new Label(reservation.getStatut()), 1, 4);

            dialog.getDialogPane().setContent(grid);

            // Bouton pour fermer
            ButtonType closeButtonType = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().add(closeButtonType);

            // Appliquer le style
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
            dialog.getDialogPane().getStyleClass().add("dialog-pane");

            dialog.showAndWait();
        }
    }

    private void handleEditReservation(Reservation reservation) {
        if (isAdmin && reservation != null) {
            // Vérifier si la réservation est expirée
            LocalDateTime currentDate = LocalDateTime.now();
            if (reservation.getDateFin() != null && reservation.getDateFin().isBefore(currentDate)) {
                service.deleteReservation(reservation.getId());
                loadReservations();
                showInfo("Information", "La réservation a été supprimée car elle était expirée.");
                return;
            }

            Reservation updatedReservation = showEditStatusDialog(reservation);
            if (updatedReservation != null) {
                try {
                    reservation.setStatut(updatedReservation.getStatut());
                    service.updateReservation(reservation);
                    loadReservations();
                    showSuccess("Succès", "Statut mis à jour avec succès.");
                } catch (Exception e) {
                    showError("Erreur", "Erreur lors de la mise à jour du statut: " + e.getMessage());
                }
            }
        } else {
            showError("Erreur", "Seul un administrateur peut modifier les réservations.");
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

    private Reservation showEditStatusDialog(Reservation reservation) {
        Dialog<Reservation> dialog = new Dialog<>();
        dialog.setTitle("Modifier Réservation");
        dialog.setHeaderText("Modifier le statut de la réservation");

        ComboBox<String> statutCombo = new ComboBox<>(FXCollections.observableArrayList("en attente", "en cours"));
        statutCombo.setPromptText("Statut");
        statutCombo.setStyle("-fx-pref-width: 200px;");

        if (reservation != null) {
            statutCombo.setValue(reservation.getStatut());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Statut:"), 0, 0);
        grid.add(statutCombo, 1, 0);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("SAVE", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (statutCombo.getValue() == null) {
                    showError("Erreur", "Veuillez choisir un statut.");
                    return null;
                }
                Reservation updatedReservation = reservation;
                updatedReservation.setStatut(statutCombo.getValue());
                return updatedReservation;
            }
            return null;
        });

        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("dialog-pane");
        dialog.getDialogPane().lookupButton(saveButtonType).getStyleClass().add("save-button");

        return dialog.showAndWait().orElse(null);
    }

    private String mapTypeAbonnementIdToName(int id) {
        return switch (id) {
            case 50 -> "Abonnement Basic";
            case 105 -> "Abonnement Starter";
            case 78 -> "EMMALIVAR";
            default -> "Inconnu";
        };
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