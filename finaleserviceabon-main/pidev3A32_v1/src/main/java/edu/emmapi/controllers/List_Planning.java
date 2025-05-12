package edu.emmapi.controllers;

import edu.emmapi.entities.Planning;
import edu.emmapi.services.PlanningService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class List_Planning {

        @FXML
        private TableColumn<Planning, String> Datec;
        @FXML
        private TableColumn<Planning, String> coursc;
        @FXML
        private TableColumn<Planning, String> status;
        @FXML
        private TableView<Planning> table_planning;
        @FXML
        private TextField recherche; // Champ de recherche
        @FXML
        private TableColumn<Planning, String> typec;
        @FXML
        private Button voirbutton;
        private PlanningService planningService;
        private ObservableList<Planning> planningList;

        @FXML
        public void initialize() {
                planningService = new PlanningService();
                planningList = FXCollections.observableArrayList();

                // Configuration des colonnes de la table
                Datec.setCellValueFactory(new PropertyValueFactory<>("date_planning"));
                coursc.setCellValueFactory(new PropertyValueFactory<>("cours"));
                status.setCellValueFactory(new PropertyValueFactory<>("status"));
                typec.setCellValueFactory(new PropertyValueFactory<>("type_activite"));

                // Activer le tri sur chaque colonne
                Datec.setSortable(true);
                coursc.setSortable(true);
                status.setSortable(true);
                typec.setSortable(true);

                // Activer le tri par défaut (par exemple par "Datec")
                table_planning.getSortOrder().add(Datec);

                loadPlanningData(); // Charger les données du planning

                // Listener pour la sélection d'une ligne
                table_planning.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue != null) {
                                // Fermer la fenêtre de la liste des plannings avant d'ouvrir la fenêtre de détails
                                closeCurrentWindow();
                                // Ouvrir la page de détails pour le planning sélectionné
                                showDetailPage(newValue);
                        }
                });
        }

        private void loadPlanningData() {
                try {
                        List<Planning> plannings = planningService.getAllDataplannig(); // Charger tous les plannings
                        planningList.clear();
                        planningList.addAll(plannings);
                        table_planning.setItems(planningList);
                } catch (SQLException e) {
                        showAlert("Erreur lors du chargement des plannings : " + e.getMessage());
                }
        }

        public void refreshPlanningList() {
                loadPlanningData(); // Recharger les données
        }

        private void showDetailPage(Planning planning) {
                try {
                        // Charger la page de détails
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Detail_P.fxml"));
                        Parent detailRoot = loader.load();

                        // Passer les détails à la page de détails
                        Detail_P detailController = loader.getController();
                        detailController.setPlanningDetails(planning);

                        // Afficher la nouvelle scène
                        Stage stage = new Stage();
                        stage.setScene(new Scene(detailRoot));
                        stage.setTitle("Détails du Planning");
                        stage.show();
                } catch (Exception e) {
                        showAlert("Erreur lors de l'ouverture de la page de détails : " + e.getMessage());
                }
        }

        private void closeCurrentWindow() {
                // Fermer la fenêtre actuelle (liste des plannings)
                Stage currentStage = (Stage) table_planning.getScene().getWindow();
                currentStage.close();
        }

        private void showAlert(String message) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(message);
                alert.showAndWait();
        }

        @FXML
        void Recherche() {
                // Récupérer le terme de recherche depuis le champ texte
                String chaine = recherche.getText().trim();

                // Si le terme de recherche n'est pas vide, effectuer la recherche
                if (!chaine.isEmpty()) {
                        try {
                                // Créer une nouvelle instance de PlanningService et récupérer la liste filtrée
                                List<Planning> filteredPlannings = planningService.searchPlannings(chaine);

                                // Vider la liste actuelle dans la table
                                planningList.clear();

                                // Ajouter les résultats filtrés à la liste observable de la table
                                planningList.addAll(filteredPlannings);

                                // Mettre à jour la table avec la liste filtrée
                                table_planning.setItems(planningList);
                        } catch (SQLException e) {
                                showAlert("Erreur lors de la recherche : " + e.getMessage());
                        }
                } else {
                        // Si aucun terme de recherche n'est entré, recharger la liste complète
                        refreshPlanningList();
                }
        }
}









