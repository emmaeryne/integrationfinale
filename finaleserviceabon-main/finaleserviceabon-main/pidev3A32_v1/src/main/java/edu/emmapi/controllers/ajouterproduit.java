package edu.emmapi.controllers;

import edu.emmapi.entities.produit;
import edu.emmapi.services.CategorieService;

import edu.emmapi.services.ProduitService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ajouterproduit {
    @FXML
    private ComboBox<String> prixComboBox;
    @FXML
    private ComboBox<String> CategorieComboBox;
    @FXML
    private ComboBox<String> FournisseurComboBox;
    @FXML
    private TextField NomProduit;
    @FXML
    private TextField Prix;
    @FXML
    private Spinner Stock;
    @FXML
    private Button btnsauvgarde;
    @FXML
    private DatePicker Date;
    @FXML
    private ComboBox<String> CurrencyComboBox;
    @FXML
    private Label ConvertedPriceLabel;

    private final ProduitService produitService = new ProduitService();
    private final CategorieService categorieService = new CategorieService();
    private static final String FLASK_API_URL = "http://127.0.0.1:5004/predict";
    private static final String API_KEY = "AIzaSyAppRebP1i5iNeX_hbZk6DZ465NatLYDZE";
    private static final String SEARCH_ENGINE_ID = "1256d1da04f58449a";
    private static final String EXCHANGE_API_KEY = "26f646280530cb3a70059bc8";
    private static final String BASE_CURRENCY = "TND";

    @FXML
    public void initialize() {
        System.out.println("🔄 Initialisation du contrôleur...");
        loadCurrencies();
        chargerCategories();

        FournisseurComboBox.setDisable(true);// Désactiver au départ
        Prix.setDisable(true);
        // prixField.setDisable(true);
        Date.setDisable(true);
        Stock.setDisable(true); if (Stock != null) {
            Stock.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));
            // Min: 0, Max: 100, Valeur par défaut: 0
        } else {
            System.out.println("❌ ERREUR : Stock est null !");
        }
        CurrencyComboBox.setDisable(true);
        btnsauvgarde.setDisable(true);
        prixComboBox.setDisable(true);


        PauseTransition pause = new PauseTransition(Duration.seconds(1));

        NomProduit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                pause.setOnFinished(event -> {
                    String categorieChoisie = CategorieComboBox.getValue();
                    if (categorieChoisie != null && !categorieChoisie.isEmpty()) {
                        String categoriePredite = verifierCategorieProduit(newValue);
                        if (!categoriePredite.equalsIgnoreCase(categorieChoisie.trim())) {
                            showAlert(Alert.AlertType.ERROR, "Erreur",
                                    "Le produit '" + newValue + "' ne correspond pas à la catégorie '" + categorieChoisie + "'\n\n"
                                            + "Catégorie suggérée : " + categoriePredite);
                            NomProduit.clear();
                        } else {
                            System.out.println("✅ Catégorie correcte : " + categoriePredite);
                            remplirFournisseur(categorieChoisie, newValue);
                            Prix.setDisable(false);
                            Date.setDisable(false);
                            Stock.setDisable(false);
                            CurrencyComboBox.setDisable(false);
                            btnsauvgarde.setDisable(false);
                            prixComboBox.setDisable(false);
                            // Rendre non modifiables le champ du nom du produit et la sélection de catégorie
                            NomProduit.setEditable(false);
                            CategorieComboBox.setDisable(false);
                        }
                    }
                });
                pause.playFromStart();
            }
        });

        CategorieComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("📌 Catégorie sélectionnée : " + newValue);
            if (newValue != null && !NomProduit.getText().isEmpty()) {
                remplirFournisseur(newValue, NomProduit.getText());
            }
        });

        CurrencyComboBox.valueProperty().addListener((observable, oldValue, newValue) -> convertCurrency());
    }



    private void loadCurrencies() {
        String[] currencies = {"USD", "EUR", "GBP", "JPY", "CAD", "AUD", "TND"};
        CurrencyComboBox.getItems().addAll(currencies);
        CurrencyComboBox.setValue("USD");
    }

    private void chargerCategories() {
        List<String> categories = categorieService.getAllCategorieNoms();
        ObservableList<String> categorieList = FXCollections.observableArrayList(categories);
        CategorieComboBox.setItems(categorieList);
    }

    @FXML
    void ajouterProduit(ActionEvent event) {
        try {
            if (NomProduit.getText().isEmpty() || CategorieComboBox.getSelectionModel().isEmpty() ||
                    Prix.getText().isEmpty() || Stock.getValue() == null ||
                    Date.getValue() == null || FournisseurComboBox.getSelectionModel().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs !");
                return;
            }

            if (!isDateValid(Date.getValue())) {
                showAlert(Alert.AlertType.ERROR, "Erreur de validation",
                        "La date sélectionnée doit être entre le 01/01/2023 et aujourd'hui.");
                return;
            }

            String nomProduit = NomProduit.getText().trim();

            if (produitService.produitExiste(nomProduit)) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le produit '" + nomProduit + "' existe déjà !");
                resetToInitialState();
                return;
            }

            String categorie = CategorieComboBox.getSelectionModel().getSelectedItem();
            float prix = Float.parseFloat(Prix.getText());
            int stock = (Integer) Stock.getValue(); // ✅ Cast explicite en Integer
            String dateProduit = Date.getValue().toString();
            String fournisseurChoisi = FournisseurComboBox.getSelectionModel().getSelectedItem();

            produit produit = new produit(nomProduit, categorie, prix, stock, dateProduit, fournisseurChoisi);
            produitService.ajouterProduit(produit);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Le produit a été ajouté avec succès !");
               String numeroDestinataire = "+21652108929"; // Remplace par un vrai numéro
               String messageTexte = "✅ Produit ajouté : " + nomProduit + " (" + categorie + ") Prix : " + prix + " TND";
              //SmsService.envoyerSms(numeroDestinataire, messageTexte);


            resetFields();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Veuillez entrer des valeurs numériques valides pour Prix et Stock !");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", e.getMessage());
        }
    }

    @FXML
    public void sauvgarderproduit(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/page1.fxml"));
            Parent parent = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Gestion des Produits");
            stage.setScene(new Scene(parent));
            stage.show();

            Stage currentStage = (Stage) btnsauvgarde.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isDateValid(LocalDate date) {
        return date != null && !date.isBefore(LocalDate.of(2023, 1, 1)) && !date.isAfter(LocalDate.now());
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
    private void resetToInitialState() {
        // Vider les champs
        NomProduit.clear();
        CategorieComboBox.getSelectionModel().clearSelection();
        Prix.clear();
        if (Stock.getValueFactory() != null) {
            Stock.getValueFactory().setValue(0); // Remettre le Spinner à 0
        }
        Date.setValue(null);
        FournisseurComboBox.getItems().clear();

        // Désactiver les contrôles pour revenir à l'état initial
        FournisseurComboBox.setDisable(true);
        Prix.setDisable(true);
        Date.setDisable(true);
        Stock.setDisable(true);
        CurrencyComboBox.setDisable(true);
        btnsauvgarde.setDisable(true);
        prixComboBox.setDisable(true);
    }

    private void resetFields() {
        NomProduit.clear();
        CategorieComboBox.getSelectionModel().clearSelection();
        Prix.clear();
        Stock.getValueFactory().setValue(0); // ✅ Remet le spinner à 0
        Date.setValue(null);
        FournisseurComboBox.getItems().clear();
        FournisseurComboBox.setDisable(true);
    }
    private List<String> rechercherMagasins(String produit) {
        List<String> magasins = new ArrayList<>();
        List<String> magasinsEtPrix = rechercherPrixEtMagasin(NomProduit.getText().trim(),CategorieComboBox.getSelectionModel().getSelectedItem()); // Appel de la fonction existante

        for (String item : magasinsEtPrix) {
            String magasin = item.split(" : ")[0]; // Extraire uniquement le nom du magasin
            if (!magasins.contains(magasin)) { // Éviter les doublons
                magasins.add(magasin);
            }
        }

        return magasins;
    }

    private void remplirFournisseur(String categorieText, String nomProduit) {
        new Thread(() -> {
            List<String> fournisseurs = rechercherFournisseursGoogle(categorieText, nomProduit);

            if (fournisseurs.isEmpty()) {
                // Aucun fournisseur trouvé, chercher uniquement les magasins
                List<String> magasins = rechercherMagasins(nomProduit);

                Platform.runLater(() -> {
                    FournisseurComboBox.getItems().clear();

                    if (!magasins.isEmpty()) {
                        FournisseurComboBox.getItems().add("⚠️ Aucun fournisseur trouvé, mais voici des magasins :");
                        FournisseurComboBox.getItems().addAll(magasins);
                    } else {
                        FournisseurComboBox.getItems().add("⚠️ Aucun fournisseur ni magasin trouvé !");
                    }

                    FournisseurComboBox.setDisable(false);
                });

            } else {
                // Des fournisseurs sont trouvés, on les affiche normalement
                Platform.runLater(() -> {
                    FournisseurComboBox.getItems().clear();
                    FournisseurComboBox.getItems().addAll(fournisseurs);
                    FournisseurComboBox.setDisable(false);
                });
            }
        }).start();
    }

    private List<String> rechercherFournisseursGoogle(String categorie, String nomProduit) {
        List<String> fournisseurs = new ArrayList<>();

        try {
            if (categorie == null || categorie.trim().isEmpty() || nomProduit == null || nomProduit.trim().isEmpty()) {
                System.out.println("⚠️ Catégorie ou produit vide, recherche annulée.");
                return fournisseurs;
            }

            // ✅ Construire une requête Google ciblée sur la Tunisie en excluant les fichiers PDF et DOC
            String query = "Fournisseur " + nomProduit + " Tunisie site:.tn";


            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String urlString = "https://www.googleapis.com/customsearch/v1?q=" + encodedQuery +
                    "&key=" + API_KEY + "&cx=" + SEARCH_ENGINE_ID;

            System.out.println("🔍 Envoi de la requête Google Search API : " + urlString);

            // ✅ Effectuer la requête HTTP
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            System.out.println("🔍 Code réponse HTTP : " + responseCode);

            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // ✅ Extraire les fournisseurs de la réponse JSON
                JSONObject json = new JSONObject(response.toString());
                JSONArray items = json.optJSONArray("items");

                if (items != null && items.length() > 0) {
                    for (int i = 0; i < Math.min(items.length(), 5); i++) {
                        JSONObject item = items.getJSONObject(i);
                        String title = item.optString("title", "Fournisseur inconnu");
                        String link = item.optString("link", "");

                        // 🔹 Vérifier que le lien est un site marchand valide et non un document
                        if (!link.endsWith(".pdf") && !link.endsWith(".doc") && !link.endsWith(".ppt")) {
                            fournisseurs.add(title);

                        }
                    }
                    System.out.println("✅ Fournisseurs trouvés : " + fournisseurs);
                } else {
                    System.out.println("⚠️ Aucun fournisseur spécifique trouvé.");
                }
            } else {
                System.err.println("🛑 Erreur API Google Search : Réponse HTTP " + responseCode);
            }

        } catch (IOException e) {
            System.err.println("🚨 Erreur réseau lors de la requête Google Search API : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Erreur inattendue lors de la recherche des fournisseurs : " + e.getMessage());
            e.printStackTrace();
        }

        return fournisseurs;
    }

    @FXML
    private void convertCurrency() {
        String targetCurrency = CurrencyComboBox.getValue();

        if (Prix.getText().isEmpty()) {
            ConvertedPriceLabel.setText("Prix Converti : -");
            return;
        }

        try {
            float priceInEUR = Float.parseFloat(Prix.getText());

            // Construire l'URL de l'API
            String urlString = "https://v6.exchangerate-api.com/v6/" + EXCHANGE_API_KEY + "/latest/" + BASE_CURRENCY;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject conversionRates = jsonResponse.getJSONObject("conversion_rates");

            if (conversionRates.has(targetCurrency)) {
                double rate = conversionRates.getDouble(targetCurrency);
                double convertedPrice = priceInEUR * rate;

                ConvertedPriceLabel.setText(String.format("Prix Converti : %.2f %s", convertedPrice, targetCurrency));
            } else {
                ConvertedPriceLabel.setText("Erreur : Devise non supportée !");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer un prix valide !");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur API", "Impossible de récupérer les taux de conversion !");
        }
    }

    @FXML
    private void verifierProduit(ActionEvent event) {
        String nomProduit = NomProduit.getText().trim();

        if (nomProduit.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ vide", "Veuillez entrer un nom de produit !");
            return;
        }

        String categoriePredite = verifierCategorieProduit(nomProduit);
        String categorieChoisie = CategorieComboBox.getValue();

        if (categoriePredite.equalsIgnoreCase("Inconnue")) {
            showAlert(Alert.AlertType.ERROR, "Erreur API", "Impossible de déterminer la catégorie !");
            return;
        }

        if (!categoriePredite.equalsIgnoreCase(categorieChoisie)) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Le produit '" + nomProduit + "' ne correspond pas à la catégorie '" + categorieChoisie + "'.\n\nCatégorie suggérée : " + categoriePredite);
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "Le produit '" + nomProduit + "' correspond bien à la catégorie sélectionnée !");
        }
    }

    private void verifierCategorieProduitAsync(String nomProduit) {
        new Thread(() -> {
            String categoriePredite = verifierCategorieProduit(nomProduit);
            Platform.runLater(() -> {
                String categorieChoisie = CategorieComboBox.getValue();
                if (categoriePredite.equalsIgnoreCase("Inconnue")) {
                    showAlert(Alert.AlertType.ERROR, "Erreur API", "Impossible de déterminer la catégorie !");
                } else if (!categoriePredite.equalsIgnoreCase(categorieChoisie)) {
                    showAlert(Alert.AlertType.ERROR, "Erreur",
                            "Le produit '" + nomProduit + "' ne correspond pas à la catégorie '" + categorieChoisie +
                                    "'.\n\nCatégorie suggérée : " + categoriePredite);
                }
            });
        }).start();
    }

    private String verifierCategorieProduit(String nomProduit) {
        try {
            System.out.println("🔍 Envoi de la requête à Flask...");

            URL url = new URL(FLASK_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000); // Timeout de connexion 10 sec
            conn.setReadTimeout(200000); // Timeout de lecture 10 sec

            nomProduit = nomProduit.trim();  // 🔹 Éliminer les espaces
            if (nomProduit.isEmpty()) {
                System.out.println("⚠️ Erreur : Nom du produit vide !");
                return "Inconnue";
            }

            String jsonInputString = "{\"nom_produit\": \"" + nomProduit + "\"}";
            System.out.println("📤 JSON envoyé : " + jsonInputString);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInputString.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            System.out.println("🔄 Code de réponse HTTP : " + responseCode);

            // 🔹 Lire la réponse complète
            BufferedReader reader;
            if (responseCode == 200) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            System.out.println("📩 Réponse Flask : " + response.toString());

            if (responseCode != 200) {
                return "Inconnue";
            }

            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.optString("categorie_predite", "Inconnue").trim().toLowerCase(); // ✅ Correction

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur API", "Impossible de contacter le serveur Flask !");
            e.printStackTrace();
            return "Inconnue";
        }
    }
    @FXML
    private TextField nomProduitField, prixField;


    @FXML
    private void verifierPrixMarche() {
        String nomProduit = NomProduit.getText().trim();
        String categorieSelectionnee = CategorieComboBox.getSelectionModel().getSelectedItem();

        // ✅ Vérification si les champs sont bien remplis
        if (nomProduit.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ vide", "Veuillez entrer un produit !");
            return;
        }
        if (categorieSelectionnee == null) {
            showAlert(Alert.AlertType.WARNING, "Catégorie manquante", "Veuillez sélectionner une catégorie !");
            return;
        }

        new Thread(() -> {
            // ✅ Récupérer les résultats (prix et magasins)
            List<String> prixEtMagasins = rechercherPrixEtMagasin(nomProduit, categorieSelectionnee);

            // ✅ Mettre à jour l'UI avec les résultats
            Platform.runLater(() -> {
                prixComboBox.getItems().clear(); // Nettoyer les anciennes valeurs

                if (!prixEtMagasins.isEmpty()) {
                    prixComboBox.getItems().addAll(prixEtMagasins); // Ajouter les prix avec magasins
                    prixComboBox.getSelectionModel().selectFirst(); // Sélectionner automatiquement le premier prix
                } else {
                    prixComboBox.getItems().add("⚠️ Aucun prix trouvé pour ce produit !");
                }
            });
        }).start();
    }


    // Classe interne pour représenter une quantité extraite du texte
    private static class Quantite {
        double value;
        String unit;
        Quantite(double value, String unit) {
            this.value = value;
            this.unit = unit;
        }
    }

    // Méthode pour extraire une quantité à partir d'un texte (par exemple "500g", "1kg", "200 ml", "1L")
    private Quantite extraireQuantite(String texte) {
        Pattern pattern = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(kg|g|l|ml)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(texte);
        if (matcher.find()) {
            double val = Double.parseDouble(matcher.group(1));
            String unit = matcher.group(2).toLowerCase();
            return new Quantite(val, unit);
        }
        return null;
    }

    private double extrairePrixDepuisTexte(String texte) {
        List<Double> prixTrouves = new ArrayList<>();

        // 1. Extraction avec devise (€ ou eur ou euro)
        Pattern patternCurrency = Pattern.compile(
                "(?:€|eur|euro)\\s*(\\d+(?:[.,]\\d+)?)" +        // devise avant le nombre
                        "|(\\d+(?:[.,]\\d+)?)\\s*(?:€|eur|euro)",         // devise après le nombre
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = patternCurrency.matcher(texte);
        while (matcher.find()) {
            String nombreStr = (matcher.group(1) != null) ? matcher.group(1) : matcher.group(2);
            if (nombreStr != null) {
                nombreStr = nombreStr.replace(",", ".");
                try {
                    double prix = Double.parseDouble(nombreStr);
                    if (prix > 0.1 && prix < 50) { // plage de prix plausible
                        prixTrouves.add(prix);
                    }
                } catch (NumberFormatException e) {
                    // Ignorer en cas d'erreur
                }
            }
        }

        // 2. Si aucune valeur n'est trouvée avec devise, tenter sans symbole,
        // en excluant les nombres suivis d'unités (l, ml, g, kg)
        if (prixTrouves.isEmpty()) {
            Pattern patternNoCurrency = Pattern.compile(
                    "(\\d+(?:[.,]\\d+)?)(?!\\s*(?:l|ml|g|kg))",
                    Pattern.CASE_INSENSITIVE
            );
            matcher = patternNoCurrency.matcher(texte);
            while (matcher.find()) {
                String nombreStr = matcher.group(1);
                if (nombreStr != null) {
                    nombreStr = nombreStr.replace(",", ".");
                    try {
                        double prix = Double.parseDouble(nombreStr);
                        if (prix > 0.1 && prix < 50) {
                            prixTrouves.add(prix);
                        }
                    } catch (NumberFormatException e) {
                        // Ignorer
                    }
                }
            }
        }

        if (prixTrouves.isEmpty()) {
            return -1;
        }
        // Pour minimiser le risque d'une fausse détection, on retourne le plus petit prix trouvé.
        return prixTrouves.stream().min(Double::compare).get();
    }


    // Méthode existante pour extraire le nom du produit à partir du titre
    private String extraireNomProduit(String titre) {
        if (titre.contains(" - ")) {
            return titre.split(" - ")[0].trim();
        }
        return titre.trim();
    }

    // Méthode existante pour extraire le nom du magasin à partir du lien
    private String extraireNomMagasin(String url) {
        try {
            URL siteUrl = new URL(url);
            String host = siteUrl.getHost();
            String[] parts = host.split("\\.");
            if (parts.length > 2) {
                host = parts[parts.length - 2] + "." + parts[parts.length - 1];
            }
            return host;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "Magasin inconnu";
        }
    }

    // Méthode qui construit la requête pour récupérer un produit de la catégorie choisie en Europe
// et qui retourne le prix unitaire (€/g, €/l ou €/unit)
    private List<String> rechercherPrixEtMagasin(String nomproduit, String categorie) {
        List<String> resultats = new ArrayList<>();
        try {
            // Construire une requête ciblée :
            // On recherche le produit et la catégorie, en forçant la présence du terme "Europe"
            // et en excluant les documents (PDF, DOC, PPT)
            String query = String.format("\"%s\" \"%s\" \"Europe\" -inurl:pdf -inurl:doc -inurl:ppt (\"€\" OR euro OR prix OR acheter OR vente)",
                    nomproduit, categorie);
            System.out.println("🔍 Requête Google Search API : " + query);
            URL url = new URL("https://www.googleapis.com/customsearch/v1?q=" +
                    URLEncoder.encode(query, StandardCharsets.UTF_8) +
                    "&key=" + API_KEY + "&cx=" + SEARCH_ENGINE_ID);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println("🔍 Réponse API : " + response.toString());
            JSONArray items = new JSONObject(response.toString()).optJSONArray("items");
            if (items != null) {
                // Parcourir au maximum 5 résultats
                for (int i = 0; i < Math.min(items.length(), 5); i++) {
                    JSONObject item = items.getJSONObject(i);
                    String title = item.optString("title", "");
                    String snippet = item.optString("snippet", "");
                    String link = item.optString("link", "");

                    // Vérifier que le résultat contient bien la catégorie
                    String texteAnalyse = title.toLowerCase() + " " + snippet.toLowerCase();
                    if (!texteAnalyse.contains(categorie.toLowerCase())) {
                        System.out.println("🚫 Ignoré : Résultat non pertinent (" + title + ")");
                        continue;
                    }

                    // Extraire le prix total depuis le titre et le snippet
                    double prixExtrait = extrairePrixDepuisTexte(title + " " + snippet);
                    // Extraire le nom du produit à partir du titre
                    String nomProduitExtrait = extraireNomProduit(title);
                    // Extraire le nom du magasin à partir du lien
                    String magasin = extraireNomMagasin(link);

                    System.out.println("📌 Produit trouvé : " + title);
                    System.out.println("📝 Nom extrait : " + nomProduitExtrait);
                    System.out.println("💰 Prix extrait : " + prixExtrait);
                    System.out.println("🏪 Magasin : " + magasin);

                    // Extraction de la quantité pour calculer le prix unitaire
                    Quantite quantite = extraireQuantite(title + " " + snippet);
                    double unitPrice = prixExtrait;
                    String unitStr = "";
                    if (quantite != null) {
                        // Conversion en prix unitaire selon l'unité trouvée
                        if (quantite.unit.equalsIgnoreCase("kg")) {
                            unitPrice = prixExtrait / (quantite.value * 1000); // prix par gramme
                            unitStr = "€/g";
                        } else if (quantite.unit.equalsIgnoreCase("g")) {
                            unitPrice = prixExtrait / quantite.value;
                            unitStr = "€/g";
                        } else if (quantite.unit.equalsIgnoreCase("l")) {
                            unitPrice = prixExtrait / quantite.value;
                            unitStr = "€/l";
                        } else if (quantite.unit.equalsIgnoreCase("ml")) {
                            unitPrice = prixExtrait / (quantite.value / 1000.0); // conversion en litre
                            unitStr = "€/l";
                        }
                    } else {
                        // Si aucune quantité n'est trouvée, considérer le prix comme unitaire
                        unitStr = "€/unit";
                    }

                    System.out.println("💵 Prix unitaire : " + unitPrice + " " + unitStr);
                    System.out.println("--------------------------------");

                    // Si un prix pertinent et un magasin sont trouvés, on ajoute le résultat à la liste
                    if (prixExtrait > 0 && magasin != null) {
                        resultats.add(nomProduitExtrait + " - " + magasin + " : " + String.format("%.2f", unitPrice) + " " + unitStr);
                        // Vous pouvez choisir de sortir après le premier résultat pertinent en ajoutant : break;
                    }
                }
            } else {
                System.out.println("⚠️ Aucun résultat trouvé !");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            System.out.println("⚠️ Erreur lors de la requête Google Custom Search !");
        }
        return resultats;
    }


}