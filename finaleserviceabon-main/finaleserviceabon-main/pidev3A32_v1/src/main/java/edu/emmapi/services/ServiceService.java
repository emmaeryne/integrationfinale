package edu.emmapi.services;

import edu.emmapi.entities.Service;
import edu.emmapi.interfaces.IServiceService;
import edu.emmapi.tools.MyConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

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

        String sql = "INSERT INTO service (nom, description, prix, est_actif, capacite_max, " +
                "categorie, duree_minutes, niveau, created_at, updated_at, note, nombre_reservations, image, salle) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, service.getNom());
            pst.setString(2, service.getDescription());
            pst.setDouble(3, service.getPrix());
            pst.setBoolean(4, service.isEstActif());
            pst.setInt(5, service.getCapaciteMax());
            pst.setString(6, service.getCategorie());
            pst.setInt(7, service.getDureeMinutes());
            pst.setInt(8, service.getNiveau());
            pst.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            pst.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            if (service.getNote() != null) {
                pst.setDouble(11, service.getNote());
            } else {
                pst.setNull(11, Types.DOUBLE);
            }
            pst.setInt(12, service.getNombreReservations());
            pst.setString(13, service.getImage());
            pst.setString(14, service.getSalle() != null ? service.getSalle() : ""); // Provide a value for salle

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
            throw new RuntimeException("Erreur lors de l'ajout du service: " + e.getMessage(), e);
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
                throw new SQLException("Aucun service trouvé avec l'ID " + idService);
            }
        }
    }

    @Override
    public void updateService(Service service) throws SQLException {
        if (!service.estValide()) {
            throw new IllegalArgumentException("Service invalide");
        }

        String sql = "UPDATE service SET nom = ?, description = ?, prix = ?, est_actif = ?, " +
                "capacite_max = ?, categorie = ?, duree_minutes = ?, niveau = ?, updated_at = ?, " +
                "note = ?, nombre_reservations = ?, image = ?, salle = ? WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, service.getNom());
            pst.setString(2, service.getDescription());
            pst.setDouble(3, service.getPrix());
            pst.setBoolean(4, service.isEstActif());
            pst.setInt(5, service.getCapaciteMax());
            pst.setString(6, service.getCategorie());
            pst.setInt(7, service.getDureeMinutes());
            pst.setInt(8, service.getNiveau());
            pst.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            if (service.getNote() != null) {
                pst.setDouble(10, service.getNote());
            } else {
                pst.setNull(10, Types.DOUBLE);
            }
            pst.setInt(11, service.getNombreReservations());
            pst.setString(12, service.getImage());
            pst.setString(13, service.getSalle() != null ? service.getSalle() : "");
            pst.setLong(14, service.getId());

            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Aucun service trouvé avec l'ID " + service.getId());
            }
        }
    }

    @Override
    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM service";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                services.add(mapResultSetToService(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des services: " + e.getMessage(), e);
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
            throw new RuntimeException("Erreur lors de la récupération du service: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public Service getServiceById(int idService) throws SQLException {
        return null;
    }

    @Override
    public List<Service> searchServices(String keyword) {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM service WHERE nom LIKE ? OR description LIKE ? OR image LIKE ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, "%" + keyword + "%");
            pst.setString(2, "%" + keyword + "%");
            pst.setString(3, "%" + keyword + "%");

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    services.add(mapResultSetToService(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de services: " + e.getMessage(), e);
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
            throw new RuntimeException("Erreur lors de la récupération des services par catégorie: " + e.getMessage(), e);
        }

        return services;
    }

    @Override
    public void updateServiceNote(Long serviceId, double note) {
        String sql = "UPDATE service SET note = ?, updated_at = ? WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            if (note >= 0 && note <= 5) {
                pst.setDouble(1, note);
            } else {
                pst.setNull(1, Types.DOUBLE);
            }
            pst.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pst.setLong(3, serviceId);

            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Aucun service trouvé avec l'ID " + serviceId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la note: " + e.getMessage(), e);
        }
    }

    @Override
    public void incrementReservationCount(Long serviceId) {
        String sql = "UPDATE service SET nombre_reservations = nombre_reservations + 1, updated_at = ? WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pst.setLong(2, serviceId);

            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Aucun service trouvé avec l'ID " + serviceId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'incrémentation du nombre de réservations: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Integer> getReservationsPerDay() throws SQLException {
        Map<String, Integer> reservationsPerDay = new LinkedHashMap<>();
        String sql = "SELECT DAYNAME(created_at) AS day, COUNT(*) AS count " +
                "FROM service WHERE nombre_reservations > 0 " +
                "GROUP BY DAYNAME(created_at)";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String day = rs.getString("day");
                int count = rs.getInt("count");
                reservationsPerDay.put(day, count);
            }
        }

        // Ensure all days are present
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (String day : days) {
            reservationsPerDay.putIfAbsent(day, 0);
        }

        return reservationsPerDay;
    }

    @Override
    public Map<String, Double> getRevenuePerDay() throws SQLException {
        Map<String, Double> revenuePerDay = new LinkedHashMap<>();
        String sql = "SELECT DAYNAME(created_at) AS day, SUM(prix * nombre_reservations) AS revenue " +
                "FROM service WHERE nombre_reservations > 0 " +
                "GROUP BY DAYNAME(created_at)";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String day = rs.getString("day");
                double revenue = rs.getDouble("revenue");
                revenuePerDay.put(day, revenue);
            }
        }

        // Ensure all days are present
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (String day : days) {
            revenuePerDay.putIfAbsent(day, 0.0);
        }

        return revenuePerDay;
    }

    @Override
    public Map<String, Integer> getActiveServicesPerDay() throws SQLException {
        Map<String, Integer> activeServicesPerDay = new LinkedHashMap<>();
        String sql = "SELECT DAYNAME(created_at) AS day, COUNT(*) AS count " +
                "FROM service WHERE est_actif = TRUE " +
                "GROUP BY DAYNAME(created_at)";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String day = rs.getString("day");
                int count = rs.getInt("count");
                activeServicesPerDay.put(day, count);
            }
        }

        // Ensure all days are present
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (String day : days) {
            activeServicesPerDay.putIfAbsent(day, 0);
        }

        return activeServicesPerDay;
    }

    private Service mapResultSetToService(ResultSet rs) throws SQLException {
        Service service = new Service();
        service.setId(rs.getLong("id"));
        service.setNom(rs.getString("nom"));
        service.setDescription(rs.getString("description"));
        service.setPrix(rs.getDouble("prix"));
        service.setEstActif(rs.getBoolean("est_actif"));
        service.setCapaciteMax(rs.getInt("capacite_max"));
        service.setCategorie(rs.getString("categorie"));
        service.setDureeMinutes(rs.getInt("duree_minutes"));
        service.setNiveau(rs.getInt("niveau"));
        service.setNombreReservations(rs.getInt("nombre_reservations"));
        service.setImage(rs.getString("image"));
        service.setSalle(rs.getString("salle")); // Map the salle column

        Double note = rs.getDouble("note");
        if (!rs.wasNull()) {
            service.setNote(note);
        } else {
            service.setNote(null);
        }

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
}