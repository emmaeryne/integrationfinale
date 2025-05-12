

package edu.emmapi.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainClass extends Application {

    public static void main(String[] args) {
        // Lance l'application JavaFX
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Charger la première vue (homepage.fxml)
            FXMLLoader homepageLoader = new FXMLLoader(getClass().getResource("/AbonnementView.fxml"));
            Parent homepageRoot = homepageLoader.load();
            Scene homepageScene = new Scene(homepageRoot);

            // Charger la vue météo (weather.fxml)
            FXMLLoader weatherLoader = new FXMLLoader(getClass().getResource("/fxml/weather.fxml"));
            Parent weatherRoot = weatherLoader.load();
            Scene weatherScene = new Scene(weatherRoot, 400, 200);

            // Charger la vue IA (ai.fxml)
            FXMLLoader aiLoader = new FXMLLoader(getClass().getResource("/fxml/ai.fxml"));
            Parent aiRoot = aiLoader.load();
            Scene aiScene = new Scene(aiRoot, 400, 200);

            // Configurer la fenêtre principale avec la première vue
            primaryStage.setTitle("Application avec API et IA");
            primaryStage.setScene(homepageScene);
            primaryStage.setResizable(false); // Empêcher le redimensionnement de la fenêtre (optionnel)
            primaryStage.show();

            // Ajouter un bouton pour basculer entre les vues
            Button switchToWeatherButton = new Button("Voir la météo");
            switchToWeatherButton.setOnAction(e -> primaryStage.setScene(weatherScene));

            Button switchToAIButton = new Button("Voir l'IA");
            switchToAIButton.setOnAction(e -> primaryStage.setScene(aiScene));

            Button switchToHomepageButton = new Button("Retour à l'accueil");
            switchToHomepageButton.setOnAction(e -> primaryStage.setScene(homepageScene));

            // Ajouter les boutons à la première vue
            ((VBox) homepageRoot).getChildren().addAll(switchToWeatherButton, switchToAIButton);

            // Ajouter un bouton de retour à l'accueil dans les autres vues
            ((VBox) weatherRoot).getChildren().add(switchToHomepageButton);
            ((VBox) aiRoot).getChildren().add(switchToHomepageButton);

        } catch (IOException e) {
            // Gestion des erreurs de chargement du FXML
            System.err.println("Erreur lors du chargement du fichier FXML : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
