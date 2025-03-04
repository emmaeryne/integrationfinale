
/*package edu.emmapi.controllers;

import edu.emmapi.entities.TypeAbonnement;
import edu.emmapi.services.TypeAbonnementService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

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
    @FXML private ComboBox<String> sortComboBox; // Added ComboBox for sorting
    @FXML private Button generateRecommendationsButton;
    @FXML private Button analyzeTrendsButton;
    @FXML private TextArea feedbackArea;

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

        // Set up sorting options
        sortComboBox.setItems(FXCollections.observableArrayList("prix_asc", "prix_desc"));
    }

    private void setupTableColumns() {
        nomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        prixColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrix()).asObject());
        dureeEnMoisColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDureeEnMois()).asObject());
        isPremiumColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().getIsPremium()));
    }

    private void loadTypeAbonnements() {
        List<TypeAbonnement> loadedTypeAbonnements = typeAbonnementService.getAllTypeAbonnements(null);
        typeAbonnements.clear();
        typeAbonnements.addAll(loadedTypeAbonnements);
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTypeAbonnements(newValue);
        });
    }

    private void filterTypeAbonnements(String keyword) {
        if (keyword.isEmpty()) {
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
    }

    @FXML
    private void handleAjouterTypeAbonnement() {
        try {
            validateInput();
            TypeAbonnement typeAbonnement = new TypeAbonnement();
            typeAbonnement.setNom(nomField.getText());
            typeAbonnement.setDescription(descriptionArea.getText());
            typeAbonnement.setPrix(Double.parseDouble(prixField.getText()));
            typeAbonnement.setDureeEnMois(dureeEnMoisSpinner.getValue());
            typeAbonnement.setIsPremium(isPremiumCheck.isSelected());

            typeAbonnementService.ajouterTypeAbonnement(typeAbonnement);
            loadTypeAbonnements();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Veuillez entrer un prix valide.");
        }
    }

    @FXML
    private void handleUpdateTypeAbonnement() {
        TypeAbonnement selected = typeAbonnementTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                validateInput();
                selected.setNom(nomField.getText());
                selected.setDescription(descriptionArea.getText());
                selected.setPrix(Double.parseDouble(prixField.getText()));
                selected.setDureeEnMois(dureeEnMoisSpinner.getValue());
                selected.setIsPremium(isPremiumCheck.isSelected());

                typeAbonnementService.updateTypeAbonnement(selected);
                loadTypeAbonnements();
                clearFields();
            } catch (NumberFormatException e) {
                showAlert("Veuillez entrer un prix valide.");
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
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    typeAbonnementService.deleteTypeAbonnement(selected.getId());
                    loadTypeAbonnements();
                    clearFields();
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
    }

    @FXML
    private void handleAnalyzeTrends() {
        String insights = typeAbonnementService.analyserTendances();
        showAlert(insights);
    }

    private void validateInput() {
        if (nomField.getText().isEmpty() || descriptionArea.getText().isEmpty() || prixField.getText().isEmpty()) {
            showAlert("Tous les champs doivent être remplis.");
            throw new IllegalArgumentException("Input validation failed");
        }
    }

    @FXML
    private void clearFields() {
        nomField.clear();
        descriptionArea.clear();
        prixField.clear();
        dureeEnMoisSpinner.getValueFactory().setValue(1);
        isPremiumCheck.setSelected(false);
    }

    private void showRecommendations(List<TypeAbonnement> recommendations) {
        StringBuilder sb = new StringBuilder("Recommandations de prix :\n");
        for (TypeAbonnement type : recommendations) {
            sb.append("Type: ")
                    .append(type.getNom())
                    .append(" - ")
                    .append("Considérer une réduction de prix.\n");
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Recommandations de Prix");
        alert.setHeaderText(null);
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Avertissement");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}*/
package edu.emmapi.controllers;

import edu.emmapi.entities.TypeAbonnement;
import edu.emmapi.services.TypeAbonnementService;
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

        sortComboBox.setItems(FXCollections.observableArrayList("prix_asc", "prix_desc"));
        dureeEnMoisSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 120, 1));
    }

    private void setupTableColumns() {
        nomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        prixColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrix()).asObject());
        dureeEnMoisColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDureeEnMois()).asObject());
        isPremiumColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().getIsPremium()));
    }

    private void loadTypeAbonnements() {
        List<TypeAbonnement> loadedTypeAbonnements = typeAbonnementService.getAllTypeAbonnements(null);
        typeAbonnements.clear();
        typeAbonnements.addAll(loadedTypeAbonnements);
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTypeAbonnements(newValue);
        });
    }

    private void filterTypeAbonnements(String keyword) {
        if (keyword.isEmpty()) {
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
    }

    @FXML
    private void handleAjouterTypeAbonnement() {
        try {
            validateInputWithoutDescription(); // Ne valider que les autres champs
            TypeAbonnement typeAbonnement = new TypeAbonnement();
            typeAbonnement.setNom(nomField.getText());
            typeAbonnement.setPrix(Double.parseDouble(prixField.getText()));
            typeAbonnement.setDureeEnMois(dureeEnMoisSpinner.getValue());
            typeAbonnement.setIsPremium(isPremiumCheck.isSelected());
            typeAbonnement.setDescription(genererDescription(typeAbonnement)); // Génération automatique

            typeAbonnementService.ajouterTypeAbonnement(typeAbonnement);
            loadTypeAbonnements();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Veuillez entrer un prix valide.");
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
                selected.setDescription(genererDescription(selected)); // Génération automatique

                typeAbonnementService.updateTypeAbonnement(selected);
                loadTypeAbonnements();
                clearFields();
            } catch (NumberFormatException e) {
                showAlert("Veuillez entrer un prix valide.");
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
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    typeAbonnementService.deleteTypeAbonnement(selected.getId());
                    loadTypeAbonnements();
                    clearFields();
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
    }

    @FXML
    private void handleAnalyzeTrends() {
        String insights = typeAbonnementService.analyserTendances();
        showAlert(insights);
    }

    @FXML
    private void handleLoadExcel() {
        try {
            // Chemin complet vers le fichier sur votre bureau (exemple pour Windows)
            String cheminFichier = "C:\\Users\\MSI\\Downloads\\abonnement.xlsx"; // Chemin exact
            FileInputStream file = new FileInputStream(cheminFichier);
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);

            typeAbonnements.clear();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Ignorer l'en-tête

                // Vérifier que la ligne contient au moins la première cellule (Nom)
                if (row.getCell(0) == null || row.getCell(0).getStringCellValue().trim().isEmpty()) {
                    continue; // Ignorer les lignes vides ou sans nom
                }

                TypeAbonnement abonnement = new TypeAbonnement();

                // Nom (colonne 0)
                abonnement.setNom(row.getCell(0).getStringCellValue());

                // Prix (colonne 1) - Gérer STRING ou NUMERIC
                if (row.getCell(1) != null) {
                    switch (row.getCell(1).getCellType()) {
                        case NUMERIC:
                            abonnement.setPrix(row.getCell(1).getNumericCellValue());
                            break;
                        case STRING:
                            try {
                                abonnement.setPrix(Double.parseDouble(row.getCell(1).getStringCellValue().trim()));
                            } catch (NumberFormatException e) {
                                abonnement.setPrix(0.0); // Valeur par défaut si le texte n’est pas convertible
                            }
                            break;
                        default:
                            abonnement.setPrix(0.0); // Valeur par défaut pour les autres types
                    }
                } else {
                    abonnement.setPrix(0.0); // Valeur par défaut si vide
                }

                // Durée (colonne 2) - Gérer STRING ou NUMERIC
                if (row.getCell(2) != null) {
                    switch (row.getCell(2).getCellType()) {
                        case NUMERIC:
                            abonnement.setDureeEnMois((int) row.getCell(2).getNumericCellValue());
                            break;
                        case STRING:
                            try {
                                abonnement.setDureeEnMois(Integer.parseInt(row.getCell(2).getStringCellValue().trim()));
                            } catch (NumberFormatException e) {
                                abonnement.setDureeEnMois(1); // Valeur par défaut si le texte n’est pas convertible
                            }
                            break;
                        default:
                            abonnement.setDureeEnMois(1); // Valeur par défaut pour les autres types
                    }
                } else {
                    abonnement.setDureeEnMois(1); // Valeur par défaut si vide
                }

                // Premium (colonne 3) - Gérer STRING ou BOOLEAN
                if (row.getCell(3) != null) {
                    switch (row.getCell(3).getCellType()) {
                        case BOOLEAN:
                            abonnement.setIsPremium(row.getCell(3).getBooleanCellValue());
                            break;
                        case STRING:
                            try {
                                abonnement.setIsPremium(Boolean.parseBoolean(row.getCell(3).getStringCellValue().trim()));
                            } catch (Exception e) {
                                abonnement.setIsPremium(false); // Valeur par défaut si le texte n’est pas convertible
                            }
                            break;
                        default:
                            abonnement.setIsPremium(false); // Valeur par défaut pour les autres types
                    }
                } else {
                    abonnement.setIsPremium(false); // Valeur par défaut si vide
                }

                // Générer la description
                abonnement.setDescription(genererDescription(abonnement));
                typeAbonnements.add(abonnement);
            }
            workbook.close();
            file.close();
            typeAbonnementTable.refresh();
            showAlert("Abonnements chargés depuis Excel avec descriptions générées !");
        } catch (IOException e) {
            showAlert("Erreur lors du chargement du fichier Excel : " + e.getMessage());
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

    // Validation sans exiger la description (puisqu'elle est générée automatiquement)
    private void validateInputWithoutDescription() {
        if (nomField.getText().isEmpty() || prixField.getText().isEmpty()) {
            showAlert("Le nom et le prix doivent être remplis.");
            throw new IllegalArgumentException("Input validation failed");
        }
    }

    @FXML
    private void clearFields() {
        nomField.clear();
        descriptionArea.clear();
        prixField.clear();
        dureeEnMoisSpinner.getValueFactory().setValue(1);
        isPremiumCheck.setSelected(false);
    }

    private void showRecommendations(List<TypeAbonnement> recommendations) {
        StringBuilder sb = new StringBuilder("Recommandations de prix :\n");
        for (TypeAbonnement type : recommendations) {
            sb.append("Type: ")
                    .append(type.getNom())
                    .append(" - ")
                    .append("Considérer une réduction de prix.\n");
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Recommandations de Prix");
        alert.setHeaderText(null);
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Avertissement");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}