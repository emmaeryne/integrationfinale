package edu.emmapi.services;

import edu.emmapi.entities.Owner;
import edu.emmapi.interfaces.IUserService;
import edu.emmapi.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OwnerService implements IUserService<Owner> {

    public Connection cnx;

    public OwnerService() {
        cnx = MyConnection.getInstance().getCnx();
    }

    @Override
    public void addEntity(Owner owner) throws SQLException {
        String requete = "INSERT INTO users (username, email, password_hash, is_active, role, official_id, security_question_id, security_answer) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(requete)) {
            pst.setString(1, owner.getUsername());
            pst.setString(2, owner.getEmail());
            pst.setString(3, owner.getPasswordHash());
            pst.setBoolean(4, owner.isActive());
            pst.setString(5, owner.getRole());
            pst.setString(6, owner.getOfficialId());
            pst.setInt(7, owner.getSecurityQuestionId());
            pst.setString(8, owner.getSecurityAnswer());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'Owner : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void deleteEntity(Owner owner) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, owner.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'Owner : " + e.getMessage());
        }
    }

    @Override
    public void updateEntity(Owner owner, boolean newEtat) throws SQLException {
        String sql = "UPDATE users SET is_active = ? WHERE role = ? AND id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setBoolean(1, newEtat); // Remplir le premier paramètre (is_active)
            ps.setString(2, owner.getRole());   // Remplir le deuxième paramètre (role)
            ps.setInt(3, owner.getId());       // Remplir le troisième paramètre (id)
            ps.executeUpdate();               // Exécuter la mise à jour
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'Owner : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Owner> getAllData() {
        List<Owner> owners = new ArrayList<>();
        String query = "SELECT * FROM users WHERE role = 'OWNER'";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String role = rs.getString("role");
                String officialId = rs.getString("official_id");
                String email = rs.getString("email");
                String passwordHash = rs.getString("password_hash");
                boolean active = rs.getBoolean("is_active");
                String username = rs.getString("username");
                int securityQuestionId = rs.getInt("security_question_id");
                String securityAnswer = rs.getString("security_answer");

                Owner owner = new Owner(id, username, email, passwordHash, active, role, securityQuestionId, securityAnswer, officialId);
                owners.add(owner);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des Owners : " + e.getMessage());
        }
        return owners;
    }

    public String RoleSelect(String email, String password) {
        String query = "SELECT role FROM users WHERE email = ? AND password_hash = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setString(1, email);       // Set the email parameter
            pstmt.setString(2, password);    // Set the password parameter

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role"); // Retrieve the role from the result set
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la sélection du rôle : " + e.getMessage());
        }
        return null; // Return null if no role is found or there is an error
    }

    public String PassSelect(String email, String password) {
        String query = "SELECT password_hash FROM users WHERE email = ? ";

        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setString(1, email);       // Set the email parameter

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password_hash"); // Retrieve the role from the result set
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la sélection du rôle : " + e.getMessage());
        }
        return null; // Return null if no role is found or there is an error
    }

    public boolean ValiditeSelect(String email, String password) {
        String query = "SELECT is_active FROM users WHERE email = ? AND password_hash = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("is_active");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de la validité : " + e.getMessage());
        }
        return false;
    }

    public int IDselect(String email, String password) {
        String query = "SELECT id FROM users WHERE email = ? AND password_hash = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id"); // Retrieve the ID from the result set
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la sélection de l'ID : " + e.getMessage());
        }
        return 0;
    }

    public String chercherEmail(String email) {
        String query = "SELECT role FROM users WHERE email = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setString(1, email);       // Set the email parameter

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role"); // Retrieve the role from the result set
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'email : " + e.getMessage());
        }
        return null;
    }
    public void ActiverAccount(String email, boolean newEtat) throws SQLException {
        String sql = "UPDATE users SET is_active = ? WHERE email = ? ";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setBoolean(1, newEtat); // Remplir le premier paramètre (is_active)
            ps.setString(2, email);   // Remplir le deuxième paramètre (role)
            ps.executeUpdate();               // Exécuter la mise à jour
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'Owner : " + e.getMessage());
            throw e;
        }
    }
}