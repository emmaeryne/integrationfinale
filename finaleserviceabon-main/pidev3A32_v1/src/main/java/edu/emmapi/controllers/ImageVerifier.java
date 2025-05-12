package edu.emmapi.controllers;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ImageVerifier {

    private static final String FLASK_API_URL = "http://127.0.0.1:5000/predict"; // URL de l'API Flask

    public static boolean verifierImage(File file, String nomCategorie) {
        if (file == null || !file.exists() || !file.isFile()) {
            afficherAlerte("⚠️ Fichier invalide !");
            return false;
        }

        try {
            boolean match = envoyerImageAuServeur(file, nomCategorie);

            if (match) {
                afficherAlerte("✅ Image validée !");
            } else {
                afficherAlerte("❌ L'image ne correspond pas à la catégorie !");
            }

            return match;

        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte("⚠️ Erreur lors de la vérification de l'image.");
            return false;
        }
    }

    private static boolean envoyerImageAuServeur(File file, String nomCategorie) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Détection automatique du type MIME
        String mimeType = Files.probeContentType(file.toPath());
        if (mimeType == null || (!mimeType.startsWith("image/"))) {
            System.err.println("❌ Format d'image non supporté !");
            return false;
        }

        // Construire la requête avec l'image et le texte
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", file.getName(),
                        RequestBody.create(file, MediaType.parse(mimeType))) // Détection automatique du format
                .addFormDataPart("text", nomCategorie)
                .build();

        Request request = new Request.Builder()
                .url(FLASK_API_URL)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("❌ Erreur serveur: " + response.code());
                return false;
            }

            String responseBody = response.body() != null ? response.body().string() : "";
            System.out.println("🔍 Réponse du serveur: " + responseBody);

            return responseBody.contains("\"match\": true"); // Vérifie si la correspondance est trouvée
        }
    }

    private static void afficherAlerte(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Vérification d'image");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
