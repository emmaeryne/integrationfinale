package edu.emmapi.services;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class OpenAIService {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = System.getenv("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"); // Sécurisé !

    public static String getAIResponse(String userMessage) {
        if (API_KEY == null) {
            return "Erreur : Clé API non trouvée !";
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(API_URL);
            request.setHeader("Authorization", "Bearer " + API_KEY);
            request.setHeader("Content-Type", "application/json");

            String jsonBody = "{"
                    + "\"model\": \"gpt-4o-mini\","
                    + "\"messages\": [{\"role\": \"user\", \"content\": \"" + userMessage + "\"}],"
                    + "\"temperature\": 0.7"
                    + "}";

            request.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonResponse = objectMapper.readTree(response.getEntity().getContent());
                return jsonResponse.get("choices").get(0).get("message").get("content").asText();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Erreur lors de la requête API.";
        }
    }
}
