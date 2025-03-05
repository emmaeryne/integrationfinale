package edu.emmapi.services;


import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment; // Mis à jour
import com.itextpdf.layout.properties.TextAlignment; // Mis à jour
import com.itextpdf.layout.properties.UnitValue; // Mis à jour
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.Background; // Mis à jour
import edu.emmapi.entities.Commande;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ExportPDFService {

    public File exportToPDF(List<Commande> commandes) {
        String dest = "commandes.pdf";
        File file = new File(dest);

        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(file));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Load images from the resources folder
            URL logoLeftUrl = getClass().getClassLoader().getResource("hjk-removebg-preview.png");
            URL logoRightUrl = getClass().getClassLoader().getResource("243925.png");

            if (logoLeftUrl == null || logoRightUrl == null) {
                throw new IOException("One or both images could not be found in the resources folder.");
            }

            // Add the title "Liste des commandes" at the top center
            Paragraph title = new Paragraph("Liste des commandes")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(18)
                    .setBold()
                    .setMarginTop(20);
            document.add(title);

            // Add the left logo (top-left corner)
            Image logoLeft = new Image(ImageDataFactory.create(logoLeftUrl));
            logoLeft.setWidth(100); // Set width
            logoLeft.setHeight(100); // Set height
            logoLeft.setFixedPosition(10, pdf.getDefaultPageSize().getTop() - 110, UnitValue.createPointValue(100)); // Position: left=10, top=110
            document.add(logoLeft);

            // Add the right logo (top-right corner)
            Image logoRight = new Image(ImageDataFactory.create(logoRightUrl));
            logoRight.setWidth(30); // Set width
            logoRight.setHeight(30); // Set height
            logoRight.setFixedPosition(pdf.getDefaultPageSize().getRight() - 50, pdf.getDefaultPageSize().getTop() - 50, UnitValue.createPointValue(30)); // Position: right=50, top=50
            document.add(logoRight);

            // Add space between logos and table
            document.add(new Paragraph("\n"));



            // Create a table with 4 columns
            Table table = new Table(UnitValue.createPercentArray(4)).useAllAvailableWidth();
            table.setMarginTop(20);
            table.setMarginBottom(20);

            // Add table headers
            table.addHeaderCell(new Cell().add(new Paragraph("ID Commande").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Date de Commande").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Statut").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("ID Utilisateur").setBold()));

            // Fill the table with data
            for (Commande commande : commandes) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(commande.getIdCommande()))));
                table.addCell(new Cell().add(new Paragraph(commande.getDateDeCommande().toString())));
                table.addCell(new Cell().add(new Paragraph(commande.getStatus())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(commande.getIdUtilisateur()))));
            }

            document.add(table);

            // Add the footer at the bottom center
            Paragraph footer = new Paragraph("Complexe Sportif, 123 Rue de la Paix, 75000 Ariana, Tel : 99646424, Email : info@www.hive.com")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(10)
                    .setFixedPosition(pdf.getDefaultPageSize().getWidth() / 2 - 150, 20, 300); // Position: center, bottom=20
            document.add(footer);

            document.close();

            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

