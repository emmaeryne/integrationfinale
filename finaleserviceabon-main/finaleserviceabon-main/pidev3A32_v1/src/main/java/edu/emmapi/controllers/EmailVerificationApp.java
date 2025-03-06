package edu.emmapi.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class EmailVerificationApp extends Application {

    // Replace with your API key (or use an environment variable)
    private static final String HUNTER_API_KEY = "51ebed6f136f3456dc8bb794c07d8270788706fa";
    private static final String HUNTER_API_URL = "https://api.hunter.io/v2/email-verifier";

    @Override
    public void start(Stage primaryStage) {
        TextField emailField = new TextField();
        Button verifyButton = new Button("Vérifier l'email");
        Label resultLabel = new Label();

        verifyButton.setOnAction(event -> {
            String email = emailField.getText();
            boolean isValid = verifyEmail(email);
            if (isValid) {
                resultLabel.setText("L'email est valide.");
                resultLabel.setStyle("-fx-text-fill: green");
            } else {
                resultLabel.setText("L'email est invalide ou n'existe pas.");
                resultLabel.setStyle("-fx-text-fill: red");
            }
        });

        VBox root = new VBox(10, emailField, verifyButton, resultLabel);
        Scene scene = new Scene(root, 300, 200);

        primaryStage.setTitle("Vérification d'email");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private boolean verifyEmail(String email) {
        OkHttpClient client = new OkHttpClient();

        // Construct the URL for the request
        HttpUrl.Builder urlBuilder = HttpUrl.parse(HUNTER_API_URL).newBuilder();
        urlBuilder.addQueryParameter("email", email);
        urlBuilder.addQueryParameter("api_key", HUNTER_API_KEY);
        String url = urlBuilder.build().toString();

        // Create the HTTP request
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // Parse the response JSON
                String responseBody = response.body().string();
                System.out.println("Réponse de l'API : " + responseBody); // Debugging

                // Parse the response to get the status
                JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();

                // Check if the expected fields exist in the response
                if (jsonResponse.has("data") && jsonResponse.getAsJsonObject("data").has("status")) {
                    String status = jsonResponse.getAsJsonObject("data").get("status").getAsString();

                    // Check if the email is deliverable
                    return "valid".equals(status); // Now using 'status' instead of 'result'
                } else {
                    System.out.println("Erreur dans la réponse de l'API: le champ 'status' est manquant.");
                    return false;
                }
            } else {
                // Handle error responses from the API
                System.out.println("Erreur de l'API : " + response.code() + " - " + response.message());
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
