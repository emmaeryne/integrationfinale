/*package edu.emmapi.controllers;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ClientServiceController {
    @FXML private ComboBox<Service> serviceComboBox;
    @FXML private VBox serviceDetailsCard;
    @FXML private Label serviceName;
    @FXML private Label servicePrice;
    @FXML private Label serviceLevel;
    @FXML private Label serviceDuration;
    @FXML private Slider ratingSlider;
    @FXML private Label reservationCountLabel;
    @FXML private Label weatherLabel;
    @FXML private ImageView weatherIcon;
    @FXML private WebView gifWebView;
    @FXML private Label serviceImageLabel; // New label for image URL/path

    private ServiceService serviceService;
    private WeatherService weatherService;
    private static final String ACCOUNT_SID = "AC9ee4ac8e139bf2eebe288428e78ad967";
    private static final String AUTH_TOKEN = "3bf285de19ceb88b10218de82da7be4e";
    private static final String TWILIO_NUMBER = "+17403325976";
    private GiphyApi giphyApi;

    @FXML
    public void initialize() {
        serviceService = new ServiceService();
        weatherService = new WeatherService();
        giphyApi = new GiphyApi();
        loadServices();
        configureComboBox();
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        gifWebView.getEngine().setOnError(event ->
                System.err.println("Erreur lors du chargement du GIF dans le WebView: " + event.getMessage()));

        gifWebView.getEngine().setOnAlert(event ->
                System.out.println("Alerte du WebView: " + event.getData()));

        serviceDetailsCard.setVisible(false);
    }

    private void loadServices() {
        ObservableList<Service> services = FXCollections.observableArrayList(serviceService.getAllServices());
        serviceComboBox.setItems(services);
    }

    private void configureComboBox() {
        serviceComboBox.setConverter(new StringConverter<Service>() {
            @Override
            public String toString(Service service) {
                return service != null ? service.getNom() : null;
            }

            @Override
            public Service fromString(String string) {
                return null;
            }
        });

        serviceComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) ->
                handleServiceSelection());
    }

    @FXML
    private void handleServiceSelection() {
        Service selectedService = serviceComboBox.getValue();
        if (selectedService != null) {
            displayServiceDetails(selectedService);
            serviceDetailsCard.setVisible(true);
            loadGifForService(selectedService.getNom());
        } else {
            serviceDetailsCard.setVisible(false);
            gifWebView.getEngine().load(null);
        }
    }

    private void loadGifForService(String serviceName) {
        new Thread(() -> {
            try {
                String gifUrl = giphyApi.searchGif(serviceName.toLowerCase().trim());
                Platform.runLater(() -> {
                    System.out.println("GIF URL: " + gifUrl);
                    gifWebView.getEngine().load(gifUrl != null && !gifUrl.isEmpty() ?
                            gifUrl : "https://media.giphy.com/media/xT9DPFwYqUqK02v7hC/giphy.gif");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    gifWebView.getEngine().load("https://media.giphy.com/media/xT9DPFwYqUqK02v7hC/giphy.gif");
                    showNotification("Erreur lors du chargement du GIF: " + e.getMessage(), "error");
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void displayServiceDetails(Service service) {
        serviceName.setText(service.getNom() != null ? service.getNom() : "N/A");
        servicePrice.setText(String.format("Prix: %.2f €", service.getPrix()));
        serviceLevel.setText("Niveau: " + service.getNiveauDifficulte());
        serviceDuration.setText("Durée: " + service.getDureeMinutes() + " minutes");
        reservationCountLabel.setText("Réservations: " + service.getNombreReservations());
        serviceImageLabel.setText("Image: " + (service.getImage() != null ? service.getImage() : "Aucune"));
    }

    @FXML
    private void handleReserve() {
        Service selectedService = serviceComboBox.getValue();
        if (selectedService != null) {
            double rating = ratingSlider.getValue();
            try {
                serviceService.incrementReservationCount(selectedService.getId());
                serviceService.updateServiceNote(selectedService.getId(), rating);
                selectedService.setNombreReservations(selectedService.getNombreReservations() + 1);
                reservationCountLabel.setText("Réservations: " + selectedService.getNombreReservations());

            } catch (Exception e) {
                showNotification("Erreur lors de la réservation: " + e.getMessage(), "error");
                e.printStackTrace();
            }
        } else {
            showNotification("Veuillez sélectionner un service.", "error");
        }
    }

    @FXML
    private void handleGetWeather() {
        new Thread(() -> {
            try {
                JSONObject weatherData = weatherService.getWeatherData("Tunis");
                Platform.runLater(() -> {
                    if (weatherData != null) {
                        try {
                            processWeatherData(weatherData);
                        } catch (JSONException e) {
                            showNotification("Erreur lors du traitement des données météorologiques: " + e.getMessage(), "error");
                            e.printStackTrace();
                        }
                    } else {
                        showNotification("Impossible d'obtenir les données météorologiques.", "error");
                    }
                });
            } catch (IOException | JSONException e) {
                Platform.runLater(() ->
                        showNotification("Erreur lors de la récupération des données météorologiques: " + e.getMessage(), "error"));
                e.printStackTrace();
            }
        }).start();
    }

    private void processWeatherData(JSONObject weatherData) throws JSONException {
        if (!weatherData.has("main") || !weatherData.has("weather")) {
            throw new JSONException("Données météorologiques incomplètes.");
        }

        double temperature = weatherData.getJSONObject("main").getDouble("temp");
        String conditions = weatherData.getJSONArray("weather").getJSONObject(0).getString("description");
        String iconCode = weatherData.getJSONArray("weather").getJSONObject(0).getString("icon");

        weatherLabel.setText(String.format("Température: %.1f°C, Conditions: %s", temperature, conditions));
        loadWeatherIcon(iconCode);
    }

    private void loadWeatherIcon(String iconCode) {
        String iconUrl = "http://openweathermap.org/img/wn/" + iconCode + ".png";
        try {
            Image image = new Image(iconUrl, true);
            weatherIcon.setImage(image);
        } catch (Exception e) {
            weatherIcon.setImage(null);
            showNotification("Erreur lors du chargement de l'icône météo: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    private void sendConfirmationSMS(Service service, double rating) {
        String clientNumber = "+21627417033";
        String message = String.format("Votre réservation pour %s a été confirmée. Note attribuée: %.1f/5. Merci!",
                service.getNom(), rating);

        try {
            Message twilioMessage = Message.creator(
                            new PhoneNumber(clientNumber),
                            new PhoneNumber(TWILIO_NUMBER),
                            message)
                    .create();

            showNotification("SMS de confirmation envoyé (SID: " + twilioMessage.getSid() + ").", "info");
        } catch (Exception e) {
            showNotification("Erreur lors de l'envoi du SMS: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    private void showNotification(String message, String type) {
        Alert alert = new Alert(type.equals("error") ? Alert.AlertType.ERROR :
                type.equals("info") ? Alert.AlertType.INFORMATION : Alert.AlertType.INFORMATION);
        alert.setTitle(type.equals("error") ? "Erreur" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}*/

package edu.emmapi.controllers;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ClientServiceController {
    @FXML private ComboBox<Service> serviceComboBox;
    @FXML private VBox serviceDetailsCard;
    @FXML private Label serviceName;
    @FXML private Label servicePrice;
    @FXML private Label serviceLevel;
    @FXML private Label serviceDuration;
    @FXML private Slider ratingSlider;
    @FXML private Label reservationCountLabel;
    @FXML private Label weatherLabel;
    @FXML private ImageView weatherIcon;
    @FXML private WebView gifWebView;

    private ServiceService serviceService;
    private WeatherService weatherService;
    private static final String ACCOUNT_SID = "AC9ee4ac8e139bf2eebe288428e78ad967";
    private static final String AUTH_TOKEN = "3bf285de19ceb88b10218de82da7be4e";
    private static final String TWILIO_NUMBER = "+17403325976";
    private GiphyApi giphyApi;

    @FXML
    public void initialize() {
        serviceService = new ServiceService();
        weatherService = new WeatherService();
        giphyApi = new GiphyApi();
        loadServices();
        configureComboBox();
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        gifWebView.getEngine().setOnError(event ->
                System.err.println("Error loading GIF in WebView: " + event.getMessage()));

        gifWebView.getEngine().setOnAlert(event ->
                System.out.println("WebView alert: " + event.getData()));

        serviceDetailsCard.setVisible(false);
    }

    private void loadServices() {
        ObservableList<Service> services = FXCollections.observableArrayList(serviceService.getAllServices());
        serviceComboBox.setItems(services);
    }

    private void configureComboBox() {
        serviceComboBox.setConverter(new StringConverter<Service>() {
            @Override
            public String toString(Service service) {
                return service != null ? service.getNom() : "";
            }

            @Override
            public Service fromString(String string) {
                return null;
            }
        });

        serviceComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) ->
                handleServiceSelection());
    }

    @FXML
    private void handleServiceSelection() {
        Service selectedService = serviceComboBox.getValue();
        if (selectedService != null) {
            displayServiceDetails(selectedService);
            serviceDetailsCard.setVisible(true);
            loadGifForService(selectedService.getNom());
        } else {
            serviceDetailsCard.setVisible(false);
            gifWebView.getEngine().load(null);
        }
    }

    private void loadGifForService(String serviceName) {
        new Thread(() -> {
            try {
                String gifUrl = giphyApi.searchGif(serviceName.toLowerCase().trim());
                Platform.runLater(() -> {
                    System.out.println("GIF URL: " + gifUrl);
                    gifWebView.getEngine().load(gifUrl != null && !gifUrl.isEmpty() ?
                            gifUrl : "https://media.giphy.com/media/xT9DPFwYqUqK02v7hC/giphy.gif");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    gifWebView.getEngine().load("https://media.giphy.com/media/xT9DPFwYqUqK02v7hC/giphy.gif");
                    showNotification("Error loading GIF: " + e.getMessage(), "error");
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void displayServiceDetails(Service service) {
        serviceName.setText(service.getNom() != null ? service.getNom() : "N/A");
        servicePrice.setText(String.format("Price: %.2f €", service.getPrix()));
        serviceLevel.setText("Level: " + (service.getNiveauDifficulte() != null ? service.getNiveauDifficulte() : "N/A"));
        serviceDuration.setText("Duration: " + service.getDureeMinutes() + " minutes");
        reservationCountLabel.setText("Reservations: " + service.getNombreReservations());
    }

    @FXML
    private void handleReserve() {
        Service selectedService = serviceComboBox.getValue();
        if (selectedService != null) {
            double rating = ratingSlider.getValue();
            try {
                serviceService.incrementReservationCount(selectedService.getId());
                serviceService.updateServiceNote(selectedService.getId(), rating);
                selectedService.setNombreReservations(selectedService.getNombreReservations() + 1);
                reservationCountLabel.setText("Reservations: " + selectedService.getNombreReservations());
                sendConfirmationSMS(selectedService, rating);
                showNotification("Reservation successful!", "info");
            } catch (Exception e) {
                showNotification("Error during reservation: " + e.getMessage(), "error");
                e.printStackTrace();
            }
        } else {
            showNotification("Please select a service.", "error");
        }
    }

    @FXML
    private void handleGetWeather() {
        new Thread(() -> {
            try {
                JSONObject weatherData = weatherService.getWeatherData("Tunis");
                Platform.runLater(() -> {
                    if (weatherData != null) {
                        try {
                            processWeatherData(weatherData);
                        } catch (JSONException e) {
                            showNotification("Error processing weather data: " + e.getMessage(), "error");
                            e.printStackTrace();
                        }
                    } else {
                        showNotification("Unable to fetch weather data.", "error");
                    }
                });
            } catch (IOException | JSONException e) {
                Platform.runLater(() ->
                        showNotification("Error fetching weather data: " + e.getMessage(), "error"));
                e.printStackTrace();
            }
        }).start();
    }

    private void processWeatherData(JSONObject weatherData) throws JSONException {
        if (!weatherData.has("main") || !weatherData.has("weather")) {
            throw new JSONException("Incomplete weather data.");
        }

        double temperature = weatherData.getJSONObject("main").getDouble("temp");
        String conditions = weatherData.getJSONArray("weather").getJSONObject(0).getString("description");
        String iconCode = weatherData.getJSONArray("weather").getJSONObject(0).getString("icon");

        weatherLabel.setText(String.format("Temperature: %.1f°C, Conditions: %s", temperature, conditions));
        loadWeatherIcon(iconCode);
    }

    private void loadWeatherIcon(String iconCode) {
        String iconUrl = "http://openweathermap.org/img/wn/" + iconCode + ".png";
        try {
            Image image = new Image(iconUrl, true);
            weatherIcon.setImage(image);
        } catch (Exception e) {
            weatherIcon.setImage(null);
            showNotification("Error loading weather icon: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    private void sendConfirmationSMS(Service service, double rating) {
        String clientNumber = "+21627417033";
        String message = String.format("Your reservation for %s has been confirmed. Rating: %.1f/5. Thank you!",
                service.getNom(), rating);

        try {
            Message twilioMessage = Message.creator(
                            new PhoneNumber(clientNumber),
                            new PhoneNumber(TWILIO_NUMBER),
                            message)
                    .create();

            showNotification("Confirmation SMS sent (SID: " + twilioMessage.getSid() + ").", "info");
        } catch (Exception e) {
            showNotification("Error sending SMS: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    private void showNotification(String message, String type) {
        Alert alert = new Alert(type.equals("error") ? Alert.AlertType.ERROR :
                type.equals("info") ? Alert.AlertType.INFORMATION : Alert.AlertType.INFORMATION);
        alert.setTitle(type.equals("error") ? "Error" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}