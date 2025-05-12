package edu.emmapi.controllers;

import edu.emmapi.entities.categorieproduit;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.ByteArrayInputStream;

public class CategorieCellFactory extends ListCell<categorieproduit> {
    private final HBox hbox = new HBox(10);
    private final ImageView imageView = new ImageView();
    private final Text textNom = new Text();
    private final Text textDescription = new Text();

    public CategorieCellFactory() {
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        imageView.setPreserveRatio(true); // Pour garder les proportions de l'image
        hbox.getChildren().addAll(imageView, textNom, textDescription);
        hbox.setStyle("-fx-padding: 5; -fx-alignment: CENTER_LEFT;");
    }

    @Override
    protected void updateItem(categorieproduit categorie, boolean empty) {
        super.updateItem(categorie, empty);

        if (empty || categorie == null) {
            setGraphic(null);
        } else {
            textNom.setText(categorie.getNomcategorie());
            textDescription.setText(" | " +
                    (categorie.getDescription() != null ? categorie.getDescription() : "Aucune description"));

            if (categorie.getImage() != null) {
                try {
                    ByteArrayInputStream bis = new ByteArrayInputStream(categorie.getImage());
                    Image img = new Image(bis);

                    if (!img.isError()) {
                        imageView.setImage(img);
                    } else {
                        System.out.println("❌ Erreur de chargement de l'image pour la catégorie : " + categorie.getNomcategorie());
                        imageView.setImage(null);
                    }
                } catch (Exception e) {
                    System.out.println("❌ Exception lors du chargement de l'image : " + e.getMessage());
                    imageView.setImage(null);
                }
            } else {
                imageView.setImage(null); // Si jamais l'image est absente
            }

            setGraphic(hbox);
        }
    }
}
