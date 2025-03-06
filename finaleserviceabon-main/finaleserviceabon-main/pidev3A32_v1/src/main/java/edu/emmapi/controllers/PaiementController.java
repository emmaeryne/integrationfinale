package edu.emmapi.controllers;

import edu.emmapi.entities.Paiement;
import edu.emmapi.services.PaiementService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;



import java.io.IOException;
import java.util.List;


public class PaiementController {
    @FXML private Button AjPa;
    @FXML private Button modifP;
    @FXML private TableView<Paiement> paiementTable;
    @FXML private TableColumn<Paiement, Integer> idCommandeColumn;
    @FXML private TableColumn<Paiement, Integer> idUtilisateurColumn;
    @FXML private TableColumn<Paiement, Double> montantColumn;
    @FXML private TableColumn<Paiement, String> modeDePaiementColumn;
    @FXML private TableColumn<Paiement, String> dateDePaiementColumn;
    @FXML private TableColumn<Paiement, String> statusColumn;
    private final PaiementService paiementService = new PaiementService();

    private final ObservableList<Paiement> paiementData = FXCollections.observableArrayList();
    @FXML
    public void initialize() {
        // Lier les colonnes aux propriétés de la classe Paiement
        idCommandeColumn.setCellValueFactory(new PropertyValueFactory<>("idCommande"));
        //idUtilisateurColumn.setCellValueFactory(new PropertyValueFactory<>("idUtilisateur"));
        montantColumn.setCellValueFactory(new PropertyValueFactory<>("montant"));
        modeDePaiementColumn.setCellValueFactory(new PropertyValueFactory<>("modeDePaiement"));
        dateDePaiementColumn.setCellValueFactory(new PropertyValueFactory<>("dateDePaiement"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Charger les données
        loadPaiements();
    }

    private void loadPaiements() {
        paiementData.clear();
        List<Paiement> paiements = paiementService.getAllPaiements();
        paiementData.addAll(paiements);
        paiementTable.setItems(paiementData);
    }



    @FXML
    private void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homepage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 500));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void gotoPa(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajoutPaiement.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void gotomodif(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierPaiement.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void supprimerPaiement() {
        try {
            Paiement selectedPaiement = paiementTable.getSelectionModel().getSelectedItem();
            if (selectedPaiement == null) {
                showAlert("Erreur", "Veuillez sélectionner un paiement à supprimer.");
                return;
            }

            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirmation");
            confirmDialog.setHeaderText("Suppression de Paiement");
            confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer ce paiement ?");

            confirmDialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    paiementService.supprimerPaiement(selectedPaiement.getIdCommande());
                    loadPaiements();
                    showAlert("Succès", "Paiement supprimé avec succès !");
                }
            });
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de supprimer le paiement.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}


