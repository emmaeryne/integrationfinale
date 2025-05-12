package edu.emmapi.services.service;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherService {
    private static final String WEATHER_API_KEY = "7a41789a616812043fb138c81cc893aa";
    private static final String WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private final HttpClient httpClient;

    public WeatherService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public JSONObject getWeatherData(String city) throws Exception {
        String url = String.format("%s?q=%s&appid=%s&units=metric&lang=fr", WEATHER_BASE_URL, city, WEATHER_API_KEY);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return new JSONObject(response.body());
        } else {
            System.err.println("Weather API error: " + response.body());
            return null;
        }
    }
}