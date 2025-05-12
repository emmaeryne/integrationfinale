package edu.emmapi.controllers;

import edu.emmapi.entities.Commande;
import edu.emmapi.entities.Paiement;
import edu.emmapi.services.CommandeService;
import edu.emmapi.services.PaiementService;
import edu.emmapi.tools.MyConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AjoutPaiement {

    @FXML private TextField cardNumberField;
    @FXML private TextField expiryDateField;
    @FXML private TextField cvvField;
    @FXML private TextField amountField;

    @FXML
    private TableView<Commande> commandeTable;
    @FXML
    private TableColumn<Commande, Integer> colId;
    @FXML
    private TableColumn<Commande, Integer> colUtilisateur;
    @FXML
    private TableColumn<Commande, LocalDate> colDate;
    @FXML
    private TableColumn<Commande, String> colStatut;

    @FXML
    private TextField paiementCommandeId;
    @FXML
    private TextField paiementUtilisateurId;
    @FXML
    private TextField paiementMontant;
    @FXML
    private ComboBox<String> paiementMode;
    @FXML
    private DatePicker paiementDate;
    @FXML
    private ComboBox<String> paiementStatus;

    private final PaiementService paiementService = new PaiementService();
    private final CommandeService commandeService = new CommandeService();
    private final Map<Integer, Integer> userPaymentCount = new HashMap<>();
    private ObservableList<Commande> commandesObservableList = FXCollections.observableArrayList();

    @FXML
    private void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PaiementView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 500));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // Configurer les colonnes du TableView
        colId.setCellValueFactory(cellData -> cellData.getValue().idCommandeProperty().asObject());
        colUtilisateur.setCellValueFactory(cellData -> cellData.getValue().idUtilisateurProperty().asObject());
        colDate.setCellValueFactory(cellData -> cellData.getValue().dateDeCommandeProperty());
        colStatut.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        // Charger les commandes dans le TableView
        loadCommandes();

        // Ajouter un écouteur pour remplir les champs lors de la sélection d'une commande
        commandeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateCommandeFields(newSelection);
            }
        });

        // Configurer les ComboBox
        paiementMode.getItems().addAll();
        paiementStatus.getItems().addAll("Pending");
    }

    private void populateCommandeFields(Commande selectedCommande) {
        paiementCommandeId.setText(String.valueOf(selectedCommande.getIdCommande()));
        paiementUtilisateurId.setText(String.valueOf(selectedCommande.getIdUtilisateur()));
    }

    private void loadCommandes() {
        commandesObservableList.clear(); // Vider la liste observable
        List<Commande> commandes = commandeService.getAllCommandes(); // Récupérer les commandes depuis le service
        commandesObservableList.addAll(commandes); // Ajouter les commandes à la liste observable
        commandeTable.setItems(commandesObservableList); // Mettre à jour la TableView
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void applyDiscount(double discount) {
        double montant = Double.parseDouble(paiementMontant.getText().trim());
        double montantReduit = montant * (1 - discount);
        paiementMontant.setText(String.valueOf(montantReduit));
        showAlert("Réduction appliquée", "Une réduction de " + (int)(discount * 100) + "% a été appliquée.");
    }

    // Classe pour communiquer entre JavaScript et JavaFX
    public class JavaConnector {
        public void sendValue(int value) {
            // Traitez la valeur reçue de la roue de la chance
            System.out.println("Valeur reçue: " + value);
            // Vous pouvez mettre à jour l'interface utilisateur ou effectuer d'autres actions ici
        }
    }

    @FXML
    private void effectuerPaiement(ActionEvent actionEvent) {
        // Valider les champs obligatoires
        if (!validateFields()) {
            return;
        }

        try {
            int idCommande = Integer.parseInt(paiementCommandeId.getText().trim());
            int idUtilisateur = Integer.parseInt(paiementUtilisateurId.getText().trim());
            double montant = Double.parseDouble(paiementMontant.getText().trim());
            String modeDePaiement = paiementMode.getValue();
            String status = paiementStatus.getValue();
            LocalDate date = paiementDate.getValue();

            // Vérifier si l'utilisateur a déjà effectué des paiements
            int paymentCount = userPaymentCount.getOrDefault(idUtilisateur, 0) + 1;
            userPaymentCount.put(idUtilisateur, paymentCount);

            // Appliquer une réduction de 10% uniquement à la 3ème fois
            if (paymentCount == 3) {
                // Appliquer une réduction de 10% pour le 3ème paiement
                montant = montant * 0.90;

                // Afficher une boîte de dialogue personnalisée pour la réduction
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Réduction Appliquée");
                alert.setHeaderText("Félicitations !");

                // Créer un contenu personnalisé
                Label contentLabel = new Label("Une réduction de 10% a été appliquée pour votre 3ème paiement.");
                contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2E8B57;");

                // Ajouter une icône (optionnel)
                try {
                    // Charger l'image avec JavaFX
                    javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResourceAsStream("/404070770_2091685387832685_273154292922322738_n.png"));
                    ImageView icon = new ImageView(image);
                    icon.setFitWidth(50);
                    icon.setFitHeight(50);
                    alert.setGraphic(icon);
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Impossible de charger l'icône.");
                }

                // Appliquer un style à la boîte de dialogue
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.setStyle("-fx-background-color: linear-gradient(to bottom, #F0FFF0, #E0FFFF);");
                dialogPane.getStylesheets().add(getClass().getResource("/dialog.css").toExternalForm());

                // Ajouter le contenu à la boîte de dialogue
                dialogPane.setContent(contentLabel);

                // Afficher la boîte de dialogue
                alert.showAndWait();

                // Mettre à jour le montant dans le champ de texte
                paiementMontant.setText(String.valueOf(montant));
            }

            // Convertir le mode de paiement et le statut
            String modeDePaiementDB = convertPaymentMode(modeDePaiement);
            String statusDB = convertPaymentStatus(status);

            // Créer un objet Paiement
            Paiement paiement = new Paiement(idCommande, idUtilisateur, montant, modeDePaiementDB, date, statusDB);

            // Insérer le paiement dans la base de données
            insertPayment(paiement);

        } catch (NumberFormatException e) {
            showAlert("Erreur", "ID et Montant doivent être des nombres valides.");
        } catch (IllegalArgumentException e) {
            showAlert("Erreur", e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de l'enregistrement du paiement.");
        }
    }

    private boolean validateFields() {
        if (paiementCommandeId.getText().trim().isEmpty() ||
                paiementUtilisateurId.getText().trim().isEmpty() ||
                paiementMontant.getText().trim().isEmpty() ||
                paiementMode.getValue() == null ||
                paiementDate.getValue() == null ||
                paiementStatus.getValue() == null) {

            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return false;
        }
        return true;
    }

    private void insertPayment(Paiement paiement) throws SQLException {
        String query = "INSERT INTO paiement (idCommande, idUtilisateur, montant, modeDePaiement, dateDePaiement, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, paiement.getIdCommande());
            pstmt.setInt(2, paiement.getIdUtilisateur());
            pstmt.setDouble(3, paiement.getMontant());
            pstmt.setString(4, paiement.getModeDePaiement());
            pstmt.setDate(5, Date.valueOf(paiement.getDateDePaiement()));
            pstmt.setString(6, paiement.getStatus());

            int rowsInserted = pstmt.executeUpdate();

            if (rowsInserted > 0) {
                // Récupérer l'ID généré
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    paiement.setIdPaiement(generatedKeys.getInt(1));
                }
                showAlert("Succès", "Paiement effectué avec succès !");

                // Rafraîchir la liste des commandes
                loadCommandes();
                commandeTable.refresh(); // Rafraîchir la TableView
            } else {
                showAlert("Erreur", "Échec de l'enregistrement du paiement.");
            }
        }
    }

    private String convertPaymentMode(String modeDePaiement) {
        switch (modeDePaiement) {
            case "Cash":
                return "Cash";
            case "Carte bancaire":
                return "Credit Card";
            case "Virement":
                return "Bank Transfer";
            default:
                throw new IllegalArgumentException("Mode de paiement non valide.");
        }
    }

    private String convertPaymentStatus(String status) {
        switch (status) {
            case "Pending":
                return "Pending";
            default:
                throw new IllegalArgumentException("Statut de paiement non valide.");
        }
    }
}