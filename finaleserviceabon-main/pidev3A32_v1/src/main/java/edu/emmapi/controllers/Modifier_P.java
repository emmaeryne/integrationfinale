/*package edu.pidev3a32.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Modifier_P {

    @FXML
    private RadioButton buttonconfirmemodifier;

    @FXML
    private RadioButton buttononattentemodif;

    @FXML
    private TextField courmodifstextfield;

    @FXML
    private TextField datefieldtextfield;

    @FXML
    private Button okmodifier;

    @FXML
    private TextField typemodiftextfiels;

    @FXML


    private int planningId; // Stocker l'ID du planning

    // Méthode pour définir les détails du planning
    public void setPlanningDetails(int id, String cours, String date, String type, String status) {
        planningId = id;
        courmodifstextfield.setText(cours);
        datefieldtextfield.setText(date);
        typemodiftextfiels.setText(type);

        // Définir le bouton radio en fonction du statut
        buttononattentemodif.setSelected("En Attente".equals(status));
        buttonconfirmemodifier.setSelected("Confirme".equals(status));
    }

    @FXML
    private void handleOkModifier() {
        String cours = courmodifstextfield.getText();
        String date =  datefieldtextfield.getText();
        String type = typemodiftextfiels.getText();
        String status = buttononattentemodif.isSelected() ? "En Attente" : "Confirme";

        if (validateFields(cours, date, type)) {
            updatePlanning(planningId, cours, date, type, status);
            showAlert("Modification réussie", "Les détails du planning ont été mis à jour avec succès.");

            // Charger la nouvelle scène
            loadPlanningList();
        } else {
            showAlert("Erreur", "Veuillez remplir tous les champs correctement.");
        }
    }

    private void loadPlanningList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/list_planning.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de list_planning
            List_Planning listPlanningController = loader.getController();
            listPlanningController.refreshPlanningList(); // Rafraîchir la liste

            Stage stage = (Stage) okmodifier.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Plannings");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la liste des plannings.");
        }
    }

    private boolean validateFields(String cours, String date, String type) {
        return !cours.isEmpty() && !date.isEmpty() && !type.isEmpty();
    }

    private void updatePlanning(int id, String cours, String date, String type, String status) {
        // Logique pour mettre à jour le planning dans la base de données
        System.out.println("Mise à jour du planning :");
        System.out.println("ID : " + id);
        System.out.println("Cours : " + cours);
        System.out.println("Date : " + date);
        System.out.println("Type d'activité : " + type);
        System.out.println("Status : " + status);

        // Ajoutez ici la logique pour mettre à jour le planning, par exemple :
        // planningService.updatePlanning(new Planning(id, cours, date, type, status));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}*/
package edu.emmapi.controllers;

import edu.emmapi.entities.Planning;
import edu.emmapi.services.PlanningService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.sql.SQLException;

public class Modifier_P {

    @FXML
    private RadioButton buttonconfirmemodifier;

    @FXML
    private RadioButton buttononattentemodif;

    @FXML
    private TextField courmodifstextfield;

    @FXML
    private TextField datefieldtextfield;

    @FXML
    private Button okmodifier;

    @FXML
    private TextField typemodiftextfiels;

    private int planningId;

    // Méthode pour définir les détails du planning à modifier
    public void setPlanningDetails(int id, String cours, String date, String type, String status) {
        planningId = id;
        courmodifstextfield.setText(cours);
        datefieldtextfield.setText(date);
        typemodiftextfiels.setText(type);

        // Définir le bouton radio en fonction du statut
        buttononattentemodif.setSelected("En Attente".equals(status));
        buttonconfirmemodifier.setSelected("Confirme".equals(status));
    }

    @FXML
    private void handleOkModifier() {
        String cours = courmodifstextfield.getText();
        String date = datefieldtextfield.getText();
        String type = typemodiftextfiels.getText();
        String status = buttononattentemodif.isSelected() ? "En Attente" : "Confirme";

        if (validateFields(cours, date, type)) {
            Planning planning = new Planning(planningId, type, java.sql.Date.valueOf(date), status, Integer.parseInt(cours));
            updatePlanning(planning);

            // Afficher un message de succès
            showAlert("Modification réussie", "Les détails du planning ont été mis à jour avec succès.");

            // Fermer la fenêtre de la liste actuelle avant de lancer la scène mise à jour
            closeCurrentWindowAndOpenList();
        } else {
            showAlert("Erreur", "Veuillez remplir tous les champs correctement.");
        }
    }

    private void closeCurrentWindowAndOpenList() {
        try {
            // Fermer la fenêtre actuelle (fenêtre de modification)
            Stage currentStage = (Stage) okmodifier.getScene().getWindow();
            currentStage.close();

            // Charger la scène de la liste des plannings mise à jour
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/list_plannning.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la page Liste_Planning
            List_Planning listPlanningController = loader.getController();
            listPlanningController.refreshPlanningList(); // Rafraîchir la liste des plannings

            // Créer une nouvelle scène et la montrer
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Liste des Plannings");
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la liste des plannings.");
        }
    }

    private boolean validateFields(String cours, String date, String type) {
        return !cours.isEmpty() && !date.isEmpty() && !type.isEmpty();
    }

    private void updatePlanning(Planning planning) {
        PlanningService planningService = new PlanningService();
        try {
            planningService.updateplanning(planning); // Mettre à jour le planning dans la base de données
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la mise à jour du planning.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}





