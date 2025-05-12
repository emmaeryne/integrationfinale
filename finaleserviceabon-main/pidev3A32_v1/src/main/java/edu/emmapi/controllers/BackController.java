package edu.emmapi.controllers;

import edu.emmapi.entities.Paiement;
import edu.emmapi.entities.Commande;
import edu.emmapi.services.PaiementService;
import edu.emmapi.services.CommandeService;
import edu.emmapi.tools.MyConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class BackController implements Initializable {
    @FXML private TableView<Paiement> paiementTableView;
    @FXML private TableColumn<Paiement, Integer> idColumn;
    @FXML private TableColumn<Paiement, Integer> utilisateurColumn;
    @FXML private TableColumn<Paiement, Double> montantColumn;
    @FXML private TableColumn<Paiement, String> modeColumn;
    @FXML private TableColumn<Paiement, String> dateColumn;
    @FXML private TableColumn<Paiement, String> statusColumn;

    @FXML private TableView<Commande> commandeTableView;
    @FXML private TableColumn<Commande, Integer> idCommandeColumn;
    @FXML private TableColumn<Commande, Integer> utilisateurCommandeColumn;
    @FXML private TableColumn<Commande, String> dateCommandeColumn;
    @FXML private TableColumn<Commande, String> statusCommandeColumn;

    private ObservableList<Paiement> allPaiements;
    private ObservableList<Commande> allCommandes;
    private final PaiementService paiementService = new PaiementService();
    private final CommandeService commandeService = new CommandeService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configurer les colonnes de la TableView des paiements
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idPaiement"));
        utilisateurColumn.setCellValueFactory(new PropertyValueFactory<>("idUtilisateur"));
        montantColumn.setCellValueFactory(new PropertyValueFactory<>("montant"));
        modeColumn.setCellValueFactory(new PropertyValueFactory<>("modeDePaiement"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateDePaiement"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Configurer les colonnes de la TableView des commandes
        idCommandeColumn.setCellValueFactory(new PropertyValueFactory<>("idCommande"));
        utilisateurCommandeColumn.setCellValueFactory(new PropertyValueFactory<>("idUtilisateur"));
        dateCommandeColumn.setCellValueFactory(new PropertyValueFactory<>("dateCommande"));
        statusCommandeColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Charger les paiements et les commandes
        loadPaiements();
        loadCommandes();
    }

    private void loadPaiements() {
        allPaiements = FXCollections.observableArrayList(paiementService.getAllPaiements());
        paiementTableView.setItems(allPaiements);
    }

    private void loadCommandes() {
        allCommandes = FXCollections.observableArrayList(commandeService.getAllCommandes());
        commandeTableView.setItems(allCommandes);
    }

    @FXML
    public void refreshTable(ActionEvent actionEvent) {
        loadPaiements(); // Recharger les paiements
        loadCommandes(); // Recharger les commandes
    }

    private void updatePaiementStatus(Paiement paiement, String newStatus) {
        String query = "UPDATE paiement SET status = ? WHERE idUtilisateur = ?";
        try (Connection connection = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, paiement.getIdUtilisateur());
            pstmt.executeUpdate();
            loadPaiements(); // Rafraîchir la table après la mise à jour
            showAlert("Succès", "Le paiement a été mis à jour avec succès!");
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de mettre à jour le paiement: " + e.getMessage());
        }
    }

    private void updateCommandeStatus(Commande commande, String newStatus) {
        String query = "UPDATE commande SET status = ? WHERE idCommande = ?";
        try (Connection connection = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, commande.getIdCommande());
            pstmt.executeUpdate();
            loadCommandes(); // Rafraîchir la table après la mise à jour
            showAlert("Succès", "La commande a été mise à jour avec succès!");
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de mettre à jour la commande: " + e.getMessage());
        }
    }

    @FXML
    public void validerPaiement(ActionEvent actionEvent) {
        Paiement selectedPaiement = paiementTableView.getSelectionModel().getSelectedItem();
        if (selectedPaiement != null) {
            updatePaiementStatus(selectedPaiement, "Completed");
        } else {
            showAlert("Erreur", "Veuillez sélectionner un paiement à valider.");
        }
    }

    @FXML
    public void rejeterPaiement(ActionEvent actionEvent) {
        Paiement selectedPaiement = paiementTableView.getSelectionModel().getSelectedItem();
        if (selectedPaiement != null) {
            updatePaiementStatus(selectedPaiement, "Failed");
        } else {
            showAlert("Erreur", "Veuillez sélectionner un paiement à rejeter.");
        }
    }

    @FXML
    public void validerCommande(ActionEvent actionEvent) {
        Commande selectedCommande = commandeTableView.getSelectionModel().getSelectedItem();
        if (selectedCommande != null) {
            updateCommandeStatus(selectedCommande, "Completed");
        } else {
            showAlert("Erreur", "Veuillez sélectionner une commande à valider.");
        }
    }

    @FXML
    public void rejeterCommande(ActionEvent actionEvent) {
        Commande selectedCommande = commandeTableView.getSelectionModel().getSelectedItem();
        if (selectedCommande != null) {
            updateCommandeStatus(selectedCommande, "Failed");
        } else {
            showAlert("Erreur", "Veuillez sélectionner une commande à rejeter.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void goToBackInterface(ActionEvent actionEvent) {
        loadScene(actionEvent, "/pagehome.fxml");
    }

    @FXML

    public void goToAdminC(ActionEvent actionEvent) {
        loadScene(actionEvent, "/CommandeBack.fxml");
    }

    public void gotoPai(ActionEvent actionEvent) {
        loadScene(actionEvent, "/BackPaiement.fxml");
    }

    private void loadScene(ActionEvent event, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 800));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la scène : " + e.getMessage());
        }
    }
}