package edu.emmapi.controllers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import java.io.IOException;

import edu.emmapi.entities.produit;

public class QRCodeCellFactory implements Callback<TableColumn<produit, produit>, TableCell<produit, produit>> {

    @Override
    public TableCell<produit, produit> call(TableColumn<produit, produit> param) {
        return new TableCell<>() {
            private final ImageView qrCodeView = new ImageView();

            @Override
            protected void updateItem(produit item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    // ✅ Générer un QR Code avec les détails du produit
                    String data = "Nom: " + item.getNom_Produit() + "\n"
                            + "Catégorie: " + item.getCategorie() + "\n"
                            + "Prix: " + item.getPrix() + " TND\n"
                            + "Date: " + item.getDate() + "\n"
                            + "Fournisseur: " + item.getFournisseur();

                    Image qrImage = generateQRCode(data);
                    qrCodeView.setImage(qrImage);
                    qrCodeView.setFitWidth(80);
                    qrCodeView.setFitHeight(80);
                    setGraphic(qrCodeView);
                }
            }
        };
    }

    private Image generateQRCode(String data) {
        try {
            int width = 100;
            int height = 100;

            BitMatrix bitMatrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, width, height);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // Convertir BufferedImage en Image JavaFX
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

            return new Image(inputStream);

        } catch (WriterException | IOException e) {
            System.err.println("❌ Erreur lors de la génération du QR Code : " + e.getMessage());
            return null;
        }
    }
}
