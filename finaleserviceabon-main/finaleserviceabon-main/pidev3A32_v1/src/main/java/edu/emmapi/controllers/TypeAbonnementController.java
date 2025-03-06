
package edu.emmapi.controllers;

import edu.emmapi.entities.TypeAbonnement;
import edu.emmapi.services.TypeAbonnementService;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class TypeAbonnementController {
    @FXML private TableView<TypeAbonnement> typeAbonnementTable;
    @FXML private TableColumn<TypeAbonnement, String> nomColumn;
    @FXML private TableColumn<TypeAbonnement, String> descriptionColumn;
    @FXML private TableColumn<TypeAbonnement, Double> prixColumn;
    @FXML private TableColumn<TypeAbonnement, Integer> dureeEnMoisColumn;
    @FXML private TableColumn<TypeAbonnement, Boolean> isPremiumColumn;

    @FXML private TextField nomField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField prixField;
    @FXML private Spinner<Integer> dureeEnMoisSpinner;
    @FXML private CheckBox isPremiumCheck;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private Button generateRecommendationsButton;
    @FXML private Button analyzeTrendsButton;
    @FXML private TextArea feedbackArea;
    @FXML private Button loadExcelButton;
    @FXML private ProgressIndicator loadingIndicator;

    private TypeAbonnementService typeAbonnementService;
    private ObservableList<TypeAbonnement> typeAbonnements;

    @FXML
    public void initialize() {
        typeAbonnementService = new TypeAbonnementService();
        typeAbonnements = FXCollections.observableArrayList();
        typeAbonnementTable.setItems(typeAbonnements);
        setupTableColumns();
        loadTypeAbonnements();
        setupSearch();
        setupTableSelectionListener();
        setupColumnSorting(); // Ajout du tri par colonne

        sortComboBox.setItems(FXCollections.observableArrayList("prix_asc", "prix_desc"));
        dureeEnMoisSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 120, 1));

        // Mettre à jour la description en temps réel
        setupRealTimeDescription();
    }

    private void setupTableColumns() {
        nomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        prixColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrix()).asObject());
        dureeEnMoisColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDureeEnMois()).asObject());
        isPremiumColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().getIsPremium()));
    }

    private void setupColumnSorting() {
        // Activer le tri par clic sur les en-têtes de colonne
        nomColumn.setSortable(true);
        descriptionColumn.setSortable(true);
        prixColumn.setSortable(true);
        dureeEnMoisColumn.setSortable(true);
        isPremiumColumn.setSortable(true);
    }

    private void loadTypeAbonnements() {
        List<TypeAbonnement> loadedTypeAbonnements = typeAbonnementService.getAllTypeAbonnements(null);
        typeAbonnements.clear();
        typeAbonnements.addAll(loadedTypeAbonnements);
        updateFeedback("Liste des abonnements chargée avec succès.");
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTypeAbonnements(newValue);
            updateFeedback("Recherche en cours pour : " + newValue);
        });
    }

    private void filterTypeAbonnements(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            typeAbonnementTable.setItems(typeAbonnements);
        } else {
            List<TypeAbonnement> filteredList = typeAbonnements.stream()
                    .filter(type -> type.getNom().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
            typeAbonnementTable.setItems(FXCollections.observableArrayList(filteredList));
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();
        filterTypeAbonnements(keyword);
    }

    @FXML
    private void handleSort() {
        String selectedSort = sortComboBox.getValue();
        List<TypeAbonnement> sortedTypeAbonnements = typeAbonnementService.getAllTypeAbonnements(selectedSort);
        typeAbonnements.clear();
        typeAbonnements.addAll(sortedTypeAbonnements);
        updateFeedback("Liste triée par : " + (selectedSort != null ? selectedSort : "défaut"));
    }

    @FXML
    private void handleAjouterTypeAbonnement() {
        try {
            validateInputWithoutDescription();
            TypeAbonnement typeAbonnement = new TypeAbonnement();
            typeAbonnement.setNom(nomField.getText());
            typeAbonnement.setPrix(Double.parseDouble(prixField.getText()));
            typeAbonnement.setDureeEnMois(dureeEnMoisSpinner.getValue());
            typeAbonnement.setIsPremium(isPremiumCheck.isSelected());
            typeAbonnement.setDescription(genererDescription(typeAbonnement));

            typeAbonnementService.ajouterTypeAbonnement(typeAbonnement);
            loadTypeAbonnements();
            clearFields();
            updateFeedback("Abonnement ajouté avec succès : " + typeAbonnement.getNom());
        } catch (NumberFormatException e) {
            showAlert("Veuillez entrer un prix valide (nombre).");
        } catch (Exception e) {
            showAlert("Erreur lors de l’ajout : " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateTypeAbonnement() {
        TypeAbonnement selected = typeAbonnementTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                validateInputWithoutDescription();
                selected.setNom(nomField.getText());
                selected.setPrix(Double.parseDouble(prixField.getText()));
                selected.setDureeEnMois(dureeEnMoisSpinner.getValue());
                selected.setIsPremium(isPremiumCheck.isSelected());
                selected.setDescription(genererDescription(selected));

                typeAbonnementService.updateTypeAbonnement(selected);
                loadTypeAbonnements();
                clearFields();
                updateFeedback("Abonnement mis à jour avec succès : " + selected.getNom());
            } catch (NumberFormatException e) {
                showAlert("Veuillez entrer un prix valide (nombre).");
            } catch (Exception e) {
                showAlert("Erreur lors de la mise à jour : " + e.getMessage());
            }
        } else {
            showAlert("Veuillez sélectionner un abonnement à mettre à jour.");
        }
    }

    @FXML
    private void handleDeleteTypeAbonnement() {
        TypeAbonnement selected = typeAbonnementTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Êtes-vous sûr de vouloir supprimer cet abonnement ?");
            confirmationAlert.setHeaderText(null);
            //confirmationAlert.setStyle("-fx-background-color: #2E3440; -fx-text-fill: #f4eeee;");
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    typeAbonnementService.deleteTypeAbonnement(selected.getId());
                    loadTypeAbonnements();
                    clearFields();
                    updateFeedback("Abonnement supprimé avec succès : " + selected.getNom());
                }
            });
        } else {
            showAlert("Veuillez sélectionner un abonnement à supprimer.");
        }
    }

    private void setupTableSelectionListener() {
        typeAbonnementTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displaySelectedTypeAbonnement(newValue);
                updateFeedback("Abonnement sélectionné : " + newValue.getNom());
            }
        });
    }

    private void displaySelectedTypeAbonnement(TypeAbonnement typeAbonnement) {
        nomField.setText(typeAbonnement.getNom());
        descriptionArea.setText(typeAbonnement.getDescription());
        prixField.setText(String.valueOf(typeAbonnement.getPrix()));
        dureeEnMoisSpinner.getValueFactory().setValue(typeAbonnement.getDureeEnMois());
        isPremiumCheck.setSelected(typeAbonnement.getIsPremium());
    }

    @FXML
    private void handleGeneratePriceRecommendations() {
        List<TypeAbonnement> recommendations = typeAbonnementService.genererRecommandationsPrix();
        showRecommendations(recommendations);
        updateFeedback("Recommandations de prix générées.");
    }

    @FXML
    private void handleAnalyzeTrends() {
        String insights = typeAbonnementService.analyserTendances();
        showAlert(insights);
        updateFeedback("Analyse des tendances effectuée.");
    }

    @FXML
    private void handleLoadExcel() {
        loadingIndicator.setVisible(true); // Afficher l’indicateur de chargement
        try {
            String cheminFichier = "src\\main\\resources\\excel\\abonnement.xlsx"; // Chemin exact
            FileInputStream file = new FileInputStream(cheminFichier);
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);

            typeAbonnements.clear();
            int rowsProcessed = 0;
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Ignorer l’en-tête

                if (row.getCell(0) == null || (row.getCell(0).getStringCellValue() != null && row.getCell(0).getStringCellValue().trim().isEmpty())) {
                    continue; // Ignorer les lignes vides ou sans nom
                }

                TypeAbonnement abonnement = new TypeAbonnement();

                // Nom (colonne 0)
                abonnement.setNom(row.getCell(0) != null ? row.getCell(0).getStringCellValue() : "");

                // Prix (colonne 1)
                if (row.getCell(1) != null) {
                    switch (row.getCell(1).getCellType()) {
                        case NUMERIC:
                            abonnement.setPrix(row.getCell(1).getNumericCellValue());
                            break;
                        case STRING:
                            try {
                                abonnement.setPrix(Double.parseDouble(row.getCell(1).getStringCellValue().trim()));
                            } catch (NumberFormatException e) {
                                abonnement.setPrix(0.0);
                                updateFeedback("Avertissement : Prix non valide pour une ligne, valeur par défaut utilisée (0.0).");
                            }
                            break;
                        default:
                            abonnement.setPrix(0.0);
                    }
                } else {
                    abonnement.setPrix(0.0);
                }

                // Durée (colonne 2)
                if (row.getCell(2) != null) {
                    switch (row.getCell(2).getCellType()) {
                        case NUMERIC:
                            abonnement.setDureeEnMois((int) row.getCell(2).getNumericCellValue());
                            break;
                        case STRING:
                            try {
                                abonnement.setDureeEnMois(Integer.parseInt(row.getCell(2).getStringCellValue().trim()));
                            } catch (NumberFormatException e) {
                                abonnement.setDureeEnMois(1);
                                updateFeedback("Avertissement : Durée non valide pour une ligne, valeur par défaut utilisée (1).");
                            }
                            break;
                        default:
                            abonnement.setDureeEnMois(1);
                    }
                } else {
                    abonnement.setDureeEnMois(1);
                }

                // Premium (colonne 3)
                if (row.getCell(3) != null) {
                    switch (row.getCell(3).getCellType()) {
                        case BOOLEAN:
                            abonnement.setIsPremium(row.getCell(3).getBooleanCellValue());
                            break;
                        case STRING:
                            try {
                                abonnement.setIsPremium(Boolean.parseBoolean(row.getCell(3).getStringCellValue().trim()));
                            } catch (Exception e) {
                                abonnement.setIsPremium(false);
                                updateFeedback("Avertissement : Valeur Premium non valide pour une ligne, valeur par défaut utilisée (false).");
                            }
                            break;
                        default:
                            abonnement.setIsPremium(false);
                    }
                } else {
                    abonnement.setIsPremium(false);
                }

                abonnement.setDescription(genererDescription(abonnement));
                typeAbonnements.add(abonnement);
                rowsProcessed++;
            }
            workbook.close();
            file.close();
            typeAbonnementTable.refresh();
            updateFeedback(rowsProcessed + " abonnements chargés depuis Excel avec succès.");
            showAlert("Abonnements chargés avec succès !");
        } catch (IOException e) {
            updateFeedback("Erreur lors du chargement du fichier Excel : " + e.getMessage());
            showAlert("Erreur lors du chargement du fichier Excel : " + e.getMessage());
        } finally {
            loadingIndicator.setVisible(false); // Cacher l’indicateur une fois terminé
        }
    }

    // Méthode pour générer une description automatiquement
    private String genererDescription(TypeAbonnement abonnement) {
        String nom = abonnement.getNom();
        double prix = abonnement.getPrix();
        int duree = abonnement.getDureeEnMois();
        boolean isPremium = abonnement.getIsPremium();

        return String.format("Découvrez l'%s pour seulement %.2f€/mois ! Profitez d'une expérience %s pendant %d mois, parfaite pour %s.",
                nom, prix, isPremium ? "premium complète" : "essentielle", duree,
                isPremium ? "les utilisateurs exigeants" : "un usage quotidien");
    }

    // Validation sans exiger la description
    private void validateInputWithoutDescription() {
        if (nomField.getText().isEmpty()) {
            showAlert("Le nom doit être rempli.");
            throw new IllegalArgumentException("Le nom est requis.");
        }
        if (prixField.getText().isEmpty() || !prixField.getText().matches("\\d+(\\.\\d+)?")) {
            showAlert("Veuillez entrer un prix valide (nombre, ex. 15.50).");
            throw new IllegalArgumentException("Prix invalide.");
        }
    }

    private void setupRealTimeDescription() {
        // Mettre à jour la description en temps réel en fonction des champs
        ChangeListener<String> textChangeListener = (obs, oldValue, newValue) -> updateDescription();
        ChangeListener<Number> numberChangeListener = (obs, oldValue, newValue) -> updateDescription();
        ChangeListener<Boolean> booleanChangeListener = (obs, oldValue, newValue) -> updateDescription();

        nomField.textProperty().addListener(textChangeListener);
        prixField.textProperty().addListener(textChangeListener);
        dureeEnMoisSpinner.valueProperty().addListener(numberChangeListener);
        isPremiumCheck.selectedProperty().addListener(booleanChangeListener);
    }

    private void updateDescription() {
        try {
            TypeAbonnement temp = new TypeAbonnement();
            temp.setNom(nomField.getText().isEmpty() ? "Abonnement" : nomField.getText());
            temp.setPrix(prixField.getText().isEmpty() ? 0.0 : Double.parseDouble(prixField.getText()));
            temp.setDureeEnMois(dureeEnMoisSpinner.getValue());
            temp.setIsPremium(isPremiumCheck.isSelected());
            descriptionArea.setText(genererDescription(temp));
        } catch (NumberFormatException e) {
            descriptionArea.setText("Veuillez entrer un prix valide.");
        }
    }

    @FXML
    private void clearFields() {
        nomField.clear();
        descriptionArea.setText(""); // Réinitialiser la description générée
        prixField.clear();
        dureeEnMoisSpinner.getValueFactory().setValue(1);
        isPremiumCheck.setSelected(false);
        updateFeedback("Champs réinitialisés.");
    }

    private void updateFeedback(String message) {
        if (feedbackArea != null) {
            feedbackArea.setText(message + "\n" + (feedbackArea.getText().length() > 0 ? feedbackArea.getText() : ""));
        }
    }

    private void showRecommendations(List<TypeAbonnement> recommendations) {
        StringBuilder sb = new StringBuilder("Recommandations de prix :\n");
        for (TypeAbonnement type : recommendations) {
            sb.append("Type: ").append(type.getNom()).append(" - Considérer une réduction de prix.\n");
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Recommandations de Prix");
        alert.setHeaderText(null);
        alert.setContentText(sb.toString());
       // alert.setStyle("-fx-background-color: #2E3440; -fx-text-fill: #f4eeee;");
        alert.showAndWait();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Avertissement");
        alert.setHeaderText(null);
        alert.setContentText(message);
       // alert.setStyle("-fx-background-color: #2E3440; -fx-text-fill: #f4eeee;");
        alert.showAndWait();
    }
}