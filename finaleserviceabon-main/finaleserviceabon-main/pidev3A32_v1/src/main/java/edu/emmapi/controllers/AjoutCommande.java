package edu.emmapi.controllers;

import edu.emmapi.entities.Commande;
import edu.emmapi.entities.CommandeProduits;
import edu.emmapi.entities.produit;
import edu.emmapi.services.CommandeProduitsService;
import edu.emmapi.services.CommandeService;
import edu.emmapi.services.EmailServiceCommande;
import edu.emmapi.services.ProduitService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class AjoutCommande implements Initializable {

    @FXML
    private TableView<produit> tableProduit;
    @FXML
    private TableColumn<produit, String> colNom;
    @FXML
    private TableColumn<produit, String> colCategorie;
    @FXML
    private TableColumn<produit, Float> colPrix;
    @FXML
    private TableColumn<produit, String> colDate;
    @FXML
    private TableColumn<produit, String> colFournisseur;
    @FXML
    private TextField searchField;
    @FXML
    private TextField commandeUtilisateur;
    @FXML
    private DatePicker commandeDate;
    @FXML
    private ComboBox<String> commandeStatus;
    @FXML
    private HBox buttonContainer;
    @FXML
    private ListView<String> commandeList;

    private final ProduitService produitService = new ProduitService();
    private final CommandeService commandeService = new CommandeService();
    private ObservableList<produit> produitsObservableList = FXCollections.observableArrayList();
    private FilteredList<produit> filteredList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerProduits();
        setupTableColumns();

    }

    private void chargerProduits() {
        List<produit> produitsList = produitService.afficherProduit();
        produitsObservableList.setAll(produitsList);
        filteredList = new FilteredList<>(produitsObservableList, p -> true);
        tableProduit.setItems(filteredList);
    }

    private void setupTableColumns() {
        colNom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom_Produit()));
        colCategorie.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategorie()));
        colPrix.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrix()));
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate()));
        colFournisseur.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFournisseur()));

        // Allow multiple selection
        tableProduit.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }


    @FXML
    private void goBack(ActionEvent event) {

    }

    private void loadScene(ActionEvent event, String s) {
    }

    @FXML
    private void createCommande() {
        if (commandeUtilisateur.getText().isEmpty() || commandeDate.getValue() == null || commandeStatus.getValue() == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        try {
            LocalDate dateDeCommande = commandeDate.getValue();
            int idUtilisateur = Integer.parseInt(commandeUtilisateur.getText().trim());
            String status = commandeStatus.getValue();

            Commande commande = new Commande(dateDeCommande, idUtilisateur, status);
            int idCommande = commandeService.ajouterCommande(commande);

            if (idCommande != -1) {
                CommandeProduitsService commandeProduitsService = new CommandeProduitsService();

                for (produit produit : tableProduit.getSelectionModel().getSelectedItems()) {
                    CommandeProduits commandeProduits = new CommandeProduits(
                            idCommande,
                            produit.getId(),
                            1,
                            produit.getPrix()
                    );
                    commandeProduitsService.ajouterCommandeProduit(commandeProduits);
                }


                // Créer le texte pour le code QR
                String qrText = "Commande ID: " + idCommande + "\n"
                        + "Date: " + dateDeCommande + "\n"
                        + "Utilisateur ID: " + idUtilisateur + "\n"
                        + "Statut: " + status;

                // Envoyer un e-mail avec le code QR
                String toEmail = "salmahaouari6@gmail.com";
                String subject = "Nouvelle Commande Ajoutée";
                String body = "Bonjour,\n\n"
                        + "Nous sommes ravis de vous informer qu'une nouvelle commande a été passée avec succès ! 🎉\n\n"
                        + "📅 **Date de la commande :** " + dateDeCommande + "\n"
                        + "👤 **ID Utilisateur :** " + idUtilisateur + "\n\n"
                        + "Cette commande marque une nouvelle étape dans notre collaboration. Nous nous engageons à vous fournir un service de qualité et à répondre à vos attentes avec professionnalisme.\n\n"
                        + "Merci de votre confiance et de votre soutien continu. Nous restons à votre disposition pour toute question ou information supplémentaire.\n\n"
                        + "Cordialement,\n"
                        + "L'équipe de Hive  🌟\n\n"
                        + "---\n"
                        + "💡 **Conseil du jour :** Profitez de nos offres spéciales et restez connecté pour ne rien manquer de nos nouveautés !";

                EmailServiceCommande emailService = new EmailServiceCommande();
                emailService.sendEmail(toEmail, subject, body, qrText, dateDeCommande, idUtilisateur);

                showAlert("Succès", "Commande ajoutée avec succès !");
            } else {
                showAlert("Erreur", "Échec de l'ajout de la commande.");
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'ID utilisateur doit être un nombre valide.");
            e.printStackTrace();
        }
    }



    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
