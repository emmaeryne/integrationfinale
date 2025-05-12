package edu.emmapi.controllers;



import edu.emmapi.entities.categorieproduit;
import edu.emmapi.services.ProduitCategorie;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class Page2 {
    @FXML private ListView<categorieproduit> listCategorie;
    @FXML private Button btnModifier, btnSupprimer, btnAjouter, btnEnregistrer, btnChoisirImage,btnListproduit;
    @FXML private TextField txtNomCategorie, txtDescriptionCategorie;
    @FXML private TextField txtIdCategorie;

    @FXML private ImageView imageViewCategorie;

    private final ProduitCategorie categorieService = new ProduitCategorie();
    private List<categorieproduit> categoriesList;
    private categorieproduit categorieSelectionnee;
    private File selectedFile = null;

    @FXML
    public void initialize() {
        chargerCategories();
        // Rendre les champs en lecture seule par défaut
        txtNomCategorie.setEditable(false);

        listCategorie.setCellFactory(new Callback<ListView<categorieproduit>, ListCell<categorieproduit>>() {
            @Override
            public ListCell<categorieproduit> call(ListView<categorieproduit> listView) {
                return new CategorieCellFactory();  // Utilisation de la cellule personnalisée
            }
        });

        listCategorie.setOnMouseClicked(event -> {
            int selectedIndex = listCategorie.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                categorieSelectionnee = categoriesList.get(selectedIndex);

                // Affichage des informations dans les champs
                txtIdCategorie.setText(String.valueOf(categorieSelectionnee.getId())); // Afficher l'ID
                txtNomCategorie.setText(categorieSelectionnee.getNomcategorie());
                txtDescriptionCategorie.setText(categorieSelectionnee.getDescription());

                if (categorieSelectionnee.getImage() != null) {
                    imageViewCategorie.setImage(new Image(new ByteArrayInputStream(categorieSelectionnee.getImage())));
                }
            }
        });
    }

    private void chargerCategories() {
        categoriesList = categorieService.afficherCategorieProduit();

        if (categoriesList.isEmpty()) {
            System.out.println("⚠️ Aucune catégorie trouvée !");
        } else {
            for (categorieproduit cat : categoriesList) {
                System.out.println("✔️ Catégorie : " + cat.getNomcategorie() + ", Image Size: "
                        + (cat.getImage() != null ? cat.getImage().length + " bytes" : "null"));
            }
        }

        ObservableList<categorieproduit> categoriesFormat = FXCollections.observableArrayList(categoriesList);
        listCategorie.setItems(categoriesFormat);
    }


    @FXML
    private void modifierCategorie(ActionEvent actionEvent) {
        if (categorieSelectionnee != null) {
            // Empêcher la modification de l'ID et du nom
            txtIdCategorie.setEditable(false);
            txtNomCategorie.setEditable(false);

            // Mise à jour de la description et de l'image
            categorieSelectionnee.setDescription(txtDescriptionCategorie.getText());

            if (selectedFile != null) {
                try {
                    byte[] imageData = new FileInputStream(selectedFile).readAllBytes();
                    categorieSelectionnee.setImage(imageData);
                } catch (IOException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'image !");
                }
            }

            // Modification de la catégorie
            categorieService.modifierCategorieProduit(categorieSelectionnee);
            chargerCategories();

            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "Catégorie modifiée !\nID: " + categorieSelectionnee.getId() +
                            "\nNom: " + categorieSelectionnee.getNomcategorie());
        } else {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez sélectionner une catégorie.");
        }
    }


    @FXML
    private void supprimerCategorie(ActionEvent actionEvent) {
        if (categorieSelectionnee != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setContentText("Voulez-vous vraiment supprimer cette catégorie ?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // 1. Supprimer la catégorie
                    categorieService.supprimerCategorieProduit(categorieSelectionnee);

                    // 2. Recharger la liste
                    chargerCategories();

                    // 3. Réinitialiser la sélection et les champs
                    categorieSelectionnee = null;
                    listCategorie.getSelectionModel().clearSelection();
                    txtIdCategorie.clear();
                    txtNomCategorie.clear();
                    txtDescriptionCategorie.clear();
                    imageViewCategorie.setImage(null);

                    // 4. Afficher le message de succès
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Catégorie supprimée !");
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez sélectionner une catégorie.");
        }
    }


    @FXML
    public void choisirImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", ".png", ".jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            selectedFile = file;
            imageViewCategorie.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    public void ouvrirAjouterCategorie() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajoutercategorie.fxml"));
            Parent parent = loader.load();

            // Ouvrir une nouvelle fenêtre pour ajouter un produit
            Stage stage = new Stage();
            stage.setTitle("Ajouter categorie");
            stage.setScene(new Scene(parent));
            stage.show();

            Stage currentStage = (Stage) btnAjouter.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }

    public void ouvrirpage1(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/page1.fxml"));
            Parent parent = loader.load();

            // Ouvrir une nouvelle fenêtre pour ajouter un produit
            Stage stage = new Stage();
            stage.setTitle("List produit ");
            stage.setScene(new Scene(parent));
            stage.show();

            Stage currentStage = (Stage) btnListproduit.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}