
/*package edu.emmapi.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

import java.io.IOException;

public class HomePageController {
    @FXML
    private Hyperlink courslab;

    @FXML
    private Hyperlink detaillab;

    @FXML
    private Hyperlink participantlab;

    @FXML
    private Hyperlink planninglab;

    @FXML
    private Hyperlink serviceid;

    @FXML
    private Hyperlink abonnementid;

    @FXML
    private void openServiceDashboard(ActionEvent event) {
        openDashboard("/dashboard.fxml", "Service Dashboard");
    }

    @FXML
    private void openAbonnementDashboard(ActionEvent event) {
        openDashboard("/type_abonnement.fxml", "Abonnement Dashboard");
    }



    private void openDashboard(String fxmlPath, String title) {
        try {
            System.out.println("FXML Location: " + getClass().getResource(fxmlPath));

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
            // TODO: Afficher une alerte à l'utilisateur
        }
    }

    @FXML
    public void openServiceDashboard2(ActionEvent actionEvent) {
        openDashboard("/service_view.fxml", "Service Dashboard");
    }
    @FXML
    public void opentypeDashboard2(ActionEvent actionEvent) {
        openDashboard("/AbonnementView.fxml", "Service Dashboard");
    }
}*/
package edu.emmapi.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HomePageController {
    @FXML private Hyperlink courslab;
    @FXML private Hyperlink detaillab;
    @FXML private Hyperlink participantlab;
    @FXML private Hyperlink planninglab;
    @FXML private Hyperlink serviceid;
    @FXML private Hyperlink abonnementid;

    // Map pour suivre les fenêtres ouvertes
    private final Map<String, Stage> openStages = new HashMap<>();

    @FXML
    public void initialize() {
        // Initialisation facultative, par exemple pour désactiver certains liens si nécessaire
        System.out.println("Tableau de bord initialisé.");
    }

    // Navigation vers les différents dashboards


    @FXML
    private void openAbonnementDashboard(ActionEvent event) {
        openDashboard("/type_abonnement.fxml", "Abonnement Dashboard");
    }

    @FXML
    private void openServiceDashboard2(ActionEvent event) {
        openDashboard("/service_view.fxml", "Service Dashboard ");
    }

    @FXML
    private void openTypeDashboard2(ActionEvent event) {
        openDashboard("/AbonnementView.fxml", "Type Abonnement");
    }

    @FXML
    private void openCoursDashboard(ActionEvent event) {
        openDashboard("/Ajouter_Cours.fxml", "Cours Dashboard");
    }
    @FXML
    private void openProduit(ActionEvent event) {
        openDashboard("/page1.fxml", "produit admin");
    }

    @FXML
    private void openCommandeView(ActionEvent event) {
        openDashboard("/CommandeView .fxml", "Commande");
    }


    //@FXML
    //private void openDetailsDashboard(ActionEvent event) {
       // openDashboard("/details.fxml", "Détails Dashboard");
    //}

    @FXML
    private void openParticipantsDashboard(ActionEvent event) {
        openDashboard("/Ajouter_Participant.fxml", "Participants Dashboard");
    }
    @FXML
    private void opencoachDashboard(ActionEvent event) {
        openDashboard("/CoachInterface.fxml", "Coach Dashboard");
    }

    @FXML
    private void openPlanningDashboard(ActionEvent event) {
        openDashboard("/Ajouter_Planning.fxml", "Planning Dashboard");
    }


    @FXML
    private void openclient1Dashboard(ActionEvent event) {
        openDashboard("/client1.fxml", "Services");
    }
    @FXML
    private void openpaiement(ActionEvent event) {
        openDashboard("/PaiementView.fxml", "Paiement");
    }

    // Méthode générique pour ouvrir un dashboard
    private void openDashboard(String fxmlPath, String title) {
        // Vérifier si le dashboard est déjà ouvert
        if (openStages.containsKey(fxmlPath)) {
            Stage existingStage = openStages.get(fxmlPath);
            existingStage.requestFocus(); // Mettre la fenêtre existante au premier plan
            return;
        }

        try {
            System.out.println("Tentative de chargement : " + getClass().getResource(fxmlPath));
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.setOnCloseRequest(e -> openStages.remove(fxmlPath)); // Retirer de la map quand fermé
            stage.show();

            // Ajouter à la map des fenêtres ouvertes
            openStages.put(fxmlPath, stage);
        } catch (IOException e) {
            showErrorAlert("Erreur de chargement", "Impossible de charger " + title + " : " + e.getMessage());
            System.err.println("Erreur lors du chargement du FXML : " + e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException e) {
            showErrorAlert("Erreur de ressource", "Le fichier FXML " + fxmlPath + " n'a pas été trouvé.");
            System.err.println("Ressource FXML introuvable : " + fxmlPath);
        }
    }

    // Méthode pour afficher une alerte d'erreur
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Fermer toutes les fenêtres ouvertes
    @FXML
    private void closeAllDashboards(ActionEvent event) {
        for (Stage stage : openStages.values()) {
            stage.close();
        }
        openStages.clear();
        System.out.println("Toutes les fenêtres ont été fermées.");
    }

    // Rafraîchir le tableau de bord principal (optionnel)
    @FXML
    private void refreshDashboard(ActionEvent event) {
        // Logique pour rafraîchir le tableau de bord principal si nécessaire
        System.out.println("Tableau de bord principal rafraîchi.");
    }

    public void openCommande(ActionEvent actionEvent) {
        openDashboard("/CommandeView .fxml", "commandeview");

    }

    public void openPaiement(ActionEvent actionEvent) {
        openDashboard("/PaiementView.fxml", "paiement" );

    }
    public void openPLANNINGUSER(ActionEvent actionEvent) {
        openDashboard("/planninguser.fxml", "planning" );

    }
    public void openinscriptionDashboard(ActionEvent actionEvent) {
        openDashboard("/pages/joueur/AjoutProfilJoueur.fxml", "inscription" );

    }
    public void opentournois(ActionEvent actionEvent) {
        openDashboard("/pages/tournoi_match/AfficheTournois.fxml", "Tournois" );

    }
}