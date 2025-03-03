package edu.emmapi.controllers;

import edu.emmapi.entities.produit;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatistiquesController {

    @FXML
    private PieChart pieChart;
    @FXML
    private ComboBox<String> comboBoxCritere;
    @FXML
    private Button btnFermer;

    // Liste des produits récupérée depuis la TableView de page1.fxml
    private List<produit> produits;

    @FXML
    public void initialize() {
        // Initialiser le ComboBox avec les critères
        comboBoxCritere.setItems(FXCollections.observableArrayList("Catégorie", "Date", "Prix", "Fournisseur"));

        // Gérer le changement de critère pour mettre à jour le PieChart
        comboBoxCritere.setOnAction(event -> mettreAJourPieChart(comboBoxCritere.getValue()));
    }

    // Méthode pour recevoir les produits depuis produitpage1.java
    public void setProduits(List<produit> produits) {
        this.produits = produits;
        System.out.println("✅ Produits reçus dans StatistiquesController: " + produits.size());

        mettreAJourPieChart("Catégorie"); // Charger par défaut les statistiques par catégorie
    }

    // Mettre à jour le PieChart selon le critère choisi
    private void mettreAJourPieChart(String critere) {
        if (produits == null || produits.isEmpty()) {
            System.out.println("⚠ Aucune donnée reçue !");
            return;
        }

        Map<String, Long> stats = new HashMap<>();

        switch (critere) {
            case "Catégorie":
                stats = produits.stream()
                        .collect(Collectors.groupingBy(produit::getCategorie, Collectors.counting()));
                break;
            case "Date":
                stats = produits.stream()
                        .collect(Collectors.groupingBy(produit::getDate, Collectors.counting()));
                break;
            case "Prix":
                stats = produits.stream()
                        .collect(Collectors.groupingBy(p -> String.valueOf(p.getPrix()), Collectors.counting()));
                break;
            case "Fournisseur":
                stats = produits.stream()
                        .collect(Collectors.groupingBy(produit::getFournisseur, Collectors.counting()));
                break;
            default:
                return;
        }

        // Calcul du total des produits
        long total = produits.size();

        // Créer les données du PieChart avec les pourcentages
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Long> entry : stats.entrySet()) {
            String key = entry.getKey();
            long count = entry.getValue();
            double percentage = (count * 100.0) / total;

            PieChart.Data data = new PieChart.Data(key + " (" + String.format("%.1f", percentage) + "%)", count);
            pieChartData.add(data);
        }

        pieChart.setData(pieChartData);
    }

    // Fermer la fenêtre
    @FXML
    private void fermerFenetre() {
        Stage stage = (Stage) btnFermer.getScene().getWindow();
        stage.close();
    }
}
