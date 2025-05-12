package edu.emmapi.services;

import edu.emmapi.entities.CategorieItem;
import edu.emmapi.tools.MyConnection;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CategorieService {
    private final Connection connection;
    private final Map<Integer, String> categoriesCache = new ConcurrentHashMap<>();

    public CategorieService() {
        this.connection = MyConnection.getInstance().getCnx();
        refreshCache(); // Initialisation du cache au démarrage
    }

    /**
     * Récupère toutes les catégories sous forme d'objets CategorieItem
     */
    public List<CategorieItem> getAllCategorieItems() {
        return categoriesCache.entrySet().stream()
                .map(entry -> new CategorieItem(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(CategorieItem::getNom))
                .collect(Collectors.toList());
    }

    /**
     * Récupère le nom d'une catégorie par son ID
     */
    public String getNomCategorieById(int id) {
        return categoriesCache.get(id);
    }

    /**
     * Vérifie si une catégorie existe
     */
    public boolean categorieExiste(int id) {
        return categoriesCache.containsKey(id);
    }

    /**
     * Rafraîchit le cache des catégories
     */
    public void refreshCache() {
        String query = "SELECT id, nomcategorie FROM categorie_produit";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            categoriesCache.clear();
            while (rs.next()) {
                categoriesCache.put(rs.getInt("id"), rs.getString("nomcategorie"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du rafraîchissement du cache des catégories", e);
        }
    }

    /**
     * Ajoute une nouvelle catégorie
     */
    public boolean ajouterCategorie(String nomCategorie) {
        String sql = "INSERT INTO categorie_produit (nomcategorie) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nomCategorie);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        categoriesCache.put(generatedKeys.getInt(1), nomCategorie);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout de la catégorie", e);
        }
        return false;
    }
}
