package edu.emmapi.controllers;

import edu.emmapi.entities.Participant;
import edu.emmapi.services.ParticipantService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.geometry.Pos;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Element;
import java.sql.SQLException;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class Participantt {

    @FXML
    private FlowPane participantFlowPane; // FlowPane pour afficher les participants

    private ParticipantService participantService = new ParticipantService();
    private List<Participant> allParticipants;

    @FXML
    private Button generatePdfButton; // Le bouton pour générer le PDF

    // Méthode d'initialisation pour charger les participants
    public void initialize() {
        try {
            // Récupérer tous les participants depuis la base de données
            allParticipants = participantService.getAllDataparticipant();

            // Ajouter les informations de chaque participant (Nom, Prénom, Email, etc.)
            for (Participant participant : allParticipants) {
                HBox participantCard = createParticipantCard(participant);
                participantFlowPane.getChildren().add(participantCard);
            }

            // Définir l'orientation du FlowPane en horizontal pour aligner les cartes côte à côte
            participantFlowPane.setOrientation(javafx.geometry.Orientation.HORIZONTAL);
            participantFlowPane.setHgap(10); // Espace horizontal entre les cartes
            participantFlowPane.setVgap(10); // Espace vertical entre les cartes

            // Ajouter un événement pour le bouton PDF
            generatePdfButton.setOnAction(e -> generateAndOpenPdf());

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur de chargement");
            alert.setHeaderText("Erreur lors du chargement des participants");
            alert.setContentText("Impossible de récupérer les données des participants.");
            alert.showAndWait();
        }
    }

    // Méthode pour créer une carte avec toutes les informations du participant
    private HBox createParticipantCard(Participant participant) {
        // Créer une carte rectangulaire avec une taille plus petite
        HBox card = new HBox(10);
        card.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 10px; -fx-border-radius: 10px; -fx-border-color: #ddd; -fx-border-width: 1px;");
        card.setPrefWidth(200);  // Largeur de la carte
        card.setPrefHeight(150); // Hauteur de la carte
        card.setAlignment(Pos.CENTER); // Centrer les éléments dans la carte

        // Créer un VBox pour afficher les informations verticalement
        VBox infoBox = new VBox(5);  // Un VBox pour les informations verticales
        infoBox.setAlignment(Pos.TOP_LEFT);

        // Ajouter un effet de survol sur la carte
        card.setOnMouseEntered(this::onCardMouseEnter);
        card.setOnMouseExited(this::onCardMouseExit);

        // Informations sur le participant
        Text nameText = new Text("Nom: " + participant.getNom() + " " + participant.getPrenom());
        nameText.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Text prenomText = new Text("Prénom: " + participant.getPrenom());
        prenomText.setStyle("-fx-font-size: 12px;");

        Text emailText = new Text("Email: " + participant.getTel());
        emailText.setStyle("-fx-font-size: 12px;");

        Text phoneText = new Text("Téléphone: " + participant.getAdresse());
        phoneText.setStyle("-fx-font-size: 12px;");

        // Ajouter les informations dans le VBox
        infoBox.getChildren().addAll(nameText, prenomText, emailText, phoneText);

        // Ajouter le VBox à la carte
        card.getChildren().add(infoBox);

        // Retourner le HBox
        return card;
    }

    // Effet au survol de la carte (ajouter une ombre portée)
    private void onCardMouseEnter(MouseEvent event) {
        HBox source = (HBox) event.getSource();
        DropShadow shadow = new DropShadow(10, 2, 2, javafx.scene.paint.Color.GRAY);
        source.setEffect(shadow);
    }

    // Effet à la sortie du survol (retirer l'ombre)
    private void onCardMouseExit(MouseEvent event) {
        HBox source = (HBox) event.getSource();
        source.setEffect(null);
    }

    // Méthode pour générer et ouvrir un PDF
    private void generateAndOpenPdf() {
        Document document = new Document();
        try {
            // Créer un PDF à l'emplacement désiré
            PdfWriter.getInstance(document, new FileOutputStream("participants.pdf"));
            document.open();

            // Créer le titre avec une police en gras
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Liste des Participants", titleFont);
            title.setAlignment(Element.ALIGN_CENTER); // Aligner au centre
            document.add(title);

            // Ajouter un espace après le titre
            document.add(new Paragraph("\n"));

            // Ajouter les informations sur chaque participant dans le PDF
            for (Participant participant : allParticipants) {
                document.add(new Paragraph("Nom: " + participant.getNom()));
                document.add(new Paragraph("Prénom: " + participant.getPrenom()));
                document.add(new Paragraph("Email: " + participant.getTel()));
                document.add(new Paragraph("Téléphone: " + participant.getAdresse()));
                document.add(new Paragraph("--------------------------------------------------"));
            }

            document.close();

            // Ouvrir automatiquement le PDF une fois généré
            File pdfFile = new File("participants.pdf");
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            } else {
                System.out.println("Erreur : le système ne supporte pas l'ouverture automatique du PDF.");
            }

        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
            showError("Erreur lors de la création du PDF");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors de l'ouverture du PDF");
        }
    }

    // Méthode pour afficher un message d'erreur
    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
