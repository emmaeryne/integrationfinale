package edu.emmapi.controllers;

import edu.emmapi.entities.Owner;
import edu.emmapi.entities.user;
import edu.emmapi.services.OwnerService;
import edu.emmapi.services.userService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class afficherOwners {

    @FXML
    private AnchorPane listeView;

    @FXML
    private FlowPane cardContainer;

    @FXML
    private TextField searchField;

    @FXML
    private Label errorLabel;

    private final userService userService = new userService();
    private final OwnerService ownerService = new OwnerService();
    private ObservableList<user> usersList;
    private FilteredList<user> filteredUsers;

    @FXML
    public void initialize() {
        cardContainer.setHgap(10); // Espacement horizontal entre les cartes
        cardContainer.setVgap(10); // Espacement vertical entre les cartes
        cardContainer.setStyle("-fx-padding: 10px;");

        loadOwners();
        setupSearch();
    }

    private void loadOwners() {
        System.out.println("Chargement des propriétaires...");
        List<Owner> users = ownerService.getAllData(); // Récupérer les données à jour
        System.out.println("Nombre de propriétaires récupérés : " + users.size());

        usersList = FXCollections.observableArrayList(users);
        filteredUsers = new FilteredList<>(usersList, p -> true);

        cardContainer.getChildren().clear(); // Effacer les cartes existantes
        System.out.println("Cartes existantes effacées.");

        for (user user : filteredUsers) {
            UserCard card = new UserCard(user); // Recréer les cartes
            cardContainer.getChildren().add(card);
        }
        System.out.println("Nouvelles cartes ajoutées.");
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredUsers.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Afficher tous les utilisateurs si la recherche est vide
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return user.getUsername().toLowerCase().contains(lowerCaseFilter);
            });

            cardContainer.getChildren().clear();
            for (user user : filteredUsers) {
                UserCard card = new UserCard(user);
                cardContainer.getChildren().add(card);
            }
        });
    }

    @FXML
    private void handleSearch() {
        // La recherche est déjà gérée par le listener sur searchField
    }

    @FXML
    private void toListeClients(javafx.event.ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/AfficherClients.fxml"));
        Scene currentScene = listeView.getScene();
        currentScene.setRoot(root);

        Stage stage = (Stage) currentScene.getWindow();
        stage.setWidth(820);
        stage.setHeight(750);
        stage.setResizable(false);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private class UserCard extends VBox {

        private final user user;

        public UserCard(user user) {
            this.user = user;

            // Style de la carte
            this.setSpacing(10);
            this.setStyle("-fx-background-color: #ffffff; -fx-padding: 15px; -fx-border-color: #cccccc; -fx-border-radius: 5px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

            // Placeholder pour l'image (peut être remplacé par une vraie image)
            Rectangle imagePlaceholder = new Rectangle(100, 100, Color.LIGHTGRAY);
            imagePlaceholder.setArcWidth(10);
            imagePlaceholder.setArcHeight(10);

            // Labels pour afficher les informations de l'utilisateur
            Label nameLabel = new Label("Username: " + user.getUsername());
            Label emailLabel = new Label("Email: " + user.getEmail());
            Label roleLabel = new Label("Role: " + user.getRole());

            // Bouton pour supprimer l'utilisateur
            Button deleteButton = new Button("Supprimer");
            deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
            deleteButton.setOnAction(event -> handleDelete(user));

            // Conteneur pour les boutons
            HBox buttonContainer = new HBox(10);
            buttonContainer.getChildren().addAll(deleteButton);

            // Ajout des éléments à la carte
            this.getChildren().addAll(imagePlaceholder, nameLabel, emailLabel, roleLabel, buttonContainer);
        }

        private void handleDelete(user user) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation");
            confirmationAlert.setHeaderText("Supprimer l'utilisateur ?");
            confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                System.out.println("Suppression de l'utilisateur : " + user.getUsername());
                userService.deleteEntity(user); // Supprimer l'utilisateur
                System.out.println("Utilisateur supprimé avec succès.");
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur supprimé avec succès.");
                Platform.runLater(() -> loadOwners()); // Recharger la liste des utilisateurs dans le thread JavaFX
            }
        }
    }
}