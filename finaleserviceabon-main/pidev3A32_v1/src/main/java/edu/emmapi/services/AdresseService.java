package edu.emmapi.services;

import edu.emmapi.entities.Adresse;
import edu.emmapi.entities.user;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdresseService {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hive3";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public Adresse saveAdresse(Adresse adresse) {
        String sql = "INSERT INTO adresse (id, user_id, name, firstname, lastname, company, adress, postal, city, country, phone) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            adresse.setId(generateUniqueId(conn));
            stmt.setInt(1, adresse.getId());
            stmt.setInt(2, adresse.getuser().getId());
            stmt.setString(3, adresse.getName());
            stmt.setString(4, adresse.getFirstname());
            stmt.setString(5, adresse.getLastname());
            stmt.setString(6, adresse.getCompany());
            stmt.setString(7, adresse.getAdress());
            stmt.setString(8, adresse.getPostal());
            stmt.setString(9, adresse.getCity());
            stmt.setString(10, adresse.getCountry());
            stmt.setString(11, adresse.getPhone());

            stmt.executeUpdate();
            return adresse;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving address: " + e.getMessage());
        }
    }

    public Optional<Adresse> findById(Integer id) {
        String sql = "SELECT * FROM adresse WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Adresse adresse = new Adresse();
                adresse.setId(rs.getInt("id"));

                // Vous devrez récupérer l'utilisateur ici si nécessaire
                // adresse.setUser(userService.findById(rs.getInt("user_id")));

                adresse.setName(rs.getString("name"));
                adresse.setFirstname(rs.getString("firstname"));
                adresse.setLastname(rs.getString("lastname"));
                adresse.setCompany(rs.getString("company"));
                adresse.setAdress(rs.getString("adress"));
                adresse.setPostal(rs.getString("postal"));
                adresse.setCity(rs.getString("city"));
                adresse.setCountry(rs.getString("country"));
                adresse.setPhone(rs.getString("phone"));

                return Optional.of(adresse);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding address by id: " + e.getMessage());
        }
    }

    public List<Adresse> findByUser(user user) {
        List<Adresse> adresses = new ArrayList<>();
        String sql = "SELECT * FROM adresse WHERE user_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, user.getId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Adresse adresse = new Adresse();
                adresse.setId(rs.getInt("id"));
                adresse.setuser(user);
                adresse.setName(rs.getString("name"));
                adresse.setFirstname(rs.getString("firstname"));
                adresse.setLastname(rs.getString("lastname"));
                adresse.setCompany(rs.getString("company"));
                adresse.setAdress(rs.getString("adress"));
                adresse.setPostal(rs.getString("postal"));
                adresse.setCity(rs.getString("city"));
                adresse.setCountry(rs.getString("country"));
                adresse.setPhone(rs.getString("phone"));

                adresses.add(adresse);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding addresses by user: " + e.getMessage());
        }

        return adresses;
    }

    public void updateAdresse(Adresse adresse) {
        String sql = "UPDATE adresse SET name = ?, firstname = ?, lastname = ?, company = ?, " +
                "adress = ?, postal = ?, city = ?, country = ?, phone = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, adresse.getName());
            stmt.setString(2, adresse.getFirstname());
            stmt.setString(3, adresse.getLastname());
            stmt.setString(4, adresse.getCompany());
            stmt.setString(5, adresse.getAdress());
            stmt.setString(6, adresse.getPostal());
            stmt.setString(7, adresse.getCity());
            stmt.setString(8, adresse.getCountry());
            stmt.setString(9, adresse.getPhone());
            stmt.setInt(10, adresse.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating address: " + e.getMessage());
        }
    }

    public void deleteAdresse(Integer id) {
        String sql = "DELETE FROM adresse WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting address: " + e.getMessage());
        }
    }

    private int generateUniqueId(Connection conn) throws SQLException {
        String sql = "SELECT MAX(id) FROM adresse";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int maxId = rs.next() ? rs.getInt(1) : 0;
            return maxId + 1;
        }
    }
}