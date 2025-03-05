package edu.emmapi.services;

import edu.emmapi.entities.Commande;
import edu.emmapi.tools.MyConnection;



import java.sql.*;
import java.util.ArrayList;
import java.util.List;




import java.sql.*;


public class CommandeService {
    private final Connection connection;

    public CommandeService() {
        this.connection = MyConnection.getInstance().getCnx();
    }

    public int ajouterCommande(Commande commande) {
        Connection conn = MyConnection.getInstance().getCnx(); // Get the latest connection
        String query = "INSERT INTO Commande (dateDeCommande, idUtilisateur, status) VALUES (?, ?, ?)";
        int generatedId = -1;

        try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDate(1, java.sql.Date.valueOf(commande.getDateDeCommande()));
            pstmt.setInt(2, commande.getIdUtilisateur());
            pstmt.setString(3, commande.getStatus());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ SQL Error in ajouterCommande: " + e.getMessage());
            e.printStackTrace();
        }
        return generatedId;
    }

    public void ajouterProduitACommande(int idCommande, int idProduit, int quantite, double prixUnitaire) {
        String query = "INSERT INTO commande_produits (idCommande, idProduit, quantite, prixUnitaire) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idCommande);
            pstmt.setInt(2, idProduit);
            pstmt.setInt(3, quantite);
            pstmt.setDouble(4, prixUnitaire);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Commande> getAllCommandes() {
        List<Commande> commandes = new ArrayList<>();
        String query = "SELECT * FROM commande";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                commandes.add(new Commande(
                        rs.getInt("idCommande"),
                        rs.getDate("dateDeCommande").toLocalDate(),
                        rs.getInt("idUtilisateur"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }

    public void modifierCommande(Commande commande) {
        String query = "UPDATE commande SET status = ? WHERE idCommande = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, commande.getStatus());
            pstmt.setInt(2, commande.getIdCommande());
            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Commande mise à jour avec succès.");
            } else {
                System.out.println("Échec de la mise à jour de la commande.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void supprimerCommande(int idCommande) {
        String deleteCommandeProduitsQuery = "DELETE FROM commande_produits WHERE idCommande = ?";
        String deleteCommandeQuery = "DELETE FROM commande WHERE idCommande = ?";

        try {
            connection.setAutoCommit(false); // Begin transaction

            // Delete products linked to the order first
            try (PreparedStatement pstmtProduits = connection.prepareStatement(deleteCommandeProduitsQuery)) {
                pstmtProduits.setInt(1, idCommande);
                pstmtProduits.executeUpdate();
            }

            // Now delete the order itself
            try (PreparedStatement pstmtCommande = connection.prepareStatement(deleteCommandeQuery)) {
                pstmtCommande.setInt(1, idCommande);
                int rowsDeleted = pstmtCommande.executeUpdate();

                if (rowsDeleted > 0) {
                    System.out.println("Commande supprimée avec succès.");
                } else {
                    System.out.println("Commande introuvable.");
                }
            }

            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            try {
                connection.rollback(); // Rollback in case of failure
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException autoCommitEx) {
                autoCommitEx.printStackTrace();
            }
        }
    }


    public Commande getCommandeById(int idCommande) {
        String query = "SELECT * FROM commande WHERE idCommande = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idCommande);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Commande(
                        rs.getInt("idCommande"),
                        rs.getDate("dateDeCommande").toLocalDate(),
                        rs.getInt("idUtilisateur"),
                        rs.getString("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean checkUtilisateurExists(int idUtilisateur) {
        String query = "SELECT COUNT(*) FROM utilisateur WHERE idUtilisateur = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idUtilisateur);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


}
