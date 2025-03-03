package edu.emmapi.services;



import edu.emmapi.interfaces.ICategorieservice;
import edu.emmapi.entities.categorieproduit;
import edu.emmapi.tools.MyConnection;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProduitCategorie implements ICategorieservice {
    private Connection connection;

    public ProduitCategorie() {
        this.connection = MyConnection.getInstance().getCnx();
    }

    @Override
    public void ajouterCategorieProduit(categorieproduit c) {
        String query = "INSERT INTO `categorieproduit` (`nomcategorie`, `image`, `description`) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, c.getNomcategorie());

            if (c.getImage() != null) {
                statement.setBinaryStream(2, new ByteArrayInputStream(c.getImage()), c.getImage().length);
            } else {
                statement.setNull(2, java.sql.Types.BLOB);
            }

            statement.setString(3, c.getDescription()); // Ajout de la description

            statement.executeUpdate();
            System.out.println("Catégorie produit ajoutée avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la catégorie produit : " + e.getMessage());
        }
    }

    @Override
    public void supprimerCategorieProduit(categorieproduit c) {
        String query = "DELETE FROM `categorieproduit` WHERE `id` = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, c.getId());
            statement.executeUpdate(); // Exécuter la suppression
            System.out.println("Catégorie produit supprimée avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la catégorie produit : " + e.getMessage());
        }
    }

    @Override
    public void modifierCategorieProduit(categorieproduit c) {
        String query = "UPDATE `categorieproduit` SET `nomcategorie` = ?, `image` = ?, `description` = ? WHERE `id` = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, c.getNomcategorie());

            if (c.getImage() != null) {
                statement.setBinaryStream(2, new ByteArrayInputStream(c.getImage()), c.getImage().length);
            } else {
                statement.setNull(2, java.sql.Types.BLOB);
            }

            statement.setString(3, c.getDescription()); // Ajout de la description
            statement.setInt(4, c.getId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Catégorie produit modifiée avec succès !");
            } else {
                System.out.println("Aucune catégorie produit trouvée avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la catégorie produit : " + e.getMessage());
        }
    }


    @Override
    public List<categorieproduit> afficherCategorieProduit() {
        List<categorieproduit> categorieProduits = new ArrayList<>();
        String query = "SELECT `id`, `nomcategorie`, `image`, `description` FROM `categorieproduit`";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nomcategorie = resultSet.getString("nomcategorie");
                byte[] image = resultSet.getBytes("image");
                String description = resultSet.getString("description"); // Récupération de la description

                categorieproduit c = new categorieproduit(id, nomcategorie, image, description);
                categorieProduits.add(c);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'affichage des catégories produit : " + e.getMessage());
        }

        return categorieProduits;
    }
}
