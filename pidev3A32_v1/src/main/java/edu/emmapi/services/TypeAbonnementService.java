/*package edu.emmapi.services;

import edu.emmapi.entities.TypeAbonnement;
import edu.emmapi.interfaces.ITypeAbonnement;
import edu.emmapi.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TypeAbonnementService implements ITypeAbonnement {
    private final Connection conn;

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


    public List<TypeAbonnement> genererRecommandationsPrix() {
        List<TypeAbonnement> typesAbonnement = getAllTypeAbonnements();
        List<TypeAbonnement> recommendations = new ArrayList<>();

        for (TypeAbonnement type : typesAbonnement) {
            // Example logic for recommendations
            if (type.getPrix() > 50 && type.getIsPremium() == false) {
                recommendations.add(type);
            }
        }

        return recommendations;
    }

    public String analyserTendances() {
        // Example analysis logic
        return "Analyse des tendances: Les abonnements Premium sont de plus en plus populaires.";
    }

    @Override
    public List<TypeAbonnement> getAllTypeAbonnements() {
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
}*/

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

public class TypeAbonnementService implements ITypeAbonnement {
    private final Connection conn;

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
        List<TypeAbonnement> typesAbonnement = getAllTypeAbonnements();
        List<TypeAbonnement> recommendations = new ArrayList<>();

        for (TypeAbonnement type : typesAbonnement) {
            // Example logic for recommendations
            if (type.getPrix() > 50 && !type.getIsPremium()) {
                recommendations.add(type);
            }
        }

        return recommendations;
    }



    public String analyserTendances() {
        // Example analysis logic
        return "Analyse des tendances: Les abonnements Premium sont de plus en plus populaires.";
    }

    @Override
    public List<TypeAbonnement> getAllTypeAbonnements() {
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
}*/
package edu.emmapi.services;

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
}