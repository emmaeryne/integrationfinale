package edu.emmapi.controllers;

import edu.emmapi.entities.Commande;
import edu.emmapi.services.CommandeService;
import javafx.fxml.FXML;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;



public class CommandeController {

    @FXML private HBox buttonContainer; // Add this line to reference the button container
    @FXML
    private TableView<Commande> commandeTable;
    @FXML
    private TableColumn<Commande, Integer> idCommandeColumn;
    @FXML
    private TableColumn<Commande, LocalDate> dateCommandeColumn;
    @FXML
    private TableColumn<Commande, Integer> idUtilisateurColumn;
    @FXML
    private TableColumn<Commande, String> statusColumn;

    private final ObservableList<Commande> commandeData = FXCollections.observableArrayList();
    private final CommandeService commandeService = new CommandeService();




    @FXML
    public void initialize() {
        System.out.println("Initializing CommandeController...");
        //  System.out.println("idCommandeColumn: " + idCommandeColumn);
        System.out.println("dateCommandeColumn: " + dateCommandeColumn);
        System.out.println("idUtilisateurColumn: " + idUtilisateurColumn);
        System.out.println("statusColumn: " + statusColumn);

        // Lier les colonnes aux propriétés de la classe Commande
        idCommandeColumn.setCellValueFactory(new PropertyValueFactory<>("idCommande"));
        dateCommandeColumn.setCellValueFactory(new PropertyValueFactory<>("dateDeCommande"));
        idUtilisateurColumn.setCellValueFactory(new PropertyValueFactory<>("idUtilisateur"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Charger les données
        loadCommandes();
    }

    @FXML
    private void goBack(ActionEvent event) {
        loadScene(event, "/homepage.fxml");
        animateButtons(); // Call the animation method

    }
    private void animateButtons() {
        for (Node button : buttonContainer.getChildren()) {
            button.setOpacity(0); // Start with the button invisible
            TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), button);
            translateTransition.setFromY(-20); // Start position (above)
            translateTransition.setToY(0); // End position (original position)
            translateTransition.setOnFinished(event -> {
                FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), button);
                fadeTransition.setFromValue(0);
                fadeTransition.setToValue(1);
                fadeTransition.play();
            });
            translateTransition.play();
        }
    }
    private void loadCommandes() {
        commandeData.clear();
        List<Commande> commandes = commandeService.getAllCommandes(); // Utilisation de l'instance
        commandeData.addAll(commandes);
        commandeTable.setItems(commandeData);
    }

    @FXML
    private void supprimerCommande() {
        Commande selectedCommande = commandeTable.getSelectionModel().getSelectedItem();
        if (selectedCommande != null) {
            commandeService.supprimerCommande(selectedCommande.getIdCommande()); // Utilisation de l'instance
            showAlert("Succès", "Commande supprimée avec succès.");
            loadCommandes();
        } else {
            showAlertSupp("Erreur", "Veuillez sélectionner une commande.");
        }
    }



    @FXML
    private void refreshCommandes() {
        loadCommandes();
        showAlert("Info", "Liste des commandes rafraîchie avec succès !");
    }

    @FXML
    private void gotoAj(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutCommande.fxml"));
            Parent parent = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


////

    }



    @FXML
    public void gotomodif(ActionEvent event) {
        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierCommande.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur
            ModifierCommande controller = loader.getController();
            if (controller == null) {
                System.err.println("❌ Controller for ModifierCommande is NULL!");
                showAlert("Erreur", "Le contrôleur de la vue de modification est introuvable.");
                return; // Arrêter l'exécution si le contrôleur est null
            } else {
                System.out.println("✅ ModifierCommande Controller Loaded Successfully.");
            }

            // Créer une nouvelle scène et l'afficher
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 500));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la vue de modification : " + e.getMessage());
        }
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showAlertSupp(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


