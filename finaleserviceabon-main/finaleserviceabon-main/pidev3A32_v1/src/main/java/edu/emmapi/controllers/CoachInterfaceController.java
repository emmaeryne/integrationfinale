package edu.emmapi.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Supposons une nouvelle entité Service et un service associé
import edu.emmapi.entities.Service; // À créer ou adapter selon votre modèle
import edu.emmapi.services.ServiceService; // À créer ou adapter

public class CoachInterfaceController {
    @FXML private TableView<ServiceReservation> reservationTable;
    @FXML private TableColumn<ServiceReservation, String> serviceNameColumn;
    @FXML private TableColumn<ServiceReservation, Integer> clientCountColumn;
    @FXML private TextField searchField;
    @FXML private TextArea feedbackArea;

    private ServiceService serviceService; // Remplace TypeAbonnementService
    private ObservableList<ServiceReservation> reservations;

    public static class ServiceReservation {
        private final SimpleStringProperty serviceName;
        private final SimpleIntegerProperty clientCount;

        public ServiceReservation(String serviceName, int clientCount) {
            this.serviceName = new SimpleStringProperty(serviceName);
            this.clientCount = new SimpleIntegerProperty(clientCount);
        }

        public String getServiceName() {
            return serviceName.get();
        }

        public int getClientCount() {
            return clientCount.get();
        }

        public void setServiceName(String serviceName) {
            this.serviceName.set(serviceName);
        }

        public void setClientCount(int clientCount) {
            this.clientCount.set(clientCount);
        }
    }

    @FXML
    public void initialize() {
        serviceService = new ServiceService(); // Initialisation du service des services
        reservations = FXCollections.observableArrayList();
        reservationTable.setItems(reservations);

        // Configuration des colonnes avec cellules stylisées en noir
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        serviceNameColumn.setCellFactory(column -> new TableCell<ServiceReservation, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #000000;"); // Texte en noir
                }
            }
        });

        clientCountColumn.setCellValueFactory(new PropertyValueFactory<>("clientCount"));
        clientCountColumn.setCellFactory(column -> new TableCell<ServiceReservation, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.toString());
                    setStyle("-fx-text-fill: #000000; -fx-alignment: CENTER;"); // Texte en noir, centré
                }
            }
        });

        loadReservations();
        setupSearch();
    }

    private void loadReservations() {
        List<Service> services = serviceService.getAllServices(); // Nouvelle méthode pour récupérer les services
        Map<String, Integer> reservationCounts = getReservationCounts();

        reservations.clear();
        for (Service service : services) {
            int clientCount = reservationCounts.getOrDefault(service.getNom(), 0);
            reservations.add(new ServiceReservation(service.getNom(), clientCount));
        }
        updateFeedback("Liste des réservations de services chargée avec succès : " + reservations.size() + " services.");
    }

    private Map<String, Integer> getReservationCounts() {
        // Simule le nombre de clients par service (à remplacer par une vraie logique)
        Map<String, Integer> counts = new HashMap<>();
        List<Service> services = serviceService.getAllServices();
        for (Service service : services) {
            counts.put(service.getNom(), (int) (Math.random() * 10)); // Simulation
        }
        return counts;
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterReservations(newValue);
            updateFeedback("Recherche en cours pour : " + newValue);
        });
    }

    private void filterReservations(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            reservationTable.setItems(reservations);
        } else {
            ObservableList<ServiceReservation> filteredList = reservations.filtered(
                    reservation -> reservation.getServiceName().toLowerCase().contains(keyword.toLowerCase())
            );
            reservationTable.setItems(filteredList);
            updateFeedback("Résultats filtrés : " + filteredList.size() + " services trouvés.");
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();
        filterReservations(keyword);
    }

    @FXML
    private void handleRefresh() {
        loadReservations();
        searchField.clear();
        updateFeedback("Données des services actualisées.");
    }

    @FXML
    private void handleExportToExcel() {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Réservations Coach");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Nom du Service");
            header.createCell(1).setCellValue("Nombre de Clients");

            int rowNum = 1;
            for (ServiceReservation reservation : reservations) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(reservation.getServiceName());
                row.createCell(1).setCellValue(reservation.getClientCount());
            }

            FileOutputStream fileOut = new FileOutputStream("reservations_coach.xlsx");
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
            updateFeedback("Exportation réussie vers reservations_coach.xlsx");
        } catch (IOException e) {
            updateFeedback("Erreur lors de l'exportation : " + e.getMessage());
        }
    }

    private void updateFeedback(String message) {
        if (feedbackArea != null) {
            feedbackArea.setText(message + "\n" + (feedbackArea.getText().length() > 0 ? feedbackArea.getText() : ""));
        }
    }
}