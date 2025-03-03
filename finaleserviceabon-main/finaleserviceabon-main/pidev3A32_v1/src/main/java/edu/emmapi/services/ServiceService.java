/*package edu.emmapi.services;

import edu.emmapi.entities.Service;
import edu.emmapi.interfaces.IServiceService;
import edu.emmapi.tools.MyConnection;

import java.sql.*;
import java.util.*;
import java.time.LocalDateTime;

public class ServiceService implements IServiceService {
    private final Connection conn;

    public ServiceService() {
        this.conn = MyConnection.getInstance().getCnx();
    }

    @Override
    public Service ajouterService(Service service) {
        if (!service.estValide()) {
            throw new IllegalArgumentException("Service invalide");
        }

        String sql = "INSERT INTO service (nom, description, prix, estActif, capaciteMax, " +
                "categorie, dureeMinutes, niveau, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, service.getNom());
            pst.setString(2, service.getDescription());
            pst.setDouble(3, service.getPrix());
            pst.setBoolean(4, service.isEstActif());
            pst.setInt(5, service.getcapaciteMax());
            pst.setString(6, service.getCategorie());
            pst.setInt(7, service.getdureeMinutes());
            pst.setInt(8, service.getNiveau());
            pst.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = pst.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("La création du service a échoué");
            }

            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    service.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("La création a échoué, aucun ID obtenu.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout du service", e);
        }

        return service;
    }

    @Override
    public void deleteService(Long idService) throws SQLException {
        String sql = "DELETE FROM service WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, idService); // Use Long for ID

            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La suppression du service a échoué");
            }
        }
    }

    @Override
    public void updateService(Service service) throws SQLException {
        if (!service.estValide()) {
            throw new IllegalArgumentException("Service invalide");
        }

        String sql = "UPDATE service SET nom = ?, description = ?, prix = ?, estActif = ?, " +
                "capaciteMax = ?, categorie = ?, dureeMinutes = ?, niveau = ?, updated_at = ? WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, service.getNom());
            pst.setString(2, service.getDescription());
            pst.setDouble(3, service.getPrix());
            pst.setBoolean(4, service.isEstActif());
            pst.setInt(5, service.getcapaciteMax());
            pst.setString(6, service.getCategorie());
            pst.setInt(7, service.getdureeMinutes());
            pst.setInt(8, service.getNiveau());
            pst.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            pst.setLong(10, service.getId());

            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La mise à jour du service a échoué");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du service", e);
        }
    }

    @Override
    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM service";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Service service = mapResultSetToService(rs);
                services.add(service);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des services", e);
        }

        return services;
    }

    @Override
    public Service getServiceById(Long idService) {
        String sql = "SELECT * FROM service WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, idService);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToService(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du service", e);
        }

        return null;
    }

    @Override
    public Service getServiceById(int idService) throws SQLException {
        String sql = "SELECT * FROM service WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idService);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToService(rs);
                } else {
                    throw new SQLException("Aucun service trouvé avec cet ID");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du service", e);
        }
    }

    @Override
    public List<Service> searchServices(String keyword) {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM service WHERE nom LIKE ? OR description LIKE ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, "%" + keyword + "%");
            pst.setString(2, "%" + keyword + "%");

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    services.add(mapResultSetToService(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de services", e);
        }

        return services;
    }

    @Override
    public List<Service> getServicesByCategory(String category) {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM service WHERE categorie = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, category);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    services.add(mapResultSetToService(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des services par catégorie", e);
        }

        return services;
    }

    private Service mapResultSetToService(ResultSet rs) throws SQLException {
        Service service = new Service();
        service.setId(rs.getLong("id"));
        service.setNom(rs.getString("nom"));
        service.setDescription(rs.getString("description"));
        service.setPrix(rs.getDouble("prix"));
        service.setEstActif(rs.getBoolean("estActif"));
        service.setcapaciteMax(rs.getInt("capaciteMax"));
        service.setCategorie(rs.getString("categorie"));
        service.setdureeMinutes(rs.getInt("dureeMinutes"));
        service.setNiveau(rs.getInt("niveau"));
        service.setNote(rs.getDouble("note"));
        service.setNombreReservations(rs.getInt("nombreReservations"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            service.setCreated_at(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            service.setUpdated_at(updatedAt.toLocalDateTime());
        }

        return service;
    }
}*/



package edu.emmapi.services;

import edu.emmapi.entities.Service;
import edu.emmapi.interfaces.IServiceService;
import edu.emmapi.tools.MyConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceService implements IServiceService {
    private final Connection conn;

    public ServiceService() {
        this.conn = MyConnection.getInstance().getCnx();
    }

    @Override
    public Service ajouterService(Service service) {
        if (!service.estValide()) {
            throw new IllegalArgumentException("Service invalide");
        }

        String sql = "INSERT INTO service (nom, description, prix, estActif, capaciteMax, " +
                "categorie, dureeMinutes, niveau, created_at, note, nombreReservations) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, service.getNom());
            pst.setString(2, service.getDescription());
            pst.setDouble(3, service.getPrix());
            pst.setBoolean(4, service.isEstActif());
            pst.setInt(5, service.getcapaciteMax());
            pst.setString(6, service.getCategorie());
            pst.setInt(7, service.getdureeMinutes());
            pst.setInt(8, service.getNiveau());
            pst.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            pst.setDouble(10, service.getNote());
            pst.setInt(11, service.getNombreReservations());

            int affectedRows = pst.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("La création du service a échoué");
            }

            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    service.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("La création a échoué, aucun ID obtenu.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout du service", e);
        }

        return service;
    }

    @Override
    public void deleteService(Long idService) throws SQLException {
        String sql = "DELETE FROM service WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, idService);

            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La suppression du service a échoué");
            }
        }
    }

    @Override
    public void updateService(Service service) throws SQLException {
        if (!service.estValide()) {
            throw new IllegalArgumentException("Service invalide");
        }

        String sql = "UPDATE service SET nom = ?, description = ?, prix = ?, estActif = ?, " +
                "capaciteMax = ?, categorie = ?, dureeMinutes = ?, niveau = ?, updated_at = ?, note = ?, nombreReservations = ? WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, service.getNom());
            pst.setString(2, service.getDescription());
            pst.setDouble(3, service.getPrix());
            pst.setBoolean(4, service.isEstActif());
            pst.setInt(5, service.getcapaciteMax());
            pst.setString(6, service.getCategorie());
            pst.setInt(7, service.getdureeMinutes());
            pst.setInt(8, service.getNiveau());
            pst.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            pst.setDouble(10, service.getNote());
            pst.setInt(11, service.getNombreReservations());
            pst.setLong(12, service.getId());

            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La mise à jour du service a échoué");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du service", e);
        }
    }

    @Override
    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM service";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Service service = mapResultSetToService(rs);
                services.add(service);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des services", e);
        }

        return services;
    }

    @Override
    public Service getServiceById(Long idService) {
        String sql = "SELECT * FROM service WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, idService);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToService(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du service", e);
        }

        return null;
    }

    @Override
    public Service getServiceById(int idService) throws SQLException {
        String sql = "SELECT * FROM service WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idService);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToService(rs);
                } else {
                    throw new SQLException("Aucun service trouvé avec cet ID");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du service", e);
        }
    }

    @Override
    public List<Service> searchServices(String keyword) {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM service WHERE nom LIKE ? OR description LIKE ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, "%" + keyword + "%");
            pst.setString(2, "%" + keyword + "%");

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    services.add(mapResultSetToService(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de services", e);
        }

        return services;
    }

    @Override
    public List<Service> getServicesByCategory(String category) {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM service WHERE categorie = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, category);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    services.add(mapResultSetToService(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des services par catégorie", e);
        }

        return services;
    }



    private Service mapResultSetToService(ResultSet rs) throws SQLException {
        Service service = new Service();
        service.setId(rs.getLong("id"));
        service.setNom(rs.getString("nom"));
        service.setDescription(rs.getString("description"));
        service.setPrix(rs.getDouble("prix"));
        service.setEstActif(rs.getBoolean("estActif"));
        service.setcapaciteMax(rs.getInt("capaciteMax"));
        service.setCategorie(rs.getString("categorie"));
        service.setdureeMinutes(rs.getInt("dureeMinutes"));
        service.setNiveau(rs.getInt("niveau"));
        service.setNote(rs.getDouble("note"));
        service.setNombreReservations(rs.getInt("nombreReservations"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            service.setCreated_at(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            service.setUpdated_at(updatedAt.toLocalDateTime());
        }

        return service;
    }

    @Override
    public void updateServiceNote(Long serviceId, double note) {
        String sql = "UPDATE service SET note = ?, updated_at = ? WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setDouble(1, note);
            pst.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pst.setLong(3, serviceId);

            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La mise à jour de la note a échoué");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la note", e);
        }
    }

    @Override
    public void incrementReservationCount(Long serviceId) {
        String sql = "UPDATE service SET nombreReservations = nombreReservations + 1 WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, serviceId);
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'incrémentation du nombre de réservations", e);
        }
    }
}