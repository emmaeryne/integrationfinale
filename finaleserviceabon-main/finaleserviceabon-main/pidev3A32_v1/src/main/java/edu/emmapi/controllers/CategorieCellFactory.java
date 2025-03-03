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
        hbox.getChildren().addAll(imageView, textNom, textDescription);
    }

    @Override
    protected void updateItem(categorieproduit categorie, boolean empty) {
        super.updateItem(categorie, empty);
        if (empty || categorie == null) {
            setGraphic(null);
        } else {
            textNom.setText(categorie.getNomcategorie());
            textDescription.setText(" | " + (categorie.getDescription() != null ? categorie.getDescription() : "Aucune description"));

            if (categorie.getImage() != null) {
                imageView.setImage(new Image(new ByteArrayInputStream(categorie.getImage())));
            } else {
                imageView.setImage(null);
            }

            setGraphic(hbox);
        }
    }
}

