package edu.emmapi.controllers;

import edu.emmapi.entities.Commande;
import edu.emmapi.entities.Paiement;
import edu.emmapi.services.CommandeService;
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
import java.time.LocalDate;
import java.util.List;

public class ModifierPaiement {
    @FXML
    private ListView<String> commandeListView;
    @FXML private TextField paiementCommandeId;
    @FXML private TextField paiementUtilisateurId;
    @FXML private TextField paiementMontant;
    @FXML private ComboBox<String> paiementMode;
    @FXML private DatePicker paiementDate;
    @FXML private ComboBox<String> paiementStatus;
    @FXML
    private TableView<Paiement> paiementTable;
    @FXML
    private TableColumn<Paiement, Integer> idCommandeColumn;
    @FXML
    private TableColumn<Paiement, Integer> idUtilisateurColumn;
    @FXML
    private TableColumn<Paiement, Double> montantColumn;
    @FXML
    private TableColumn<Paiement, String> modeDePaiementColumn;
    @FXML
    private TableColumn<Paiement, LocalDate> dateDePaiementColumn;
    @FXML
    private TableColumn<Paiement, String> statusColumn;

    private final ObservableList<Paiement> paiementData = FXCollections.observableArrayList();
    private final PaiementService paiementService = new PaiementService();
    private final CommandeService commandeService = new CommandeService();

    @FXML
    public void initialize() {
        // Initialisation des colonnes du TableView
        idCommandeColumn.setCellValueFactory(new PropertyValueFactory<>("idCommande"));
        idUtilisateurColumn.setCellValueFactory(new PropertyValueFactory<>("idUtilisateur"));
        montantColumn.setCellValueFactory(new PropertyValueFactory<>("montant"));
        modeDePaiementColumn.setCellValueFactory(new PropertyValueFactory<>("modeDePaiement"));
        dateDePaiementColumn.setCellValueFactory(new PropertyValueFactory<>("dateDePaiement"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Charger les paiements dans le TableView
        loadPaiements();

        // Initialisation des ComboBox
        paiementMode.getItems().addAll("Cash", "Carte bancaire", "Virement");
        paiementStatus.getItems().addAll("Pending"); // Seulement "Pending" est disponible

        // Ajouter un écouteur pour remplir les champs lors de la sélection d'une ligne
        paiementTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populatePaiementFields(newSelection);
            }
        });
    }

    /**
     * 🏷 Extract payment details from ListView selection and populate the fields.
     */
    private void populatePaiementFields(Paiement selectedPaiement) {
        paiementCommandeId.setText(String.valueOf(selectedPaiement.getIdCommande()));
        paiementUtilisateurId.setText(String.valueOf(selectedPaiement.getIdUtilisateur()));
        paiementMontant.setText(String.valueOf(selectedPaiement.getMontant()));
        paiementMode.setValue(selectedPaiement.getModeDePaiement());
        paiementDate.setValue(selectedPaiement.getDateDePaiement());
        paiementStatus.setValue(selectedPaiement.getStatus());
    }

    private void loadPaiements() {
        paiementData.clear();
        List<Paiement> paiements = paiementService.getAllPaiements();
        paiementData.addAll(paiements);
        paiementTable.setItems(paiementData);
    }
    private void loadCommandes() {
        if (commandeListView == null) {
            System.err.println("⚠ commandeListView is NULL, skipping loadCommandes()");
            return;
        }
        commandeListView.getItems().clear();
        List<Commande> commandes = commandeService.getAllCommandes();
        for (Commande commande : commandes) {
            commandeListView.getItems().add("Commande #" + commande.getIdCommande());
        }
    }
    @FXML
    private void modifierPaiement() {
        // Valider les champs obligatoires
        if (paiementCommandeId.getText().isEmpty() || paiementUtilisateurId.getText().isEmpty() ||
                paiementMontant.getText().isEmpty() || paiementMode.getValue() == null || paiementDate.getValue() == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        // Récupérer les valeurs des champs
        int idCommande, idUtilisateur;
        double montant;
        try {
            idCommande = Integer.parseInt(paiementCommandeId.getText());
            idUtilisateur = Integer.parseInt(paiementUtilisateurId.getText());
            montant = Double.parseDouble(paiementMontant.getText());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "ID ou Montant invalide.");
            return;
        }

        LocalDate dateDePaiement = paiementDate.getValue();
        String modeDePaiement = paiementMode.getValue();
        String status = "Pending"; // Forcer le statut à "Pending"

        // Vérifier si le paiement existe
        Paiement existingPaiement = paiementService.getPaiementById(idCommande);
        if (existingPaiement == null) {
            showAlert("Erreur", "Le paiement sélectionné n'existe pas.");
            return;
        }

        // Créer un objet Paiement mis à jour
        Paiement updatedPaiement = new Paiement(idCommande, idUtilisateur, montant, modeDePaiement, dateDePaiement, status);

        // Appeler le service pour mettre à jour le paiement
        paiementService.modifierPaiement(updatedPaiement);

        // Rafraîchir la liste des paiements
        loadPaiements();

        // Afficher un message de succès
        showAlert("Succès", "Paiement modifié avec succès !");
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void goBack(ActionEvent event) {
        switchScene(event, "/PaiementView.fxml");
    }

    @FXML
    public void gotoPa(ActionEvent event) {
        switchScene(event, "/views/ModifierPaiement.fxml");
    }

    private void switchScene(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 500));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}