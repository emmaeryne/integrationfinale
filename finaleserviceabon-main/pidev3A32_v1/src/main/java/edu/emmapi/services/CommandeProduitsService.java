package edu.emmapi.services;


import edu.emmapi.entities.CommandeProduits;
import edu.emmapi.entities.produit;
import edu.emmapi.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeProduitsService {




    public void ajouterCommandeProduit(CommandeProduits commandeProduits) {
        String query = "INSERT INTO commande_produits (idCommande, id, quantite, prix) VALUES (?, ?, ?, ?)";
        try (Connection connection = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            // Vérifier si le produit existe
            ProduitService produitService = new ProduitService();
            produit produit = produitService.getProduitById(commandeProduits.getId());
            if (produit == null) {
                System.err.println("❌ Le produit avec l'ID " + commandeProduits.getId() + " n'existe pas.");
                return;
            }

            // Ajouter le produit à la commande
            pstmt.setInt(1, commandeProduits.getIdCommande());
            pstmt.setInt(2, commandeProduits.getId());
            pstmt.setInt(3, commandeProduits.getQuantite());
            pstmt.setFloat(4, commandeProduits.getPrix());
            pstmt.executeUpdate();

            System.out.println("✅ Produit ajouté à la commande avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout du produit à la commande : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<CommandeProduits> getProduitsByCommande(int idCommande) {
        List<CommandeProduits> produitsList = new ArrayList<>();
        String query = "SELECT * FROM commande_produits WHERE idCommande = ?";
        try (Connection connection = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, idCommande);
            ResultSet rs = pstmt.executeQuery();

            // Récupérer les produits associés à la commande
            ProduitService produitService = new ProduitService();
            while (rs.next()) {
                int productId = rs.getInt("id");
                produit produit = produitService.getProduitById(productId);

                if (produit != null) {
                    CommandeProduits commandeProduits = new CommandeProduits(
                            rs.getInt("idCommande"),
                            productId,
                            rs.getInt("quantite"),
                            rs.getFloat("prix")
                    );
                    produitsList.add(commandeProduits);
                    System.out.println("✅ Produit trouvé : " + produit.getNom_Produit() + " (ID: " + productId + ")");
                } else {
                    System.err.println("❌ Produit ID " + productId + " non trouvé.");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des produits de la commande : " + e.getMessage());
            e.printStackTrace();
        }

        return produitsList;
    }
}