/*package edu.pidev3a32.services;

import edu.pidev3a32.entities.Cours_Participant;
import edu.pidev3a32.interfaces.Cours_Participant_Service;
import edu.pidev3a32.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursParticipantService implements Cours_Participant_Service {

    // Ajouter un participant à un cours
    @Override
    public void addCoursParticipant(Object entity) throws SQLException {
        Cours_Participant coursParticipant = (Cours_Participant) entity;

        String requete = "INSERT INTO Cours_Participant (id_Cours, id_Participant) VALUES (?, ?)";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, coursParticipant.getId_cours());
            pstmt.setInt(2, coursParticipant.getId_participant());

            pstmt.executeUpdate();
            System.out.println("Cours-Participant ajouté !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de Cours-Participant : " + e.getMessage());
            throw e;
        }
    }

    // Mettre à jour une association Cours-Participant
    @Override
    public void updateCoursParticipant(Object entity) throws SQLException {
        Cours_Participant coursParticipant = (Cours_Participant) entity;

        String requete = "UPDATE Cours_Participant SET id_Cours = ?, id_Participant = ? WHERE id_Cours = ? AND id_Participant = ?";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, coursParticipant.getId_cours());
            pstmt.setInt(2, coursParticipant.getId_participant());
            pstmt.setInt(3, coursParticipant.getId_cours());
            pstmt.setInt(4, coursParticipant.getId_participant());

            pstmt.executeUpdate();
            System.out.println("Cours-Participant mis à jour !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de Cours-Participant : " + e.getMessage());
            throw e;
        }
    }

    // Supprimer une association Cours-Participant
    @Override
    public void deleteCoursParticipant(Object entity) throws SQLException {
        Cours_Participant coursParticipant = (Cours_Participant) entity;

        String requete = "DELETE FROM Cours_Participant WHERE id_Cours = ? AND id_Participant = ?";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, coursParticipant.getId_cours());
            pstmt.setInt(2, coursParticipant.getId_participant());

            pstmt.executeUpdate();
            System.out.println("Cours-Participant supprimé !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de Cours-Participant : " + e.getMessage());
            throw e;
        }
    }

    // Récupérer toutes les associations Cours-Participant
    @Override
    public List<Cours_Participant> getAllDataCoursParticipant() throws SQLException {
        List<Cours_Participant> results = new ArrayList<>();
        String requete = "SELECT * FROM Cours_Participant";

        try (Connection conn = MyConnection.getInstance().getCnx();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(requete)) {

            while (rs.next()) {
                Cours_Participant coursParticipant = new Cours_Participant();
                coursParticipant.setId_cours(rs.getInt("id_Cours"));
                coursParticipant.setId_participant(rs.getInt("id_Participant"));

                results.add(coursParticipant);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des données Cours-Participant : " + e.getMessage());
            throw e;
        }
        return results;
    }

    // Récupérer les cours d'un participant
    @Override
    public List<Cours_Participant> getCoursByParticipant(int participantId) throws SQLException {
        List<Cours_Participant> results = new ArrayList<>();
        String requete = "SELECT * FROM Cours_Participant WHERE id_Participant = ?";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, participantId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Cours_Participant coursParticipant = new Cours_Participant();
                    coursParticipant.setId_cours(rs.getInt("id_Cours"));
                    coursParticipant.setId_participant(rs.getInt("id_Participant"));

                    results.add(coursParticipant);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des cours pour le participant : " + e.getMessage());
            throw e;
        }
        return results;
    }


    public boolean isCoursParticipantExist(int coursId, int participantId) {
        String query = "SELECT COUNT(*) FROM cours_participant WHERE cours_id = ? AND participant_id = ?";

        // Obtenez la connexion via l'instance de MyConnection
        try (PreparedStatement stmt = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            stmt.setInt(1, coursId);
            stmt.setInt(2, participantId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;  // La combinaison existe déjà
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // La combinaison n'existe pas
    }

}*/
/*package edu.pidev3a32.services;

import edu.pidev3a32.entities.Cours_Participant;
import edu.pidev3a32.interfaces.Cours_Participant_Service;
import edu.pidev3a32.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursParticipantService implements Cours_Participant_Service {

    // Ajouter un participant à un cours
    @Override
    public void addCoursParticipant(Object entity) throws SQLException {
        Cours_Participant coursParticipant = (Cours_Participant) entity;

        // Vérification de l'existence avant d'ajouter
        if (isCoursParticipantExist(coursParticipant.getId_cours(), coursParticipant.getId_participant())) {
            System.out.println("La combinaison Cours-Participant existe déjà.");
            return;  // On ne fait rien si l'association existe déjà
        }

        String requete = "INSERT INTO Cours_Participant (id_Cours, id_Participant) VALUES (?, ?)";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, coursParticipant.getId_cours());
            pstmt.setInt(2, coursParticipant.getId_participant());

            pstmt.executeUpdate();
            System.out.println("Cours-Participant ajouté !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de Cours-Participant : " + e.getMessage());
            throw e;
        }
    }

    // Mettre à jour une association Cours-Participant
    @Override
    public void updateCoursParticipant(Object entity) throws SQLException {
        Cours_Participant coursParticipant = (Cours_Participant) entity;

        String requete = "UPDATE Cours_Participant SET id_Cours = ?, id_Participant = ? WHERE id_Cours = ? AND id_Participant = ?";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, coursParticipant.getId_cours());
            pstmt.setInt(2, coursParticipant.getId_participant());
            pstmt.setInt(3, coursParticipant.getId_cours());
            pstmt.setInt(4, coursParticipant.getId_participant());

            pstmt.executeUpdate();
            System.out.println("Cours-Participant mis à jour !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de Cours-Participant : " + e.getMessage());
            throw e;
        }
    }

    // Supprimer une association Cours-Participant
    @Override
    public void deleteCoursParticipant(Object entity) throws SQLException {
        Cours_Participant coursParticipant = (Cours_Participant) entity;

        String requete = "DELETE FROM Cours_Participant WHERE id_Cours = ? AND id_Participant = ?";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, coursParticipant.getId_cours());
            pstmt.setInt(2, coursParticipant.getId_participant());

            pstmt.executeUpdate();
            System.out.println("Cours-Participant supprimé !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de Cours-Participant : " + e.getMessage());
            throw e;
        }
    }

    // Récupérer toutes les associations Cours-Participant
    @Override
    public List<Cours_Participant> getAllDataCoursParticipant() throws SQLException {
        List<Cours_Participant> results = new ArrayList<>();
        String requete = "SELECT * FROM Cours_Participant";

        try (Connection conn = MyConnection.getInstance().getCnx();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(requete)) {

            while (rs.next()) {
                Cours_Participant coursParticipant = new Cours_Participant();
                coursParticipant.setId_cours(rs.getInt("id_Cours"));
                coursParticipant.setId_participant(rs.getInt("id_Participant"));

                results.add(coursParticipant);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des données Cours-Participant : " + e.getMessage());
            throw e;
        }
        return results;
    }

    // Récupérer les cours d'un participant
    @Override
    public List<Cours_Participant> getCoursByParticipant(int participantId) throws SQLException {
        List<Cours_Participant> results = new ArrayList<>();
        String requete = "SELECT * FROM Cours_Participant WHERE id_Participant = ?";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, participantId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Cours_Participant coursParticipant = new Cours_Participant();
                    coursParticipant.setId_cours(rs.getInt("id_Cours"));
                    coursParticipant.setId_participant(rs.getInt("id_Participant"));

                    results.add(coursParticipant);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des cours pour le participant : " + e.getMessage());
            throw e;
        }
        return results;
    }

    // Vérifier si un participant est déjà inscrit à un cours
    public boolean isCoursParticipantExist(int coursId, int participantId) {
        String query = "SELECT COUNT(*) FROM cours_participant WHERE cours_id = ? AND participant_id = ?";

        try (PreparedStatement stmt = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            stmt.setInt(1, coursId);
            stmt.setInt(2, participantId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;  // La combinaison existe déjà
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // La combinaison n'existe pas
    }
}*/

/*package edu.pidev3a32.services;

import edu.pidev3a32.entities.Cours_Participant;
import edu.pidev3a32.interfaces.Cours_Participant_Service;
import edu.pidev3a32.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursParticipantService implements Cours_Participant_Service {

    // Ajouter un participant à un cours
    @Override
    public void addCoursParticipant(Object entity) throws SQLException {
        Cours_Participant coursParticipant = (Cours_Participant) entity;

        // Vérification de l'existence avant d'ajouter
        if (isCoursParticipantExist(coursParticipant.getId_cours(), coursParticipant.getId_participant())) {
            System.out.println("La combinaison Cours-Participant existe déjà.");
            return;  // On ne fait rien si l'association existe déjà
        }

        String requete = "INSERT INTO Cours_Participant (id_Cours, id_Participant) VALUES (?, ?)";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, coursParticipant.getId_cours());
            pstmt.setInt(2, coursParticipant.getId_participant());

            pstmt.executeUpdate();
            System.out.println("Cours-Participant ajouté !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de Cours-Participant : " + e.getMessage());
            throw e;
        }
    }

    // Mettre à jour une association Cours-Participant
    @Override
    public void updateCoursParticipant(Object entity) throws SQLException {
        Cours_Participant coursParticipant = (Cours_Participant) entity;

        String requete = "UPDATE Cours_Participant SET id_Cours = ?, id_Participant = ? WHERE id_Cours = ? AND id_Participant = ?";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, coursParticipant.getId_cours());
            pstmt.setInt(2, coursParticipant.getId_participant());
            pstmt.setInt(3, coursParticipant.getId_cours());
            pstmt.setInt(4, coursParticipant.getId_participant());

            pstmt.executeUpdate();
            System.out.println("Cours-Participant mis à jour !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de Cours-Participant : " + e.getMessage());
            throw e;
        }
    }

    // Supprimer une association Cours-Participant
    @Override
    public void deleteCoursParticipant(Object entity) throws SQLException {
        Cours_Participant coursParticipant = (Cours_Participant) entity;

        String requete = "DELETE FROM Cours_Participant WHERE id_Cours = ? AND id_Participant = ?";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, coursParticipant.getId_cours());
            pstmt.setInt(2, coursParticipant.getId_participant());

            pstmt.executeUpdate();
            System.out.println("Cours-Participant supprimé !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de Cours-Participant : " + e.getMessage());
            throw e;
        }
    }

    // Récupérer toutes les associations Cours-Participant
    @Override
    public List<Cours_Participant> getAllDataCoursParticipant() throws SQLException {
        List<Cours_Participant> results = new ArrayList<>();
        String requete = "SELECT * FROM Cours_Participant";

        try (Connection conn = MyConnection.getInstance().getCnx();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(requete)) {

            while (rs.next()) {
                Cours_Participant coursParticipant = new Cours_Participant();
                coursParticipant.setId_cours(rs.getInt("id_Cours"));
                coursParticipant.setId_participant(rs.getInt("id_Participant"));

                results.add(coursParticipant);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des données Cours-Participant : " + e.getMessage());
            throw e;
        }
        return results;
    }

    // Récupérer les cours d'un participant
    @Override
    public List<Cours_Participant> getCoursByParticipant(int participantId) throws SQLException {
        List<Cours_Participant> results = new ArrayList<>();
        String requete = "SELECT * FROM Cours_Participant WHERE id_Participant = ?";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, participantId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Cours_Participant coursParticipant = new Cours_Participant();
                    coursParticipant.setId_cours(rs.getInt("id_Cours"));
                    coursParticipant.setId_participant(rs.getInt("id_Participant"));

                    results.add(coursParticipant);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des cours pour le participant : " + e.getMessage());
            throw e;
        }
        return results;
    }

    // Vérifier si un participant est déjà inscrit à un cours
    public boolean isCoursParticipantExist(int coursId, int participantId) {
        String query = "SELECT COUNT(*) FROM Cours_Participant WHERE id_Cours = ? AND id_Participant = ?";

        try (PreparedStatement stmt = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            stmt.setInt(1, coursId);
            stmt.setInt(2, participantId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;  // La combinaison existe déjà
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'existence de Cours-Participant : " + e.getMessage());
        }
        return false;  // La combinaison n'existe pas
    }
}*/

/*package edu.pidev3a32.services;

import edu.pidev3a32.entities.Cours_Participant;
import edu.pidev3a32.interfaces.Cours_Participant_Service;
import edu.pidev3a32.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursParticipantService implements Cours_Participant_Service {

    // Ajouter un participant à un cours
    @Override
    public void addCoursParticipant(Object entity) throws SQLException {
        Cours_Participant coursParticipant = (Cours_Participant) entity;

        // Vérification de l'existence avant d'ajouter
        if (isCoursParticipantExist(coursParticipant.getId_cours(), coursParticipant.getId_participant())) {
            System.out.println("La combinaison Cours-Participant existe déjà.");
            return;  // On ne fait rien si l'association existe déjà
        }

        String requete = "INSERT INTO Cours_Participant (id_Cours, id_Participant) VALUES (?, ?)";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, coursParticipant.getId_cours());
            pstmt.setInt(2, coursParticipant.getId_participant());

            pstmt.executeUpdate();
            System.out.println("Cours-Participant ajouté !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de Cours-Participant : " + e.getMessage());
            throw e;
        }
    }

    // Mettre à jour une association Cours-Participant
    @Override
    public void updateCoursParticipant(Object entity) throws SQLException {
        Cours_Participant coursParticipant = (Cours_Participant) entity;

        String requete = "UPDATE Cours_Participant SET id_Cours = ?, id_Participant = ? WHERE id_Cours = ? AND id_Participant = ?";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, coursParticipant.getId_cours());
            pstmt.setInt(2, coursParticipant.getId_participant());
            pstmt.setInt(3, coursParticipant.getId_cours());
            pstmt.setInt(4, coursParticipant.getId_participant());

            pstmt.executeUpdate();
            System.out.println("Cours-Participant mis à jour !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de Cours-Participant : " + e.getMessage());
            throw e;
        }
    }

    // Supprimer une association Cours-Participant
    @Override
    public void deleteCoursParticipant(Object entity) throws SQLException {
        Cours_Participant coursParticipant = (Cours_Participant) entity;

        String requete = "DELETE FROM Cours_Participant WHERE id_Cours = ? AND id_Participant = ?";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, coursParticipant.getId_cours());
            pstmt.setInt(2, coursParticipant.getId_participant());

            pstmt.executeUpdate();
            System.out.println("Cours-Participant supprimé !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de Cours-Participant : " + e.getMessage());
            throw e;
        }
    }

    // Récupérer toutes les associations Cours-Participant
    @Override
    public List<Cours_Participant> getAllDataCoursParticipant() throws SQLException {
        List<Cours_Participant> results = new ArrayList<>();
        String requete = "SELECT * FROM Cours_Participant";

        try (Connection conn = MyConnection.getInstance().getCnx();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(requete)) {

            while (rs.next()) {
                Cours_Participant coursParticipant = new Cours_Participant();
                coursParticipant.setId_cours(rs.getInt("id_Cours"));
                coursParticipant.setId_participant(rs.getInt("id_Participant"));

                results.add(coursParticipant);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des données Cours-Participant : " + e.getMessage());
            throw e;
        }
        return results;
    }

    // Récupérer les cours d'un participant
    @Override
    public List<Cours_Participant> getCoursByParticipant(int participantId) throws SQLException {
        List<Cours_Participant> results = new ArrayList<>();
        String requete = "SELECT * FROM Cours_Participant WHERE id_Participant = ?";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, participantId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Cours_Participant coursParticipant = new Cours_Participant();
                    coursParticipant.setId_cours(rs.getInt("id_Cours"));
                    coursParticipant.setId_participant(rs.getInt("id_Participant"));

                    results.add(coursParticipant);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des cours pour le participant : " + e.getMessage());
            throw e;
        }
        return results;
    }

    // Vérifier si un participant est déjà inscrit à un cours
    public boolean isCoursParticipantExist(int coursId, int participantId) {
        String query = "SELECT COUNT(*) FROM Cours_Participant WHERE id_Cours = ? AND id_Participant = ?";

        try (PreparedStatement stmt = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            stmt.setInt(1, coursId);
            stmt.setInt(2, participantId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;  // La combinaison existe déjà
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'existence de Cours-Participant : " + e.getMessage());
        }
        return false;  // La combinaison n'existe pas
    }
}*/
/*package edu.emmapi.services;

import edu.emmapi.entities.Cours_Participant;
import edu.emmapi.interfaces.Cours_Participant_Service;
import edu.emmapi.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursParticipantService implements Cours_Participant_Service {

    // Ajouter un participant à un cours
    @Override
    public void addCoursParticipant(Object entity) throws SQLException {
        Cours_Participant coursParticipant = (Cours_Participant) entity;

        // Vérification de l'existence avant d'ajouter
        if (isCoursParticipantExist(coursParticipant.getId_cours(), coursParticipant.getId_participant())) {
            System.out.println("La combinaison Cours-Participant existe déjà.");
            return;  // On ne fait rien si l'association existe déjà
        }

        String requete = "INSERT INTO Cours_Participant (id_Cours, id_Participant) VALUES (?, ?)";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, coursParticipant.getId_cours());
            pstmt.setInt(2, coursParticipant.getId_participant());

            pstmt.executeUpdate();
            System.out.println("Cours-Participant ajouté !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de Cours-Participant : " + e.getMessage());
            throw e;
        }
    }

    // Mettre à jour une association Cours-Participant
    @Override
    public void updateCoursParticipant(Object entity) throws SQLException {
        Cours_Participant coursParticipant = (Cours_Participant) entity;

        String requete = "UPDATE Cours_Participant SET id_Cours = ?, id_Participant = ? WHERE id_Cours = ? AND id_Participant = ?";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, coursParticipant.getId_cours());
            pstmt.setInt(2, coursParticipant.getId_participant());
            pstmt.setInt(3, coursParticipant.getId_cours());
            pstmt.setInt(4, coursParticipant.getId_participant());

            pstmt.executeUpdate();
            System.out.println("Cours-Participant mis à jour !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de Cours-Participant : " + e.getMessage());
            throw e;
        }
    }

    // Supprimer une association Cours-Participant
    @Override
    public void deleteCoursParticipant(Object entity) throws SQLException {
        Cours_Participant coursParticipant = (Cours_Participant) entity;

        String requete = "DELETE FROM Cours_Participant WHERE id_Cours = ? AND id_Participant = ?";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, coursParticipant.getId_cours());
            pstmt.setInt(2, coursParticipant.getId_participant());

            pstmt.executeUpdate();
            System.out.println("Cours-Participant supprimé !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de Cours-Participant : " + e.getMessage());
            throw e;
        }
    }

    // Récupérer toutes les associations Cours-Participant
    @Override
    public List<Cours_Participant> getAllDataCoursParticipant() throws SQLException {
        List<Cours_Participant> results = new ArrayList<>();
        String requete = "SELECT * FROM Cours_Participant";

        try (Connection conn = MyConnection.getInstance().getCnx();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(requete)) {

            while (rs.next()) {
                Cours_Participant coursParticipant = new Cours_Participant();
                coursParticipant.setId_cours(rs.getInt("id_Cours"));
                coursParticipant.setId_participant(rs.getInt("id_Participant"));

                results.add(coursParticipant);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des données Cours-Participant : " + e.getMessage());
            throw e;
        }
        return results;
    }

    // Récupérer les cours d'un participant
    @Override
    public List<Cours_Participant> getCoursByParticipant(int participantId) throws SQLException {
        List<Cours_Participant> results = new ArrayList<>();
        String requete = "SELECT * FROM Cours_Participant WHERE id_Participant = ?";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, participantId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Cours_Participant coursParticipant = new Cours_Participant();
                    coursParticipant.setId_cours(rs.getInt("id_Cours"));
                    coursParticipant.setId_participant(rs.getInt("id_Participant"));

                    results.add(coursParticipant);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des cours pour le participant : " + e.getMessage());
            throw e;
        }
        return results;
    }

    // Récupérer les participants d'un cours
    public List<Cours_Participant> getParticipantsByCourse(int coursId) throws SQLException {
        List<Cours_Participant> results = new ArrayList<>();
        String requete = "SELECT * FROM Cours_Participant WHERE id_Cours = ?";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, coursId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Cours_Participant coursParticipant = new Cours_Participant();
                    coursParticipant.setId_cours(rs.getInt("id_Cours"));
                    coursParticipant.setId_participant(rs.getInt("id_Participant"));

                    results.add(coursParticipant);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des participants pour le cours : " + e.getMessage());
            throw e;
        }
        return results;
    }

    // Vérifier si un participant est déjà inscrit à un cours
    public boolean isCoursParticipantExist(int coursId, int participantId) {
        String query = "SELECT COUNT(*) FROM Cours_Participant WHERE id_Cours = ? AND id_Participant = ?";

        try (PreparedStatement stmt = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            stmt.setInt(1, coursId);
            stmt.setInt(2, participantId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;  // La combinaison existe déjà
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'existence de Cours-Participant : " + e.getMessage());
        }
        return false;  // La combinaison n'existe pas
    }
}*/
package edu.emmapi.services;

import edu.emmapi.entities.Cours_Participant;
import edu.emmapi.interfaces.Cours_Participant_Service;
import edu.emmapi.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursParticipantService implements Cours_Participant_Service {

    // Ajouter un participant à un cours
    @Override
    public void addCoursParticipant(Object entity) throws SQLException {
        Cours_Participant coursParticipant = (Cours_Participant) entity;

        // Vérification de l'existence du cours et du participant avant d'ajouter
        if (!isCoursExist(coursParticipant.getId_cours())) {
            System.out.println("Le cours n'existe pas.");
            return;  // Si le cours n'existe pas, on ne fait rien
        }

        if (!isParticipantExist(coursParticipant.getId_participant())) {
            System.out.println("Le participant n'existe pas.");
            return;  // Si le participant n'existe pas, on ne fait rien
        }

        // Vérification de l'existence de l'association avant d'ajouter
        if (isCoursParticipantExist(coursParticipant.getId_cours(), coursParticipant.getId_participant())) {
            System.out.println("La combinaison Cours-Participant existe déjà.");
            return;  // On ne fait rien si l'association existe déjà
        }

        String requete = "INSERT INTO Cours_Participant (id_Cours, id_Participant) VALUES (?, ?)";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, coursParticipant.getId_cours());
            pstmt.setInt(2, coursParticipant.getId_participant());

            pstmt.executeUpdate();
            System.out.println("Cours-Participant ajouté !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de Cours-Participant : " + e.getMessage());
            throw e;
        }
    }

    // Mettre à jour une association Cours-Participant
    @Override
    public void updateCoursParticipant(Object entity) throws SQLException {
        Cours_Participant coursParticipant = (Cours_Participant) entity;

        // Vérification de l'existence du cours et du participant avant de mettre à jour
        if (!isCoursExist(coursParticipant.getId_cours())) {
            System.out.println("Le cours n'existe pas.");
            return;  // Si le cours n'existe pas, on ne fait rien
        }

        if (!isParticipantExist(coursParticipant.getId_participant())) {
            System.out.println("Le participant n'existe pas.");
            return;  // Si le participant n'existe pas, on ne fait rien
        }

        String requete = "UPDATE Cours_Participant SET id_Cours = ?, id_Participant = ? WHERE id_Cours = ? AND id_Participant = ?";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, coursParticipant.getId_cours());
            pstmt.setInt(2, coursParticipant.getId_participant());
            pstmt.setInt(3, coursParticipant.getId_cours());
            pstmt.setInt(4, coursParticipant.getId_participant());

            pstmt.executeUpdate();
            System.out.println("Cours-Participant mis à jour !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de Cours-Participant : " + e.getMessage());
            throw e;
        }
    }

    // Supprimer une association Cours-Participant
    @Override
    public void deleteCoursParticipant(Object entity) throws SQLException {
        Cours_Participant coursParticipant = (Cours_Participant) entity;

        // Vérification de l'existence de l'association avant de supprimer
        if (!isCoursParticipantExist(coursParticipant.getId_cours(), coursParticipant.getId_participant())) {
            System.out.println("L'association Cours-Participant n'existe pas.");
            return;  // Si l'association n'existe pas, on ne fait rien
        }

        String requete = "DELETE FROM Cours_Participant WHERE id_Cours = ? AND id_Participant = ?";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, coursParticipant.getId_cours());
            pstmt.setInt(2, coursParticipant.getId_participant());

            pstmt.executeUpdate();
            System.out.println("Cours-Participant supprimé !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de Cours-Participant : " + e.getMessage());
            throw e;
        }
    }

    // Récupérer toutes les associations Cours-Participant
    @Override
    public List<Cours_Participant> getAllDataCoursParticipant() throws SQLException {
        List<Cours_Participant> results = new ArrayList<>();
        String requete = "SELECT * FROM Cours_Participant";

        try (Connection conn = MyConnection.getInstance().getCnx();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(requete)) {

            while (rs.next()) {
                Cours_Participant coursParticipant = new Cours_Participant();
                coursParticipant.setId_cours(rs.getInt("id_Cours"));
                coursParticipant.setId_participant(rs.getInt("id_Participant"));

                results.add(coursParticipant);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des données Cours-Participant : " + e.getMessage());
            throw e;
        }
        return results;
    }

    // Récupérer les cours d'un participant
    @Override
    public List<Cours_Participant> getCoursByParticipant(int participantId) throws SQLException {
        List<Cours_Participant> results = new ArrayList<>();
        String requete = "SELECT * FROM Cours_Participant WHERE id_Participant = ?";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, participantId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Cours_Participant coursParticipant = new Cours_Participant();
                    coursParticipant.setId_cours(rs.getInt("id_Cours"));
                    coursParticipant.setId_participant(rs.getInt("id_Participant"));

                    results.add(coursParticipant);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des cours pour le participant : " + e.getMessage());
            throw e;
        }
        return results;
    }

    // Récupérer les participants d'un cours
    public List<Cours_Participant> getParticipantsByCourse(int coursId) throws SQLException {
        List<Cours_Participant> results = new ArrayList<>();
        String requete = "SELECT * FROM Cours_Participant WHERE id_Cours = ?";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {

            pstmt.setInt(1, coursId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Cours_Participant coursParticipant = new Cours_Participant();
                    coursParticipant.setId_cours(rs.getInt("id_Cours"));
                    coursParticipant.setId_participant(rs.getInt("id_Participant"));

                    results.add(coursParticipant);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des participants pour le cours : " + e.getMessage());
            throw e;
        }
        return results;
    }

    // Vérifier si un participant est déjà inscrit à un cours
    public boolean isCoursParticipantExist(int coursId, int participantId) {
        String query = "SELECT COUNT(*) FROM Cours_Participant WHERE id_Cours = ? AND id_Participant = ?";

        try (PreparedStatement stmt = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            stmt.setInt(1, coursId);
            stmt.setInt(2, participantId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;  // La combinaison existe déjà
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'existence de Cours-Participant : " + e.getMessage());
        }
        return false;  // La combinaison n'existe pas
    }

    // Vérifier si le cours existe
    private boolean isCoursExist(int coursId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Cours WHERE id_Cours = ?";
        try (PreparedStatement stmt = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            stmt.setInt(1, coursId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;  // Le cours existe
            }
        }
        return false;  // Le cours n'existe pas
    }

    // Vérifier si le participant existe
    private boolean isParticipantExist(int participantId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Participant WHERE id = ?";
        try (PreparedStatement stmt = MyConnection.getInstance().getCnx().prepareStatement(query)) {
            stmt.setInt(1, participantId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;  // Le participant existe
            }
        }
        return false;  // Le participant n'existe pas
    }
}


