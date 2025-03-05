package edu.emmapi.controllers;

import edu.emmapi.entities.Commande;
import edu.emmapi.entities.Paiement;
import edu.emmapi.services.CommandeService;
import edu.emmapi.services.PaiementService;
import edu.pidev3a32.tools.MyConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import java.sql.*;

import java.util.HashMap;
import java.util.Map;
public class AjoutPaiement {

    @FXML
    private ListView<String> commandeListView;
    @FXML
    private ListView<String> paiementList;
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
    public void initialize() {
        paiementMode.getItems().addAll("Cash", "Carte bancaire", "Virement");
        paiementMode.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if ("Carte bancaire".equals(newVal)) {
                handlePaymentModeChange(null);
            }
        });
        if (commandeListView == null) {
            System.out.println("❌ commandeListView is null! Check fx:id in FXML.");
        } else {
            loadCommandes();
        }

        if (paiementMode == null) {
            System.out.println("❌ paiementMode is null! Check fx:id in FXML.");
        }
        /*else {
            paiementMode.getItems().addAll("Cash", "Carte bancaire", "Chèque", "Virement");
        }*/

        if (paiementStatus == null) {
            System.out.println("❌ paiementStatus is null! Check fx:id in FXML.");
        } else {
            paiementStatus.getItems().addAll("Pending", "Completed", "Failed");
        }

        if (paiementList == null) {
            System.out.println("❌ paiementList is null! Check fx:id in FXML.");
        } else {
            loadPaiements();
        }
    }


    private void populatePaiementFields(String selectedPaiement) {
        try {
            String[] details = selectedPaiement.split("\\|");
            int idCommande = Integer.parseInt(details[0].split(":")[1].trim());
            int idUtilisateur = Integer.parseInt(details[1].split(":")[1].trim());
            double montant = Double.parseDouble(details[2].split(":")[1].replace("TND", "").trim());
            String modeDePaiement = details[3].split(":")[1].trim();
            String status = details[4].split(":")[1].trim();

            paiementCommandeId.setText(String.valueOf(idCommande));
            paiementUtilisateurId.setText(String.valueOf(idUtilisateur));
            paiementMontant.setText(String.valueOf(montant));
            paiementMode.setValue(modeDePaiement);
            paiementStatus.setValue(status);

        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les détails du paiement.");
        }
    }

    private void loadCommandes() {
        commandeListView.getItems().clear();
        List<Commande> commandes = commandeService.getAllCommandes();
        for (Commande commande : commandes) {
            commandeListView.getItems().add("Commande #" + commande.getIdCommande());
        }
    }

    private void loadPaiements() {
        paiementList.getItems().clear();
        List<Paiement> paiements = paiementService.getAllPaiements();

        if (paiements.isEmpty()) {
            paiementList.getItems().add("Aucun paiement disponible.");
            return;
        }

        for (Paiement paiement : paiements) {
            String paiementInfo = "Paiement ID: " + paiement.getIdCommande() +
                    " | Utilisateur: " + paiement.getIdUtilisateur() +
                    " | Montant: " + paiement.getMontant() + " TND" +
                    " | Mode: " + paiement.getModeDePaiement() +
                    " | Status: " + paiement.getStatus();
            paiementList.getItems().add(paiementInfo);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    private void processPayment(String spinResult) {
        try {
            // Récupération et validation des valeurs
            int idCommande = Integer.parseInt(paiementCommandeId.getText().trim());
            int idUtilisateur = Integer.parseInt(paiementUtilisateurId.getText().trim());
            double montantInitial = Double.parseDouble(paiementMontant.getText().trim());
            LocalDate date = paiementDate.getValue();

            // Application de la remise selon le résultat de la roue
            double remise = Double.parseDouble(spinResult.replace("%", "")) / 100;
            double montantFinal = montantInitial * (1 - remise);

            // Conversion des valeurs pour la BDD
            String modeDePaiement = convertirModePaiement(paiementMode.getValue());
            String status = convertirStatus(paiementStatus.getValue());

            // Création de l'objet Paiement
            Paiement paiement = new Paiement(
                    idCommande,
                    idUtilisateur,
                    montantFinal,
                    modeDePaiement,
                    date,
                    status
            );

            // Insertion en base de données
            insererPaiement(paiement);

            showAlert("Succès",
                    String.format("Paiement effectué!\nMontant initial: %.2f TND\nRemise: %.0f%%\nMontant final: %.2f TND",
                            montantInitial,
                            remise * 100,
                            montantFinal));

            loadPaiements();

        } catch (NumberFormatException e) {
            showAlert("Erreur", "Format numérique invalide");
        } catch (SQLException e) {
            showAlert("Erreur BDD", e.getMessage());
        } catch (IllegalArgumentException e) {
            showAlert("Erreur", e.getMessage());
        }
    }
    // Méthodes helper
    private double calculerRemise(int spinResult) {
        return switch (spinResult) {
            case 1 -> 0.10; // 10% de remise
            case 2 -> 0.15; // 15%
            case 3 -> 0.20; // 20%
            case 4 -> 0.25; // 25%
            case 5 -> 0.30; // 30%
            case 6 -> 0.50; // 50% de remise (jackpot)
            default -> throw new IllegalArgumentException("Résultat de roue invalide");
        };
    }

    private String convertirModePaiement(String mode) {
        return switch (mode) {
            case "Cash" -> "CASH";
            case "Carte bancaire" -> "CREDIT_CARD";
            case "Virement" -> "BANK_TRANSFER";
            default -> throw new IllegalArgumentException("Mode de paiement invalide");
        };
    }

    private String convertirStatus(String status) {
        return switch (status) {
            case "Pending" -> "PENDING";
            case "Completed" -> "COMPLETED";
            case "Failed" -> "FAILED";
            default -> throw new IllegalArgumentException("Statut invalide");
        };
    }

    private void insererPaiement(Paiement paiement) throws SQLException {
        String query = "INSERT INTO paiement (idCommande, idUtilisateur, montant, modeDePaiement, dateDePaiement, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = MyConnection.getInstance().getCnx(); // Récupérer la connexion
             PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, paiement.getIdCommande());
            pstmt.setInt(2, paiement.getIdUtilisateur());
            pstmt.setDouble(3, paiement.getMontant());
            pstmt.setString(4, paiement.getModeDePaiement());
            pstmt.setDate(5, Date.valueOf(paiement.getDateDePaiement()));
            pstmt.setString(6, paiement.getStatus());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Échec de l'insertion");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    paiement.setIdPaiement(generatedKeys.getInt(1));
                }
            }
        }
    }

    @FXML
    private void effectuerPaiement(ActionEvent actionEvent) {
        // Valider les champs obligatoires
        if (paiementCommandeId.getText().trim().isEmpty() ||
                paiementUtilisateurId.getText().trim().isEmpty() ||
                paiementMontant.getText().trim().isEmpty() ||
                paiementMode.getValue() == null ||
                paiementDate.getValue() == null ||
                paiementStatus.getValue() == null) {

            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        try {
            int idCommande = Integer.parseInt(paiementCommandeId.getText().trim());
            int idUtilisateur = Integer.parseInt(paiementUtilisateurId.getText().trim());
            double montant = Double.parseDouble(paiementMontant.getText().trim());
            String modeDePaiement = paiementMode.getValue();
            String status = paiementStatus.getValue();
            LocalDate date = paiementDate.getValue();

            // Vérifier si l'utilisateur a déjà effectué 2 paiements
            int count = userPaymentCount.getOrDefault(idUtilisateur, 0);
            if (count == 2) {
                // Appliquer une réduction de 10% pour le 3ème paiement
                montant = montant * 0.90;
                showAlert("Réduction", "Une réduction de 10% a été appliquée pour votre 3ème paiement.");
            }

            // Convertir le mode de paiement en valeurs compatibles avec la base de données
            String modeDePaiementDB;
            switch (modeDePaiement) {
                case "Cash":
                    modeDePaiementDB = "Cash";
                    break;
                case "Carte bancaire":
                    modeDePaiementDB = "Credit Card";
                    break;
                case "Virement":
                    modeDePaiementDB = "Bank Transfer";
                    break;
                default:
                    showAlert("Erreur", "Mode de paiement non valide.");
                    return;
            }

            // Convertir le statut en valeurs compatibles avec la base de données
            String statusDB;
            switch (status) {
                case "Pending":
                    statusDB = "Pending";
                    break;
                case "Completed":
                    statusDB = "Completed";
                    break;
                case "Failed":
                    statusDB = "Failed";
                    break;
                default:
                    showAlert("Erreur", "Statut de paiement non valide.");
                    return;
            }

            // Créer un objet Paiement
            Paiement paiement = new Paiement(idCommande, idUtilisateur, montant, modeDePaiementDB, date, statusDB);

            // Insérer le paiement dans la base de données
            String query = "INSERT INTO paiement (idCommande, idUtilisateur, montant, modeDePaiement, dateDePaiement, status) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection connection = MyConnection.getInstance().getCnx(); // Récupérer la connexion
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

                    // Mettre à jour le compteur de paiements pour l'utilisateur
                    userPaymentCount.put(idUtilisateur, count + 1);

                    // Afficher le montant réduit dans le champ de texte
                    paiementMontant.setText(String.valueOf(montant));

                    // Rafraîchir la liste des paiements
                    loadPaiements();

                    showAlert("Succès", "Paiement effectué avec succès !");
                } else {
                    showAlert("Erreur", "Échec de l'enregistrement du paiement.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Une erreur s'est produite lors de l'enregistrement du paiement.");
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "ID et Montant doivent être des nombres valides.");
        }
    }
    @FXML
    private void handlePaymentModeChange(ActionEvent event) {
        if ("Carte bancaire".equals(paiementMode.getValue())) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/StripePayment.fxml"));
                Parent root = loader.load();

                StripePaymentController controller = loader.getController();
                controller.initializeData(
                        Double.parseDouble(paiementMontant.getText()),
                        Integer.parseInt(paiementCommandeId.getText()),
                        this
                );

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible d'ouvrir l'interface de paiement.");
            }
        }
    }

    public void updatePaymentStatus(int idCommande, String status) {
        Paiement paiement = paiementService.getPaiementById(idCommande);
        if (paiement != null) {
            paiement.setStatus(status);
            paiementService.modifierPaiement(paiement);
            loadPaiements();
        }
    }



}
