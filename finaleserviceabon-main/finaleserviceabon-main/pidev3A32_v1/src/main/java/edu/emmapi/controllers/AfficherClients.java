package edu.emmapi.controllers;

import edu.emmapi.entities.user;
import edu.emmapi.services.userService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
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

public class AfficherClients {

    @FXML
    private AnchorPane listeView;

    @FXML
    private FlowPane cardContainer;

    @FXML
    private TextField searchField;

    private final userService userService = new userService();
    private ObservableList<user> usersList;
    private FilteredList<user> filteredUsers;

    @FXML
    public void initialize() {
        cardContainer.setHgap(10); // Espacement horizontal entre les cartes
        cardContainer.setVgap(10); // Espacement vertical entre les cartes
        cardContainer.setStyle("-fx-padding: 10px;");

        loadClients();
        setupSearch();
    }

    private void loadClients() {
        List<user> users = userService.getAllData();
        usersList = FXCollections.observableArrayList(users);
        filteredUsers = new FilteredList<>(usersList, p -> true);

        cardContainer.getChildren().clear(); // Effacer les cartes existantes

        for (user user : filteredUsers) {
            UserCard card = new UserCard(user);
            cardContainer.getChildren().add(card);
        }
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
    private void toListeProprietaire(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/AfficherOwners.fxml"));
        Scene currentScene = searchField.getScene();
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

    public void ToListCoach(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/afficherCoach.fxml"));
        Scene currentScene = searchField.getScene();
        currentScene.setRoot(root);

        Stage stage = (Stage) currentScene.getWindow();
        stage.setWidth(820);
        stage.setHeight(750);
        stage.setResizable(false);
    }

    public void GoToHomePage(MouseEvent mouseEvent) {
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
            deleteButton.setOnAction(event -> {
                try {
                    handleDelete(user);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            // Conteneur pour les boutons
            HBox buttonContainer = new HBox(10);
            buttonContainer.getChildren().addAll(deleteButton);

            // Ajout des éléments à la carte
            this.getChildren().addAll(imagePlaceholder, nameLabel, emailLabel, roleLabel, buttonContainer);
        }

        private void handleDelete(user user) throws IOException {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation");
            confirmationAlert.setHeaderText("Supprimer l'utilisateur ?");
            confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                userService.deleteEntity(user);
                Parent root = FXMLLoader.load(getClass().getResource("/AfficherClients.fxml"));

                Scene currentScene = searchField.getScene();

                currentScene.setRoot(root);

                Stage stage = (Stage) currentScene.getWindow();
                stage.setWidth(1000);
                stage.setHeight(600);  //
                stage.setResizable(false);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur supprimé avec succès.");
                loadClients(); // Recharger la liste des utilisateurs
            }
        }
    }
}