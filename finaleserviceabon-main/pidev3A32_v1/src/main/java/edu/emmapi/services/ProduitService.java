package edu.emmapi.services;

import edu.emmapi.entities.produit;
import edu.emmapi.interfaces.Iproduit;
import edu.emmapi.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitService implements Iproduit {

    // ✅ Suppression de `private final Connection connection;`

    public ProduitService() {
        // ✅ Plus besoin de stocker la connexion ici
    }

    @Override
    public void ajouterProduit(produit p) throws SQLException {
        String query = "INSERT INTO produit (Nom_Produit, Categorie, Prix, Stock_Dispo, Date, Fournisseur) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = MyConnection.getInstance().getCnx();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, p.getNom_Produit());
            statement.setInt(2, p.getCategorie());
            statement.setFloat(3, p.getPrix());
            statement.setInt(4, p.getStock_disponible());
            statement.setString(5, p.getDate());
            statement.setString(6, p.getFournisseur());

            statement.executeUpdate();
            System.out.println("✅ Produit ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout du produit : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void supprimerProduit(produit p) {
        String query = "DELETE FROM produit WHERE id = ?";
        try (Connection connection = MyConnection.getInstance().getCnx();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, p.getId());
            statement.executeUpdate();
            System.out.println("✅ Produit supprimé !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression du produit : " + e.getMessage());
        }
    }

    @Override
    public void modifierProduit(produit p) {
        String query = "UPDATE produit SET Nom_Produit = ?, Categorie = ?, Prix = ?, Stock_Dispo = ?, Date = ?, Fournisseur = ? WHERE id = ?";
        try (Connection connection = MyConnection.getInstance().getCnx();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, p.getNom_Produit());
            statement.setInt(2, p.getCategorie());
            statement.setFloat(3, p.getPrix());
            statement.setInt(4, p.getStock_disponible());
            statement.setString(5, p.getDate());
            statement.setString(6, p.getFournisseur());
            statement.setInt(7, p.getId());

            statement.executeUpdate();
            System.out.println("✅ Produit modifié !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification du produit : " + e.getMessage());
        }
    }

    @Override
    public List<produit> afficherProduit() {
        List<produit> produits = new ArrayList<>();
        String query = "SELECT * FROM produit";
        try (Connection connection = MyConnection.getInstance().getCnx();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                produit p = new produit();
                p.setId(resultSet.getInt("id"));
                p.setNom_Produit(resultSet.getString("Nom_Produit"));
                p.setCategorie(resultSet.getInt("Categorie"));
                p.setPrix(resultSet.getFloat("Prix"));
                p.setStock_disponible(resultSet.getInt("Stock_Dispo"));
                p.setDate(resultSet.getString("Date"));
                p.setFournisseur(resultSet.getString("Fournisseur"));
                produits.add(p);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'affichage des produits : " + e.getMessage());
        }
        return produits;
    }


    public boolean produitExiste(String nomProduit) throws SQLException {
        String sql = "SELECT COUNT(*) FROM produit WHERE Nom_Produit = ?";
        try (Connection connection = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, nomProduit);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    public produit getProduitById(int productId) {
        String query = "SELECT * FROM produit WHERE id = ?";
        produit p = null;

        try (Connection connection = MyConnection.getInstance().getCnx();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, productId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                p = new produit();
                p.setId(resultSet.getInt("id"));
                p.setNom_Produit(resultSet.getString("Nom_Produit"));
                p.setCategorie(resultSet.getInt("Categorie"));
                p.setPrix(resultSet.getFloat("Prix"));
                p.setStock_disponible(resultSet.getInt("Stock_disponible"));
                p.setDate(resultSet.getString("Date"));
                p.setFournisseur(resultSet.getString("Fournisseur"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération du produit : " + e.getMessage());
        }

        return p;
    }
    public List<produit> getAllProduits() {
        List<produit> produits = new ArrayList<>();
        String query = "SELECT * FROM produit"; // Assurez-vous que la table s'appelle bien "produit"

        try (Connection connection = MyConnection.getInstance().getCnx();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                produit p = new produit();
                p.setId(resultSet.getInt("id"));
                p.setNom_Produit(resultSet.getString("Nom_Produit"));
                p.setCategorie(resultSet.getInt("Categorie"));
                p.setPrix(resultSet.getFloat("Prix"));
                p.setStock_disponible(resultSet.getInt("Stock_disponible"));
                p.setDate(resultSet.getString("Date"));
                p.setFournisseur(resultSet.getString("Fournisseur"));
                produits.add(p);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des produits : " + e.getMessage());
        }

        return produits;
    }
}
