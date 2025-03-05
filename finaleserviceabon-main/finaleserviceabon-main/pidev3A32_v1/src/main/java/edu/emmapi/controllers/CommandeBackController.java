package edu.emmapi.controllers;

import edu.emmapi.services.CommandeService;
import edu.emmapi.entities.Commande;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import edu.emmapi.services.ExportPDFService;

import edu.emmapi.tools.MyConnection;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CommandeBackController implements Initializable {

    @FXML private TableView<Commande> commandeTable;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterStatus;
    @FXML private DatePicker filterDate;
    @FXML private Pagination pagination;
    @FXML private LineChart<String, Number> commandeChart;

    private final CommandeService commandeService = new CommandeService();
    private final ExportPDFService exportPDFService = new ExportPDFService();
    private ObservableList<Commande> commandes;
    private FilteredList<Commande> filteredCommandes;

    @FXML private TableColumn<Commande, Integer> idCommandeColumn;
    @FXML private TableColumn<Commande, String> dateDeCommandeColumn;
    @FXML private TableColumn<Commande, String> statusColumn;
    @FXML private TableColumn<Commande, Integer> idUtilisateurColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeSearch(); // Initialiser la recherche
        configureTableColumns(); // Configurer les colonnes de la table
        loadCommandes(); // Charger les commandes
        setupFilters(); // Configurer les filtres
        setupPagination(); // Configurer la pagination
        setupChart(); // Configurer le graphique

        commandeTable.setRowFactory(tv -> new CommandeRow()); // Personnaliser les lignes
    }

    private void configureTableColumns() {
        idCommandeColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdCommande()).asObject());
        dateDeCommandeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateDeCommande().toString()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        idUtilisateurColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdUtilisateur()).asObject());
    }

    private void loadScene(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 500));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la vue demandée.");
        }
    }

    @FXML
    public void retour(ActionEvent actionEvent) {
        loadScene(actionEvent, "/homepage.fxml");
    }

    // Classe interne pour personnaliser les lignes
    private class CommandeRow extends TableRow<Commande> {
        @Override
        protected void updateItem(Commande item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setStyle(""); // Réinitialiser le style si la ligne est vide
            } else {
                switch (item.getStatus()) {
                    case "Validated":
                        setStyle("-fx-background-color: lightgreen;"); // Couleur pour Validated
                        break;
                    case "Completed":
                        setStyle("-fx-background-color: lightblue;"); // Couleur pour Completed
                        break;
                    case "Pending":
                        setStyle("-fx-background-color: lightyellow;"); // Couleur pour Pending
                        break;
                    default:
                        setStyle(""); // Réinitialiser le style pour les autres statuts
                        break;
                }
            }
        }
    }

    @FXML
    private void initializeSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredCommandes.setPredicate(commande -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Afficher toutes les commandes si le champ de recherche est vide
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // Filtre par ID, date, statut ou ID utilisateur
                return String.valueOf(commande.getIdCommande()).contains(lowerCaseFilter)
                        || commande.getDateDeCommande().toString().contains(lowerCaseFilter)
                        || commande.getStatus().toLowerCase().contains(lowerCaseFilter)
                        || String.valueOf(commande.getIdUtilisateur()).contains(lowerCaseFilter);
            });
        });
    }

    private void loadCommandes() {
        commandes = FXCollections.observableArrayList(commandeService.getAllCommandes());
        filteredCommandes = new FilteredList<>(commandes);
        commandeTable.setItems(filteredCommandes);
    }

    private void setupFilters() {
        // Ajouter les statuts possibles au ComboBox
        filterStatus.getItems().addAll("Canceled", "Validated");
        filterStatus.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        filterDate.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void applyFilters() {
        filteredCommandes.setPredicate(commande -> {
            boolean matchesStatus = filterStatus.getValue() == null || commande.getStatus().equals(filterStatus.getValue());
            boolean matchesDate = filterDate.getValue() == null || commande.getDateDeCommande().isEqual(filterDate.getValue());
            return matchesStatus && matchesDate;
        });
    }

    private void setupPagination() {
        pagination.setPageCount((int) Math.ceil(filteredCommandes.size() / 10.0));
        pagination.currentPageIndexProperty().addListener((obs, oldVal, newVal) -> {
            int fromIndex = newVal.intValue() * 10;
            int toIndex = Math.min(fromIndex + 10, filteredCommandes.size());
            commandeTable.setItems(FXCollections.observableArrayList(filteredCommandes.subList(fromIndex, toIndex)));
        });
    }

    private void setupChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Commandes par jour");

        // Ajouter les données au graphique
        commandes.stream()
                .collect(Collectors.groupingBy(Commande::getDateDeCommande, Collectors.counting()))
                .forEach((date, count) -> {
                    XYChart.Data<String, Number> data = new XYChart.Data<>(date.toString(), count);
                    series.getData().add(data);
                });

        commandeChart.getData().add(series);

        // Animation pour faire apparaître les données
        for (XYChart.Data<String, Number> data : series.getData()) {
            // Créer un FadeTransition pour chaque point de données
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), data.getNode());
            fadeTransition.setFromValue(0); // Commencer à 0 (invisible)
            fadeTransition.setToValue(1); // Finir à 1 (visible)
            fadeTransition.setDelay(Duration.seconds(0.5)); // Délai avant le début de l'animation
            fadeTransition.play();

            // Optionnel : ajouter une translation pour un effet de mouvement
            TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1), data.getNode());
            translateTransition.setFromY(20); // Commencer 20 pixels plus bas
            translateTransition.setToY(0); // Finir à la position d'origine
            translateTransition.setDelay(Duration.seconds(0.5)); // Délai avant le début de l'animation
            translateTransition.play();
        }
    }

    @FXML
    private void onSearch() {
        String searchText = searchField.getText().toLowerCase();
        filteredCommandes.setPredicate(commande -> {
            boolean matchesStatus = commande.getStatus().toLowerCase().contains(searchText);
            boolean matchesDate = commande.getDateDeCommande().toString().contains(searchText);
            boolean matchesUserId = String.valueOf(commande.getIdUtilisateur()).contains(searchText);
            return matchesStatus || matchesDate || matchesUserId;
        });
    }

    @FXML
    private void refreshCommandes() {
        loadCommandes();
    }

    @FXML
    private void exportToPDF() {
        File pdfFile = exportPDFService.exportToPDF(commandeTable.getItems());

        if (pdfFile != null && pdfFile.exists()) {
            try {
                Desktop.getDesktop().open(pdfFile); // Ouvrir le fichier PDF
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible d'ouvrir le fichier PDF.");
            }
        } else {
            showAlert("Erreur", "Le fichier PDF n'a pas pu être généré.");
        }
    }

    private void updateCommandes(List<Commande> newCommandes) {
        commandes.setAll(newCommandes);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthodes pour valider et rejeter les commandes
    private void updateCommandeStatus(Commande commande, String newStatus) {
        try {
            String query = "UPDATE commande SET status = ? WHERE idUtilisateur = ?";
            Connection connection = MyConnection.getInstance().getCnx();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, commande.getIdUtilisateur()); // Utiliser idUtilisateur
            pstmt.executeUpdate();
            loadCommandes(); // Recharger les commandes après la mise à jour
            showAlert("Succès", "La commande a été mise à jour avec succès!");
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de mettre à jour la commande: " + e.getMessage());
        }
    }

    @FXML
    public void validerCommande(ActionEvent actionEvent) {
        Commande selectedCommande = commandeTable.getSelectionModel().getSelectedItem();
        if (selectedCommande != null) {
            updateCommandeStatus(selectedCommande, "Validated");
        } else {
            showAlert("Erreur", "Veuillez sélectionner une commande à valider.");
        }
    }

    @FXML
    public void rejeterCommande(ActionEvent actionEvent) {
        Commande selectedCommande = commandeTable.getSelectionModel().getSelectedItem();
        if (selectedCommande != null) {
            updateCommandeStatus(selectedCommande, "Canceled");
        } else {
            showAlert("Erreur", "Veuillez sélectionner une commande à rejeter.");
        }
    }
}