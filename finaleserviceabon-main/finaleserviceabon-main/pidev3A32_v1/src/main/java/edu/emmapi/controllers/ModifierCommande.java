package edu.emmapi.controllers;

import edu.emmapi.entities.Commande;
import edu.emmapi.services.CommandeService;
import edu.emmapi.services.ProduitService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ModifierCommande {
    @FXML private TableView<Commande> commandeTable; // Remplacez ListView par TableView
    @FXML private TableColumn<Commande, Integer> colId;
    @FXML private TableColumn<Commande, Integer> colUtilisateur;
    @FXML private TableColumn<Commande, LocalDate> colDate;
    @FXML private TableColumn<Commande, String> colStatut;

    @FXML private TextField commandeUtilisateur;
    @FXML private DatePicker commandeDate;
    @FXML private ComboBox<String> commandeStatus;

    private final ProduitService produitService = new ProduitService();
    private final CommandeService commandeService = new CommandeService();

    @FXML
    public void initialize() {
        // Configurer les colonnes
        colId.setCellValueFactory(cellData -> cellData.getValue().idCommandeProperty().asObject());
        colUtilisateur.setCellValueFactory(cellData -> cellData.getValue().idUtilisateurProperty().asObject());
        colDate.setCellValueFactory(cellData -> cellData.getValue().dateDeCommandeProperty());
        colStatut.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        // Charger les commandes
        loadCommandes();

        // Ajouter un écouteur pour remplir les champs lors de la sélection d'une commande
        commandeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateCommandeFields(newSelection);
            }
        });
    }

    private void populateCommandeFields(Commande selectedCommande) {
        commandeUtilisateur.setText(String.valueOf(selectedCommande.getIdUtilisateur()));
        commandeDate.setValue(selectedCommande.getDateDeCommande());
        commandeStatus.setValue(selectedCommande.getStatus());
    }

    private void loadCommandes() {
        List<Commande> commandes = commandeService.getAllCommandes();
        ObservableList<Commande> commandeObservableList = FXCollections.observableArrayList(commandes);
        commandeTable.setItems(commandeObservableList);
    }

    @FXML
    private void modifierCommande() {
        Commande selectedCommande = commandeTable.getSelectionModel().getSelectedItem();
        if (selectedCommande == null) {
            showAlert("Erreur", "Veuillez sélectionner une commande à modifier.");
            return;
        }

        // Mettre à jour uniquement la date de la commande
        LocalDate nouvelleDate = commandeDate.getValue();
        if (nouvelleDate != null) {
            selectedCommande.setDateDeCommande(nouvelleDate);
            commandeService.modifierCommande(selectedCommande); // Sauvegarder les modifications
            showAlert("Succès", "Date de la commande mise à jour avec succès.");
            loadCommandes(); // Rafraîchir la table
        } else {
            showAlert("Erreur", "Veuillez sélectionner une date valide.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void goBack(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommandeView .fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 500));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}