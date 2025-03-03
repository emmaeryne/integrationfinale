
package edu.emmapi.controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import edu.emmapi.entities.Service;
import edu.emmapi.services.ServiceService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.beans.property.SimpleStringProperty;

import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

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
    @FXML private LineChart<String, Number> statsChart;
    @FXML private VBox notificationPane;
    @FXML private ProgressIndicator loadingIndicator;

    private ServiceService serviceService;
    private ObservableList<Service> services;
    private FilteredList<Service> filteredServices;
    private Timeline autoRefreshTimeline;

    @FXML
    public void initialize() {
        serviceService = new ServiceService();
        services = FXCollections.observableArrayList();
        filteredServices = new FilteredList<>(services, p -> true);

        serviceTable.setItems(filteredServices);

        niveauCombo.setItems(FXCollections.observableArrayList("Débutant", "Intermédiaire", "Avancé"));
        niveauCombo.setValue("Débutant");

        categorieCombo.setItems(FXCollections.observableArrayList("Fitness", "Musculation", "Cardio", "Yoga"));
        categorieCombo.setValue("Fitness");

        setupTableColumns();
        setupFilters();
        setupCharts();
        setupAutoRefresh();
        loadServices();
        setupValidation();

        // Listener for service name field to automatically generate description
        nomField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
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
    }

    @FXML
    private void generateServiceDescription() {
        String serviceName = nomField.getText().trim();
        String category = categorieCombo.getValue();
        String level = niveauCombo.getValue();

        if (serviceName.isEmpty()) {
            showNotification("Veuillez entrer un nom de service", "error");
            return;
        }

        loadingIndicator.setVisible(true);

        new Thread(() -> {
            try {
                String description = generateFallbackDescription(serviceName, category, level);
                javafx.application.Platform.runLater(() -> {
                    descriptionArea.setText(description);
                    loadingIndicator.setVisible(false);
                    showNotification("Description générée avec succès", "success");
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    showNotification("Erreur lors de la génération: " + e.getMessage(), "error");
                });
            }
        }).start();
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

    private void setupTableColumns() {
        TableColumn<Service, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<Service, Double> prixCol = new TableColumn<>("Prix");
        prixCol.setCellValueFactory(new PropertyValueFactory<>("prix"));
        prixCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double prix, boolean empty) {
                super.updateItem(prix, empty);
                if (empty || prix == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f €", prix));
                }
            }
        });

        TableColumn<Service, String> niveauCol = new TableColumn<>("Niveau de Difficulté");
        niveauCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNiveauDifficulte()));

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
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editBtn, deleteBtn);
                    setGraphic(buttons);
                }
            }
        });

        serviceTable.getColumns().addAll(nomCol, prixCol, niveauCol, actionCol);
    }

    private void setupFilters() {
        searchField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredServices.setPredicate(service -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    return service.getNom().toLowerCase().contains(lowerCaseFilter) ||
                            service.getDescription().toLowerCase().contains(lowerCaseFilter);
                })
        );

        categorieFilter.setItems(FXCollections.observableArrayList("Toutes", "Fitness", "Musculation", "Cardio", "Yoga"));
        categorieFilter.setValue("Toutes");
        categorieFilter.setOnAction(e -> applyFilters());

        prixFilter.setMin(0);
        prixFilter.setMax(1000);
        prixFilter.setValue(1000);
        prixFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void applyFilters() {
        String selectedCategory = categorieFilter.getValue();
        if (selectedCategory == null) {
            selectedCategory = "Toutes";
        }

        String finalSelectedCategory = selectedCategory;
        filteredServices.setPredicate(service -> {
            boolean matchesCategory = finalSelectedCategory.equals("Toutes") || service.getCategorie().equals(finalSelectedCategory);
            boolean matchesPrice = service.getPrix() <= prixFilter.getValue();
            return matchesCategory && matchesPrice;
        });
    }

    private void setupCharts() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Réservations par jour");

        series.getData().add(new XYChart.Data<>("Lun", 23));
        series.getData().add(new XYChart.Data<>("Mar", 14));
        series.getData().add(new XYChart.Data<>("Mer", 15));
        series.getData().add(new XYChart.Data<>("Jeu", 24));
        series.getData().add(new XYChart.Data<>("Ven", 34));
        series.getData().add(new XYChart.Data<>("Sam", 36));
        series.getData().add(new XYChart.Data<>("Dim", 22));

        statsChart.getData().add(series);
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
            if (newVal != null && !newVal.matches("^[a-zA-Z0-9\\s-]{3,100}$")) {
                nomField.setStyle("-fx-border-color: red;");
            } else {
                nomField.setStyle("");
            }
        });

        prixField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d+")) {
                prixField.setStyle("-fx-border-color: red;");
            } else {
                try {
                    double prix = Double.parseDouble(newVal);
                    if (prix <= 0 || prix > 1000) {
                        prixField.setStyle("-fx-border-color: red;");
                    } else {
                        prixField.setStyle("");
                    }
                } catch (NumberFormatException e) {
                    prixField.setStyle("-fx-border-color: red;");
                }
            }
        });
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
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                // Set up a custom font
                Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
                Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
                Font cellFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

                // Title
                Paragraph title = new Paragraph("Liste des Services", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);
                document.add(new Paragraph("\n")); // Adding space

                // Create a table with custom styles
                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10f);
                table.setSpacingAfter(10f);

                // Header Row
                table.addCell(createCell("Nom", headerFont));
                table.addCell(createCell("Prix", headerFont));
                table.addCell(createCell("Niveau", headerFont));
                table.addCell(createCell("Catégorie", headerFont));

                // Adding services to the table
                for (Service service : services) {
                    table.addCell(createCell(service.getNom(), cellFont));
                    table.addCell(createCell(String.format("%.2f €", service.getPrix()), cellFont));
                    table.addCell(createCell(service.getNiveauDifficulte(), cellFont));
                    table.addCell(createCell(service.getCategorie(), cellFont));
                }

                document.add(table);
                document.close();

                showNotification("PDF exporté avec succès", "success");
            } catch (DocumentException | IOException e) {
                showNotification("Erreur lors de l'exportation du PDF: " + e.getMessage(), "error");
            }
        }
    }

    // Helper method to create a cell with custom font
    private PdfPCell createCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setBorderWidth(1);
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        return cell;
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

            if (categorie == null || niveau == null) {
                showNotification("Veuillez sélectionner une catégorie et un niveau", "error");
                return;
            }

            Service service = new Service();
            service.setNom(nomField.getText());
            service.setDescription(descriptionArea.getText());
            service.setPrix(Double.parseDouble(prixField.getText()));
            service.setcapaciteMax(capaciteSpinner.getValue());
            service.setCategorie(categorie);
            service.setdureeMinutes(dureeSpinner.getValue());
            service.setNiveau(niveau.equals("Débutant") ? 1 :
                    niveau.equals("Intermédiaire") ? 2 : 3);
            service.setEstActif(actifCheck.isSelected());

            loadingIndicator.setVisible(true);

            new Thread(() -> {
                Service nouveauService = serviceService.ajouterService(service);
                javafx.application.Platform.runLater(() -> {
                    services.add(nouveauService);
                    serviceTable.refresh();
                    clearFields();
                    loadingIndicator.setVisible(false);
                    showNotification("Service ajouté avec succès", "success");
                });
            }).start();

        } catch (Exception e) {
            showNotification("Erreur lors de l'ajout: " + e.getMessage(), "error");
        }
    }

    private void showNotification(String message, String type) {
        Label notification = new Label(message);
        notification.getStyleClass().add("notification-" + type);
        notification.setMaxWidth(Double.MAX_VALUE);
        notification.setAlignment(javafx.geometry.Pos.CENTER);

        notificationPane.getChildren().add(notification);

        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(5),
                evt -> notificationPane.getChildren().remove(notification)
        ));
        timeline.play();
    }

    private boolean validateFields() {
        boolean isValid = true;
        StringBuilder errors = new StringBuilder();

        if (nomField.getText().trim().isEmpty() ||
                !nomField.getText().matches("^[a-zA-Z0-9\\s-]{3,100}$")) {
            errors.append("Nom invalide\n");
            isValid = false;
        }

        try {
            double prix = Double.parseDouble(prixField.getText());
            if (prix <= 0 || prix > 1000) {
                errors.append("Prix invalide (0-1000)\n");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            errors.append("Prix invalide\n");
            isValid = false;
        }

        if (!isValid) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de validation");
            alert.setHeaderText(null);
            alert.setContentText(errors.toString());
            alert.showAndWait();
        }

        return isValid;
    }

    private void handleEdit(Service service) {
        nomField.setText(service.getNom());
        descriptionArea.setText(service.getDescription());
        prixField.setText(String.valueOf(service.getPrix()));
        capaciteSpinner.getValueFactory().setValue(service.getcapaciteMax());
        categorieCombo.setValue(service.getCategorie());
        dureeSpinner.getValueFactory().setValue(service.getdureeMinutes());
        niveauCombo.setValue(service.getNiveauDifficulte());
        actifCheck.setSelected(service.isEstActif());

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de mise à jour");
        alert.setHeaderText("Mettre à jour le service ?");
        alert.setContentText("Êtes-vous sûr de vouloir mettre à jour ce service ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            updateService(service);
        }
    }

    private void updateService(Service service) {
        try {
            if (!validateFields()) {
                showNotification("Veuillez corriger les erreurs", "error");
                return;
            }

            service.setNom(nomField.getText());
            service.setDescription(descriptionArea.getText());
            service.setPrix(Double.parseDouble(prixField.getText()));
            service.setcapaciteMax(capaciteSpinner.getValue());
            service.setCategorie(categorieCombo.getValue());
            service.setdureeMinutes(dureeSpinner.getValue());
            service.setNiveau(niveauCombo.getValue().equals("Débutant") ? 1 :
                    niveauCombo.getValue().equals("Intermédiaire") ? 2 : 3);
            service.setEstActif(actifCheck.isSelected());

            loadingIndicator.setVisible(true);

            new Thread(() -> {
                try {
                    serviceService.updateService(service);
                    javafx.application.Platform.runLater(() -> {
                        serviceTable.refresh();
                        clearFields();
                        loadingIndicator.setVisible(false);
                        showNotification("Service mis à jour avec succès", "success");
                    });
                } catch (SQLException e) {
                    javafx.application.Platform.runLater(() -> {
                        loadingIndicator.setVisible(false);
                        showNotification("Erreur lors de la mise à jour: " + e.getMessage(), "error");
                    });
                }
            }).start();

        } catch (Exception e) {
            showNotification("Erreur lors de la mise à jour: " + e.getMessage(), "error");
        }
    }

    private void handleDelete(Service service) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce service ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceService.deleteService(service.getId());
                services.remove(service);
                showNotification("Service supprimé avec succès", "success");
            } catch (Exception e) {
                showNotification("Erreur lors de la suppression : " + e.getMessage(), "error");
            }
        }
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
    }

    @FXML
    private void loadServices() {
        loadingIndicator.setVisible(true);
        services.clear();

        new Thread(() -> {
            List<Service> loadedServices = serviceService.getAllServices();
            javafx.application.Platform.runLater(() -> {
                services.addAll(loadedServices);
                loadingIndicator.setVisible(false);
            });
        }).start();
    }

    public void cleanup() {
        if (autoRefreshTimeline != null) {
            autoRefreshTimeline.stop();
        }
    }
}