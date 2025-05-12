package edu.emmapi.controllers;

import edu.emmapi.entities.CategorieItem;
import edu.emmapi.entities.produit;
import edu.emmapi.services.CategorieService;
import edu.emmapi.services.ProduitService;
import edu.emmapi.services.SmsService;
import javafx.animation.PauseTransition;
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
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Contrôleur pour l’écran « Ajouter Produit ».
 */
public class ajouterproduit {

    /* ---------- FXML ---------- */
    @FXML private ComboBox<String> prixComboBox;
    @FXML private ComboBox<String> CategorieComboBox;
    @FXML private ComboBox<String> FournisseurComboBox;
    @FXML private TextField NomProduit;
    @FXML private TextField Prix;
    @FXML private Spinner<Integer> Stock;                 // → générique <Integer>
    @FXML private Button btnsauvgarde;
    @FXML private DatePicker Date;
    @FXML private ComboBox<String> CurrencyComboBox;
    @FXML private Label ConvertedPriceLabel;

    /* ---------- Services ---------- */
    private final ProduitService produitService   = new ProduitService();
    private final CategorieService categorieService = new CategorieService();

    /* ---------- Constantes API ---------- */
    private static final String FLASK_API_URL      = "http://127.0.0.1:5004/predict";
    private static final String GOOGLE_API_KEY     = "AIzaSyAppRebP1i5iNeX_hbZk6DZ465NatLYDZE";
    private static final String SEARCH_ENGINE_ID   = "1256d1da04f58449a";
    private static final String EXCHANGE_API_KEY   = "26f646280530cb3a70059bc8";
    private static final String BASE_CURRENCY      = "TND";

    /* ---------- Initialisation ---------- */
    @FXML
    private void initialize() {
        loadCurrencies();
        chargerCategories();

        // désactivation initiale
        FournisseurComboBox.setDisable(true);
        Prix.setDisable(true);
        Date.setDisable(true);
        CurrencyComboBox.setDisable(true);
        Stock.setDisable(true);
        btnsauvgarde.setDisable(true);
        prixComboBox.setDisable(true);

        // spinner 0-100
        Stock.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));

        /* ---- Vérification de la cohérence nom⇄catégorie après 1 s de pause ---- */
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        NomProduit.textProperty().addListener((obs, oldV, newV) -> {
            if (newV.isEmpty()) return;
            pause.setOnFinished(evt -> {
                String cat = CategorieComboBox.getValue();
                if (cat == null || cat.isEmpty()) return;

                String catPredite = verifierCategorieProduit(newV);
                if (!catPredite.equalsIgnoreCase(cat.trim())) {
                    showAlert(Alert.AlertType.ERROR, "Erreur",
                            "Le produit « " + newV + " » ne correspond pas à la catégorie « " + cat +
                                    " ».\n\nCatégorie suggérée : " + catPredite);
                    NomProduit.clear();
                } else {
                    remplirFournisseur(cat, newV);
                    activerSaisieDetailProduit();
                }
            });
            pause.playFromStart();
        });

        /* ---- (re)chargement fournisseurs quand catégorie change ---- */
        CategorieComboBox.valueProperty().addListener((o, ov, nv) -> {
            if (nv != null && !NomProduit.getText().isEmpty()) {
                remplirFournisseur(nv, NomProduit.getText());
            }
        });

        /* ---- Conversion devise instantanée ---- */
        CurrencyComboBox.valueProperty().addListener((o, ov, nv) -> convertCurrency());
    }

    /* -------------------------------------------------------------------------
     *  Gestion des états UI
     * ---------------------------------------------------------------------- */
    private void activerSaisieDetailProduit() {
        Prix.setDisable(false);
        Date.setDisable(false);
        Stock.setDisable(false);
        CurrencyComboBox.setDisable(false);
        btnsauvgarde.setDisable(false);
        prixComboBox.setDisable(false);

        NomProduit.setEditable(false);
        CategorieComboBox.setDisable(false);
    }

    private void resetToInitialState() {
        NomProduit.clear();
        NomProduit.setEditable(true);
        CategorieComboBox.getSelectionModel().clearSelection();
        CategorieComboBox.setDisable(false);

        Prix.clear();
        Date.setValue(null);
        CurrencyComboBox.getSelectionModel().selectFirst();
        Stock.getValueFactory().setValue(0);

        FournisseurComboBox.getItems().clear();
        ConvertedPriceLabel.setText("Prix Converti : -");

        FournisseurComboBox.setDisable(true);
        Prix.setDisable(true);
        Date.setDisable(true);
        CurrencyComboBox.setDisable(true);
        Stock.setDisable(true);
        btnsauvgarde.setDisable(true);
        prixComboBox.setDisable(true);
    }

    private void resetFields() {   // après insertion réussie
        resetToInitialState();
    }

    /* -------------------------------------------------------------------------
     *  Chargement données (catégories, devises…)
     * ---------------------------------------------------------------------- */
    private void loadCurrencies() {
        CurrencyComboBox.setItems(FXCollections.observableArrayList(
                "USD", "EUR", "GBP", "JPY", "CAD", "AUD", "TND"
        ));
        CurrencyComboBox.getSelectionModel().select("USD");
    }

    private void chargerCategories() {
        List<String> noms = categorieService.getAllCategorieItems()
                .stream()
                .map(CategorieItem::getNom)
                .sorted()
                .collect(Collectors.toList());
        CategorieComboBox.setItems(FXCollections.observableArrayList(noms));
    }

    /* -------------------------------------------------------------------------
     *  Bouton « Ajouter »
     * ---------------------------------------------------------------------- */
    @FXML
    private void ajouterProduit(ActionEvent event) {
        try {
            /* --- validation simple des champs obligatoires --- */
            if (NomProduit.getText().isEmpty()                       ||
                    CategorieComboBox.getSelectionModel().isEmpty()      ||
                    Prix.getText().isEmpty()                             ||
                    Stock.getValue() == null                             ||
                    Date.getValue() == null                              ||
                    FournisseurComboBox.getSelectionModel().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Champs vides",
                        "Veuillez remplir tous les champs.");
                return;
            }

            /* --- validation date --- */
            if (!isDateValid(Date.getValue())) {
                showAlert(Alert.AlertType.ERROR, "Date invalide",
                        "La date doit être comprise entre le 01/01/2023 et aujourd’hui.");
                return;
            }

            /* --- unicité produit --- */
            String nomProduit = NomProduit.getText().trim();
            if (produitService.produitExiste(nomProduit)) {
                showAlert(Alert.AlertType.ERROR, "Doublon",
                        "Le produit « " + nomProduit + " » existe déjà.");
                resetToInitialState();
                return;
            }

            /* --- création entité produit --- */
            int    categorieId      = getCategorieIdByNom(CategorieComboBox.getValue());
            float  prix             = Float.parseFloat(Prix.getText());
            int    stock            = Stock.getValue();
            String dateProduit      = Date.getValue().toString();
            String fournisseur      = FournisseurComboBox.getValue();

            produit p = new produit(nomProduit, categorieId, prix, stock, dateProduit, fournisseur);
            produitService.ajouterProduit(p);

            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "Produit ajouté avec succès !");

            // SMS (facultatif)
            SmsService.envoyerSms("+21652108929",
                    "✅ Produit ajouté : " + nomProduit +
                            " (" + CategorieComboBox.getValue() + ") Prix : " + prix + " " + BASE_CURRENCY);

            resetFields();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Prix ou stock invalide.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", e.getMessage());
        }
    }

    /* -------------------------------------------------------------------------
     *  Navigation vers la page1
     * ---------------------------------------------------------------------- */
    @FXML
    private void sauvgarderproduit(ActionEvent evt) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/page1.fxml"));
            Parent root       = loader.load();
            Stage stage       = new Stage();
            stage.setTitle("Gestion des Produits");
            stage.setScene(new Scene(root));
            stage.show();

            ((Stage) btnsauvgarde.getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* -------------------------------------------------------------------------
     *  Conversions & validations
     * ---------------------------------------------------------------------- */
    private boolean isDateValid(LocalDate d) {
        return d != null &&
                !d.isBefore(LocalDate.of(2023, 1, 1)) &&
                !d.isAfter(LocalDate.now());
    }

    private void showAlert(Alert.AlertType t, String titre, String message) {
        Alert a = new Alert(t);
        a.setTitle(titre);
        a.setHeaderText(null);
        a.setContentText(message);
        a.show();
    }

    /* == conversion devise == */
    @FXML
    private void convertCurrency() {
        if (Prix.getText().isEmpty()) {
            ConvertedPriceLabel.setText("Prix Converti : -");
            return;
        }

        String targetCurrency = CurrencyComboBox.getValue();
        try {
            double prixTnd = Double.parseDouble(Prix.getText());
            String url = "https://v6.exchangerate-api.com/v6/" +
                    EXCHANGE_API_KEY + "/latest/" + BASE_CURRENCY;

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");

            JSONObject json       = new JSONObject(readAll(conn.getInputStream()));
            JSONObject rates      = json.getJSONObject("conversion_rates");

            if (!rates.has(targetCurrency)) {
                ConvertedPriceLabel.setText("Devise non supportée");
                return;
            }

            double converted = prixTnd * rates.getDouble(targetCurrency);
            ConvertedPriceLabel.setText(
                    String.format("Prix Converti : %.2f %s", converted, targetCurrency));

        } catch (IOException | JSONException | NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Conversion impossible",
                    "Erreur lors de la récupération du taux.");
        }
    }

    /* -------------------------------------------------------------------------
     *  Vérification catégorie
     * ---------------------------------------------------------------------- */
    private String verifierCategorieProduit(String nomProduit) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(FLASK_API_URL).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInput = "{\"nom_produit\":\"" + nomProduit.trim() + "\"}";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() != 200) return "Inconnue";

            JSONObject resp = new JSONObject(readAll(conn.getInputStream()));
            return resp.optString("categorie_predite", "Inconnue").trim();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur réseau",
                    "Impossible de contacter le serveur Flask.");
            return "Inconnue";
        }
    }

    /* -------------------------------------------------------------------------
     *  Recherche prix & fournisseurs (Google Custom Search)
     * ---------------------------------------------------------------------- */

    /* → déclenché par un bouton « Vérifier prix » dans la vue */
    @FXML
    private void verifierPrixMarche() {
        String produit  = NomProduit.getText().trim();
        String categorie = CategorieComboBox.getValue();

        if (produit.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ vide", "Saisissez un produit.");
            return;
        }
        if (categorie == null) {
            showAlert(Alert.AlertType.WARNING, "Catégorie manquante", "Sélectionnez une catégorie.");
            return;
        }

        new Thread(() -> {
            List<String> prixMag = rechercherPrixEtMagasin(produit, categorie);
            Platform.runLater(() -> {
                prixComboBox.getItems().clear();
                if (prixMag.isEmpty()) {
                    prixComboBox.getItems().add("⚠️ Aucun prix trouvé");
                } else {
                    prixComboBox.getItems().addAll(prixMag);
                    prixComboBox.getSelectionModel().selectFirst();
                }
            });
        }).start();
    }

    /* → alimenter la ComboBox fournisseur après validation catégorie/produit */
    private void remplirFournisseur(String categorie, String nomProduit) {
        new Thread(() -> {
            List<String> fournisseurs = rechercherFournisseursGoogle(categorie, nomProduit);
            if (fournisseurs.isEmpty()) {
                fournisseurs = rechercherMagasins(nomProduit);
            }

            List<String> finalList = fournisseurs.isEmpty() ?
                    Collections.singletonList("⚠️ Aucun fournisseur trouvé") :
                    fournisseurs;

            Platform.runLater(() -> {
                FournisseurComboBox.setItems(FXCollections.observableArrayList(finalList));
                FournisseurComboBox.setDisable(false);
            });
        }).start();
    }

    /* -------------------------------------------------------------------------
     *  Helpers réseau / parsing
     * ---------------------------------------------------------------------- */
    private String readAll(InputStream is) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    private List<String> rechercherFournisseursGoogle(String categorie, String produit) {
        List<String> fournisseurs = new ArrayList<>();
        try {
            String query =
                    "Fournisseur " + produit + " Tunisie site:.tn -" +
                            "filetype:(pdf|doc|ppt)";
            String url = "https://www.googleapis.com/customsearch/v1?q=" +
                    URLEncoder.encode(query, StandardCharsets.UTF_8) +
                    "&key=" + GOOGLE_API_KEY + "&cx=" + SEARCH_ENGINE_ID;

            JSONArray items = new JSONObject(readAll(new URL(url).openStream()))
                    .optJSONArray("items");

            if (items == null) return fournisseurs;

            for (int i = 0; i < Math.min(items.length(), 5); i++) {
                JSONObject item = items.getJSONObject(i);
                String title = item.optString("title", "Fournisseur inconnu");
                String link  = item.optString("link", "");
                if (link.matches(".*\\.(pdf|doc|ppt)$")) continue;
                fournisseurs.add(title);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return fournisseurs;
    }

    private List<String> rechercherMagasins(String produit) {
        List<String> magasins = new ArrayList<>();
        for (String r : rechercherPrixEtMagasin(produit, CategorieComboBox.getValue())) {
            String mag = r.split(" : ")[0];  // « nomProduit - magasin »
            if (!magasins.contains(mag)) magasins.add(mag);
        }
        return magasins;
    }

    /* --------- Google Custom Search : prix & magazins ---------- */
    private List<String> rechercherPrixEtMagasin(String nomProduit, String categorie) {
        List<String> resultats = new ArrayList<>();
        try {
            String query = String.format("\"%s\" \"%s\" Europe (\"€\"|euro|prix) -" +
                            "filetype:(pdf|doc|ppt)",
                    nomProduit, categorie);

            String url = "https://www.googleapis.com/customsearch/v1?q=" +
                    URLEncoder.encode(query, StandardCharsets.UTF_8) +
                    "&key=" + GOOGLE_API_KEY + "&cx=" + SEARCH_ENGINE_ID;
            JSONArray items = new JSONObject(readAll(new URL(url).openStream()))
                    .optJSONArray("items");
            if (items == null) return resultats;

            for (int i = 0; i < Math.min(items.length(), 5); i++) {
                JSONObject it   = items.getJSONObject(i);
                String titre    = it.optString("title", "");
                String snippet  = it.optString("snippet", "");
                String link     = it.optString("link", "");

                String texteAnalyse = (titre + " " + snippet).toLowerCase();
                if (!texteAnalyse.contains(categorie.toLowerCase())) continue;

                double prix     = extrairePrixDepuisTexte(titre + " " + snippet);
                if (prix < 0) continue;

                String magasin  = extraireNomMagasin(link);
                Quantite q      = extraireQuantite(titre + " " + snippet);

                double unitPrice = prix;
                String unitStr   = "€/unit";
                if (q != null) {
                    switch (q.unit) {
                        case "kg" -> { unitPrice = prix / (q.value * 1000); unitStr = "€/g"; }
                        case "g"  -> { unitPrice = prix / q.value;           unitStr = "€/g"; }
                        case "l"  -> { unitPrice = prix / q.value;           unitStr = "€/l"; }
                        case "ml" -> { unitPrice = prix / (q.value / 1000);  unitStr = "€/l"; }
                    }
                }

                resultats.add(extraireNomProduit(titre) + " - " + magasin +
                        " : " + String.format(Locale.US, "%.2f", unitPrice) + " " + unitStr);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return resultats;
    }

    public void verifierProduit(ActionEvent actionEvent) {
    }

    /* -------------------------------------------------------------------------
     *  Regex utils (prix, quantité…)
     * ---------------------------------------------------------------------- */
    private record Quantite(double value, String unit) {}

    private Quantite extraireQuantite(String txt) {
        Matcher m = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(kg|g|l|ml)",
                Pattern.CASE_INSENSITIVE).matcher(txt);
        if (!m.find()) return null;
        return new Quantite(Double.parseDouble(m.group(1)), m.group(2).toLowerCase());
    }

    private double extrairePrixDepuisTexte(String txt) {
        List<Double> prixTrouves = new ArrayList<>();
        /* € avant ou après le nombre */
        Matcher m = Pattern.compile("(?:€|eur|euro)\\s*(\\d+(?:[.,]\\d+)?)" +
                        "|(\\d+(?:[.,]\\d+)?)\\s*(?:€|eur|euro)",
                Pattern.CASE_INSENSITIVE).matcher(txt);
        while (m.find()) {
            String n = m.group(1) != null ? m.group(1) : m.group(2);
            prixTrouves.add(Double.parseDouble(n.replace(',', '.')));
        }
        if (prixTrouves.isEmpty()) {
            m = Pattern.compile("(\\d+(?:[.,]\\d+)?)(?!\\s*(?:l|ml|g|kg))")
                    .matcher(txt);
            while (m.find()) prixTrouves.add(Double.parseDouble(
                    m.group(1).replace(',', '.')));
        }
        return prixTrouves.isEmpty() ? -1 : Collections.min(prixTrouves);
    }

    private String extraireNomProduit(String titre) {
        return titre.contains(" - ") ? titre.split(" - ")[0].trim()
                : titre.trim();
    }

    private String extraireNomMagasin(String lien) {
        try {
            String host = new URL(lien).getHost();
            String[] p  = host.split("\\.");
            return (p.length > 2) ? p[p.length - 2] + '.' + p[p.length - 1] : host;
        } catch (MalformedURLException e) {
            return "Magasin inconnu";
        }
    }

    /* -------------------------------------------------------------------------
     *  Helpers catégorie
     * ---------------------------------------------------------------------- */
    private int getCategorieIdByNom(String nom) {
        return categorieService.getAllCategorieItems()
                .stream()
                .filter(ci -> ci.getNom().equals(nom))
                .map(CategorieItem::getId)
                .findFirst()
                .orElse(-1);
    }
}
