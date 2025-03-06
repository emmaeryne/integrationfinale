package edu.emmapi.services;

import edu.emmapi.entities.Coach;
import edu.emmapi.entities.user;
import edu.emmapi.interfaces.IUserService;
import edu.emmapi.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class userService implements IUserService<user> {
    static Connection cnx;
    public userService() {
        cnx =MyConnection.getInstance().getCnx();
    }
    @Override
    public void addEntity(user user) throws SQLException {
        if (emailExists(user.getEmail())) {
            throw new SQLException("L'email " + user.getEmail() + " est déjà utilisé.");
        }

        String requete = "INSERT INTO users (username, email, password_hash, is_active, role, security_question_id, security_answer) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete)) {
            pst.setString(1, user.getUsername());
            pst.setString(2, user.getEmail());
            pst.setString(3, user.getPasswordHash());
            pst.setBoolean(4, user.isActive());
            pst.setString(5, user.getRole());
            pst.setInt(6,user.getSecurityQuestionId());
            pst.setString(7, user.getSecurityAnswer());
            pst.executeUpdate();
        }
    }
    public boolean emailExists(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public void addEntityCoach(Coach coach) throws SQLException {
        String requete = "INSERT INTO users (username, email, password_hash, is_active, role, specialty, experience_years, certifications, security_question_id, security_answer) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete)) {
            // Set the parameters for each placeholder
            pst.setString(1, coach.getUsername());
            pst.setString(2, coach.getEmail());
            pst.setString(3, coach.getPasswordHash());
            pst.setBoolean(4, coach.isActive());
            pst.setString(5, coach.getRole());
            pst.setString(6, coach.getSpecialty());

            // Set the missing parameters (7 and 8)
            pst.setInt(7, coach.getExperienceYears());  // Experience years
            pst.setString(8, coach.getCertifications());  // Certifications

            // Set the final parameters (9 and 10)
            pst.setInt(9, coach.getSecurityQuestionId());  // Security question ID
            pst.setString(10, coach.getSecurityAnswer());  // Security answer

            // Execute the query
            pst.executeUpdate();
        }
    }



    @Override
    public void deleteEntity(user user) {
        String requete = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete)) {
            pst.setInt(1, user.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
        }

    }


    @Override
    public List<user> getAllData() {
        List<user> users = new ArrayList<>();
        String query = "SELECT * FROM users WHERE role = 'USER'";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String role = rs.getString("role");
                String email = rs.getString("email");
                String passwordHash = rs.getString("password_hash");
                boolean active = rs.getBoolean("is_active");
                String username = rs.getString("username");
                int securityQuestionId = rs.getInt("security_question_id");
                String securityAnswer = rs.getString("security_answer");
                user user = new user(id,username,email,passwordHash,active,role,securityQuestionId,securityAnswer);
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des services : " + e.getMessage());
        }
        return users;
    }
    public static List<Coach> getAllDataCoach() {
        List<Coach> coachs = new ArrayList<>();
        String query = "SELECT * FROM users WHERE role = 'COACH'";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String role = rs.getString("role");
                String email = rs.getString("email");
                String passwordHash = rs.getString("password_hash");
                boolean active = rs.getBoolean("is_active");
                String username = rs.getString("username");
                String speciality = rs.getString("specialty");
                int experienceYears = rs.getInt("experience_years");
                String certifications = rs.getString("certifications");
                int securityQuestionId = rs.getInt("security_question_id");
                String securityAnswer = rs.getString("security_answer");
                Coach coach = new Coach(id,username,email,passwordHash,active,role,securityQuestionId,securityAnswer,speciality,experienceYears,certifications);
                coachs.add(coach);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des services : " + e.getMessage());
        }
        return coachs;
    }


    public user getAllForSession(String email, String password) {
        String query = "SELECT * FROM users WHERE email = ? AND password_hash = ?";
        user user = null;

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { // Récupère uniquement le premier utilisateur trouvé
                    int id = rs.getInt("id");
                    String role = rs.getString("role");
                    boolean active = rs.getBoolean("is_active");
                    String username = rs.getString("username");
                    int securityQuestionId = rs.getInt("security_question_id");
                    String securityAnswer = rs.getString("security_answer");

                    user = new user(id, username, email, password, active, role, securityQuestionId, securityAnswer);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'utilisateur : " + e.getMessage());
        }
        return user; // Retourne null si aucun utilisateur n'est trouvé
    }


    public void updateEntity(user user, boolean newEtat) throws SQLException {
        String sql = "UPDATE users SET is_active = ? WHERE role = ? AND id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setBoolean(1, newEtat); // Remplir le premier paramètre (is_active)
            ps.setString(2, user.getRole());   // Remplir le deuxième paramètre (role)
            ps.setInt(3, user.getId());              // Remplir le troisième paramètre (id)
            ps.executeUpdate();                 // Exécuter la mise à jour
        }
    }
    public user getUserById(int id) throws SQLException {
        String query = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String role = rs.getString("role");
                    String email = rs.getString("email");
                    String passwordHash = rs.getString("password_hash");
                    boolean active = rs.getBoolean("is_active");
                    String username = rs.getString("username");
                    int securityQuestionId = rs.getInt("security_question_id");
                    String securityAnswer = rs.getString("security_answer");
                    return new user(userId, username, email, passwordHash, active, role, securityQuestionId, securityAnswer);
                }
            }
        }
        return null; // Retourne null si l'utilisateur n'est pas trouvé
    }

    public String ChercherPassword(String email,String security_answer ,String question) {
        // Updated query with a placeholder for the email
        String query = "SELECT user.password_hash " +
                "FROM security_questions sq " +
                "INNER JOIN users user ON sq.id = user.security_question_id " +
                "WHERE user.email = ? " +  // Use '?' as a placeholder for the email
                "AND user.security_answer = ? " + // Use '?' for the security answer
                "AND sq.question_text = ? " +  // Use '?' for the question text
                "LIMIT 0, 25;";

        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            // Set parameters dynamically using the provided email, security answer, and question text
            pstmt.setString(1, email);  // Set the email parameter
            pstmt.setString(2, security_answer);  // Set the security answer parameter
            pstmt.setString(3, question);  // Set the question text parameter

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password_hash");  // Retrieve the password hash from the result set
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'password_hash : " + e.getMessage());
        }
        return null;
    }


    public void updatePwd(String email, String password) {
        String sql = "UPDATE users SET password_hash = ? WHERE email = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            // Set the new password and email values
            ps.setString(1, password);
            ps.setString(2, email);

            // Execute the update
            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Password updated successfully for email: " + email);
            } else {
                System.out.println("No user found with email: " + email);
            }
        } catch (SQLException e) {
            System.err.println("Error updating password for email: " + email + " - " + e.getMessage());
            // You could rethrow the exception or handle it accordingly
        }
    }

}// Changer PersonneService en UserService

/*
    @Override
    public void addEntity(user user) throws SQLException {
        String requete = "INSERT INTO users (username, email, password_hash, is_active, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete)) {
            pst.setString(1, user.getUsername());
            pst.setString(2, user.getEmail());
            pst.setString(3, user.getPasswordHash());
            pst.setBoolean(4, user.isActive());
            pst.setString(5, user.getRole());
            pst.executeUpdate();
        }
    }

    @Override
    public void deleteEntity(user user) {
        // Implémenter la suppression d'un utilisateur
        String requete = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete)) {
            pst.setInt(1, user.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
        }
    }

    @Override
    public void updateEntity(int id, user user) {
        // Implémenter la mise à jour d'un utilisateur
        String requete = "UPDATE users SET username = ?, email = ?, password_hash = ?, is_active = ?, role = ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete)) {
            pst.setString(1, user.getUsername());
            pst.setString(2, user.getEmail());
            pst.setString(3, user.getPasswordHash());
            pst.setBoolean(4, user.isActive());
            pst.setString(5, user.getRole());
            pst.setTimestamp(6, Timestamp.valueOf(user.getUpdatedAt())); // Convertir LocalDateTime en Timestamp
            pst.setInt(7, id); // ID de l'utilisateur à mettre à jour

            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'utilisateur : " + e.getMessage());
        }
    }

    @Override
    public List<user> getAllData() {
        List<user> results = new ArrayList<>();
        String requete = "SELECT id, username, email, password_hash, is_active, role, created_at, updated_at FROM users"; // Sélection de toutes les colonnes

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pst = conn.prepareStatement(requete);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                user user = new user();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setActive(rs.getBoolean("is_active"));
                user.setRole(rs.getString("role"));
                user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime()); // Convertir Timestamp en LocalDateTime
                user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime()); // Convertir Timestamp en LocalDateTime
                results.add(user);
            }

        } catch (SQLException e) {
            // Journalisation de l'erreur
            System.err.println("Erreur lors de la récupération des données : " + e.getMessage());
        }

        // Retourne une liste immuable pour éviter des modifications externes
        return Collections.unmodifiableList(results);
    }
}*/