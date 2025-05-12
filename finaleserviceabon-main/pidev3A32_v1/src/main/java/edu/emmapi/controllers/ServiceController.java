package edu.emmapi.controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import edu.emmapi.entities.Service;
import edu.emmapi.services.ServiceService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.beans.property.SimpleStringProperty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceController {
    @FXML private TableView<Service> serviceTable;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categorieFilter;
    @FXML private Slider prixFilter;
    @FXML private TextField nomField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField prixField;
    @FXML private Spinner<Integer> capaciteSpinner;
    @FXML private ComboBox<String> categorieCombo;
    @FXML private Spinner<Integer> dureeSpinner;
    @FXML private ComboBox<String> niveauCombo;
    @FXML private CheckBox actifCheck;
    @FXML private ComboBox<String> salleCombo;
    @FXML private TextField imageField;
    @FXML private LineChart<String, Number> statsChart;
    @FXML private VBox notificationPane;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Button toggleStatsButton;

    private ServiceService serviceService;
    private ObservableList<Service> services;
    private FilteredList<Service> filteredServices;
    private Timeline autoRefreshTimeline;
    private Stage statsStage;
    private BarChart<String, Number> categoryChart;
    private BarChart<String, Number> levelChart;

    @FXML
    public void initialize() {
        serviceService = new ServiceService();
        services = FXCollections.observableArrayList();
        filteredServices = new FilteredList<>(services, p -> true);

        serviceTable.setItems(filteredServices);

        // Initialize ComboBoxes
        niveauCombo.setItems(FXCollections.observableArrayList("Débutant", "Intermédiaire", "Avancé"));
        niveauCombo.setValue("Débutant");

        categorieCombo.setItems(FXCollections.observableArrayList("Fitness", "Musculation", "Cardio", "Yoga"));
        categorieCombo.setValue("Fitness");

        salleCombo.setItems(FXCollections.observableArrayList("OUVERT", "FERME"));
        salleCombo.setValue("OUVERT");

        setupTableColumns();
        setupFilters();
        setupCharts();
        setupAutoRefresh();
        setupValidation();

        nomField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                String category = categorieCombo.getValue();
                String level = niveauCombo.getValue();
                String description = generateFallbackDescription(newValue.trim(), category, level);
                descriptionArea.setText(description);
            } else {
                descriptionArea.clear();
            }
        });

        serviceTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        serviceTable.setRowFactory(tv -> {
            TableRow<Service> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    Service selectedService = row.getItem();
                    handleEdit(selectedService);
                }
            });
            return row;
        });

        services.addListener((ListChangeListener.Change<? extends Service> change) -> {
            while (change.next()) {
                updateCharts();
            }
        });
    }

    private void setupTableColumns() {
        TableColumn<Service, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<Service, Double> prixCol = new TableColumn<>("Prix");
        prixCol.setCellValueFactory(new PropertyValueFactory<>("prix"));
        prixCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double prix, boolean empty) {
                super.updateItem(prix, empty);
                setText(empty || prix == null ? null : String.format("%.2f €", prix));
            }
        });

        TableColumn<Service, String> niveauCol = new TableColumn<>("Niveau de Difficulté");
        niveauCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNiveauDifficulte()));

        TableColumn<Service, String> categorieCol = new TableColumn<>("Catégorie");
        categorieCol.setCellValueFactory(new PropertyValueFactory<>("categorie"));

        TableColumn<Service, String> salleCol = new TableColumn<>("Statut Salle");
        salleCol.setCellValueFactory(new PropertyValueFactory<>("salle"));

        TableColumn<Service, String> imageCol = new TableColumn<>("Image");
        imageCol.setCellValueFactory(new PropertyValueFactory<>("image"));

        TableColumn<Service, String> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Éditer");
            private final Button deleteBtn = new Button("Supprimer");

            {
                editBtn.getStyleClass().add("btn-primary");
                deleteBtn.getStyleClass().add("btn-danger");
                editBtn.setOnAction(e -> handleEdit(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e -> handleDelete(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(5, editBtn, deleteBtn));
            }
        });

        serviceTable.getColumns().setAll(nomCol, prixCol, niveauCol, categorieCol, salleCol, imageCol, actionCol);
    }

    private void setupFilters() {
        searchField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredServices.setPredicate(service -> {
                    if (newValue == null || newValue.isEmpty()) return true;
                    String lowerCaseFilter = newValue.toLowerCase();
                    return (service.getNom() != null && service.getNom().toLowerCase().contains(lowerCaseFilter)) ||
                            (service.getDescription() != null && service.getDescription().toLowerCase().contains(lowerCaseFilter)) ||
                            (service.getSalle() != null && service.getSalle().toLowerCase().contains(lowerCaseFilter)) ||
                            (service.getImage() != null && service.getImage().toLowerCase().contains(lowerCaseFilter));
                }));

        categorieFilter.setItems(FXCollections.observableArrayList("Toutes", "Fitness", "Musculation", "Cardio", "Yoga"));
        categorieFilter.setValue("Toutes");
        categorieFilter.setOnAction(e -> applyFilters());

        prixFilter.setMin(0);
        prixFilter.setMax(1000);
        prixFilter.setValue(1000);
        prixFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void applyFilters() {
        String selectedCategory = categorieFilter.getValue() != null ? categorieFilter.getValue() : "Toutes";
        filteredServices.setPredicate(service ->
                ("Toutes".equals(selectedCategory) || service.getCategorie().equals(selectedCategory)) &&
                        service.getPrix() <= prixFilter.getValue());
    }

    private void setupAutoRefresh() {
        autoRefreshTimeline = new Timeline(new KeyFrame(Duration.minutes(5), e -> {
            loadServices();
            showNotification("Données actualisées", "success");
        }));
        autoRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        autoRefreshTimeline.play();
    }

    private void setupValidation() {
        nomField.textProperty().addListener((obs, oldVal, newVal) -> {
            nomField.setStyle(newVal != null && newVal.matches("^[a-zA-ZÀ-ÿ0-9\\s-]{3,100}$") ? "" : "-fx-border-color: red;");
        });

        prixField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal == null || newVal.trim().isEmpty()) {
                    prixField.setStyle("-fx-border-color: red;");
                    return;
                }
                double prix = Double.parseDouble(newVal);
                prixField.setStyle(prix > 0 && prix <= 99999999.99 ? "" : "-fx-border-color: red;");
            } catch (NumberFormatException e) {
                prixField.setStyle("-fx-border-color: red;");
            }
        });

        salleCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            salleCombo.setStyle(newVal != null && (newVal.equals("OUVERT") || newVal.equals("FERME")) ? "" : "-fx-border-color: red;");
        });

        if (imageField != null) {
            imageField.textProperty().addListener((obs, oldVal, newVal) -> {
                imageField.setStyle(newVal == null || newVal.isEmpty() || newVal.length() <= 255 ? "" : "-fx-border-color: red;");
            });
        } else {
            System.err.println("Warning: imageField is not defined in the FXML file.");
        }
    }

    private String generateFallbackDescription(String serviceName, String category, String level) {
        Map<String, String> templatesByCategory = new HashMap<>();
        templatesByCategory.put("Fitness", "Notre service de %s est conçu pour améliorer votre condition physique globale. Adapté pour un niveau %s, ce cours vous permettra de développer votre endurance, votre force et votre souplesse dans une ambiance motivante.");
        templatesByCategory.put("Musculation", "Notre service de %s se concentre sur le renforcement musculaire ciblé. Pour un niveau %s, nos séances sont structurées pour maximiser vos gains musculaires tout en minimisant les risques de blessures.");
        templatesByCategory.put("Cardio", "Notre entraînement de %s est idéal pour améliorer votre système cardiovasculaire. Adapté aux %s, ce cours dynamique vous aidera à brûler des calories et à améliorer votre endurance dans une atmosphère énergique.");
        templatesByCategory.put("Yoga", "Notre séance de %s vous invite à trouver l'harmonie entre le corps et l'esprit. Ce cours de niveau %s vous guidera à travers des postures adaptées pour améliorer votre souplesse, votre force et votre concentration.");

        String template = templatesByCategory.getOrDefault(category, "Notre service de %s est spécialement conçu pour répondre à vos besoins. Adapté pour un niveau %s, ce cours vous permettra d'atteindre vos objectifs de manière efficace et agréable.");
        return String.format(template, serviceName, level.toLowerCase());
    }

    private void setupCharts() {
        XYChart.Series<String, Number> reservationSeries = new XYChart.Series<>();
        reservationSeries.setName("Réservations par jour");
        try {
            Map<String, Integer> reservations = serviceService.getReservationsPerDay();
            reservations.forEach((day, count) ->
                    reservationSeries.getData().add(new XYChart.Data<>(day.substring(0, 3), count)));
        } catch (SQLException e) {
            showNotification("Erreur lors du chargement des stats: " + e.getMessage(), "error");
        }
        statsChart.getData().add(reservationSeries);

        categoryChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        categoryChart.setTitle("Services par Catégorie");
        categoryChart.getXAxis().setLabel("Catégorie");
        categoryChart.getYAxis().setLabel("Nombre");
        ((NumberAxis) categoryChart.getYAxis()).setTickLabelFill(javafx.scene.paint.Color.web("#FFFFFF"));
        categoryChart.setStyle("-fx-background-color: #2A3B42;");

        levelChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        levelChart.setTitle("Services par Niveau");
        levelChart.getXAxis().setLabel("Niveau");
        levelChart.getYAxis().setLabel("Nombre");
        ((NumberAxis) levelChart.getYAxis()).setTickLabelFill(javafx.scene.paint.Color.web("#FFFFFF"));
        levelChart.setStyle("-fx-background-color: #2A3B42;");

        updateCharts();
    }

    private void updateCategoryChart() {
        categoryChart.getData().clear();
        XYChart.Series<String, Number> categorySeries = new XYChart.Series<>();
        categorySeries.setName("Nombre de Services par Catégorie");

        Map<String, Long> categoryCount = services.stream()
                .collect(Collectors.groupingBy(Service::getCategorie, Collectors.counting()));

        categoryCount.forEach((category, count) ->
                categorySeries.getData().add(new XYChart.Data<>(category, count)));

        categoryChart.getData().add(categorySeries);
    }

    private void updateLevelChart() {
        levelChart.getData().clear();
        XYChart.Series<String, Number> levelSeries = new XYChart.Series<>();
        levelSeries.setName("Nombre de Services par Niveau");

        Map<String, Long> levelCount = services.stream()
                .collect(Collectors.groupingBy(Service::getNiveauDifficulte, Collectors.counting()));

        levelCount.forEach((level, count) ->
                levelSeries.getData().add(new XYChart.Data<>(level, count)));

        levelChart.getData().add(levelSeries);
    }

    private void updateCharts() {
        updateCategoryChart();
        updateLevelChart();
    }

    @FXML
    private void showStatisticsPopup() {
        if (statsStage == null || !statsStage.isShowing()) {
            statsStage = new Stage();
            statsStage.setTitle("Statistiques des Services");
            VBox statsVBox = new VBox(10, categoryChart, levelChart);
            statsVBox.setPadding(new Insets(10));
            statsVBox.setStyle("-fx-background-color: #2A3B42;");

            Scene scene = new Scene(statsVBox, 600, 500);
            statsStage.setScene(scene);
            statsStage.setResizable(false);
            statsStage.show();

            updateCharts();
            statsStage.setOnCloseRequest(event -> statsStage = null);
        } else {
            statsStage.toFront();
            updateCharts();
        }
    }

    @FXML
    private void handleAjouterService() {
        try {
            if (!validateFields()) {
                showNotification("Veuillez corriger les erreurs", "error");
                return;
            }

            String categorie = categorieCombo.getValue();
            String niveau = niveauCombo.getValue();
            String salle = salleCombo.getValue();

            if (categorie == null || niveau == null || salle == null) {
                showNotification("Veuillez sélectionner une catégorie, un niveau et un statut salle", "error");
                return;
            }

            Service service = new Service();
            service.setNom(nomField.getText());
            service.setDescription(descriptionArea.getText());
            double prix = Double.parseDouble(prixField.getText());
            if (prix > 99999999.99) {
                showNotification("Le prix ne peut pas dépasser 99999999.99", "error");
                return;
            }
            service.setPrix(prix);
            service.setCapaciteMax(capaciteSpinner.getValue());
            service.setCategorie(categorie);
            service.setDureeMinutes(dureeSpinner.getValue());
            service.setNiveau(niveau.equals("Débutant") ? 1 :
                    niveau.equals("Intermédiaire") ? 2 : 3);
            service.setEstActif(actifCheck.isSelected());
            service.setSalle(salle);
            service.setImage(imageField != null ? imageField.getText() : null);

            loadingIndicator.setVisible(true);

            new Thread(() -> {
                try {
                    Service nouveauService = serviceService.ajouterService(service);
                    javafx.application.Platform.runLater(() -> {
                        services.add(nouveauService);
                        serviceTable.refresh();
                        clearFields();
                        loadingIndicator.setVisible(false);
                        showNotification("Service ajouté avec succès", "success");
                        updateCharts();
                    });
                } catch (Exception e) {
                    javafx.application.Platform.runLater(() -> {
                        loadingIndicator.setVisible(false);
                        showNotification("Erreur lors de l'ajout: " + e.getMessage(), "error");
                    });
                }
            }).start();

        } catch (NumberFormatException e) {
            loadingIndicator.setVisible(false);
            showNotification("Prix invalide", "error");
        } catch (Exception e) {
            loadingIndicator.setVisible(false);
            showNotification("Erreur lors de l'ajout: " + e.getMessage(), "error");
        }
    }

    private void handleEdit(Service service) {
        nomField.setText(service.getNom());
        descriptionArea.setText(service.getDescription());
        prixField.setText(String.valueOf(service.getPrix()));
        capaciteSpinner.getValueFactory().setValue(service.getCapaciteMax());
        categorieCombo.setValue(service.getCategorie());
        dureeSpinner.getValueFactory().setValue(service.getDureeMinutes());
        niveauCombo.setValue(service.getNiveauDifficulte());
        actifCheck.setSelected(service.isEstActif());
        salleCombo.setValue(service.getSalle());
        if (imageField != null) {
            imageField.setText(service.getImage());
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de mise à jour");
        alert.setHeaderText("Mettre à jour le service ?");
        alert.setContentText("Êtes-vous sûr de vouloir mettre à jour ce service ?");

        alert.showAndWait().filter(result -> result == ButtonType.OK).ifPresent(result -> updateService(service));
    }

    private void updateService(Service service) {
        try {
            if (!validateFields()) {
                showNotification("Veuillez corriger les erreurs", "error");
                return;
            }

            service.setNom(nomField.getText());
            service.setDescription(descriptionArea.getText());
            double prix = Double.parseDouble(prixField.getText());
            if (prix > 99999999.99) {
                showNotification("Le prix ne peut pas dépasser 99999999.99", "error");
                return;
            }
            service.setPrix(prix);
            service.setCapaciteMax(capaciteSpinner.getValue());
            service.setCategorie(categorieCombo.getValue());
            service.setDureeMinutes(dureeSpinner.getValue());
            service.setNiveau(niveauCombo.getValue().equals("Débutant") ? 1 :
                    niveauCombo.getValue().equals("Intermédiaire") ? 2 : 3);
            service.setEstActif(actifCheck.isSelected());
            service.setSalle(salleCombo.getValue());
            service.setImage(imageField != null ? imageField.getText() : null);

            loadingIndicator.setVisible(true);

            new Thread(() -> {
                try {
                    serviceService.updateService(service);
                    javafx.application.Platform.runLater(() -> {
                        serviceTable.refresh();
                        clearFields();
                        loadingIndicator.setVisible(false);
                        showNotification("Service mis à jour avec succès", "success");
                        updateCharts();
                    });
                } catch (SQLException e) {
                    javafx.application.Platform.runLater(() -> {
                        loadingIndicator.setVisible(false);
                        showNotification("Erreur lors de la mise à jour: " + e.getMessage(), "error");
                    });
                }
            }).start();

        } catch (NumberFormatException e) {
            loadingIndicator.setVisible(false);
            showNotification("Prix invalide", "error");
        } catch (Exception e) {
            loadingIndicator.setVisible(false);
            showNotification("Erreur lors de la mise à jour: " + e.getMessage(), "error");
        }
    }

    private void handleDelete(Service service) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce service ?");

        alert.showAndWait().filter(result -> result == ButtonType.OK).ifPresent(result -> {
            try {
                serviceService.deleteService(service.getId());
                services.remove(service);
                showNotification("Service supprimé avec succès", "success");
                updateCharts();
            } catch (Exception e) {
                showNotification("Erreur lors de la suppression: " + e.getMessage(), "error");
            }
        });
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();
        boolean isValid = true;

        String nom = nomField.getText();
        if (nom == null || nom.trim().isEmpty() || !nom.matches("^[a-zA-ZÀ-ÿ0-9\\s-]{3,100}$")) {
            errors.append("Nom invalide (3-100 caractères, lettres, chiffres, espaces, tirets)\n");
            isValid = false;
        }

        String prixText = prixField.getText();
        if (prixText == null || prixText.trim().isEmpty()) {
            errors.append("Prix requis\n");
            isValid = false;
        } else {
            try {
                double prix = Double.parseDouble(prixText);
                if (prix <= 0 || prix > 99999999.99) {
                    errors.append("Prix doit être entre 0.01 et 99999999.99\n");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                errors.append("Prix invalide (doit être un nombre)\n");
                isValid = false;
            }
        }

        int capacite = capaciteSpinner.getValue();
        if (capacite <= 0) {
            errors.append("Capacité maximale doit être positive\n");
            isValid = false;
        }

        String categorie = categorieCombo.getValue();
        if (categorie == null || categorie.trim().isEmpty() || categorie.length() > 50) {
            errors.append("Catégorie invalide (max 50 caractères)\n");
            isValid = false;
        }

        int duree = dureeSpinner.getValue();
        if (duree < 15 || duree > 240) {
            errors.append("Durée doit être entre 15 et 240 minutes\n");
            isValid = false;
        }

        String salle = salleCombo.getValue();
        if (salle == null || (!salle.equals("OUVERT") && !salle.equals("FERME"))) {
            errors.append("Statut salle invalide (OUVERT ou FERME requis)\n");
            isValid = false;
        }

        String image = imageField != null ? imageField.getText() : null;
        if (image != null && image.length() > 255) {
            errors.append("Image ne peut pas dépasser 255 caractères\n");
            isValid = false;
        }

        if (!isValid) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de validation");
            alert.setContentText(errors.toString());
            alert.showAndWait();
        }

        return isValid;
    }

    private void showNotification(String message, String type) {
        Label notification = new Label(message);
        notification.getStyleClass().add("notification-" + type);
        notification.setMaxWidth(Double.MAX_VALUE);
        notification.setAlignment(javafx.geometry.Pos.CENTER);
        notificationPane.getChildren().add(notification);

        new Timeline(new KeyFrame(Duration.seconds(5), evt -> notificationPane.getChildren().remove(notification))).play();
    }

    @FXML
    private void clearFields() {
        nomField.clear();
        descriptionArea.clear();
        prixField.clear();
        capaciteSpinner.getValueFactory().setValue(1);
        categorieCombo.setValue("Fitness");
        dureeSpinner.getValueFactory().setValue(30);
        niveauCombo.setValue("Débutant");
        actifCheck.setSelected(true);
        salleCombo.setValue("OUVERT");
        if (imageField != null) {
            imageField.clear();
        }
    }

    @FXML
    private void loadServices() {
        loadingIndicator.setVisible(true);
        services.clear();

        new Thread(() -> {
            try {
                List<Service> loadedServices = serviceService.getAllServices();
                javafx.application.Platform.runLater(() -> {
                    services.addAll(loadedServices);
                    loadingIndicator.setVisible(false);
                    updateCharts();
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    showNotification("Erreur lors du chargement: " + e.getMessage(), "error");
                });
            }
        }).start();
    }

    @FXML
    private void exportPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        File file = fileChooser.showSaveDialog(serviceTable.getScene().getWindow());

        if (file != null) {
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
                Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
                Font cellFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

                Paragraph title = new Paragraph("Liste des Services", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);
                document.add(new Paragraph("\n"));

                PdfPTable table = new PdfPTable(6);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10f);
                table.setSpacingAfter(10f);

                table.addCell(createCell("Nom", headerFont));
                table.addCell(createCell("Prix", headerFont));
                table.addCell(createCell("Niveau", headerFont));
                table.addCell(createCell("Catégorie", headerFont));
                table.addCell(createCell("Statut Salle", headerFont));
                table.addCell(createCell("Image", headerFont));

                for (Service service : services) {
                    table.addCell(createCell(service.getNom(), cellFont));
                    table.addCell(createCell(String.format("%.2f €", service.getPrix()), cellFont));
                    table.addCell(createCell(service.getNiveauDifficulte(), cellFont));
                    table.addCell(createCell(service.getCategorie(), cellFont));
                    table.addCell(createCell(service.getSalle(), cellFont));
                    table.addCell(createCell(service.getImage() != null ? service.getImage() : "", cellFont));
                }

                document.add(table);
                document.close();

                showNotification("PDF exporté avec succès", "success");
            } catch (DocumentException | IOException e) {
                showNotification("Erreur lors de l'exportation du PDF: " + e.getMessage(), "error");
            }
        }
    }

    private PdfPCell createCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setBorderWidth(1);
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        return cell;
    }

    public void cleanup() {
        if (autoRefreshTimeline != null) {
            autoRefreshTimeline.stop();
        }
        if (statsStage != null) {
            statsStage.close();
        }
    }
}