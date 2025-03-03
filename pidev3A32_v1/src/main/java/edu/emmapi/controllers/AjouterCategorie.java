package edu.emmapi.controllers;

import edu.emmapi.entities.categorieproduit;
import edu.emmapi.services.ProduitCategorie;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class AjouterCategorie {

    @FXML private TextField txtNomCategorie;
    @FXML private TextField txtDescriptionCategorie;
    @FXML private ImageView imageViewCategorie;
    @FXML private Button btnChoisirImage;
    @FXML private Button btnGenererDescription;
    @FXML private Button btnAjouterCategorie;
    @FXML private Button btnRetour;
    @FXML private ComboBox<String> comboLangue;  // Pour choisir la langue de traduction

    private ProduitCategorie categorieService = new ProduitCategorie();
    private File selectedFile = null;

    // Pour stocker la description générée par le service IA (par exemple, en anglais)
    private String descriptionIA = "";

    @FXML
    private void initialize() {
        // Au démarrage, seule la saisie du nom est active.
        // Les zones et boutons liés à l'image et à la description restent désactivés.
        txtDescriptionCategorie.setDisable(true);
        btnAjouterCategorie.setDisable(true);
        btnGenererDescription.setDisable(true);
        comboLangue.setDisable(true);
        comboLangue.setItems(FXCollections.observableArrayList("fr", "ar", "es", "de", "it"));
        comboLangue.setValue("fr"); // Traduction par défaut en français
    }

    // ================================
    // Bouton Choisir Image
    // ================================
    @FXML
    private void choisirImage() {
        String categoryName = txtNomCategorie.getText().trim();
        if (categoryName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez saisir le nom de la catégorie avant de choisir une image.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", ".png", ".jpg", "*.jpeg")
        );
        selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            imageViewCategorie.setImage(new Image(selectedFile.toURI().toString()));
            // Vérification automatique de la correspondance entre le nom et l'image
            Task<Boolean> task = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return verifierImage(selectedFile, categoryName);
                }
            };
            task.setOnSucceeded(evt -> {
                boolean match = task.getValue();
                if (!match) {
                    showAlert(Alert.AlertType.ERROR, "Erreur de Correspondance", "L'image ne correspond pas au nom de la catégorie.");
                    // Réinitialisation : suppression de l'image affichée et désactivation des boutons associés
                    imageViewCategorie.setImage(null);
                    selectedFile = null;
                    txtDescriptionCategorie.clear();
                    txtDescriptionCategorie.setDisable(true);
                    btnGenererDescription.setDisable(true);
                    btnAjouterCategorie.setDisable(true);
                    comboLangue.setDisable(true);
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Correspondance", "L'image correspond au nom de la catégorie.");
                    // On active uniquement le bouton générer description et la sélection de langue.
                    // Le bouton ajouter restera désactivé tant que la description n'est pas générée.
                    txtDescriptionCategorie.setDisable(true); // On génèrera la description automatiquement
                    btnGenererDescription.setDisable(false);
                    btnAjouterCategorie.setDisable(true);
                    comboLangue.setDisable(false);
                }
            });
            task.setOnFailed(evt -> {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de vérifier la correspondance : " + task.getException().getMessage());
            });
            new Thread(task).start();
        }
    }

    // ================================
    // Bouton Générer Description et Traduire
    // ================================
    @FXML
    private void genererDescription() {
        String nom = txtNomCategorie.getText().trim();
        if (nom.isEmpty() || selectedFile == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez saisir un nom et choisir une image.");
            return;
        }
        // Récupérer la langue cible depuis le ComboBox
        String langueCible = comboLangue.getValue();

        // Tâche en arrière-plan pour générer la description via l'API IA
        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                return genererDescriptionIA(selectedFile, nom);
            }
        };

        task.setOnSucceeded(evt -> {
            descriptionIA = task.getValue();
            if (descriptionIA.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur IA", "Impossible de générer la description.");
                return;
            }
            // Traduire la description (de l'anglais vers la langue cible)
            String traduction;
            try {
                traduction = traduireDescription(descriptionIA, "en", langueCible);
            } catch (IOException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur Traduction", "Échec de la traduction : " + ex.getMessage());
                return;
            }
            txtDescriptionCategorie.setText(traduction);
            showAlert(Alert.AlertType.INFORMATION, "OK", "Description générée et traduite !");
            // Une fois la description générée, on active le bouton pour ajouter la catégorie
            btnAjouterCategorie.setDisable(false);
        });

        task.setOnFailed(evt -> {
            showAlert(Alert.AlertType.ERROR, "Erreur IA", "Échec de génération : " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    // ================================
    // Bouton Ajouter Catégorie
    // ================================
    @FXML
    private void enregistrerCategorie() {
        String nom = txtNomCategorie.getText().trim();
        String description = txtDescriptionCategorie.getText().trim();

        if (nom.isEmpty() || selectedFile == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Nom et image obligatoires.");
            return;
        }

        byte[] imageData;
        try {
            imageData = Files.readAllBytes(Path.of(selectedFile.getAbsolutePath()));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de lire l'image : " + e.getMessage());
            return;
        }

        categorieproduit cat = new categorieproduit(nom, description, imageData);
        categorieService.ajouterCategorieProduit(cat);

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Catégorie ajoutée avec succès !");
        ouvrirPage2();
        // Fermer la fenêtre actuelle
        fermerFenetre();
    }

    // ================================
    // Bouton Retour
    // ================================
    @FXML
    private void retourPage2() {
        fermerFenetre();
        ouvrirPage2();
    }
    @FXML
    private void fermerFenetre() {
        Stage stage = (Stage) btnAjouterCategorie.getScene().getWindow();
        stage.close();
    }

    private void ouvrirPage2() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Page2.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gestion des Catégories");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================================
    // Appel IA pour générer la description (en anglais par exemple)
    // ================================
    private String genererDescriptionIA(File imageFile, String nomCategorie) throws IOException {
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("imageBase64", base64Image);
        jsonBody.put("nomCategorie", nomCategorie);

        URL url = new URL("http://localhost:5001/generate_caption");  // Service IA lancé sur le port 5001
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.toString().getBytes("UTF-8"));
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }
            JSONObject resp = new JSONObject(sb.toString());
            return resp.optString("description", "");
        } else {
            throw new IOException("Réponse non OK du serveur IA : " + responseCode);
        }
    }

    // ================================
    // Appel d'un service de traduction
    // ================================
    private String traduireDescription(String texte, String langueSource, String langueCible) throws IOException {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("texte", texte);
        jsonBody.put("source", langueSource);
        jsonBody.put("cible", langueCible);

        URL url = new URL("http://localhost:5002/translate");  // Service de traduction lancé sur le port 5002
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.toString().getBytes("UTF-8"));
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }
            JSONObject resp = new JSONObject(sb.toString());
            return resp.optString("traduction", "");
        } else {
            throw new IOException("Réponse non OK du serveur de traduction : " + responseCode);
        }
    }

    // ================================
    // Méthode de vérification automatique image/texte via un service Flask
    // ================================
    private boolean verifierImage(File imageFile, String text) throws IOException {
        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        URL url = new URL("http://127.0.0.1:5000/predict");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (OutputStream outputStream = conn.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true)) {

            // Partie texte
            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"text\"\r\n\r\n");
            writer.append(text).append("\r\n");
            writer.flush();

            // Partie fichier
            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"image\"; filename=\"" + imageFile.getName() + "\"\r\n");
            writer.append("Content-Type: application/octet-stream\r\n\r\n");
            writer.flush();

            // Écriture du contenu binaire
            FileInputStream inputStream = new FileInputStream(imageFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();

            writer.append("\r\n").flush();
            writer.append("--").append(boundary).append("--\r\n");
            writer.close();
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                JSONObject json = new JSONObject(response.toString());
                return json.optBoolean("match", false);
            }
        } else {
            throw new IOException("Le serveur a renvoyé un code: " + responseCode);
        }
    }

    // ================================
    // Méthode utilitaire pour afficher des alertes
    // ================================
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}