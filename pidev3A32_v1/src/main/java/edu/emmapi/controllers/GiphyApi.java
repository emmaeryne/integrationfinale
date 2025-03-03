package edu.emmapi.controllers;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

public class GiphyApi {
    private static final String API_KEY = "pHZLzVsG2AGE9SUHXXa0bd8dnrhGoPcB"; // Remplacez par votre clé API Giphy
    private static final String BASE_URL = "https://api.giphy.com/v1/gifs/search";

    public String searchGif(String query) throws Exception {
        OkHttpClient client = new OkHttpClient();
        String url = BASE_URL + "?api_key=" + API_KEY + "&q=" + query + "&limit=1";

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("Erreur lors de la requête à l'API Giphy: " + response.code());
            }

            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);

            if (jsonResponse.getJSONArray("data").length() > 0) {
                return jsonResponse.getJSONArray("data")
                        .getJSONObject(0)
                        .getJSONObject("images")
                        .getJSONObject("original")
                        .getString("url");
            } else {
                return null; // Aucun GIF trouvé
            }
        }
    }
}