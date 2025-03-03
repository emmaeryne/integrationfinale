package edu.emmapi.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import edu.emmapi.entities.Service;
import edu.emmapi.services.ServiceService;
import edu.emmapi.services.WeatherService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.util.StringConverter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ClientServiceController {
    @FXML
    private ComboBox<Service> serviceComboBox;
    @FXML
    private VBox serviceDetailsCard;
    @FXML
    private Label serviceName;
    @FXML
    private Label servicePrice;
    @FXML
    private Label serviceLevel;
    @FXML
    private Label serviceDuration;
    @FXML
    private Slider ratingSlider;
    @FXML
    private Label reservationCountLabel;
    @FXML
    private Label weatherLabel;
    @FXML
    private ImageView weatherIcon;
    @FXML
    private WebView gifWebView; // WebView pour afficher le GIF

    private ServiceService serviceService;
    private WeatherService weatherService;
    private int reservationCount = 0;
    private static final String ACCOUNT_SID = "AC72a56f6ddc07c2a3c71a36fe465d8748";
    private static final String AUTH_TOKEN = "6741b36f1b9c2b5e87f0d2315c9fd14e";
    private static final String TWILIO_NUMBER = "+15102889830";
    private GiphyApi giphyApi;

    @FXML
    public void initialize() {
        serviceService = new ServiceService();
        weatherService = new WeatherService();
        giphyApi = new GiphyApi();
        loadServices();
        configureComboBox();
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        // Configurer les logs du WebView pour le débogage
        gifWebView.getEngine().setOnError(event -> {
            System.err.println("Erreur lors du chargement du GIF dans le WebView: " + event.getMessage());
        });

        gifWebView.getEngine().setOnAlert(event -> {
            System.out.println("Alerte du WebView: " + event.getData());
        });
    }

    private void loadServices() {
        ObservableList<Service> services = FXCollections.observableArrayList(serviceService.getAllServices());
        serviceComboBox.setItems(services);
    }

    private void configureComboBox() {
        serviceComboBox.setConverter(new StringConverter<Service>() {
            @Override
            public String toString(Service service) {
                if (service == null) {
                    return null;
                } else {
                    return service.getNom();
                }
            }

            @Override
            public Service fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    private void handleServiceSelection() {
        Service selectedService = serviceComboBox.getValue();
        if (selectedService != null) {
            displayServiceDetails(selectedService);
            serviceDetailsCard.setVisible(true);
            loadGifForService(selectedService.getNom()); // Charge le GIF correspondant au service
        } else {
            serviceDetailsCard.setVisible(false);
        }
    }

    private void loadGifForService(String serviceName) {
        new Thread(() -> {
            try {
                String gifUrl = giphyApi.searchGif(serviceName.toLowerCase().trim());
                Platform.runLater(() -> {
                    System.out.println("GIF URL: " + gifUrl); // Affiche l'URL du GIF dans la console
                    if (gifUrl != null && !gifUrl.isEmpty()) {
                        gifWebView.getEngine().load(gifUrl); // Charge l'URL dans le WebView
                    } else {
                        // Charger un GIF par défaut si l'URL est vide ou null
                        gifWebView.getEngine().load("https://media.giphy.com/media/xT9DPFwYqUqK02v7hC/giphy.gif");
                        System.out.println("GIF par défaut chargé.");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    // En cas d'erreur, charger un GIF par défaut
                    gifWebView.getEngine().load("https://media.giphy.com/media/xT9DPFwYqUqK02v7hC/giphy.gif");
                    System.err.println("Erreur lors du chargement du GIF: " + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void displayServiceDetails(Service service) {
        serviceName.setText(service.getNom());
        servicePrice.setText("Prix: " + service.getPrix());
        serviceLevel.setText("Niveau: " + service.getNiveauDifficulte());
        serviceDuration.setText("Durée: " + service.getdureeMinutes() + " minutes");
    }

    @FXML
    private void handleReserve() {
        Service selectedService = serviceComboBox.getValue();
        if (selectedService != null) {
            reservationCount++;
            reservationCountLabel.setText("Réservations: " + reservationCount);
            double rating = ratingSlider.getValue();
            showNotification("Service réservé avec succès! Note: " + rating, "success");
            sendConfirmationSMS(selectedService, rating);

        } else {
            showNotification("Veuillez sélectionner un service.", "error");
        }
    }

    @FXML
    private void handleGetWeather() {
        try {
            JSONObject weatherData = weatherService.getWeatherData("Tunis");
            if (weatherData != null) {
                processWeatherData(weatherData);
            } else {
                showNotification("Impossible d'obtenir les données météorologiques.", "error");
            }
        } catch (IOException | JSONException e) {
            showNotification("Erreur lors de la récupération des données météorologiques: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    private void processWeatherData(JSONObject weatherData) throws JSONException {
        double temperature = weatherData.getJSONObject("main").getDouble("temp");
        String conditions = weatherData.getJSONArray("weather").getJSONObject(0).getString("description");
        String iconCode = weatherData.getJSONArray("weather").getJSONObject(0).getString("icon");

        weatherLabel.setText("Température: " + temperature + "°C, Conditions: " + conditions);
        loadWeatherIcon(iconCode);
    }

    private void loadWeatherIcon(String iconCode) {
        String iconUrl = "http://openweathermap.org/img/w/" + iconCode + ".png";
        try {
            Image image = new Image(iconUrl);
            weatherIcon.setImage(image);
        } catch (Exception e) {
            weatherIcon.setImage(null);
            System.err.println("Erreur sur le chargement de l'icône météo : " + e.getMessage());
        }
    }

    private void sendConfirmationSMS(Service service, double rating) {
        String clientNumber = "+21622820210";
        String message = "Votre réservation pour " + service.getNom() + " a été confirmée. ";

        try {
            Message twilioMessage = Message.creator(
                            new PhoneNumber(clientNumber),
                            new PhoneNumber(TWILIO_NUMBER),
                            message)
                    .create();

            System.out.println("SMS envoyé avec succès (Twilio SID: " + twilioMessage.getSid() + ")");
            showNotification("SMS de confirmation envoyé.", "info");

        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi du SMS (Twilio) : " + e.getMessage());
            showNotification("Erreur lors de l'envoi du SMS.", "error");
            e.printStackTrace();
        }
    }

    private void showNotification(String message, String type) {
        System.out.println(message);
    }
}