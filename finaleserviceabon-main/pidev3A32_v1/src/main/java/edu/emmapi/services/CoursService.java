package edu.emmapi.services;

import edu.emmapi.entities.Cours;
import edu.emmapi.entities.Participant;
import edu.emmapi.interfaces.Cours_Service;
import edu.emmapi.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursService implements Cours_Service<Cours> {

    private Connection conn;

    public CoursService() {
        this.conn = MyConnection.getInstance().getCnx();  // Connexion persistante pendant l'instance du service
    }

    // Ajouter un cours
    @Override
    public void addcours(Cours cours) throws SQLException {
        String requete = "INSERT INTO Cours (Nom_Cours, duree, Etat_Cours) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(requete)) {
            pstmt.setString(1, cours.getNom_Cours());
            pstmt.setInt(2, cours.getDuree());
            pstmt.setString(3, cours.getEtat_Cours());

            pstmt.executeUpdate();
            System.out.println("Cours ajouté");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du cours: " + e.getMessage());
            throw e;  // Relancer l'exception
        }
    }

    // Mettre à jour un cours
    @Override
    public void updatecour(Cours cours) {
        String requete = "UPDATE Cours SET Nom_Cours = ?, duree = ?, Etat_Cours = ? WHERE id_Cours = ?";

        try (PreparedStatement pst = conn.prepareStatement(requete)) {
            pst.setString(1, cours.getNom_Cours());
            pst.setInt(2, cours.getDuree());
            pst.setString(3, cours.getEtat_Cours());
            pst.setInt(4, cours.getId_Cours());

            pst.executeUpdate();
            System.out.println("Cours modifié !");
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la mise à jour du cours: " + ex.getMessage());
        }
    }

    // Supprimer un cours
    @Override
    public void deletecour(Cours cours) {
        String requete = "DELETE FROM Cours WHERE id_Cours = ?";

        try (PreparedStatement pst = conn.prepareStatement(requete)) {
            pst.setInt(1, cours.getId_Cours());
            pst.executeUpdate();
            System.out.println("Cours supprimé !");
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la suppression du cours: " + ex.getMessage());
        }
    }

    // Récupérer tous les cours
    @Override
    public List<Cours> getAllDatacour() {
        List<Cours> results = new ArrayList<>();
        String requete = "SELECT * FROM Cours";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(requete)) {

            while (rs.next()) {
                Cours p = new Cours();
                p.setId_Cours(rs.getInt("id_Cours"));
                p.setNom_Cours(rs.getString("Nom_Cours"));
                p.setDuree(rs.getInt("duree"));
                p.setEtat_Cours(rs.getString("Etat_Cours"));

                results.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des cours: " + e.getMessage());
        }
        return results;
    }

    // Récupérer un cours par son nom
    public Cours getCoursByName(String courseName) throws SQLException {
        String requete = "SELECT * FROM Cours WHERE Nom_Cours = ?";
        Cours cours = null;

        try (PreparedStatement pst = conn.prepareStatement(requete)) {
            pst.setString(1, courseName);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                cours = new Cours();
                cours.setId_Cours(rs.getInt("id_Cours"));
                cours.setNom_Cours(rs.getString("Nom_Cours"));
                cours.setDuree(rs.getInt("duree"));
                cours.setEtat_Cours(rs.getString("Etat_Cours"));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du cours par nom: " + e.getMessage());
            throw e;  // Relancer l'exception
        }

        return cours;
    }

    // Récupérer les participants inscrits à un cours donné
    public List<Participant> getParticipantsForCours(int id_cours) throws SQLException {
        List<Participant> participants = new ArrayList<>();
        String requete = "SELECT p.* FROM Participant p " +
                "INNER JOIN Cours_Participant cp ON p.id= cp.id " +
                "WHERE cp.id_cours = ?";

        try (PreparedStatement pst = conn.prepareStatement(requete)) {
            pst.setInt(1, id_cours);  // Lier l'ID du cours à la requête

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Participant participant = new Participant();
                participant.setId(rs.getInt("id_Participant"));
                participant.setNom(rs.getString("Nom"));
                participant.setPrenom(rs.getString("Prenom"));
                // Ajouter d'autres propriétés du participant ici si nécessaire

                participants.add(participant);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des participants : " + e.getMessage());
            throw e;
        }

        return participants;
    }

    // Cette méthode permet de récupérer tous les cours comme une simple méthode de raccourci
    public List<Cours> getAllCourses() {
        return getAllDatacour();
    }

    // Ne pas oublier de fermer la connexion une fois que l'application est terminée
    public void closeService() {
        MyConnection.getInstance().closeConnection();
    }
}
