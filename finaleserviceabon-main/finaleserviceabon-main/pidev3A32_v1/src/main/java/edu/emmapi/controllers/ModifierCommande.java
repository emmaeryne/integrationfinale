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

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ModifierCommande {
    //    @FXML private ListView<String> produitListView;
    @FXML private ListView<String> commandeList;
    @FXML private TextField commandeUtilisateur;
    @FXML private DatePicker commandeDate;
    @FXML private ComboBox<String> commandeStatus;

    private final ProduitService produitService = new ProduitService();
    private final CommandeService commandeService = new CommandeService();

    @FXML
    public void initialize() {
        // loadProduits();
        loadCommandes();


        // Add listener to populate fields when selecting a commande
        commandeList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateCommandeFields(newSelection);
            }
        });
    }

    private void populateCommandeFields(String selectedCommande) {
        try {
            // Extracting the ID from the string "Commande #12 - Validated"
            int idCommande = Integer.parseInt(selectedCommande.split("#")[1].split(" - ")[0].trim());

            // Fetch the full commande details
            Commande commande = commandeService.getCommandeById(idCommande);
            if (commande != null) {
                commandeUtilisateur.setText(String.valueOf(commande.getIdUtilisateur())); // Set user ID
                commandeDate.setValue(commande.getDateDeCommande()); // Set date
                commandeStatus.setValue(commande.getStatus()); // Set status
            }
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les détails de la commande.");
        }
    }


    /*
            private void loadProduits() {
                produitListView.getItems().clear();
                List<Produit> produits = produitService.getAllProduits();
                for (Produit produit : produits) {
                    produitListView.getItems().add(produit.getNomProduit() + " (ID: " + produit.getIdProduit() + ")");
                }
            }
    */
    private void loadCommandes() {
        commandeList.getItems().clear();
        List<Commande> commandes = commandeService.getAllCommandes();
        for (Commande commande : commandes) {
            commandeList.getItems().add("Commande #" + commande.getIdCommande() + " - " + commande.getStatus());
        }
    }

    @FXML
    private void modifierCommande() {
        String selectedCommande = commandeList.getSelectionModel().getSelectedItem();
        if (selectedCommande == null) {
            showAlert("Erreur", "Veuillez sélectionner une commande à modifier.");
            return;
        }

        int idCommande = Integer.parseInt(selectedCommande.split("#")[1].split(" - ")[0].trim());

        String newStatus = commandeStatus.getValue();


        Commande commande = commandeService.getCommandeById(idCommande);
        if (commande != null) {
            commande.setStatus("Pending");
            commandeService.modifierCommande(commande);
            showAlert("Succès", "Commande mise à jour avec succès.");
            loadCommandes();  // Refresh the list
        } else {
            showAlert("Erreur", "Commande non trouvée.");
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/homepage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 500));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }  }
}
