package edu.emmapi.services;

import edu.emmapi.entities.Planning;
import edu.emmapi.interfaces.Planning_Service;
import edu.emmapi.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlanningService implements Planning_Service<Planning> {

    private Connection conn;

    // Constructeur pour initialiser la connexion
    public PlanningService() {
        this.conn = MyConnection.getInstance().getCnx();
    }

    // Ajouter un planning
    @Override
    public void addplanning(Planning planning) throws SQLException {
        String requete = "INSERT INTO planning (type_activite, date_planning, status, cours) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pst = conn.prepareStatement(requete)) {
            pst.setString(1, planning.getType_activite());
            pst.setDate(2, planning.getDate_planning());
            pst.setString(3, planning.getStatus());
            pst.setInt(4, planning.getCours());

            int rowsAffected = pst.executeUpdate();
            System.out.println("Planning ajouté : " + rowsAffected + " ligne(s) affectée(s)");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du planning : " + e.getMessage());
            throw e;
        }
    }

    // Mettre à jour un planning
    @Override
    public void updateplanning(Planning planning) throws SQLException {
        String requete = "UPDATE planning SET type_activite = ?, date_planning = ?, status = ?, cours = ? WHERE id_planning = ?";

        try (PreparedStatement pst = conn.prepareStatement(requete)) {
            pst.setString(1, planning.getType_activite());
            pst.setDate(2, planning.getDate_planning());
            pst.setString(3, planning.getStatus());
            pst.setInt(4, planning.getCours());
            pst.setInt(5, planning.getId_planning());

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Planning modifié avec succès : " + rowsAffected + " ligne(s) affectée(s).");
            } else {
                System.out.println("Aucune ligne affectée, vérifiez l'ID du planning.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du planning : " + e.getMessage());
            throw e;
        }
    }

    // Supprimer un planning
    @Override
    public void deleteplanning(Planning planning) throws SQLException {
        String requete = "DELETE FROM planning WHERE id_planning = ?";

        if (planning == null || planning.getId_planning() <= 0) {
            System.err.println("Planning invalide ou ID non valide.");
            throw new IllegalArgumentException("Planning invalide ou ID non valide.");
        }

        try (PreparedStatement pst = conn.prepareStatement(requete)) {
            pst.setInt(1, planning.getId_planning());

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Planning supprimé : " + rowsAffected + " ligne(s) affectée(s)");
            } else {
                System.out.println("Aucun planning trouvé avec l'ID spécifié.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du planning : " + e.getMessage());
            throw e;
        }
    }

    // Récupérer tous les plannings
    @Override
    public List<Planning> getAllDataplannig() throws SQLException {
        List<Planning> results = new ArrayList<>();
        String requete = "SELECT * FROM planning";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(requete)) {

            while (rs.next()) {
                Planning planning = new Planning(
                        rs.getInt("id_planning"),
                        rs.getString("type_activite"),
                        rs.getDate("date_planning"),
                        rs.getString("status"),
                        rs.getInt("cours")
                );
                results.add(planning);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des plannings : " + e.getMessage());
            throw e;
        }
        return results;
    }

    // Méthode pour rechercher des plannings selon un terme de recherche
    public List<Planning> searchPlannings(String searchTerm) throws SQLException {
        List<Planning> results = new ArrayList<>();
        String requete = "SELECT * FROM planning WHERE type_activite LIKE ? OR status LIKE ? OR cours LIKE ?";

        try (PreparedStatement pst = conn.prepareStatement(requete)) {

            String searchPattern = "%" + searchTerm + "%";
            pst.setString(1, searchPattern); // Recherche dans type_activite
            pst.setString(2, searchPattern); // Recherche dans status
            pst.setString(3, searchPattern); // Recherche dans cours

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Planning planning = new Planning(
                            rs.getInt("id_planning"),
                            rs.getString("type_activite"),
                            rs.getDate("date_planning"),
                            rs.getString("status"),
                            rs.getInt("cours")
                    );
                    results.add(planning);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des plannings : " + e.getMessage());
            throw e;
        }
        return results;
    }

    // Méthode qui appelle getAllDataplannig pour obtenir tous les plannings
    public List<Planning> getAllplannings() throws SQLException {
        return getAllDataplannig(); // Cette méthode peut être utilisée directement pour récupérer tous les plannings
    }
}
