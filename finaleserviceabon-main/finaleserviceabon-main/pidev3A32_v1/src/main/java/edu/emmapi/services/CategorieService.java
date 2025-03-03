package edu.emmapi.services;

import edu.emmapi.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategorieService {

    private Connection connection;

    public CategorieService() {
        this.connection = MyConnection.getInstance().getCnx();
    }
    public List<String> getAllCategorieNoms() {
        List<String> categories = new ArrayList<>();
        String query = "SELECT nomcategorie FROM categorieproduit"; // Vérifiez le nom exact de votre table et colonne

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                categories.add(rs.getString("nomcategorie")); // Ajoute chaque catégorie à la liste
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }
    // Récupérer toutes les catégories avec leur ID
    public Map<Integer, String> getCategories() {
        Map<Integer, String> categories = new HashMap<>();
        String sql = "SELECT id, nomcategorie FROM categorieproduit";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nomcategorie");
                categories.put(id, nom);
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des catégories !");
            e.printStackTrace();
        }

        return categories;
    }
    public String getNomCategorieById(int id) {
        String sql = "SELECT nomcategorie FROM categorieproduit WHERE id = ?";
        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("nomcategorie"); // Retourne le nom de la catégorie
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération du nom de la catégorie !");
            e.printStackTrace();
        }
        return null; // Retourne null si aucune catégorie n'est trouvée
    }

    // Vérifier si une catégorie existe
    public boolean categorieExiste(int id) {
        return getCategories().containsKey(id);
    }
}

