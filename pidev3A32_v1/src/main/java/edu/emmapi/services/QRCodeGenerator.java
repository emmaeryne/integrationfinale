package edu.emmapi.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class QRCodeGenerator {

    public static void generateQRCodeImage(String text, int width, int height, String filePath) throws WriterException, IOException {
        BufferedImage qrImage = generateQRCode(text, width, height);
        ImageIO.write(qrImage, "PNG", new File(filePath));
    }

    private static BufferedImage generateQRCode(String text, int width, int height) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        // Conversion en image couleur
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        BufferedImage colorImage = new BufferedImage(
                qrImage.getWidth(),
                qrImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g = colorImage.createGraphics();
        g.drawImage(qrImage, 0, 0, null);
        g.dispose();

        return colorImage;
    }

    public static BufferedImage generateQRCodeWithLogo(String text, int width, int height, String logoPath)
            throws WriterException, IOException {

        BufferedImage qrImage = generateQRCode(text, width, height);
        BufferedImage logoImage = ImageIO.read(new File(logoPath));

        // Redimensionnement avec conservation des couleurs
        int logoWidth = qrImage.getWidth() / 5;
        int logoHeight = qrImage.getHeight() / 5;

        BufferedImage resizedLogo = new BufferedImage(
                logoWidth,
                logoHeight,
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g = resizedLogo.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(logoImage, 0, 0, logoWidth, logoHeight, null);
        g.dispose();

        // Fusion des images
        Graphics2D graphics = qrImage.createGraphics();
        graphics.drawImage(resizedLogo,
                (qrImage.getWidth() - logoWidth) / 2,
                (qrImage.getHeight() - logoHeight) / 2,
                null
        );
        graphics.dispose();

        return qrImage;
    }
}