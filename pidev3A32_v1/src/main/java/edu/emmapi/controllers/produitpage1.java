package edu.emmapi.controllers;
import edu.emmapi.services.ProduitService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.regex.*;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import edu.emmapi.entities.produit;
import edu.emmapi.services.ProduitService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class produitpage1 {





    @FXML
    private TableView<produit> tableProduit;
    @FXML
    private TableColumn<produit, String> colNom;
    @FXML
    private TableColumn<produit, String> colCategorie;
    @FXML
    private TableColumn<produit, Float> colPrix;
    @FXML
    private TableColumn<produit, String> colDate;
    @FXML
    private TableColumn<produit, String> colFournisseur;
    @FXML
    private TableColumn<produit, produit> colQRCode;
    @FXML
    private TextField searchField;
    @FXML
    private TextField PrixMarche;
    private ObservableList<produit> produitsObservableList = FXCollections.observableArrayList();
    private ObservableList<produit> filteredProduits = FXCollections.observableArrayList();
    private static final String API_KEY = "AIzaSyAppRebP1i5iNeX_hbZk6DZ465NatLYDZE";
    private static final String SEARCH_ENGINE_ID = "1256d1da04f58449a";
    @FXML
    private TextField NomProduit;

    @FXML
    private ComboBox<String> prixComboBox;
    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private ComboBox<String> statComboBox;
    @FXML
    private Button btnStatistiques;



    @FXML
    private AnchorPane statPane;


    @FXML
    private Button btnAjouter, btnSupprimer, btnModifier, btnEnregistrer, naviguerverscategorie;
    @FXML
    private TextField nomProduitField, prixField;
    @FXML
    private DatePicker dateField;

    private final ProduitService produitService = new ProduitService();
    private produit produitSelectionne;

    @FXML
    public void initialize() {
        System.setProperty("org.apache.logging.log4j.simplelog.StatusLogger.level", "OFF");

        chargerProduits();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterProducts());

        btnEnregistrer.setVisible(false);
        colQRCode.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        colQRCode.setCellFactory(new QRCodeCellFactory());


    }
    // private ObservableList<produit> produitsObservableList = FXCollections.observableArrayList();
    private FilteredList<produit> filteredList;
    private void chargerProduits() {
        List<produit> produitsList = produitService.afficherProduit();
        produitsObservableList.setAll(produitsList); // ✅ Stocker la liste complète

        // ✅ Initialiser la liste filtrée (uniquement la première fois)
        if (filteredList == null) {
            filteredList = new FilteredList<>(produitsObservableList, p -> true);
            tableProduit.setItems(filteredList); // ✅ Associer à la TableView une seule fois
        } else {
            filteredList.setPredicate(p -> true); // ✅ Réinitialiser le filtre si déjà existant
        }

        // ✅ Associer les colonnes
        colNom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom_Produit()));
        colCategorie.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategorie()));
        colPrix.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrix()));
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate()));
        colFournisseur.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFournisseur()));
    }
    @FXML
    private void filterProducts() {
        String searchText = searchField.getText().toLowerCase();

        filteredList.setPredicate(produit -> {
            if (searchText.isEmpty() || searchText.isBlank()) {
                return true; // ✅ Affiche tous les produits si la recherche est vide
            }

            // ✅ Vérifier si le texte correspond à l'un des champs
            return produit.getNom_Produit().toLowerCase().contains(searchText)
                    || produit.getCategorie().toLowerCase().contains(searchText)
                    || String.valueOf(produit.getPrix()).contains(searchText)
                    || produit.getDate().contains(searchText)
                    || produit.getFournisseur().toLowerCase().contains(searchText);
        });
    }
    @FXML
    private void resetSearch() {
        searchField.clear();
        filterProducts(); // ✅ Appelle le filtre pour afficher tous les produits
    }


    @FXML
    private void modifierProduit(ActionEvent actionEvent) {
        produitSelectionne = tableProduit.getSelectionModel().getSelectedItem();
        if (produitSelectionne != null) {
            nomProduitField.setText(produitSelectionne.getNom_Produit());
            prixField.setText(String.valueOf(produitSelectionne.getPrix()));
            dateField.setValue(LocalDate.parse(produitSelectionne.getDate()));

            btnModifier.setDisable(true);
            btnEnregistrer.setVisible(true);
        } else {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez sélectionner un produit à modifier.");
        }
    }

    @FXML
    private void enregistrerModification(ActionEvent actionEvent) {
        if (produitSelectionne != null) {
            try {
                if (nomProduitField.getText().isEmpty() || prixField.getText().isEmpty() || dateField.getValue() == null) {
                    showAlert(Alert.AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs !");
                    return;
                }

                if (!isNumeric(prixField.getText())) {
                    showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Le prix doit être un nombre valide !");
                    return;
                }

                if (!isDateValid(dateField.getValue())) {
                    showAlert(Alert.AlertType.ERROR, "Erreur de validation", "La date doit être entre le 01/01/2023 et aujourd'hui.");
                    return;
                }

                produitSelectionne.setNom_Produit(nomProduitField.getText());
                produitSelectionne.setPrix(Float.parseFloat(prixField.getText()));
                produitSelectionne.setDate(dateField.getValue().toString());

                produitService.modifierProduit(produitSelectionne);
                chargerProduits();
                resetFields();

                btnModifier.setDisable(false);
                btnEnregistrer.setVisible(false);

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Les modifications ont été enregistrées avec succès !");
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer un prix valide !");
            }
        }
    }

    @FXML
    private void supprimerProduit(ActionEvent actionEvent) {
        produitSelectionne = tableProduit.getSelectionModel().getSelectedItem();
        if (produitSelectionne != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setContentText("Voulez-vous vraiment supprimer ce produit ?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    produitService.supprimerProduit(produitSelectionne);
                    chargerProduits();
                    resetFields();
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit supprimé avec succès !");
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez sélectionner un produit à supprimer.");
        }
    }

    private boolean isDateValid(LocalDate date) {
        if (date == null) {
            return false;
        }
        LocalDate minDate = LocalDate.of(2023, 1, 1);
        LocalDate today = LocalDate.now();
        return !date.isBefore(minDate) && !date.isAfter(today);
    }

    private boolean isNumeric(String str) {
        try {
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void resetFields() {
        nomProduitField.clear();
        prixField.clear();
        dateField.setValue(null);
        produitSelectionne = null;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }

    @FXML
    public void ouvrirAjouterProduit(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterproduit.fxml"));

            Parent parent = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter Produit");
            stage.setScene(new Scene(parent));
            stage.show();

            Stage currentStage = (Stage) btnAjouter.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ouvrirpage2(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/page2.fxml"));
            Parent parent = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Liste Catégorie");
            stage.setScene(new Scene(parent));
            stage.show();

            Stage currentStage = (Stage) naviguerverscategorie.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Méthode d'export vers Excel avec un style attractif
    @FXML
    private void exportToExcel() {
        // Création du classeur Excel (format XLSX)
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Produits");

        // Création d'un style pour les en-têtes (fond orange attractif, texte blanc)
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.MEDIUM);
        headerStyle.setBorderTop(BorderStyle.MEDIUM);
        headerStyle.setBorderLeft(BorderStyle.MEDIUM);
        headerStyle.setBorderRight(BorderStyle.MEDIUM);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setFontHeightInPoints((short) 14);
        headerStyle.setFont(headerFont);

        // Création de la ligne d'en-tête
        Row headerRow = sheet.createRow(0);
        String[] colonnes = {"Nom Produit", "Catégorie", "Prix (TND)", "Date", "Fournisseur"};
        for (int i = 0; i < colonnes.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(colonnes[i]);
            cell.setCellStyle(headerStyle);
        }

        // Style pour les cellules de données
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setAlignment(HorizontalAlignment.CENTER);

        // Remplissage des données depuis la liste des produits
        List<produit> produits = produitService.afficherProduit();
        int rowNum = 1;
        for (produit p : produits) {
            Row row = sheet.createRow(rowNum++);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(p.getNom_Produit());
            cell0.setCellStyle(dataStyle);

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(p.getCategorie());
            cell1.setCellStyle(dataStyle);

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(p.getPrix());
            cell2.setCellStyle(dataStyle);

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(p.getDate());
            cell3.setCellStyle(dataStyle);

            Cell cell4 = row.createCell(4);
            cell4.setCellValue(p.getFournisseur());
            cell4.setCellStyle(dataStyle);
        }

        // Ajustement automatique de la largeur des colonnes
        for (int i = 0; i < colonnes.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try {
            File file = new File("Produits_attractif.xlsx");
            FileOutputStream fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();
            workbook.close();

            // Ouvrir automatiquement le fichier généré
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            }
            showAlert(Alert.AlertType.INFORMATION, "Export réussi", "Le fichier Excel attractif a été généré et ouvert avec succès !");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'enregistrer ou d'ouvrir le fichier Excel !");
        }
    }

    // Méthode d'export vers PDF avec un style attractif
    @FXML
    private void exportToPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le fichier PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));
        fileChooser.setInitialFileName("Produits_attractif.pdf");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try {
                Document document = new Document(PageSize.A4, 36, 36, 54, 36);
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                // Titre du document avec fond orange attractif
                com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.WHITE);
                Paragraph title = new Paragraph("Liste des Produits", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20);

                PdfPTable titleTable = new PdfPTable(1);
                titleTable.setWidthPercentage(100);
                PdfPCell titleCell = new PdfPCell(title);
                titleCell.setBackgroundColor(new BaseColor(255, 102, 0)); // Couleur orange
                titleCell.setBorder(PdfPCell.NO_BORDER);
                titleCell.setPadding(10);
                titleTable.addCell(titleCell);
                document.add(titleTable);
                document.add(new Paragraph("\n"));

                // Création du tableau avec 5 colonnes
                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10f);
                table.setSpacingAfter(10f);
                table.setWidths(new int[]{3, 3, 2, 3, 3});

                // Style des en-têtes
                com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
                PdfPCell cell;
                String[] headers = {"Nom Produit", "Catégorie", "Prix (TND)", "Date", "Fournisseur"};
                for (String header : headers) {
                    cell = new PdfPCell(new Phrase(header, headerFont));
                    cell.setBackgroundColor(new BaseColor(255, 102, 0)); // Même orange attractif
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(8);
                    table.addCell(cell);
                }

                // Style pour les cellules de données
                com.itextpdf.text.Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

                // Remplissage du tableau avec les produits
                List<produit> produits = produitService.afficherProduit();
                for (produit p : produits) {
                    table.addCell(new PdfPCell(new Phrase(p.getNom_Produit(), dataFont)));
                    table.addCell(new PdfPCell(new Phrase(p.getCategorie(), dataFont)));
                    table.addCell(new PdfPCell(new Phrase(String.valueOf(p.getPrix()), dataFont)));
                    table.addCell(new PdfPCell(new Phrase(p.getDate(), dataFont)));
                    table.addCell(new PdfPCell(new Phrase(p.getFournisseur(), dataFont)));
                }

                document.add(table);
                document.close();

                // Ouvrir automatiquement le fichier généré
                if (file.exists()) {
                    Desktop.getDesktop().open(file);
                }
                showAlert(Alert.AlertType.INFORMATION, "Export réussi", "Le fichier PDF attractif a été généré et ouvert avec succès !");
            } catch (DocumentException | IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'enregistrer ou d'ouvrir le fichier PDF !");
            }
        }
    }

    @FXML
    private void afficherStatistiques() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/statistiques.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la nouvelle fenêtre
            StatistiquesController statsController = loader.getController();

            // Vérifier que la TableView contient des produits
            if (tableProduit.getItems().isEmpty()) {
                System.out.println("⚠ Aucune donnée à envoyer !");
                return;
            }

            // Envoyer les produits à la fenêtre Statistiques
            statsController.setProduits(tableProduit.getItems());

            // Ouvrir la fenêtre des statistiques
            Stage stage = new Stage();
            stage.setTitle("Statistiques des Produits");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}