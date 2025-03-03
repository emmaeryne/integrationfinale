package edu.emmapi.controllers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import javafx.scene.image.Image;
import javafx.embed.swing.SwingFXUtils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

public class QRCodeUtil {

    // ✅ Générer un QR Code pour un produit sous forme d'image JavaFX
    public static Image generateQRCode(String productInfo) {
        try {
            int width = 100;
            int height = 100;

            // 📌 Générer un QR Code contenant les infos du produit
            BitMatrix bitMatrix = new MultiFormatWriter().encode(productInfo, BarcodeFormat.QR_CODE, width, height);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // 📌 Convertir BufferedImage en Image JavaFX
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

            return new Image(inputStream);

        } catch (WriterException | java.io.IOException e) {
            System.err.println("❌ Erreur lors de la génération du QR Code : " + e.getMessage());
            return null;
        }
    }
}
