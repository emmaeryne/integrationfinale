package edu.emmapi.services.service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GiphyService {
    private static final String GIPHY_API_KEY = "pHZLzVsG2AGE9SUHXXa0bd8dnrhGoPcB";
    private static final String GIPHY_BASE_URL = "https://api.giphy.com/v1/gifs/search";
    private final HttpClient httpClient;
    private static final String DEFAULT_GIF = "https://media.giphy.com/media/3oEjI6SIIHBdRxXI40/giphy.gif";

    public GiphyService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public String searchGif(String query) {
        try {
            String url = String.format("%s?api_key=%s&q=%s&limit=1&rating=g&lang=en", GIPHY_BASE_URL, GIPHY_API_KEY, query);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                JSONArray data = json.getJSONArray("data");
                if (!data.isEmpty()) {
                    return data.getJSONObject(0).getJSONObject("images").getJSONObject("original").getString("url");
                }
            }
        } catch (Exception e) {
            System.err.println("Giphy API error: " + e.getMessage());
        }
        return DEFAULT_GIF;
    }
}