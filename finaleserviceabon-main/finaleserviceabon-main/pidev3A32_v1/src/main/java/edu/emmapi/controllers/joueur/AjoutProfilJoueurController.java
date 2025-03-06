package edu.emmapi.controllers.joueur;

import edu.emmapi.entities.SessionManager;
import edu.emmapi.entities.joueur.Joueur;
import edu.emmapi.entities.user;
import edu.emmapi.services.joueur.JoueurService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class AjoutProfilJoueurController {

    @FXML
    private TextField cin;

    @FXML
    private TextField lien_photo;

    @FXML
    private ImageView photo_view;

    @FXML
    private Label titre;

    JoueurService joueurService = new JoueurService();

    user user = SessionManager.getInstance().getUser();


    @FXML
    void updatePhoto(KeyEvent event) {
        photo_view.setImage(new Image(lien_photo.getText()));
        System.out.println(lien_photo.getText());
    }

    @FXML
    void Annuler(ActionEvent event) {

    }

    @FXML
    void Inscrire(ActionEvent event) {
        String imagePath = lien_photo.getText();
        if (imagePath == null || imagePath.isEmpty()) {
            showAlert("Error", "Please enter a valid image path.");
            return;
        }

        boolean hasFace = detectFaceWithPython(imagePath);
        if (hasFace) {
            if(cin.getText().isEmpty()){
                showAlert("Erreur", "Donner votre cin");
            }
            else {
                joueurService.ajoutJoueurClient(new Joueur(user.getUsername(), 0, Integer.parseInt(cin.getText()), imagePath));
            }
        } else {
            showAlert("Error", "No face detected! Please upload a clear photo of a person.");
        }
    }

    private boolean detectFaceWithPython(String imagePath) {
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "src/main/resources/python/facial_recognition.py", imagePath);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String output = reader.readLine();
            return "1".equals(output);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    public void initialize(){
        System.out.println(user);
        titre.setText("Bienvenue " + user.getUsername());
    }
}


