package edu.emmapi.controllers;

import edu.emmapi.entities.produit;
import edu.emmapi.services.CategorieService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

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

    // Liste des produits reçue depuis la TableView de page1.fxml
    private List<produit> produits;

    // Service pour récupérer les noms de catégories
    private final CategorieService categorieService = new CategorieService();

    @FXML
    public void initialize() {
        // Critères disponibles
        comboBoxCritere.setItems(FXCollections.observableArrayList(
                "Catégorie", "Date", "Prix", "Fournisseur"
        ));

        // Quand on change de critère, on rafraîchit le camembert
        comboBoxCritere.setOnAction(evt ->
                mettreAJourPieChart(comboBoxCritere.getValue())
        );
    }

    /** Appelée depuis le contrôleur page1 pour injecter la liste des produits */
    public void setProduits(List<produit> produits) {
        this.produits = produits;
        // Afficher par défaut la répartition par catégorie
        mettreAJourPieChart("Catégorie");
    }

    /** Reconstruit le PieChart selon le critère donné */
    private void mettreAJourPieChart(String critere) {
        if (produits == null || produits.isEmpty()) return;

        Map<String, Long> stats = new HashMap<>();

        switch (critere) {
            case "Catégorie":
                stats = produits.stream()
                        .collect(Collectors.groupingBy(
                                p -> {
                                    // transform ID en nom
                                    String nom = categorieService.getNomCategorieById(p.getCategorie());
                                    return nom != null ? nom : "Inconnu";
                                },
                                Collectors.counting()
                        ));
                break;

            case "Date":
                stats = produits.stream()
                        .collect(Collectors.groupingBy(
                                produit::getDate,
                                Collectors.counting()
                        ));
                break;

            case "Prix":
                stats = produits.stream()
                        .collect(Collectors.groupingBy(
                                p -> String.format("%.2f", p.getPrix()),
                                Collectors.counting()
                        ));
                break;

            case "Fournisseur":
                stats = produits.stream()
                        .collect(Collectors.groupingBy(
                                produit::getFournisseur,
                                Collectors.counting()
                        ));
                break;

            default:
                return;
        }

        long total = produits.size();

        ObservableList<PieChart.Data> dataChart = FXCollections.observableArrayList();
        stats.forEach((key, count) -> {
            double percent = (count * 100.0) / total;
            String label = String.format("%s (%.1f%%)", key, percent);
            dataChart.add(new PieChart.Data(label, count));
        });

        pieChart.setData(dataChart);
    }

    /** Ferme la fenêtre de statistiques */
    @FXML
    private void fermerFenetre() {
        Stage stage = (Stage) btnFermer.getScene().getWindow();
        stage.close();
    }
}