

package edu.emmapi.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class mainFx extends Application {

    public static void main(String[] args) {
        // Lance l'application JavaFX
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Charger le fichier FXML de la première page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AbonnementView.fxml")); // Chemin corrigé
            Parent root = loader.load();


            // Créer une scène avec le contenu chargé
            Scene scene = new Scene(root);

            // Configurer la fenêtre principale
            primaryStage.setTitle("hive");  // Titre de la fenêtre
            primaryStage.setScene(scene);              // Associer la scène à la fenêtre
            primaryStage.setResizable(false);          // Empêcher le redimensionnement de la fenêtre (optionnel)
            primaryStage.show();                       // Afficher la fenêtre
        } catch (IOException e) {
            // Gestion des erreurs de chargement du FXML
            System.err.println("Erreur lors du chargement du fichier FXML : " + e.getMessage());
            e.printStackTrace();
        }
    }
}