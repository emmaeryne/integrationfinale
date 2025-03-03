/*package edu.pidev3a32.controllers;

import edu.pidev3a32.entities.Planning;
import edu.pidev3a32.services.PlanningService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.MouseEvent;

import java.sql.SQLException;
import java.util.List;

public class PlanningTileController {
    @FXML
    private Label courslabel;

    @FXML
    private Label detaillabel;

    @FXML
    private Label participantlabel;

    @FXML
    private TilePane planningContainer;  // TilePane to hold the planning tiles

    private PlanningService planningService;  // Service to interact with the database

    // Constructor to initialize the PlanningService
    public PlanningTileController() {
        this.planningService = new PlanningService();  // Initialize PlanningService
    }

    // Method to initialize the controller and load the planning data into the TilePane
    @FXML
    private void initialize() {
        try {
            // Fetch all plannings from the database using PlanningService
            List<Planning> plannings = planningService.getAllplannings();

            // Clear the TilePane before adding new planning tiles
            planningContainer.getChildren().clear();

            // Loop through the plannings and create a tile for each one
            for (Planning planning : plannings) {
                planningContainer.getChildren().add(createPlanningTile(planning));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error loading planning data.");
        }
    }

    // Method to create a tile for each planning
    private StackPane createPlanningTile(Planning planning) {
        // Create a rectangle for the tile with a beige or brown color
        Rectangle tile = new Rectangle(150, 150);

        // Change the color to a different beige or brown
        tile.setFill(Color.rgb(210, 180, 140));  // Classic Beige (RGB: 210, 180, 140) or #D2B48C
        // You can also try a brown shade:
        // tile.setFill(Color.rgb(181, 101, 29));  // Light Brown (RGB: 181, 101, 29) or #B5651D

        tile.setArcWidth(15);  // Rounded corners
        tile.setArcHeight(15);
        tile.setStroke(Color.DARKBLUE);  // Dark border
        tile.setStrokeWidth(2);

        // Create a label to display the planning's activity and date
        Label planningLabel = new Label(planning.getType_activite() + "\n" + planning.getDate_planning());
        planningLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        planningLabel.setTextFill(Color.WHITE);

        // Create a StackPane to hold the tile and the label together
        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(150, 150);
        stackPane.getChildren().addAll(tile, planningLabel);

        // Add an event handler when the tile is clicked
        stackPane.setOnMouseClicked(event -> handleTileClick(event, planning));

        return stackPane;  // Return the StackPane containing the tile
    }

    // Method to handle tile click events (e.g., to show more details about the planning)
    private void handleTileClick(MouseEvent event, Planning planning) {
        // For now, just print the planning's details
        System.out.println("Planning selected: " + planning.getType_activite() + " - " + planning.getDate_planning());
        // You can extend this method to open a new window, show more details, etc.
    }
}*/
/*package edu.emmapi.controllers;

import edu.emmapi.entities.Planning;
import edu.emmapi.services.PlanningService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class PlanningTileController {

    @FXML
    private TilePane planningContainer;  // Conteneur pour les tuiles de planning

    @FXML
    private Label courslabel;  // Label pour afficher des informations détaillées (mis à jour dynamiquement)

    @FXML
    private Label participantlabel;

    @FXML
    private Label detaillabel;

    private PlanningService planningService;  // Service pour interagir avec la base de données

    // Constructeur pour initialiser le PlanningService
    public PlanningTileController() {
        this.planningService = new PlanningService();
    }

    // Méthode pour initialiser le contrôleur et charger les données de planning dans le TilePane
    @FXML
    private void initialize() {
        try {
            // Récupérer tous les plannings depuis la base de données
            List<Planning> plannings = planningService.getAllplannings();

            // Vider le TilePane avant d'ajouter de nouvelles tuiles
            planningContainer.getChildren().clear();

            // Parcourir les plannings et créer une tuile pour chaque planning
            for (Planning planning : plannings) {
                planningContainer.getChildren().add(createPlanningTile(planning));
            }

            // Ajouter des événements pour chaque label
            setupLabelNavigation();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement des données de planning.");
        }
    }

    // Méthode pour configurer les événements de navigation pour les labels
    private void setupLabelNavigation() {
        // Chaque label a un chemin de navigation spécifique
        courslabel.setOnMouseClicked(event -> navigateToPage("/listcours.fxml"));
        participantlabel.setOnMouseClicked(event -> navigateToPage("/paticipantt.fxml"));
        detaillabel.setOnMouseClicked(event -> navigateToPage("/planningdetails.fxml"));
    }

    // Méthode pour créer une tuile pour chaque planning
    private StackPane createPlanningTile(Planning planning) {
        // Créer un rectangle pour la tuile avec une couleur beige
        Rectangle tile = new Rectangle(150, 150);
        tile.setFill(Color.rgb(210, 180, 140));  // Beige
        tile.setArcWidth(15);  // Coins arrondis
        tile.setArcHeight(15);
        tile.setStroke(Color.DARKBLUE);  // Bordure sombre
        tile.setStrokeWidth(2);

        // Créer un label pour afficher le type d'activité et la date
        String labelText = planning.getType_activite() + "\n" +
                planning.getDate_planning() + "\n" +
                "Statut: " + planning.getStatus(); // Ajouter le statut au label
        Label planningLabel = new Label(labelText);
        planningLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        planningLabel.setTextFill(Color.WHITE);

        // Créer un StackPane pour contenir la tuile et le label
        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(150, 150);
        stackPane.getChildren().addAll(tile, planningLabel);

        // Ajouter un gestionnaire d'événements lorsque la tuile est cliquée
        stackPane.setOnMouseClicked(event -> handleTileClick(event, planning));

        return stackPane;
    }

    // Méthode pour gérer l'événement de clic sur une tuile de planning
    private void handleTileClick(MouseEvent event, Planning planning) {
        System.out.println("Planning sélectionné : " + planning.getType_activite() + " - " + planning.getDate_planning());

        // Mettre à jour le label 'courslabel' avec les informations du planning
        String details = "Type d'activité: " + planning.getType_activite() + "\n" +
                "Date: " + planning.getDate_planning() + "\n" +
                "Statut: " + planning.getStatus() + "\n" +
                "ID du Cours: " + planning.getCours();
        courslabel.setText(details);
    }

    // Méthode pour naviguer vers une autre interface (page FXML)
    private void navigateToPage(String fxmlPath) {
        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Obtenir la scène actuelle du Stage
            Stage stage = (Stage) courslabel.getScene().getWindow();

            // Créer une nouvelle scène et l'afficher dans la fenêtre existante
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'ouverture de la fenêtre: " + e.getMessage());
        }
    }
}*/

package edu.emmapi.controllers;

import edu.emmapi.entities.Planning;
import edu.emmapi.services.PlanningService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class PlanningTileController {

    @FXML
    private TilePane planningContainer;  // Conteneur pour les tuiles de planning

    @FXML
    private Label courslabel;  // Label pour afficher des informations détaillées (mis à jour dynamiquement)

    @FXML
    private Label participantlabel;

    @FXML
    private Label detaillabel;

    private PlanningService planningService;  // Service pour interagir avec la base de données

    private static final double TILE_SIZE = 150;  // Taille des tuiles de planning

    // Constructeur pour initialiser le PlanningService
    public PlanningTileController() {
        this.planningService = new PlanningService();
    }

    // Méthode pour initialiser le contrôleur et charger les données de planning dans le TilePane
    @FXML
    private void initialize() {
        try {
            // Récupérer tous les plannings depuis la base de données
            List<Planning> plannings = planningService.getAllplannings();

            // Vider le TilePane avant d'ajouter de nouvelles tuiles
            planningContainer.getChildren().clear();

            // Parcourir les plannings et créer une tuile pour chaque planning
            for (Planning planning : plannings) {
                planningContainer.getChildren().add(createPlanningTile(planning));
            }

            // Ajouter des événements pour chaque label
            setupLabelNavigation();
        } catch (SQLException e) {
            showErrorDialog("Erreur de base de données", "Impossible de charger les plannings.", e);
        }
    }

    // Méthode pour configurer les événements de navigation pour les labels
    private void setupLabelNavigation() {
        courslabel.setOnMouseClicked(event -> navigateToPage("/listcours.fxml"));
        participantlabel.setOnMouseClicked(event -> navigateToPage("/paticipantt.fxml"));
        detaillabel.setOnMouseClicked(event -> navigateToPage("/planningdetails.fxml"));
    }

    // Méthode pour créer une tuile pour chaque planning
    private StackPane createPlanningTile(Planning planning) {
        // Créer un rectangle pour la tuile avec une couleur beige
        Rectangle tile = new Rectangle(TILE_SIZE, TILE_SIZE);
        tile.setFill(Color.rgb(210, 180, 140));  // Beige
        tile.setArcWidth(15);  // Coins arrondis
        tile.setArcHeight(15);
        tile.setStroke(Color.DARKBLUE);  // Bordure sombre
        tile.setStrokeWidth(2);

        // Créer un label pour afficher le type d'activité et la date
        String labelText = planning.getType_activite() + "\n" +
                planning.getDate_planning() + "\n" +
                "Statut: " + planning.getStatus(); // Ajouter le statut au label
        Label planningLabel = new Label(labelText);
        planningLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        planningLabel.setTextFill(Color.WHITE);

        // Créer un StackPane pour contenir la tuile et le label
        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(TILE_SIZE, TILE_SIZE);
        stackPane.getChildren().addAll(tile, planningLabel);

        // Ajouter un gestionnaire d'événements lorsque la tuile est cliquée
        stackPane.setOnMouseClicked(event -> handleTileClick(event, planning));

        return stackPane;
    }

    // Méthode pour gérer l'événement de clic sur une tuile de planning
    private void handleTileClick(MouseEvent event, Planning planning) {
        System.out.println("Planning sélectionné : " + planning.getType_activite() + " - " + planning.getDate_planning());

        // Mettre à jour le label 'courslabel' avec les informations du planning
        String details = "Type d'activité: " + planning.getType_activite() + "\n" +
                "Date: " + planning.getDate_planning() + "\n" +
                "Statut: " + planning.getStatus() + "\n" +
                "ID du Cours: " + planning.getCours();
        courslabel.setText(details);
    }

    // Méthode pour naviguer vers une autre interface (page FXML)
    private void navigateToPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Vérifie si la scène et le stage existent
            Stage stage = (Stage) planningContainer.getScene().getWindow();
            if (stage != null) {
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } else {
                showErrorDialog("Erreur", "Stage actuel est introuvable.", null);
            }
        } catch (IOException e) {
            showErrorDialog("Erreur", "Impossible de charger la page.", e);
        }
    }

    // Méthode pour afficher un message d'erreur dans une boîte de dialogue
    private void showErrorDialog(String title, String message, Exception e) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.setContentText(e != null ? e.getMessage() : "");

        alert.showAndWait();
    }
}


