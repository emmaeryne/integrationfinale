package edu.emmapi.controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import edu.emmapi.entities.Paiement;
import edu.emmapi.services.PaiementService;
import edu.emmapi.tools.MyConnection;
import org.apache.poi.ss.usermodel.Cell; // Bonne importation
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
public class BackPaiementController {

    @FXML
    private TableView<Paiement> paiementTable;
    @FXML
    private ComboBox<String> filterStatus;
    @FXML
    private ComboBox<String> filterMode;
    @FXML
    private DatePicker filterDateStart;
    @FXML
    private DatePicker filterDateEnd;
    @FXML
    private TextField searchField;
    @FXML
    private Label pageInfo;
    private ObservableList<Paiement> allPaiements = FXCollections.observableArrayList();


    private final PaiementService paiementService = new PaiementService();
    private final ObservableList<Paiement> originalData = FXCollections.observableArrayList();
    private final FilteredList<Paiement> filteredData = new FilteredList<>(originalData);
    private final Pagination pagination = new Pagination();
    private int itemsPerPage = 20;
    private int currentPage = 0;
    private final SortedList<Paiement> sortedData = new SortedList<>(filteredData);



    @FXML
    public void initialize() {
        System.out.println("Initialisation du contrôleur...");

        // Configuration initiale
        configureTable();
        loadData();
        setupFilters();
        setupPagination();
        setupRealTimeUpdates();
        System.out.println("Initialisation terminée.");

        // Définir les cell factories
        setupMontantCellFactory();
        setupStatusCellFactory();
    }

    // Méthodes existantes (configureTable, loadData, setupFilters, etc.)
    // ...

    // Ajout des nouvelles méthodes
    private void loadScene(ActionEvent event, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Get the stage from the event source (button)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 500));  // Set new scene
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Affiche la trace de l'erreur
            showAlert("Erreur", "Impossible de charger la scène : " + e.getMessage());
        }
    }

    private void loadPaiements() {
        allPaiements = FXCollections.observableArrayList(paiementService.getAllPaiements());
        paiementTable.setItems(allPaiements);
        if (pagination != null) {
            pagination.setPageCount(calculatePageCount(allPaiements.size()));
        }
    }

    private int calculatePageCount(int totalItems) {
        return (totalItems + itemsPerPage - 1) / itemsPerPage;
    }

    private Node createPage(int pageIndex) {
        int fromIndex = pageIndex * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, allPaiements.size());
        paiementTable.setItems(FXCollections.observableArrayList(allPaiements.subList(fromIndex, toIndex)));
        return paiementTable;
    }

    private void updatePaiementStatus(Paiement paiement, String newStatus) {
        try {
            // Requête SQL mise à jour pour utiliser idUtilisateur
            String query = "UPDATE paiement SET status = ? WHERE idUtilisateur = ?";
            Connection connection = MyConnection.getInstance().getCnx();
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, paiement.getIdUtilisateur()); // Utiliser idUtilisateur
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
            updatePaiementStatus(selectedPaiement, "Completed");
        } else {
            showAlert("Erreur", "Veuillez sélectionner un paiement à valider.");
        }
    }

    @FXML
    public void rejeterPaiement(ActionEvent actionEvent) {
        Paiement selectedPaiement = paiementTable.getSelectionModel().getSelectedItem();
        if (selectedPaiement != null) {
            updatePaiementStatus(selectedPaiement, "Failed");
        } else {
            showAlert("Erreur", "Veuillez sélectionner un paiement à rejeter.");
        }
    }
    private void setupMontantCellFactory() {
        TableColumn<Paiement, Double> montantColumn = (TableColumn<Paiement, Double>) paiementTable.getColumns().get(3);
        montantColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.2f TND", item));
                    if (item > 1000) {
                        setStyle("-fx-background-color: #fff3cd;"); // Couleur pour les montants élevés
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }

    private void setupStatusCellFactory() {
        TableColumn<Paiement, String> statusColumn = (TableColumn<Paiement, String>) paiementTable.getColumns().get(6);
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "Completed":
                            setStyle("-fx-background-color: #d4edda;"); // Vert pour les paiements complétés
                            break;
                        case "Pending":
                            setStyle("-fx-background-color: #fff3cd;"); // Jaune pour les paiements en attente
                            break;
                        case "Failed":
                            setStyle("-fx-background-color: #f8d7da;"); // Rouge pour les paiements échoués
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
    }

    private void updateTable() {
        // Calculer les indices de début et de fin pour la pagination
        int fromIndex = currentPage * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, allPaiements.size());

        // Vérifier si les indices sont valides
        if (fromIndex >= allPaiements.size() || fromIndex < 0) {
            paiementTable.setItems(FXCollections.observableArrayList()); // Aucune donnée à afficher
        } else {
            // Extraire la sous-liste des données à afficher
            List<Paiement> subList = allPaiements.subList(fromIndex, toIndex);
            paiementTable.setItems(FXCollections.observableArrayList(subList));
        }

        // Mettre à jour le label d'information de la page
        pageInfo.setText("Page " + (currentPage + 1) + " / " + ((allPaiements.size() + itemsPerPage - 1) / itemsPerPage));
    }

    private void setupRealTimeUpdates() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (dataHasChanged()) {
                        refreshData();
                    }
                });
            }
        }, 0, 5000); // Vérifie toutes les 5 secondes
    }

    private boolean dataHasChanged() {
        List<Paiement> newData = paiementService.getAllPaiements();
        return !newData.equals(originalData);
    }

    private void sortData() {
        // Implémentez la logique de tri ici
        // Par exemple, vous pouvez trier par montant ou par date
        Comparator<Paiement> comparator = Comparator.comparing(Paiement::getDateDePaiement); // Tri par date
        sortedData.setComparator(comparator); // Set the comparator on sortedData
    }

    private void configureTable() {
        // Effacer les colonnes existantes pour éviter les doublons
        paiementTable.getColumns().clear();

        // Créer les colonnes et les lier aux propriétés de la classe Paiement
        TableColumn<Paiement, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idPaiement"));

        TableColumn<Paiement, Integer> commandeIdColumn = new TableColumn<>("Commande ID");
        commandeIdColumn.setCellValueFactory(new PropertyValueFactory<>("idCommande"));

        TableColumn<Paiement, Integer> utilisateurIdColumn = new TableColumn<>("Utilisateur ID");
        utilisateurIdColumn.setCellValueFactory(new PropertyValueFactory<>("idUtilisateur"));

        TableColumn<Paiement, Double> montantColumn = new TableColumn<>("Montant");
        montantColumn.setCellValueFactory(new PropertyValueFactory<>("montant"));

        TableColumn<Paiement, String> modeColumn = new TableColumn<>("Mode de paiement");
        modeColumn.setCellValueFactory(new PropertyValueFactory<>("modeDePaiement"));

        TableColumn<Paiement, LocalDate> dateColumn = new TableColumn<>("Date de paiement");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateDePaiement"));

        TableColumn<Paiement, String> statusColumn = new TableColumn<>("Statut");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Ajouter les colonnes à la TableView
        paiementTable.getColumns().addAll(idColumn, commandeIdColumn, utilisateurIdColumn, montantColumn, modeColumn, dateColumn, statusColumn);
    }

    private void loadData() {
        List<Paiement> paiements = paiementService.getAllPaiements();
        System.out.println("Nombre de paiements chargés : " + paiements.size()); // Log pour débogage

        // Mettre à jour la liste observable
        allPaiements.setAll(paiements);

        // Mettre à jour la TableView avec les données filtrées
        updateTable();
    }

    private void setupFilters() {
        filterStatus.getItems().addAll("Completed", "Pending", "Failed");
        filterMode.getItems().addAll("Cash", "Carte bancaire", "Chèque", "Virement");

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        filterStatus.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        filterMode.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        filterDateStart.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        filterDateEnd.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    // --- Correction 1 : Remplacer updatePagination() par updatePage() ---
    @FXML
    private void applyFilters() {
        filteredData.setPredicate(paiement -> {
            boolean matches = true;

            // Filtre par recherche
            if (!searchField.getText().isEmpty()) {
                String searchLower = searchField.getText().toLowerCase();
                matches &= paiement.toString().toLowerCase().contains(searchLower);
            }

            // Filtre par statut
            if (filterStatus.getValue() != null) {
                matches &= paiement.getStatus().equals(filterStatus.getValue());
            }

            // Filtre par mode de paiement
            if (filterMode.getValue() != null) {
                matches &= paiement.getModeDePaiement().equals(filterMode.getValue());
            }

            // Filtre par date
            if (filterDateStart.getValue() != null && filterDateEnd.getValue() != null) {
                matches &= !paiement.getDateDePaiement().isBefore(filterDateStart.getValue()) &&
                        !paiement.getDateDePaiement().isAfter(filterDateEnd.getValue());
            }

            return matches;
        });

        // Mettre à jour la TableView après l'application des filtres
        updateTable();
    }

    private void updatePagination() {
        int pageCount = (int) Math.ceil((double) filteredData.size() / itemsPerPage);
        pagination.setPageCount(pageCount);
        pagination.setCurrentPageIndex(currentPage);
    }

    // --- Modification de setupPagination() ---
    private void setupPagination() {
        updatePagination(); // Initialiser la pagination

        pagination.currentPageIndexProperty().addListener((obs, oldVal, newVal) -> {
            currentPage = newVal.intValue();
            updatePage();
        });


    }

    // --- Modification de updatePage() pour utiliser updatePagination() ---
    private void updatePage() {
        updatePagination(); // Met à jour le nombre de pages

        int fromIndex = currentPage * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, filteredData.size());

        if (fromIndex >= filteredData.size()) {
            currentPage = 0;
            fromIndex = 0;
            toIndex = Math.min(itemsPerPage, filteredData.size());
        }

        paiementTable.setItems(FXCollections.observableArrayList(
                filteredData.subList(fromIndex, toIndex)
        ));
        pageInfo.setText("Page " + (currentPage + 1) + "/" + pagination.getPageCount());
    }


    @FXML
    private void exportCSV() {
        try (FileWriter writer = new FileWriter("Paiements.csv")) {
            // Write header
            writer.append("ID,Commande ID,Utilisateur ID,Montant,Mode,Date,Statut\n");

            // Write data
            for (Paiement paiement : filteredData) {
                writer.append(String.valueOf(paiement.getIdPaiement())).append(",");
                writer.append(String.valueOf(paiement.getIdCommande())).append(",");
                writer.append(String.valueOf(paiement.getIdUtilisateur())).append(",");
                writer.append(String.format("%.2f", paiement.getMontant())).append(",");
                writer.append(paiement.getModeDePaiement()).append(",");
                writer.append(paiement.getDateDePaiement().toString()).append(",");
                writer.append(paiement.getStatus()).append("\n");
            }

            // Open the file
            File file = new File("Paiements.csv");
            Desktop.getDesktop().open(file);

        } catch (IOException e) {
            showAlert("Erreur CSV", "Une erreur s'est produite lors de l'exportation vers CSV: " + e.getMessage());
        }
    }

    @FXML
    private void handleExport() {
        // Créer une boîte de dialogue personnalisée avec des icônes
        Alert exportDialog = new Alert(Alert.AlertType.CONFIRMATION);
        exportDialog.setTitle("Exporter les Données");
        exportDialog.setHeaderText("Choisissez un format d'exportation");

        // Ajouter des boutons avec des icônes
        ButtonType pdfButton = new ButtonType("PDF", ButtonBar.ButtonData.OTHER);
        ButtonType excelButton = new ButtonType("Excel", ButtonBar.ButtonData.OTHER);
        ButtonType csvButton = new ButtonType("CSV", ButtonBar.ButtonData.OTHER);
        ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        exportDialog.getButtonTypes().setAll(pdfButton, excelButton, csvButton, cancelButton);

        // Afficher la boîte de dialogue et gérer la réponse
        Optional<ButtonType> result = exportDialog.showAndWait();
        result.ifPresent(buttonType -> {
            if (buttonType == pdfButton) {
                exportPDF();
            } else if (buttonType == excelButton) {
                exportExcel();
            } else if (buttonType == csvButton) {
                exportCSV();
            }
        });
    }

    @FXML
    private void exportExcel() {
        try {
            // Create a new workbook and sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Paiements");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Commande ID", "Utilisateur ID", "Montant", "Mode", "Date", "Statut"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Populate data rows
            int rowNum = 1;
            for (Paiement paiement : filteredData) {
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

            // Write the output to a file
            try (FileOutputStream fileOut = new FileOutputStream("Paiements.xlsx")) {
                workbook.write(fileOut);
            }

            // Close the workbook
            workbook.close();

            // Open the file
            File file = new File("Paiements.xlsx");
            Desktop.getDesktop().open(file);

        } catch (IOException e) {
            showAlert("Erreur Excel", "Une erreur s'est produite lors de l'exportation vers Excel: " + e.getMessage());
        }
    }

    @FXML
    private void showHistory() {
        Stage historyStage = new Stage();
        TableView<Paiement> historyTable = new TableView<>();

        // Créer les colonnes du tableau
        TableColumn<Paiement, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idPaiement"));

        TableColumn<Paiement, Double> montantColumn = new TableColumn<>("Montant");
        montantColumn.setCellValueFactory(new PropertyValueFactory<>("montant"));

        TableColumn<Paiement, String> statusColumn = new TableColumn<>("Statut");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Ajouter les colonnes au tableau
        historyTable.getColumns().addAll(idColumn, montantColumn, statusColumn);

        // Charger les données historiques (exemple)
        ObservableList<Paiement> historyData = FXCollections.observableArrayList(
                new Paiement(1, 101, 201, 100.0, "Carte bancaire", LocalDate.now(), "Completed"),
                new Paiement(2, 102, 202, 200.0, "Cash", LocalDate.now(), "Pending")
        );
        historyTable.setItems(historyData);

        // Afficher la fenêtre
        VBox vbox = new VBox(historyTable);
        Scene scene = new Scene(vbox, 400, 300);
        historyStage.setScene(scene);
        historyStage.setTitle("Historique des Paiements");
        historyStage.show();
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
            Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            dateFont.setColor(127, 140, 141); // Couleur grise
            Paragraph date = new Paragraph("Exporté le " + new java.util.Date(), dateFont);
            date.setAlignment(Element.ALIGN_RIGHT);
            document.add(date);

            // Ajouter un espace après la date
            document.add(new Paragraph("\n"));

            // Ajouter le titre "Liste des Paiements" en jaune
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
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
            Font contactFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
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
    private void addTableHeader(PdfPTable table) {
        Stream.of("ID", "Commande ID", "Utilisateur ID", "Montant", "Mode", "Date", "Statut")
                .forEach(header -> table.addCell(new Phrase(header)));
    }

    private void addTableRows(PdfPTable table) {
        for (Paiement p : filteredData) {
            table.addCell(String.valueOf(p.getIdPaiement()));
            table.addCell(String.valueOf(p.getIdCommande()));
            table.addCell(String.valueOf(p.getIdUtilisateur()));
            table.addCell(String.format("%.2f TND", p.getMontant()));
            table.addCell(p.getModeDePaiement());
            table.addCell(p.getDateDePaiement().toString());
            table.addCell(p.getStatus());
        }
    }

    @FXML
    private void showCharts() {
        Stage stage = new Stage();
        VBox vbox = new VBox();

        // Ajouter un choix de type de graphique
        ChoiceBox<String> chartType = new ChoiceBox<>();
        chartType.getItems().addAll("Statut des Paiements", "Montant des Paiements par Jour");
        chartType.setValue("Statut des Paiements");

        // Graphique initial (Statut des Paiements)
        PieChart statusChart = new PieChart();
        statusChart.setTitle("Statut des Paiements");
        Map<String, Long> statusCounts = originalData.stream()
                .collect(Collectors.groupingBy(Paiement::getStatus, Collectors.counting()));
        statusCounts.forEach((status, count) ->
                statusChart.getData().add(new PieChart.Data(status + " (" + count + ")", count)));

        // Graphique alternatif (Montant des Paiements par Jour)
        LineChart<Number, Number> amountChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        amountChart.setTitle("Montant des Paiements par Jour");
        ((NumberAxis) amountChart.getXAxis()).setLabel("Date");
        ((NumberAxis) amountChart.getYAxis()).setLabel("Montant");
        Map<LocalDate, Double> dailyAmounts = originalData.stream()
                .collect(Collectors.groupingBy(
                        Paiement::getDateDePaiement,
                        Collectors.summingDouble(Paiement::getMontant)
                ));
        XYChart.Series series = new XYChart.Series();
        series.setName("Montant");
        dailyAmounts.forEach((date, amount) ->
                series.getData().add(new XYChart.Data<>(date.toEpochDay(), amount)));
        amountChart.getData().add(series);

        // Gérer le changement de type de graphique
        chartType.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            vbox.getChildren().clear();
            if (newVal.equals("Statut des Paiements")) {
                vbox.getChildren().add(statusChart);
            } else {
                vbox.getChildren().add(amountChart);
            }
        });

        vbox.getChildren().addAll(chartType, statusChart);
        stage.setScene(new Scene(vbox, 800, 600));
        stage.show();
    }

    @FXML
    private void refreshData() {
        List<Paiement> newData = paiementService.getAllPaiements();
        originalData.setAll(newData);
        applyFilters();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void previousPage(ActionEvent actionEvent) {
        if (currentPage > 0) {
            currentPage--; // Aller à la page précédente
            updateTable(); // Mettre à jour la TableView
        } else {
            showAlert("Information", "Vous êtes déjà sur la première page.");
        }
    }

    @FXML
    public void nextPage(ActionEvent actionEvent) {
        int totalPages = (allPaiements.size() + itemsPerPage - 1) / itemsPerPage;
        if (currentPage < totalPages - 1) {
            currentPage++; // Aller à la page suivante
            updateTable(); // Mettre à jour la TableView
        } else {
            showAlert("Information", "Vous êtes déjà sur la dernière page.");
        }
    }
}
