package edu.emmapi.services;

import edu.emmapi.interfaces.ICategorieservice;
import edu.emmapi.entities.categorieproduit;
import edu.emmapi.tools.MyConnection;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitCategorie implements ICategorieservice {
    private final Connection connection;

    public ProduitCategorie() {
        this.connection = MyConnection.getInstance().getCnx();
    }

    @Override
    public void ajouterCategorieProduit(categorieproduit c) {
        String query = "INSERT INTO `categorie_produit` (`nomcategorie`, `image`, `description`) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, c.getNomcategorie());

            if (c.getImage() != null) {
                statement.setBinaryStream(2, new ByteArrayInputStream(c.getImage()), c.getImage().length);
            } else {
                // Si image est obligatoire (NOT NULL), lève une erreur
                throw new SQLException("Image obligatoire pour la catégorie !");
            }

            statement.setString(3, c.getDescription());
            statement.executeUpdate();
            System.out.println("✅ Catégorie ajoutée avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @Override
    public void supprimerCategorieProduit(categorieproduit c) {
        String query = "DELETE FROM `categorie_produit` WHERE `id` = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, c.getId());
            statement.executeUpdate();
            System.out.println("✅ Catégorie supprimée avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @Override
    public void modifierCategorieProduit(categorieproduit c) {
        String query = "UPDATE `categorie_produit` SET `nomcategorie` = ?, `image` = ?, `description` = ? WHERE `id` = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, c.getNomcategorie());

            if (c.getImage() != null) {
                statement.setBinaryStream(2, new ByteArrayInputStream(c.getImage()), c.getImage().length);
            } else {
                throw new SQLException("Image obligatoire pour la modification !");
            }

            statement.setString(3, c.getDescription());
            statement.setInt(4, c.getId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Catégorie modifiée avec succès !");
            } else {
                System.out.println("⚠️ Aucune catégorie trouvée avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification : " + e.getMessage());
        }
    }

    @Override
    public List<categorieproduit> afficherCategorieProduit() {
        List<categorieproduit> categorieProduits = new ArrayList<>();
        String query = "SELECT `id`, `nomcategorie`, `image`, `description` FROM `categorie_produit`";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nomcategorie = resultSet.getString("nomcategorie");
                byte[] image = resultSet.getBytes("image");
                String description = resultSet.getString("description");

                categorieproduit c = new categorieproduit(id, nomcategorie, image, description);
                categorieProduits.add(c);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'affichage : " + e.getMessage());
        }

        return categorieProduits;
    }
}