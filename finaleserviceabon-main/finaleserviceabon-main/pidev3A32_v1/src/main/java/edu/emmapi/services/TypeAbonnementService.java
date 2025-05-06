
/*package edu.emmapi.services;

import edu.emmapi.entities.Service;
import edu.emmapi.entities.TypeAbonnement;
import edu.emmapi.interfaces.ITypeAbonnement;
import edu.emmapi.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;

public class TypeAbonnementService implements ITypeAbonnement {
    private final Connection conn;
    public Map<String, Integer> getServiceReservationCounts() {
        // Logique pour compter les clients par service depuis une base de données
        return new HashMap<>();
    }

    public TypeAbonnementService() {
        this.conn = MyConnection.getInstance().getCnx();
    }

    @Override
    public TypeAbonnement ajouterTypeAbonnement(TypeAbonnement typeAbonnement) {
        String sql = "INSERT INTO typeabonnement (nom, description, prix, dureeEnMois, isPremium) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, typeAbonnement.getNom());
            pst.setString(2, typeAbonnement.getDescription());
            pst.setDouble(3, typeAbonnement.getPrix());
            pst.setInt(4, typeAbonnement.getDureeEnMois());
            pst.setBoolean(5, typeAbonnement.getIsPremium());

            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Echec de l'ajout du type d'abonnement.");
            }

            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    typeAbonnement.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Echec de l'ajout, aucun ID obtenu.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout du type d'abonnement", e);
        }
        return typeAbonnement;
    }

    @Override
    public List<TypeAbonnement> getAllTypeAbonnements() {
        return List.of();
    }

    public Map<Long, String> genererRecommandationsPrix(List<Service> services) {
        Map<Long, String> recommendations = new HashMap<>();

        for (Service service : services) {
            if (service.getNombreReservations() < 10 && service.getPrix() > 50) {
                recommendations.put(service.getId(), "Considérer une réduction de prix");
            } else if (service.getNombreReservations() > 50 && service.getNote() >= 4.5) {
                recommendations.put(service.getId(), "Possibilité d'augmenter le prix");
            }
        }

        return recommendations;
    }

    public List<TypeAbonnement> genererRecommandationsPrix() {
        List<TypeAbonnement> typesAbonnement = getAllTypeAbonnements(null);
        List<TypeAbonnement> recommendations = new ArrayList<>();

        for (TypeAbonnement type : typesAbonnement) {
            if (type.getPrix() > 50 && !type.getIsPremium()) {
                recommendations.add(type);
            }
        }

        return recommendations;
    }

    public String analyserTendances() {
        return "Analyse des tendances: Les abonnements Premium sont de plus en plus populaires.";
   }


    @Override
    public List<TypeAbonnement> getAllTypeAbonnements(String tri) {
        List<TypeAbonnement> typeAbonnements = new ArrayList<>();
        String sql = "SELECT * FROM typeabonnement";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                TypeAbonnement typeAbonnement = new TypeAbonnement();
                typeAbonnement.setId(rs.getLong("id"));
                typeAbonnement.setNom(rs.getString("nom"));
                typeAbonnement.setDescription(rs.getString("description"));
                typeAbonnement.setPrix(rs.getDouble("prix"));
                typeAbonnement.setDureeEnMois(rs.getInt("dureeEnMois"));
                typeAbonnement.setIsPremium(rs.getBoolean("isPremium"));
                typeAbonnements.add(typeAbonnement);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des types d'abonnement", e);
        }

        // Apply sorting based on the tri parameter
        if (tri != null) {
            switch (tri) {
                case "prix_asc" -> typeAbonnements.sort(Comparator.comparing(TypeAbonnement::getPrix));
                case "prix_desc" -> typeAbonnements.sort(Comparator.comparing(TypeAbonnement::getPrix).reversed());
                // Add other sorting criteria if needed
            }
        }

        return typeAbonnements;
    }

    @Override
    public void deleteTypeAbonnement(Long id) {
        String sql = "DELETE FROM typeabonnement WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du type d'abonnement", e);
        }
    }

    @Override
    public void updateTypeAbonnement(TypeAbonnement typeAbonnement) {
        String sql = "UPDATE typeabonnement SET nom = ?, description = ?, prix = ?, dureeEnMois = ?, isPremium = ? WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, typeAbonnement.getNom());
            pst.setString(2, typeAbonnement.getDescription());
            pst.setDouble(3, typeAbonnement.getPrix());
            pst.setInt(4, typeAbonnement.getDureeEnMois());
            pst.setBoolean(5, typeAbonnement.getIsPremium());
            pst.setLong(6, typeAbonnement.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du type d'abonnement", e);
        }
    }

    @Override
    public String genererDescription(TypeAbonnement typeAbonnement) {
        String nom = typeAbonnement.getNom();
        double prix = typeAbonnement.getPrix();
        int duree = typeAbonnement.getDureeEnMois();
        boolean isPremium = typeAbonnement.getIsPremium();

        return String.format("Découvrez l'%s pour seulement %.2f€/mois ! Profitez d'une expérience %s pendant %d mois, parfaite pour %s.",
                nom, prix, isPremium ? "premium complète" : "essentielle", duree,
                isPremium ? "les utilisateurs exigeants" : "un usage quotidien");
    }
}*/
package edu.emmapi.services;

import edu.emmapi.entities.TypeAbonnement;
import edu.emmapi.tools.MyConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TypeAbonnementService {
    private final Connection conn = MyConnection.getInstance().getCnx();

    public void ajouterTypeAbonnement(TypeAbonnement typeAbonnement) {
        String sql = "INSERT INTO hive3.type_abonnement (nom, description, prix, duree_en_mois, is_premium, updated_at, reduction, prix_reduit) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, typeAbonnement.getNom());
            pst.setString(2, typeAbonnement.getDescription());
            pst.setString(3, typeAbonnement.getPrix());
            pst.setInt(4, typeAbonnement.getDureeEnMois());
            pst.setBoolean(5, typeAbonnement.isPremium());
            pst.setTimestamp(6, typeAbonnement.getUpdatedAt() != null ? Timestamp.valueOf(typeAbonnement.getUpdatedAt()) : null);
            pst.setObject(7, typeAbonnement.getReduction());
            pst.setString(8, typeAbonnement.getPrixReduit());
            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Échec de l'ajout du type d'abonnement.");
            }
            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    typeAbonnement.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout: " + e.getMessage(), e);
        }
    }

    public List<TypeAbonnement> getAllTypeAbonnements() {
        return getAllTypeAbonnements(null);
    }

    public List<TypeAbonnement> getAllTypeAbonnements(String sort) {
        List<TypeAbonnement> typeAbonnements = new ArrayList<>();
        String sql = "SELECT * FROM hive3.type_abonnement";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                TypeAbonnement typeAbonnement = new TypeAbonnement();
                typeAbonnement.setId(rs.getLong("id"));
                typeAbonnement.setNom(rs.getString("nom"));
                typeAbonnement.setDescription(rs.getString("description"));
                typeAbonnement.setPrix(rs.getString("prix"));
                typeAbonnement.setDureeEnMois(rs.getInt("duree_en_mois"));
                typeAbonnement.setPremium(rs.getBoolean("is_premium"));
                Timestamp updatedAt = rs.getTimestamp("updated_at");
                typeAbonnement.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);
                typeAbonnement.setReduction(rs.getObject("reduction") != null ? rs.getDouble("reduction") : null);
                typeAbonnement.setPrixReduit(rs.getString("prix_reduit"));
                typeAbonnements.add(typeAbonnement);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération: " + e.getMessage(), e);
        }

        if (sort != null) {
            switch (sort) {
                case "prix_asc":
                    typeAbonnements.sort(Comparator.comparing(TypeAbonnement::getPrixAsDouble));
                    break;
                case "prix_desc":
                    typeAbonnements.sort(Comparator.comparing(TypeAbonnement::getPrixAsDouble).reversed());
                    break;
            }
        }

        return typeAbonnements;
    }

    public List<TypeAbonnement> searchTypeAbonnements(String search) {
        List<TypeAbonnement> typeAbonnements = new ArrayList<>();
        String sql = "SELECT * FROM hive3.type_abonnement WHERE nom LIKE ? OR description LIKE ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            String searchPattern = "%" + search + "%";
            pst.setString(1, searchPattern);
            pst.setString(2, searchPattern);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    TypeAbonnement typeAbonnement = new TypeAbonnement();
                    typeAbonnement.setId(rs.getLong("id"));
                    typeAbonnement.setNom(rs.getString("nom"));
                    typeAbonnement.setDescription(rs.getString("description"));
                    typeAbonnement.setPrix(rs.getString("prix"));
                    typeAbonnement.setDureeEnMois(rs.getInt("duree_en_mois"));
                    typeAbonnement.setPremium(rs.getBoolean("is_premium"));
                    Timestamp updatedAt = rs.getTimestamp("updated_at");
                    typeAbonnement.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);
                    typeAbonnement.setReduction(rs.getObject("reduction") != null ? rs.getDouble("reduction") : null);
                    typeAbonnement.setPrixReduit(rs.getString("prix_reduit"));
                    typeAbonnements.add(typeAbonnement);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche: " + e.getMessage(), e);
        }
        return typeAbonnements;
    }

    public void updateTypeAbonnement(TypeAbonnement typeAbonnement) {
        String sql = "UPDATE hive3.type_abonnement SET nom = ?, description = ?, prix = ?, duree_en_mois = ?, is_premium = ?, updated_at = ?, reduction = ?, prix_reduit = ? WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, typeAbonnement.getNom());
            pst.setString(2, typeAbonnement.getDescription());
            pst.setString(3, typeAbonnement.getPrix());
            pst.setInt(4, typeAbonnement.getDureeEnMois());
            pst.setBoolean(5, typeAbonnement.isPremium());
            pst.setTimestamp(6, typeAbonnement.getUpdatedAt() != null ? Timestamp.valueOf(typeAbonnement.getUpdatedAt()) : null);
            pst.setObject(7, typeAbonnement.getReduction());
            pst.setString(8, typeAbonnement.getPrixReduit());
            pst.setLong(9, typeAbonnement.getId());
            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Échec de la mise à jour, ID introuvable: " + typeAbonnement.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour: " + e.getMessage(), e);
        }
    }

    public void deleteTypeAbonnement(Long id) {
        String sql = "DELETE FROM hive3.type_abonnement WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, id);
            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Échec de la suppression, ID introuvable: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression: " + e.getMessage(), e);
        }
    }

    public int getReservationCount(Long typeAbonnementId) {
        String sql = "SELECT COUNT(*) FROM hive3.abonnement WHERE type_abonnement_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, typeAbonnementId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage des réservations: " + e.getMessage(), e);
        }
    }

    public List<TypeAbonnement> findMostReserved() {
        List<TypeAbonnement> typeAbonnements = getAllTypeAbonnements();
        typeAbonnements.sort((t1, t2) -> Integer.compare(
                getReservationCount(t2.getId()),
                getReservationCount(t1.getId())
        ));
        return typeAbonnements.stream().limit(3).collect(Collectors.toList());
    }
}