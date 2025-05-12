
/*package edu.emmapi.controllers;

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
}*/

package edu.emmapi.controllers;

import edu.emmapi.entities.TypeAbonnement;
import edu.emmapi.services.TypeAbonnementService;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static javax.xml.xpath.XPathConstants.STRING;

public class TypeAbonnementController {
    @FXML private TableView<TypeAbonnement> typeAbonnementTable;
    @FXML private TableColumn<TypeAbonnement, String> nomColumn;
    @FXML private TableColumn<TypeAbonnement, Double> prixColumn;
    @FXML private TableColumn<TypeAbonnement, Integer> dureeEnMoisColumn;
    @FXML private TableColumn<TypeAbonnement, String> isPremiumColumn;
    @FXML private TableColumn<TypeAbonnement, String> descriptionColumn;
    @FXML private TableColumn<TypeAbonnement, Double> reductionColumn;
    @FXML private TableColumn<TypeAbonnement, Double> prixReduitColumn;
    @FXML private TableColumn<TypeAbonnement, String> updatedAtColumn;
    @FXML private TableColumn<TypeAbonnement, Integer> reservationCountColumn;
    @FXML private TableColumn<TypeAbonnement, Void> actionsColumn;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private TextField searchField;

    private TypeAbonnementService typeAbonnementService;
    private ObservableList<TypeAbonnement> typeAbonnements;

    @FXML
    public void initialize() {
        typeAbonnementService = new TypeAbonnementService();
        typeAbonnements = FXCollections.observableArrayList();
        typeAbonnementTable.setItems(typeAbonnements);

        setupTableColumns();
        setupSortComboBox();

        // Add a placeholder for empty TableView
        typeAbonnementTable.setPlaceholder(new Label("Aucun type d'abonnement trouvé."));

        loadTypeAbonnements();

        searchField.textProperty().addListener((obs, oldValue, newValue) -> handleSearch());
    }

    private void setupTableColumns() {
        nomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        prixColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrixAsDouble()).asObject());
        dureeEnMoisColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDureeEnMois()).asObject());
        isPremiumColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isPremium() ? "Oui" : "Non"));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        reductionColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getReduction() != null ? cellData.getValue().getReduction() : 0.0).asObject());
        prixReduitColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrixReduitAsDouble()).asObject());
        updatedAtColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUpdatedAt() != null ? cellData.getValue().getUpdatedAt().toString() : ""));
        reservationCountColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(typeAbonnementService.getReservationCount(cellData.getValue().getId())).asObject());

        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final Button detailsButton = new Button("Détails");

            {
                editButton.setOnAction(event -> {
                    TypeAbonnement type = getTableView().getItems().get(getIndex());
                    handleEditTypeAbonnement(type);
                });
                deleteButton.setOnAction(event -> {
                    TypeAbonnement type = getTableView().getItems().get(getIndex());
                    handleDeleteTypeAbonnement(type);
                });
                detailsButton.setOnAction(event -> {
                    TypeAbonnement type = getTableView().getItems().get(getIndex());
                    handleShowDetails(type);
                });
                editButton.setStyle("-fx-background-color: #FF7043; -fx-text-fill: #FFFFFF; -fx-font-size: 12px;");
                deleteButton.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: #FFFFFF; -fx-font-size: 12px;");
                detailsButton.setStyle("-fx-background-color: #66C2D9; -fx-text-fill: #FFFFFF; -fx-font-size: 12px;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(5, editButton, deleteButton, detailsButton));
                }
            }
        });
    }

    private void setupSortComboBox() {
        sortComboBox.setItems(FXCollections.observableArrayList("prix_asc", "prix_desc"));
        sortComboBox.setValue("prix_asc");
        sortComboBox.setOnAction(event -> handleSort());
    }

    private void loadTypeAbonnements() {
        try {
            String sort = sortComboBox.getValue();
            String search = searchField.getText().trim();
            List<TypeAbonnement> loadedTypes = search.isEmpty() ?
                    typeAbonnementService.getAllTypeAbonnements(sort) :
                    typeAbonnementService.searchTypeAbonnements(search);
            typeAbonnements.clear();
            typeAbonnements.addAll(loadedTypes);
            System.out.println("Loaded " + loadedTypes.size() + " type(s) d'abonnement. Search: '" + search + "', Sort: " + sort);
            if (loadedTypes.isEmpty()) {
                showInfo("Information", "Aucun type d'abonnement trouvé dans la base de données.");
            }
        } catch (Exception e) {
            showError("Erreur", "Erreur lors du chargement des types d'abonnement: " + e.getMessage());
            System.err.println("Erreur lors du chargement des types d'abonnement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSort() {
        loadTypeAbonnements();
        showSuccess("Succès", "Types d'abonnement triés par " + sortComboBox.getValue());
    }

    @FXML
    private void handleSearch() {
        loadTypeAbonnements();
    }

    @FXML
    private void handleAddTypeAbonnement() {
        TypeAbonnement newType = showTypeAbonnementDialog(null);
        if (newType != null) {
            try {
                typeAbonnementService.ajouterTypeAbonnement(newType);
                loadTypeAbonnements();
                showSuccess("Succès", "Type d'abonnement créé avec succès.");
            } catch (Exception e) {
                showError("Erreur", "Erreur lors de l'ajout du type d'abonnement: " + e.getMessage());
                System.err.println("Erreur lors de l'ajout du type d'abonnement: " + e.getMessage());
            }
        }
    }

    private void handleEditTypeAbonnement(TypeAbonnement selectedType) {
        if (selectedType != null) {
            TypeAbonnement updatedType = showTypeAbonnementDialog(selectedType);
            if (updatedType != null) {
                try {
                    updatedType.setUpdatedAt(LocalDateTime.now());
                    typeAbonnementService.updateTypeAbonnement(updatedType);
                    loadTypeAbonnements();
                    showSuccess("Succès", "Type d'abonnement mis à jour avec succès.");
                } catch (Exception e) {
                    showError("Erreur", "Erreur lors de la mise à jour du type d'abonnement: " + e.getMessage());
                    System.err.println("Erreur lors de la mise à jour du type d'abonnement: " + e.getMessage());
                }
            }
        } else {
            showError("Sélectionnez un type", "Veuillez sélectionner un type d'abonnement à modifier.");
        }
    }

    private void handleDeleteTypeAbonnement(TypeAbonnement selectedType) {
        if (selectedType != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Confirmer la suppression ?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        typeAbonnementService.deleteTypeAbonnement(selectedType.getId());
                        loadTypeAbonnements();
                        showSuccess("Succès", "Type d'abonnement supprimé avec succès.");
                    } catch (Exception e) {
                        showError("Erreur", "Erreur lors de la suppression du type d'abonnement: " + e.getMessage());
                        System.err.println("Erreur lors de la suppression du type d'abonnement: " + e.getMessage());
                    }
                }
            });
        } else {
            showError("Sélectionnez un type", "Veuillez sélectionner un type d'abonnement à supprimer.");
        }
    }

    private void handleShowDetails(TypeAbonnement typeAbonnement) {
        if (typeAbonnement != null) {
            try {
                int reservationCount = typeAbonnementService.getReservationCount(typeAbonnement.getId());
                String details = String.format(
                        "Nom: %s\nPrix: %.2f €\nDurée: %d mois\nPremium: %s\nDescription: %s\nRéduction: %.2f%%\nPrix Réduit: %.2f €\nMis à jour: %s\nRéservations: %d",
                        typeAbonnement.getNom(),
                        typeAbonnement.getPrixAsDouble(),
                        typeAbonnement.getDureeEnMois(),
                        typeAbonnement.isPremium() ? "Oui" : "Non",
                        typeAbonnement.getDescription() != null ? typeAbonnement.getDescription() : "N/A",
                        typeAbonnement.getReduction() != null ? typeAbonnement.getReduction() : 0.0,
                        typeAbonnement.getPrixReduitAsDouble(),
                        typeAbonnement.getUpdatedAt() != null ? typeAbonnement.getUpdatedAt().toString() : "N/A",
                        reservationCount
                );

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Détails du Type d'Abonnement");
                alert.setHeaderText(null);
                alert.setContentText(details);
                alert.getDialogPane().setMinWidth(400);
                alert.showAndWait();
            } catch (Exception e) {
                showError("Erreur", "Erreur lors de l'affichage des détails: " + e.getMessage());
                System.err.println("Erreur lors de l'affichage des détails: " + e.getMessage());
            }
        } else {
            showError("Sélectionnez un type", "Veuillez sélectionner un type d'abonnement pour voir les détails.");
        }
    }

    @FXML
    private void handleRecommendations() {
        try {
            List<TypeAbonnement> popularSubscriptions = typeAbonnementService.findMostReserved();
            StringBuilder content = new StringBuilder();
            content.append("Types d'Abonnement les Plus Réservés:\n");
            popularSubscriptions.forEach(type -> content.append(String.format("- %s (%d réservations)\n", type.getNom(), typeAbonnementService.getReservationCount(type.getId()))));

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Recommandations");
            alert.setHeaderText(null);
            alert.setContentText(content.toString());
            alert.getDialogPane().setMinWidth(400);
            alert.showAndWait();
        } catch (Exception e) {
            showError("Erreur", "Erreur lors de la récupération des recommandations: " + e.getMessage());
            System.err.println("Erreur lors de la récupération des recommandations: " + e.getMessage());
        }
    }

    @FXML
    private void handleImportExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importer un fichier Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx"));
        File file = fileChooser.showOpenDialog(typeAbonnementTable.getScene().getWindow());

        if (file != null) {
            try (FileInputStream fis = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(fis)) {
                Sheet sheet = workbook.getSheetAt(0);
                int rowsProcessed = 0;

                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue; // Skip header

                    String nom = getCellValue(row.getCell(0));
                    if (nom == null || nom.trim().isEmpty()) continue;

                    String prix = getCellValue(row.getCell(1));
                    if (prix == null || prix.trim().isEmpty()) {
                        System.err.println("Prix invalide pour " + nom + ": " + prix);
                        continue;
                    }

                    int duree = parseInt(getCellValue(row.getCell(2)), 1);
                    if (duree <= 0) {
                        System.err.println("Durée invalide pour " + nom + ": " + duree);
                        continue;
                    }

                    boolean isPremium = parseBoolean(getCellValue(row.getCell(3)), false);

                    TypeAbonnement typeAbonnement = new TypeAbonnement();
                    typeAbonnement.setNom(nom);
                    typeAbonnement.setPrix(prix);
                    typeAbonnement.setDureeEnMois(duree);
                    typeAbonnement.setPremium(isPremium);
                    typeAbonnement.setUpdatedAt(LocalDateTime.now());

                    typeAbonnementService.ajouterTypeAbonnement(typeAbonnement);
                    rowsProcessed++;
                }

                loadTypeAbonnements();
                showSuccess("Succès", rowsProcessed + " types d'abonnements importés avec succès.");
            } catch (Exception e) {
                showError("Erreur", "Erreur lors de l'importation du fichier Excel: " + e.getMessage());
                System.err.println("Erreur lors de l'importation du fichier Excel: " + e.getMessage());
            }
        }
    }

    private TypeAbonnement showTypeAbonnementDialog(TypeAbonnement typeAbonnement) {
        Dialog<TypeAbonnement> dialog = new Dialog<>();
        dialog.setTitle(typeAbonnement == null ? "Ajouter Type d'Abonnement" : "Modifier Type d'Abonnement");
        dialog.setHeaderText("Entrez les détails du type d'abonnement");

        TextField nomField = new TextField();
        nomField.setPromptText("Nom");
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");
        TextField prixField = new TextField();
        prixField.setPromptText("Prix");
        Spinner<Integer> dureeSpinner = new Spinner<>(1, 12, 1);
        dureeSpinner.setEditable(true);
        CheckBox isPremiumCheckBox = new CheckBox("Premium");
        TextField reductionField = new TextField();
        reductionField.setPromptText("Réduction (%)");
        TextField prixReduitField = new TextField();
        prixReduitField.setPromptText("Prix Réduit");

        if (typeAbonnement != null) {
            nomField.setText(typeAbonnement.getNom());
            descriptionField.setText(typeAbonnement.getDescription());
            prixField.setText(typeAbonnement.getPrix());
            dureeSpinner.getValueFactory().setValue(typeAbonnement.getDureeEnMois());
            isPremiumCheckBox.setSelected(typeAbonnement.isPremium());
            reductionField.setText(typeAbonnement.getReduction() != null ? String.valueOf(typeAbonnement.getReduction()) : "");
            prixReduitField.setText(typeAbonnement.getPrixReduit() != null ? typeAbonnement.getPrixReduit() : "");
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Prix (€):"), 0, 2);
        grid.add(prixField, 1, 2);
        grid.add(new Label("Durée (mois):"), 0, 3);
        grid.add(dureeSpinner, 1, 3);
        grid.add(new Label("Premium:"), 0, 4);
        grid.add(isPremiumCheckBox, 1, 4);
        grid.add(new Label("Réduction (%):"), 0, 5);
        grid.add(reductionField, 1, 5);
        grid.add(new Label("Prix Réduit (€):"), 0, 6);
        grid.add(prixReduitField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType(typeAbonnement == null ? "Ajouter" : "Mettre à jour", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        Runnable updateButtonState = () -> {
            boolean isValid = !nomField.getText().trim().isEmpty() &&
                    !prixField.getText().trim().isEmpty();
            saveButton.setDisable(!isValid);
        };

        nomField.textProperty().addListener((obs, old, newVal) -> updateButtonState.run());
        prixField.textProperty().addListener((obs, old, newVal) -> updateButtonState.run());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    TypeAbonnement newType = new TypeAbonnement();
                    if (typeAbonnement != null) {
                        newType.setId(typeAbonnement.getId());
                    }
                    newType.setNom(nomField.getText().trim());
                    newType.setDescription(descriptionField.getText().trim());
                    newType.setPrix(prixField.getText().trim());
                    newType.setDureeEnMois(dureeSpinner.getValue());
                    newType.setPremium(isPremiumCheckBox.isSelected());
                    String reductionText = reductionField.getText().trim();
                    newType.setReduction(reductionText.isEmpty() ? null : Double.parseDouble(reductionText));
                    String prixReduitText = prixReduitField.getText().trim();
                    newType.setPrixReduit(prixReduitText.isEmpty() ? null : prixReduitText);
                    newType.setUpdatedAt(LocalDateTime.now());

                    return newType;
                } catch (NumberFormatException e) {
                    showError("Erreur", "Veuillez entrer des valeurs numériques valides pour le prix et la réduction.");
                    return null;
                }
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return null;
        }
    }

    private double parseDouble(String value, double defaultValue) {
        try {
            return value != null ? Double.parseDouble(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private boolean parseBoolean(String value, boolean defaultValue) {
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public void handleBackToHome(javafx.event.ActionEvent actionEvent) {
        // Implement navigation back to home if needed
    }
}