package edu.emmapi.controllers;
import edu.emmapi.entities.Cours;
import edu.emmapi.entities.Participant;
import edu.emmapi.services.CoursService;
import edu.emmapi.services.ParticipantService;
import edu.emmapi.tools.MyConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class Cours_Participant {

    @FXML
    private ListView<String> listdetail;

    private final CoursService coursService = new CoursService();
    private final ParticipantService participantService = new ParticipantService();

    @FXML
    private Button button_supp;

    @FXML
    public void initialize() {
        loadCoursAndParticipantsAsync();
    }

    private void loadCoursAndParticipantsAsync() {
        Task<Void> loadDataTask = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    loadCoursAndParticipants();
                } catch (SQLException e) {
                    Platform.runLater(() -> showAlert("Erreur", "Erreur lors du chargement des données : " + e.getMessage()));
                }
                return null;
            }
        };

        Thread loadDataThread = new Thread(loadDataTask);
        loadDataThread.setDaemon(true);
        loadDataThread.start();
    }

    private void loadCoursAndParticipants() throws SQLException {
        Platform.runLater(() -> listdetail.getItems().clear()); // Effacer les anciens éléments

        List<Cours> coursList = coursService.getAllDatacour();
        if (coursList.isEmpty()) {
            Platform.runLater(() -> showAlert("Aucun Cours", "Aucun cours trouvé dans la base de données."));
            return;
        }

        for (Cours cours : coursList) {
            List<Participant> participants = participantService.getParticipantsForCours(cours.getId_Cours());

            StringBuilder sb = new StringBuilder();
            sb.append("Cours: ").append(cours.getNom_Cours()).append(" (ID: ").append(cours.getId_Cours()).append(")\n");
            sb.append("Participants:\n");

            if (participants.isEmpty()) {
                sb.append("Aucun participant inscrit.\n");
            } else {
                for (Participant participant : participants) {
                    sb.append("- ").append(participant.getNom()).append(" (ID: ").append(participant.getId()).append(")\n");
                }
            }

            Platform.runLater(() -> listdetail.getItems().add(sb.toString()));
        }
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    //@FXML
    public void deleteSelectedParticipant() {
        String selectedItem = listdetail.getSelectionModel().getSelectedItem();
        if (selectedItem == null || selectedItem.trim().isEmpty()) {
            showAlert("Sélectionner un Participant", "Veuillez sélectionner un participant à supprimer.");
            return;
        }

        int courseId = extractCourseId(selectedItem);
        int participantId = extractParticipantId(selectedItem);

        if (courseId != -1 && participantId != -1) {
            try {
                participantService.deleteParticipantFromCourse(courseId, participantId);
                showAlert("Succès", "Association participant-cours supprimée avec succès !");
                loadCoursAndParticipantsAsync(); // Recharger la liste après suppression
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la suppression du participant : " + e.getMessage());
            }
        } else {
            showAlert("Erreur", "Impossible d'extraire les IDs du cours ou du participant.");
        }
    }

    private int extractCourseId(String selectedItem) {
        String[] parts = selectedItem.split("\\(ID: ");
        if (parts.length > 1 && !parts[1].trim().isEmpty()) {
            return Integer.parseInt(parts[1].replaceAll("\\D", "").trim());
        }
        return -1;
    }

    private int extractParticipantId(String selectedItem) {
        String[] parts = selectedItem.split("- |\\(ID: ");
        if (parts.length > 1 && !parts[1].trim().isEmpty()) {
            return Integer.parseInt(parts[1].replaceAll("\\D", "").trim());
        }
        return -1;
    }
    @FXML
    public void deleteParticipantFromCourse(int courseId, int participantId) throws SQLException {
        String requete = "DELETE FROM Cours_Participant WHERE id_cours = ? AND id_participant = ?";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pst = conn.prepareStatement(requete)) {

            pst.setInt(1, courseId);
            pst.setInt(2, participantId);
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Participant avec ID " + participantId + " supprimé du cours avec ID " + courseId);
            } else {
                System.out.println("Aucune association trouvée à supprimer pour le participant avec ID " + participantId + " dans le cours " + courseId);
            }
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la suppression du participant du cours : " + ex.getMessage());
            throw ex; // Relever l'exception si vous voulez la gérer plus loin
        }
    }
}




