package edu.emmapi.services;

import edu.emmapi.entities.Reservation;
import edu.emmapi.entities.TypeAbonnement;
import edu.emmapi.tools.MyConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {
    private final Connection conn;

    public ReservationService() {
        this.conn = MyConnection.getInstance().getCnx();
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Statement stmt = conn.createStatement()) {
            // Create TypeAbonnement table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS type_abonnement (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    nom VARCHAR(100) NOT NULL,
                    description TEXT,
                    prix VARCHAR(20) NOT NULL,
                    duree_en_mois INT NOT NULL,
                    is_premium BOOLEAN NOT NULL,
                    updated_at TIMESTAMP,
                    reduction DOUBLE,
                    prix_reduit VARCHAR(20)
                )
            """);

            // Create Reservation table with foreign key
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS reservation (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    type_abonnement_id INT NOT NULL,
                    date_reservation TIMESTAMP NOT NULL,
                    date_debut TIMESTAMP,
                    date_fin TIMESTAMP,
                    statut VARCHAR(50) NOT NULL,
                    user_id INT NOT NULL,
                    FOREIGN KEY (type_abonnement_id) REFERENCES type_abonnement(id)
                )
            """);

            // Insert test data for TypeAbonnement
            stmt.executeUpdate("""
                INSERT IGNORE INTO type_abonnement (id, nom, description, prix, duree_en_mois, is_premium, updated_at, reduction, prix_reduit) VALUES
                (50, 'Basic', 'Abonnement de base', '29.99', 1, FALSE, NOW(), NULL, NULL),
                (78, 'Premium', 'Abonnement premium', '99.99', 3, TRUE, NOW(), 10.0, '89.99'),
                (105, 'Elite', 'Abonnement élite', '199.99', 6, TRUE, NOW(), 15.0, '169.99')
            """);

            // Insert test data for Reservation
            stmt.executeUpdate("""
                INSERT IGNORE INTO reservation (type_abonnement_id, date_reservation, date_debut, date_fin, statut, user_id) VALUES
                (50, '2025-04-23 18:58:52', '2025-04-08 19:58:00', '2025-05-11 19:03:00', 'en cours', 0),
                (105, '2025-04-27 22:19:43', '2025-03-31 23:19:00', '2025-05-11 23:19:00', 'en attente', 0),
                (78, '2025-05-06 16:08:59', '2025-05-21 17:08:00', '2025-05-17 17:08:00', 'en cours', 0)
            """);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database: " + e.getMessage());
        }
    }

    public List<Reservation> getAllReservations(String sort) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservation";
        if ("date_asc".equals(sort)) {
            query += " ORDER BY date_reservation ASC";
        } else if ("date_desc".equals(sort)) {
            query += " ORDER BY date_reservation DESC";
        }

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching reservations: " + e.getMessage());
        }
        return reservations;
    }

    public List<Reservation> searchReservations(String keyword) {
        List<Reservation> reservations = new ArrayList<>();
        String query = """
            SELECT r.* 
            FROM reservation r 
            JOIN type_abonnement t ON r.type_abonnement_id = t.id
            WHERE CAST(t.id AS CHAR) LIKE ? OR t.nom LIKE ? OR t.prix LIKE ?
        """;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            stmt.setString(3, "%" + keyword + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching reservations: " + e.getMessage());
        }
        return reservations;
    }

    public List<TypeAbonnement> getAllTypeAbonnements() {
        List<TypeAbonnement> types = new ArrayList<>();
        String query = "SELECT * FROM type_abonnement";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                types.add(mapResultSetToTypeAbonnement(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching type abonnements: " + e.getMessage());
        }
        return types;
    }

    public void addReservation(Reservation reservation) {
        String query = "INSERT INTO reservation (type_abonnement_id, date_reservation, date_debut, date_fin, statut, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, reservation.getTypeAbonnementId());
            stmt.setTimestamp(2, Timestamp.valueOf(reservation.getDateReservation()));
            stmt.setTimestamp(3, reservation.getDateDebut() != null ? Timestamp.valueOf(reservation.getDateDebut()) : null);
            stmt.setTimestamp(4, reservation.getDateFin() != null ? Timestamp.valueOf(reservation.getDateFin()) : null);
            stmt.setString(5, reservation.getStatut());
            stmt.setInt(6, reservation.getUserId());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Échec de l'ajout de la réservation.");
            }
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    reservation.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding reservation: " + e.getMessage());
        }
    }

    public void updateReservation(Reservation reservation) {
        String query = "UPDATE reservation SET type_abonnement_id = ?, date_reservation = ?, date_debut = ?, date_fin = ?, statut = ?, user_id = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, reservation.getTypeAbonnementId());
            stmt.setTimestamp(2, Timestamp.valueOf(reservation.getDateReservation()));
            stmt.setTimestamp(3, reservation.getDateDebut() != null ? Timestamp.valueOf(reservation.getDateDebut()) : null);
            stmt.setTimestamp(4, reservation.getDateFin() != null ? Timestamp.valueOf(reservation.getDateFin()) : null);
            stmt.setString(5, reservation.getStatut());
            stmt.setInt(6, reservation.getUserId());
            stmt.setLong(7, reservation.getId());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Échec de la mise à jour, ID introuvable: " + reservation.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating reservation: " + e.getMessage());
        }
    }

    public void deleteReservation(Long id) {
        String query = "DELETE FROM reservation WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Échec de la suppression, ID introuvable: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting reservation: " + e.getMessage());
        }
    }

    public void deleteExpiredReservations() {
        String query = "DELETE FROM reservation WHERE date_fin <= ? AND date_fin IS NOT NULL";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting expired reservations: " + e.getMessage());
        }
    }

    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setId(rs.getLong("id"));
        reservation.setTypeAbonnementId(rs.getInt("type_abonnement_id"));
        reservation.setDateReservation(rs.getTimestamp("date_reservation").toLocalDateTime());
        Timestamp dateDebut = rs.getTimestamp("date_debut");
        reservation.setDateDebut(dateDebut != null ? dateDebut.toLocalDateTime() : null);
        Timestamp dateFin = rs.getTimestamp("date_fin");
        reservation.setDateFin(dateFin != null ? dateFin.toLocalDateTime() : null);
        reservation.setStatut(rs.getString("statut"));
        reservation.setUserId(rs.getInt("user_id"));
        return reservation;
    }

    private TypeAbonnement mapResultSetToTypeAbonnement(ResultSet rs) throws SQLException {
        TypeAbonnement type = new TypeAbonnement();
        type.setId(rs.getLong("id"));
        type.setNom(rs.getString("nom"));
        type.setDescription(rs.getString("description"));
        type.setPrix(rs.getString("prix"));
        type.setDureeEnMois(rs.getInt("duree_en_mois"));
        type.setPremium(rs.getBoolean("is_premium"));
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        type.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);
        type.setReduction(rs.getObject("reduction") != null ? rs.getDouble("reduction") : null);
        type.setPrixReduit(rs.getString("prix_reduit"));
        return type;
    }
}