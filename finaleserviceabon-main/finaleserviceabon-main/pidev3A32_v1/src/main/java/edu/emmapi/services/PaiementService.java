package edu.emmapi.services;

import edu.emmapi.entities.Paiement;
import edu.emmapi.tools.MyConnection;



import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class PaiementService {
    private final Connection connection;

    public PaiementService() {
        this.connection = MyConnection.getInstance().getCnx();
    }

    public Paiement getPaiementById(int idCommande) {
        String query = "SELECT * FROM paiement WHERE idCommande = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idCommande);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Paiement(
                        rs.getInt("idCommande"),
                        rs.getInt("idUtilisateur"),
                        rs.getDouble("montant"),
                        rs.getString("modeDePaiement"),
                        rs.getDate("dateDePaiement").toLocalDate(),
                        rs.getString("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void effectuerPaiement(Paiement paiement) {
        String query = "INSERT INTO paiement (idCommande, idUtilisateur, montant, modeDePaiement, dateDePaiement, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, paiement.getIdCommande());
            pstmt.setInt(2, paiement.getIdUtilisateur());
            pstmt.setDouble(3, paiement.getMontant());
            pstmt.setString(4, paiement.getModeDePaiement());
            pstmt.setDate(5, Date.valueOf(paiement.getDateDePaiement()));
            pstmt.setString(6, paiement.getStatus());
            pstmt.executeUpdate();

            // Retrieve the generated ID and update the object
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                paiement.setIdPaiement(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void supprimerPaiement(int idCommande) {
        String query = "DELETE FROM paiement WHERE idCommande = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idCommande);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Paiement> getAllPaiements() {
        List<Paiement> paiements = new ArrayList<>();
        String query = "SELECT * FROM paiement";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                paiements.add(new Paiement(
                        rs.getInt("idCommande"),
                        rs.getInt("idUtilisateur"),
                        rs.getDouble("montant"),
                        rs.getString("modeDePaiement"),
                        rs.getDate("dateDePaiement").toLocalDate(),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return paiements;
    }

    public void modifierPaiement(Paiement paiement) {
        String query = "UPDATE paiement SET idUtilisateur = ?, montant = ?, modeDePaiement = ?, dateDePaiement = ?, status = ? WHERE idCommande = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, paiement.getIdUtilisateur());
            pstmt.setDouble(2, paiement.getMontant());
            pstmt.setString(3, paiement.getModeDePaiement());
            pstmt.setDate(4, Date.valueOf(paiement.getDateDePaiement()));
            pstmt.setString(5, paiement.getStatus());
            pstmt.setInt(6, paiement.getIdCommande());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Paiement updated successfully.");
            } else {
                System.out.println("❌ No rows updated. Check if idCommande exists.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}