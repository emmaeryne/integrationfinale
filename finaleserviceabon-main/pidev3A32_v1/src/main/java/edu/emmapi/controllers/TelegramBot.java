package edu.emmapi.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;

public class TelegramBot {

    private static final String BOT_TOKEN = "8043149501:AAFXgXaVrIBBR1OjPkkTU-qHQeVjnPF1bn4"; // Remplacez par votre token
    private static final String CHAT_ID = "ID_DU_CHAT"; // Remplacez par l'ID du chat

    public static void main(String[] args) {
        sendMessage("Bonjour, ceci est un message de test !");
    }

    public static void sendMessage(String message) {
        OkHttpClient client = new OkHttpClient();

        // URL de l'API pour envoyer un message
        String url = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";

        // Corps de la requête (format JSON)
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("chat_id", CHAT_ID);
        jsonBody.addProperty("text", message);

        RequestBody body = RequestBody.create(
                jsonBody.toString(),
                MediaType.parse("application/json")
        );

        // Créer la requête HTTP
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        // Envoyer la requête et traiter la réponse
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                System.out.println("Message envoyé : " + jsonResponse);
            } else {
                System.out.println("Erreur : " + response.code() + " - " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}