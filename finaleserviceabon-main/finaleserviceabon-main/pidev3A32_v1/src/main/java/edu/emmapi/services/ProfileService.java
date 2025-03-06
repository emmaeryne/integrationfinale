package edu.emmapi.services;

import com.google.api.services.gmail.model.Profile;
import edu.emmapi.entities.profile;
import edu.emmapi.interfaces.IService;
import edu.emmapi.tools.MyConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileService implements IService<profile> {
    Connection cnx;
    public ProfileService() {
        cnx =MyConnection.getInstance().getCnx();
    }
    @Override
    public void addEntity(profile profile,int id ) throws SQLException {
        String requete = "INSERT INTO profile (user_id, first_name, last_name, date_of_birth, profile_picture, bio, location, phone_number, website, social_media_links) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete)) {
            pst.setInt(1, id);
            pst.setString(2, profile.getFirstName());
            pst.setString(3, profile.getLastName());
            pst.setString(4, profile.getDateOfBirth());
            pst.setString(5, profile.getProfilePicture());
            pst.setString(6, profile.getBio());
            pst.setString(7, profile.getLocation());
            pst.setString(8, profile.getPhoneNumber());
            pst.setString(9, profile.getWebsite());
            pst.setString(10, profile.getSocialMediaLink());

            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du profil : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void deleteEntity(int id) {
        String requete = "DELETE FROM profile WHERE user_id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(requete)) {
            pst.setInt(1, id); // Définir l'id pour la suppression
            int rowsAffected = pst.executeUpdate(); // Exécuter la suppression
            if (rowsAffected > 0) {
                System.out.println("Profil supprimé avec succès.");
            } else {
                System.out.println("Aucun profil trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du profil : " + e.getMessage());
            e.printStackTrace(); // Ajout d'un stack trace pour mieux diagnostiquer les erreurs
        }
    }


    @Override
    public void updateEntity(int id, profile profile) {

    }

    @Override
    public List<profile> getAllData() {
        return List.of();
    }
    public int ProfileSelect(int id) {
        String query = "SELECT id FROM  profile WHERE user_id=?"; // Use AND for combining conditions

        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setInt(1, id);       // Set the email parameter
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id"); // Retrieve the role from the result set
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<profile> Data(int id) {
        List<profile> results = new ArrayList<>();
        String requete = "SELECT id, user_id, first_name, last_name, date_of_birth, profile_picture, bio, location, phone_number, website, social_media_links FROM profile WHERE user_id = ?";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pst = conn.prepareStatement(requete)) {

            // Remplir le paramètre ? dans la requête
            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    profile profile = new profile(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getInt("user_id"),
                            rs.getString("last_name"),
                            rs.getString("date_of_birth"),
                            rs.getString("profile_picture"),
                            rs.getString("bio"),
                            rs.getString("location"),
                            rs.getString("phone_number"),
                            rs.getString("website"),
                            rs.getString("social_media_links")
                    );
                    results.add(profile);
                }
            }


        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des données : " + e.getMessage());
        }

        return Collections.unmodifiableList(results);
    }


    public profile dataProfile(int id) {
        String requete = "SELECT id, user_id, first_name, last_name, date_of_birth, profile_picture, bio, location, phone_number, website, social_media_links FROM profile WHERE user_id = ?";

        profile profile = null; // Profil par défaut est null

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pst = conn.prepareStatement(requete)) {

            // Remplir le paramètre ? dans la requête
            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    profile = new profile(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getInt("user_id"),
                            rs.getString("last_name"),
                            rs.getString("date_of_birth"),
                            rs.getString("profile_picture"),
                            rs.getString("bio"),
                            rs.getString("location"),
                            rs.getString("phone_number"),
                            rs.getString("website"),
                            rs.getString("social_media_links")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des données : " + e.getMessage());
        }

        return profile; // Retourne null si aucun profil n'a été trouvé
    }

    public void updateEntity(profile profile) {
        String query = "UPDATE profile SET firstName=?, lastName=?, dateOfBirth=?, bio=?, location=?, phoneNumber=?, website=?, socialMediaLink=? WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, profile.getFirstName());
            pst.setString(2, profile.getLastName());
            pst.setString(3, profile.getDateOfBirth());
            pst.setString(4, profile.getBio());
            pst.setString(5, profile.getLocation());
            pst.setString(6, profile.getPhoneNumber());
            pst.setString(7, profile.getWebsite());
            pst.setString(8, profile.getSocialMediaLink());
            pst.setInt(9, profile.getId()); // Assurez-vous que l'ID est correctement défini
            pst.executeUpdate();
            System.out.println("Profile updated successfully!");
        } catch (SQLException e) {
            System.err.println("Error updating profile: " + e.getMessage());
        }
    }

    public profile getProfileById(int id) {
        String query = "SELECT * FROM profile WHERE user_id = ?";
        profile p = null;

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    p = new profile(
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("date_of_birth"),
                            rs.getString("profile_picture"),
                            rs.getString("bio"),
                            rs.getString("location"),
                            rs.getString("phone_number"),
                            rs.getString("website"),
                            rs.getString("social_media_link")
                    );
                    p.setId(rs.getInt("user_id"));
                }
            }
        } catch (SQLException e) {
            // Log the error with a meaningful message
            System.err.println("Erreur lors de la récupération du profil avec user_id: " + id);
            e.printStackTrace(); // Or use a logging framework like Log4j or SLF4J
        }

        return p; // Returning the profile (or null if not found or error occurs)
    }
    public List<profile>  Dataindata(int id) {
        List<profile> results = new ArrayList<>();
        String requete = "SELECT id, user_id, first_name, last_name, date_of_birth, profile_picture, bio, location, phone_number, website, social_media_links FROM profile WHERE user_id = ?";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pst = conn.prepareStatement(requete)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    profile profile = new profile(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getInt("user_id"),
                            rs.getString("last_name"),
                            rs.getString("date_of_birth"),
                            rs.getString("profile_picture"),
                            rs.getString("bio"),
                            rs.getString("location"),
                            rs.getString("phone_number"),
                            rs.getString("website"),
                            rs.getString("social_media_links")
                    );
                    results.add(profile);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des données : " + e.getMessage());
        }

        return  Collections.unmodifiableList(results);
    }

}