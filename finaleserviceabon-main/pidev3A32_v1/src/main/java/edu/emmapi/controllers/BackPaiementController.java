package edu.emmapi.controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import edu.emmapi.entities.Commande;
import edu.emmapi.entities.Paiement;
import edu.emmapi.services.PaiementService;
import edu.emmapi.tools.MyConnection;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
public class BackPaiementController implements Initializable {

    @FXML private TableView<Paiement> paiementTable;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterStatus;
    @FXML private DatePicker filterDate;
    @FXML private Pagination pagination;
    @FXML private LineChart<String, Number> paiementChart;

    private final PaiementService paiementService = new PaiementService();
    private ObservableList<Paiement> paiements;
    private FilteredList<Paiement> filteredPaiements;

    @FXML private TableColumn<Paiement, Integer> idPaiementColumn;
    @FXML private TableColumn<Paiement, Integer> idCommandeColumn;
    @FXML private TableColumn<Paiement, Integer> idUtilisateurColumn;
    @FXML private TableColumn<Paiement, Double> montantColumn;
    @FXML private TableColumn<Paiement, String> modeDePaiementColumn;
    @FXML private TableColumn<Paiement, String> dateDePaiementColumn;
    @FXML private TableColumn<Paiement, String> statusColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureTableColumns();
        loadPaiements();
        setupFilters();
        setupPagination();
        setupChart();
        configureRowColors(); // Appliquer les couleurs des lignes
        initializeSearch();
    }

    private void configureRowColors() {
        paiementTable.setRowFactory(tv -> new TableRow<Paiement>() {
            @Override
            protected void updateItem(Paiement paiement, boolean empty) {
                super.updateItem(paiement, empty);
                if (paiement == null || empty) {
                    setStyle(""); // Pas de style si la ligne est vide
                } else {
                    switch (paiement.getStatus()) {
                        case "Validated":
                            setStyle("-fx-background-color: #d4edda;"); // Vert clair pour Validated
                            break;
                        case "Failed":
                            setStyle("-fx-background-color: #f8d7da;"); // Rouge clair pour Failed
                            break;
                        case "Canceled":
                            setStyle("-fx-background-color: #fff3cd;"); // Jaune clair pour Canceled
                            break;
                        case "Pending":
                            setStyle("-fx-background-color: #d1ecf1;"); // Bleu clair pour Pending
                            break;
                        default:
                            setStyle(""); // Pas de style pour les autres statuts
                    }
                }
            }
        });
    }


    private void initializeSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredPaiements.setPredicate(paiement -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Afficher tous les paiements si le champ de recherche est vide
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // Filtre par ID, montant, mode de paiement, date ou statut
                return String.valueOf(paiement.getIdPaiement()).contains(lowerCaseFilter)
                        || String.valueOf(paiement.getMontant()).contains(lowerCaseFilter)
                        || paiement.getModeDePaiement().toLowerCase().contains(lowerCaseFilter)
                        || paiement.getDateDePaiement().toString().contains(lowerCaseFilter)
                        || paiement.getStatus().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }
    // Méthode pour valider un paiement en fonction de l'ID du paiement




    private void configureTableColumns() {
        idPaiementColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdPaiement()).asObject());
        idCommandeColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdCommande()).asObject());
        idUtilisateurColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdUtilisateur()).asObject());
        montantColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getMontant()).asObject());
        modeDePaiementColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getModeDePaiement()));
        dateDePaiementColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateDePaiement().toString()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
    }
    private void updatePaiementStatus(Paiement paiement, String newStatus) {
        try {
            // Requête SQL mise à jour pour utiliser idCommande
            String query = "UPDATE paiement SET status = ? WHERE idCommande = ?";
            Connection connection = MyConnection.getInstance().getCnx();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, paiement.getIdCommande()); // Utiliser idCommande
            pstmt.executeUpdate();
            loadPaiements(); // Recharger les paiements après la mise à jour
            showAlert("Succès", "Le paiement a été mis à jour avec succès!");
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de mettre à jour le paiement: " + e.getMessage());
        }
    }

    @FXML
    public void validerPaiement(ActionEvent actionEvent) {
        Paiement selectedPaiement = paiementTable.getSelectionModel().getSelectedItem();
        if (selectedPaiement != null) {
            updatePaiementStatus(selectedPaiement, "Validated"); // Valider le paiement
        } else {
            showAlert("Erreur", "Veuillez sélectionner un paiement à valider.");
        }
    }

    @FXML
    public void rejeterPaiement(ActionEvent actionEvent) {
        Paiement selectedPaiement = paiementTable.getSelectionModel().getSelectedItem();
        if (selectedPaiement != null) {
            updatePaiementStatus(selectedPaiement, "Failed"); // Rejeter le paiement
        } else {
            showAlert("Erreur", "Veuillez sélectionner un paiement à rejeter.");
        }
    }



    private void loadPaiements() {
        paiements = FXCollections.observableArrayList(paiementService.getAllPaiements());
        filteredPaiements = new FilteredList<>(paiements);
        paiementTable.setItems(filteredPaiements);
    }

    private void setupFilters() {
        filterStatus.getItems().addAll("Pending", "Validated" , "Failed");
        filterStatus.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        filterDate.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void applyFilters() {
        filteredPaiements.setPredicate(paiement -> {
            boolean matchesStatus = filterStatus.getValue() == null || paiement.getStatus().equals(filterStatus.getValue());
            boolean matchesDate = filterDate.getValue() == null || paiement.getDateDePaiement().isEqual(filterDate.getValue());
            return matchesStatus && matchesDate;
        });
    }

    private void setupPagination() {
        pagination.setPageCount((int) Math.ceil(filteredPaiements.size() / 10.0));
        pagination.currentPageIndexProperty().addListener((obs, oldVal, newVal) -> {
            int fromIndex = newVal.intValue() * 10;
            int toIndex = Math.min(fromIndex + 10, filteredPaiements.size());
            paiementTable.setItems(FXCollections.observableArrayList(filteredPaiements.subList(fromIndex, toIndex)));
        });
    }


    private void setupChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Paiements par jour");

        paiements.stream()
                .collect(Collectors.groupingBy(Paiement::getDateDePaiement, Collectors.counting()))
                .forEach((date, count) -> series.getData().add(new XYChart.Data<>(date.toString(), count)));

        paiementChart.getData().add(series);
    }

    @FXML
    private void refreshPaiements() {
        loadPaiements();
    }

    @FXML
    private void exportExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Paiements");

            // Header
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Commande ID", "Utilisateur ID", "Montant", "Mode", "Date", "Statut"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Data
            int rowNum = 1;
            for (Paiement paiement : filteredPaiements) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(paiement.getIdPaiement());
                row.createCell(1).setCellValue(paiement.getIdCommande());
                row.createCell(2).setCellValue(paiement.getIdUtilisateur());
                row.createCell(3).setCellValue(paiement.getMontant());
                row.createCell(4).setCellValue(paiement.getModeDePaiement());
                row.createCell(5).setCellValue(paiement.getDateDePaiement().toString());
                row.createCell(6).setCellValue(paiement.getStatus());
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Save file
            try (FileOutputStream fileOut = new FileOutputStream("Paiements.xlsx")) {
                workbook.write(fileOut);
            }

            // Open file
            Desktop.getDesktop().open(new File("Paiements.xlsx"));
        } catch (IOException e) {
            showAlert("Erreur Excel", "Erreur lors de l'exportation Excel : " + e.getMessage());
        }
    }

    @FXML
    private void exportPDF() {
        try {
            // Créer le document PDF
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("Paiements.pdf"));
            document.open();

            // Ajouter l'image en arrière-plan
            PdfContentByte canvas = writer.getDirectContentUnder(); // Récupérer le contenu sous-jacent
            Image background = Image.getInstance("src/main/resources/hjk-removebg-preview.png"); // Charger l'image

            // Redimensionner l'image pour qu'elle corresponde à la taille de la page
            background.scaleAbsolute(document.getPageSize().getWidth(), document.getPageSize().getHeight());

            // Positionner l'image en bas à gauche (0, 0)
            background.setAbsolutePosition(0, 0);

            // Définir la transparence de l'image (optionnel)
            PdfGState gState = new PdfGState();
            gState.setFillOpacity(0.5f); // Transparence à 50%
            canvas.saveState();
            canvas.setGState(gState);
            canvas.addImage(background); // Ajouter l'image au PDF
            canvas.restoreState();

            // Ajouter la date d'exportation en haut à droite
            com.itextpdf.text.Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            dateFont.setColor(127, 140, 141); // Couleur grise
            Paragraph date = new Paragraph("Exporté le " + new java.util.Date(), dateFont);
            date.setAlignment(Element.ALIGN_RIGHT);
            document.add(date);

            // Ajouter un espace après la date
            document.add(new Paragraph("\n"));

            // Ajouter le titre "Liste des Paiements" en jaune
            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            titleFont.setColor(255, 193, 7); // Couleur jaune (RGB)
            Paragraph title = new Paragraph("Liste des Paiements", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Ajouter un espace après le titre
            document.add(new Paragraph("\n"));

            // Créer la table
            PdfPTable pdfTable = new PdfPTable(7);
            pdfTable.setWidthPercentage(100); // Largeur de la table à 100%
            addTableHeader(pdfTable);
            addTableRows(pdfTable);

            // Ajouter la table au document
            document.add(pdfTable);

            // Ajouter les informations de contact en bas au milieu
            com.itextpdf.text.Font contactFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            contactFont.setColor(0, 0, 0); // Couleur noire
            Paragraph contactInfo = new Paragraph(
                    "Complexe Sportif, 123 Rue de la Paix, 75000 Ariana\n" +
                            "Tel : 99646424, Email : info@www.hive.com",
                    contactFont
            );
            contactInfo.setAlignment(Element.ALIGN_CENTER);

            // Calculer la position Y pour placer le texte en bas de la page
            float yPosition = 50; // 50 points depuis le bas de la page

            // Utiliser ColumnText pour positionner le texte
            ColumnText columnText = new ColumnText(writer.getDirectContent());
            columnText.setSimpleColumn(
                    document.left(), // Bord gauche de la page
                    yPosition,      // Position Y depuis le bas
                    document.right(), // Bord droit de la page
                    yPosition + 50,  // Hauteur de la zone de texte
                    Element.ALIGN_CENTER, // Alignement au centre
                    Element.ALIGN_MIDDLE  // Alignement vertical au milieu
            );
            columnText.addElement(contactInfo); // Ajouter le texte à ColumnText
            columnText.go(); // Dessiner le texte

            // Fermer le document
            document.close();

            // Ouvrir le fichier PDF généré
            File file = new File("Paiements.pdf");
            Desktop.getDesktop().open(file);

        } catch (DocumentException | IOException e) {
            showAlert("Erreur PDF", e.getMessage());
        }
    }
    @FXML
    private void exportCSV() {
        try (FileWriter writer = new FileWriter("Paiements.csv")) {
            // Header
            writer.append("ID,Commande ID,Utilisateur ID,Montant,Mode,Date,Statut\n");

            // Data
            for (Paiement paiement : filteredPaiements) {
                writer.append(String.valueOf(paiement.getIdPaiement())).append(",");
                writer.append(String.valueOf(paiement.getIdCommande())).append(",");
                writer.append(String.valueOf(paiement.getIdUtilisateur())).append(",");
                writer.append(String.format("%.2f", paiement.getMontant())).append(",");
                writer.append(paiement.getModeDePaiement()).append(",");
                writer.append(paiement.getDateDePaiement().toString()).append(",");
                writer.append(paiement.getStatus()).append("\n");
            }

            Desktop.getDesktop().open(new File("Paiements.csv"));
        } catch (IOException e) {
            showAlert("Erreur CSV", "Erreur lors de l'exportation CSV : " + e.getMessage());
        }
    }

    private void addTableHeader(PdfPTable table) {
        String[] headers = {"ID", "Commande ID", "Utilisateur ID", "Montant", "Mode", "Date", "Statut"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }
    }

    private void addTableRows(PdfPTable table) {
        for (Paiement paiement : filteredPaiements) {
            table.addCell(String.valueOf(paiement.getIdPaiement()));
            table.addCell(String.valueOf(paiement.getIdCommande()));
            table.addCell(String.valueOf(paiement.getIdUtilisateur()));
            table.addCell(String.format("%.2f", paiement.getMontant()));
            table.addCell(paiement.getModeDePaiement());
            table.addCell(paiement.getDateDePaiement().toString());
            table.addCell(paiement.getStatus());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}