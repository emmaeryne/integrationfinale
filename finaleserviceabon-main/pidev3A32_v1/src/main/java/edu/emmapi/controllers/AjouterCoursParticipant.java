/*package edu.pidev3a32.controllers;

import edu.pidev3a32.entities.Cours;
import edu.pidev3a32.entities.Participant;
import edu.pidev3a32.entities.Cours_Participant;
import edu.pidev3a32.services.CoursService;
import edu.pidev3a32.services.ParticipantService;
import edu.pidev3a32.services.CoursParticipantService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class AjouterCoursParticipant {

    @FXML
    private Button buttonajouter;

    @FXML
    private ComboBox<Cours> comboboxcours;  // ComboBox pour les cours

    @FXML
    private ComboBox<Participant> comboboxparticpant;  // ComboBox pour les participants

    @FXML
    private Label detaillabel;  // Label for detailed view

    @FXML
    private Label participantlabel;

    @FXML
    private Label planninglabel;  // Label for planning

    private final CoursService coursService = new CoursService();
    private final ParticipantService participantService = new ParticipantService();
    private final CoursParticipantService coursParticipantService = new CoursParticipantService();

    // Méthode appelée lors du chargement de la scène
    @FXML
    public void initialize() {
        try {
            loadCoursData();
            loadParticipantData();
            planninglabel.setOnMouseClicked(event -> onPlanningLabelClicked());  // Add click handler
            detaillabel.setOnMouseClicked(event -> onDetailLabelClicked());  // Add click handler for detail
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des données.");
        }
    }

    // Charger les données des cours dans le ComboBox
    private void loadCoursData() throws SQLException {
        List<Cours> coursList = coursService.getAllDatacour();  // Récupérer tous les cours depuis la base de données
        comboboxcours.getItems().addAll(coursList);  // Ajouter les cours dans le ComboBox
    }

    // Charger les données des participants dans le ComboBox
    private void loadParticipantData() throws SQLException {
        List<Participant> participantList = participantService.getAllDataparticipant();  // Récupérer tous les participants
        comboboxparticpant.getItems().addAll(participantList);  // Ajouter les participants dans le ComboBox

        // Utiliser un StringConverter pour afficher uniquement le nom des participants
        comboboxparticpant.setCellFactory(param -> new javafx.scene.control.ListCell<Participant>() {
            @Override
            protected void updateItem(Participant item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getNom());  // Afficher le nom du participant
            }
        });

        // Afficher également le nom dans le champ de sélection
        comboboxparticpant.setButtonCell(new javafx.scene.control.ListCell<Participant>() {
            @Override
            protected void updateItem(Participant item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getNom());
            }
        });
    }

    // Méthode appelée lorsque l'utilisateur clique sur le bouton "Ajouter"
    @FXML
    private void onAjouterButtonClicked() {
        // Récupérer l'élément sélectionné dans chaque ComboBox
        Cours selectedCours = comboboxcours.getSelectionModel().getSelectedItem();
        Participant selectedParticipant = comboboxparticpant.getSelectionModel().getSelectedItem();

        // Vérifier si les éléments sont bien sélectionnés
        if (selectedCours == null) {
            showAlert("Erreur", "Veuillez sélectionner un cours.");
            return;  // On sort de la méthode si aucun cours n'est sélectionné
        }

        if (selectedParticipant == null) {
            showAlert("Erreur", "Veuillez sélectionner un participant.");
            return;  // On sort de la méthode si aucun participant n'est sélectionné
        }

        // Créer un objet Cours_Participant pour ajouter la relation
        Cours_Participant coursParticipant = new Cours_Participant(selectedCours.getId_Cours(), selectedParticipant.getId());

        try {
            // Ajouter la relation Cours-Participant dans la base de données
            coursParticipantService.addCoursParticipant(coursParticipant);
            showAlert("Succès", "Le participant a été ajouté au cours avec succès !");

            // Charger et afficher la page listParticipantCours.fxml après l'ajout
            loadListParticipantCoursPage();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ajout du participant au cours.");
        }
    }

    // Méthode pour afficher des alertes
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour charger la page listParticipantCours.fxml
    private void loadListParticipantCoursPage() {
        try {
            // Charger le fichier FXML de la nouvelle scène
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Cours_Participant.fxml"));
            Scene scene = new Scene(loader.load());

            // Obtenir la fenêtre actuelle
            Stage stage = (Stage) buttonajouter.getScene().getWindow();
            stage.setScene(scene);  // Changer la scène
            stage.show();  // Afficher la nouvelle scène

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la page des participants.");
        }
    }

    // Méthode pour charger la page d'ajout de planning
    private void onPlanningLabelClicked() {
        loadAddPlanningPage();
    }

    // Méthode pour charger la page d'ajout de planning
    private void loadAddPlanningPage() {
        try {
            // Charger le fichier FXML de la nouvelle scène
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Ajouter_Planning.fxml"));
            Scene scene = new Scene(loader.load());

            // Obtenir la fenêtre actuelle
            Stage stage = (Stage) planninglabel.getScene().getWindow();
            stage.setScene(scene);  // Changer la scène
            stage.show();  // Afficher la nouvelle scène

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la page d'ajout de planning.");
        }
    }

    // Méthode pour charger la page de détails
    private void onDetailLabelClicked() {
        loadDetailPage();
    }

    // Méthode pour charger la page de détails
    private void loadDetailPage() {
        try {
            // Charger le fichier FXML de la nouvelle scène pour les détails
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Ajouter_Cours_Participant.fxml"));
            Scene scene = new Scene(loader.load());

            // Obtenir la fenêtre actuelle
            Stage stage = (Stage) detaillabel.getScene().getWindow();
            stage.setScene(scene);  // Changer la scène
            stage.show();  // Afficher la nouvelle scène

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la page de détails.");
        }
    }
}*/
package edu.emmapi.controllers;

import edu.emmapi.entities.Cours;
import edu.emmapi.entities.Participant;
import edu.emmapi.entities.Cours_Participant;
import edu.emmapi.services.CoursService;
import edu.emmapi.services.ParticipantService;
import edu.emmapi.services.CoursParticipantService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class AjouterCoursParticipant {

    @FXML
    private Button buttonajouter;

    @FXML
    private ComboBox<Cours> comboboxcours;  // ComboBox pour les cours

    @FXML
    private ComboBox<Participant> comboboxparticpant;  // ComboBox pour les participants

    @FXML
    private Label detaillabel;  // Label for detailed view

    @FXML
    private Label participantlabel;

    @FXML
    private Label planninglabel;

    @FXML
    private Label courslabel;  // Label for cours

    private final CoursService coursService = new CoursService();
    private final ParticipantService participantService = new ParticipantService();
    private final CoursParticipantService coursParticipantService = new CoursParticipantService();

    // Méthode appelée lors du chargement de la scène
    @FXML
    public void initialize() {
        try {
            loadCoursData();
            loadParticipantData();

            // Lier les gestionnaires d'événements aux labels
            planninglabel.setOnMouseClicked(event -> onPlanningLabelClicked());
            courslabel.setOnMouseClicked(event -> onCoursLabelClicked());  // Renommé pour plus de clarté
            participantlabel.setOnMouseClicked(event -> onParticipantLabelClicked());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des données.");
        }
    }

    // Charger les données des cours dans le ComboBox
    private void loadCoursData() throws SQLException {
        List<Cours> coursList = coursService.getAllDatacour();  // Récupérer tous les cours depuis la base de données
        comboboxcours.getItems().addAll(coursList);  // Ajouter les cours dans le ComboBox
    }

    // Charger les données des participants dans le ComboBox
    private void loadParticipantData() throws SQLException {
        List<Participant> participantList = participantService.getAllDataparticipant();  // Récupérer tous les participants
        comboboxparticpant.getItems().addAll(participantList);  // Ajouter les participants dans le ComboBox

        // Utiliser un StringConverter pour afficher uniquement le nom des participants
        comboboxparticpant.setCellFactory(param -> new javafx.scene.control.ListCell<Participant>() {
            @Override
            protected void updateItem(Participant item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getNom());  // Afficher le nom du participant
            }
        });

        // Afficher également le nom dans le champ de sélection
        comboboxparticpant.setButtonCell(new javafx.scene.control.ListCell<Participant>() {
            @Override
            protected void updateItem(Participant item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getNom());
            }
        });
    }

    // Méthode appelée lorsque l'utilisateur clique sur le bouton "Ajouter"
    @FXML
    private void onAjouterButtonClicked() {
        // Récupérer l'élément sélectionné dans chaque ComboBox
        Cours selectedCours = comboboxcours.getSelectionModel().getSelectedItem();
        Participant selectedParticipant = comboboxparticpant.getSelectionModel().getSelectedItem();

        // Vérifier si les éléments sont bien sélectionnés
        if (selectedCours == null) {
            showAlert("Erreur", "Veuillez sélectionner un cours.");
            return;  // On sort de la méthode si aucun cours n'est sélectionné
        }

        if (selectedParticipant == null) {
            showAlert("Erreur", "Veuillez sélectionner un participant.");
            return;  // On sort de la méthode si aucun participant n'est sélectionné
        }

        // Créer un objet Cours_Participant pour ajouter la relation
        Cours_Participant coursParticipant = new Cours_Participant(selectedCours.getId_Cours(), selectedParticipant.getId());

        try {
            // Ajouter la relation Cours-Participant dans la base de données
            coursParticipantService.addCoursParticipant(coursParticipant);
            showAlert("Succès", "Le participant a été ajouté au cours avec succès !");

            // Charger et afficher la page listParticipantCours.fxml après l'ajout
            loadListParticipantCoursPage();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ajout du participant au cours.");
        }
    }

    // Méthode pour afficher des alertes
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour charger la page listParticipantCours.fxml
    private void loadListParticipantCoursPage() {
        try {
            // Charger le fichier FXML de la nouvelle scène
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Cours_Participant.fxml"));
            Scene scene = new Scene(loader.load());

            // Obtenir la fenêtre actuelle
            Stage stage = (Stage) buttonajouter.getScene().getWindow();
            stage.setScene(scene);  // Changer la scène
            stage.show();  // Afficher la nouvelle scène

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la page des participants.");
        }
    }
    @FXML
    // Méthode pour charger la page d'ajout de planning
    private void onPlanningLabelClicked() {
        loadPlanningPage();
    }

    // Méthode pour charger la page d'ajout de planning
    private void loadPlanningPage() {
        try {
            // Charger le fichier FXML pour l'interface de gestion du planning
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Ajouter_Planning.fxml"));
            Scene scene = new Scene(loader.load());

            // Obtenir la fenêtre actuelle
            Stage stage = (Stage) planninglabel.getScene().getWindow();
            stage.setScene(scene);  // Remplacer la scène actuelle par la nouvelle scène
            stage.show();  // Afficher la nouvelle scène

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la page d'ajout de planning.");
        }
    }
    @FXML
    // Méthode pour charger la page de détails
    private void onCoursLabelClicked() {  // Correction du nom de la méthode
        loadDetailPage();
    }

    // Méthode pour charger la page de détails
    private void loadDetailPage() {
        try {
            // Charger le fichier FXML pour l'interface de gestion des détails
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Ajouter_Cours.fxml"));
            Scene scene = new Scene(loader.load());

            // Obtenir la fenêtre actuelle
            Stage stage = (Stage) courslabel.getScene().getWindow();
            stage.setScene(scene);  // Remplacer la scène actuelle par la nouvelle scène
            stage.show();  // Afficher la nouvelle scène

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la page de cours.");
        }
    }
    @FXML
    // Méthode pour charger la page de gestion des participants dans un cours
    private void onParticipantLabelClicked() {
        loadParticipantCoursPage();
    }

    // Méthode pour charger la page de gestion des participants dans un cours
    private void loadParticipantCoursPage() {
        try {
            // Charger le fichier FXML pour l'interface de gestion des participants dans le cours
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Ajouter_Participant.fxml"));
            Scene scene = new Scene(loader.load());

            // Obtenir la fenêtre actuelle
            Stage stage = (Stage) participantlabel.getScene().getWindow();
            stage.setScene(scene);  // Remplacer la scène actuelle par la nouvelle scène
            stage.show();  // Afficher la nouvelle scène

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la page des participants.");
        }
    }
}
