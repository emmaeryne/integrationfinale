package edu.emmapi.services;

import edu.emmapi.entities.Abonnement;
import edu.emmapi.entities.Service;
import edu.emmapi.entities.Statut;
import edu.emmapi.entities.TypeAbonnement;
import edu.emmapi.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AbonnementService {
    private final Connection conn;

    public AbonnementService() {
        this.conn = MyConnection.getInstance().getCnx();
    }

    // Create a new abonnement
    public void ajouterAbonnement(Abonnement abonnement) {
        String sql = "INSERT INTO abonnement (service_id, typeAbonnement_id, dateDebut, dateFin, estActif, " +
                "prixTotal, statut, nombreSeancesRestantes, autoRenouvellement, modePaiement, " +
                "numeroTransaction, dernierePresentence, tauxUtilisation) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setLong(1, abonnement.getService().getId());
            pst.setLong(2, abonnement.getTypeAbonnement().getId());

            // Check for null values before setting the date
            if (abonnement.getDateDebut() != null) {
                pst.setDate(3, Date.valueOf(abonnement.getDateDebut()));
            } else {
                pst.setNull(3, Types.DATE);
            }

            if (abonnement.getDateFin() != null) {
                pst.setDate(4, Date.valueOf(abonnement.getDateFin()));
            } else {
                pst.setNull(4, Types.DATE);
            }

            pst.setBoolean(5, abonnement.isEstActif());
            pst.setDouble(6, abonnement.getPrixTotal());

            // Set the statut
            pst.setString(7, abonnement.getStatut().toString());

            pst.setInt(8, abonnement.getNombreSeancesRestantes());
            pst.setBoolean(9, abonnement.isAutoRenouvellement());
            pst.setString(10, abonnement.getModePaiement());
            pst.setString(11, abonnement.getNumeroTransaction());

            // Check for null value for dernierePresentence
            if (abonnement.getDernierePresentence() != null) {
                pst.setDate(12, Date.valueOf(abonnement.getDernierePresentence()));
            } else {
                pst.setNull(12, Types.DATE);
            }

            pst.setDouble(13, abonnement.getTauxUtilisation());

            pst.executeUpdate();
            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    abonnement.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout de l'abonnement: " + e.getMessage(), e);
        }
    }

    // Read all abonnements with optional sorting
    public List<Abonnement> getAllAbonnements(String tri) {
        List<Abonnement> abonnements = new ArrayList<>();
        String sql = "SELECT a.*, s.*, t.* FROM abonnement a " +
                "JOIN service s ON a.service_id = s.id " +
                "JOIN typeabonnement t ON a.typeAbonnement_id = t.id";

        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Abonnement abonnement = new Abonnement();
                abonnement.setId(rs.getLong("a.id"));

                // Populate Service object
                Service service = new Service();
                service.setId(rs.getLong("s.id"));
                service.setNom(rs.getString("s.nom"));

                // Populate TypeAbonnement object
                TypeAbonnement typeAbonnement = new TypeAbonnement();
                typeAbonnement.setId(rs.getLong("t.id"));
                typeAbonnement.setNom(rs.getString("t.nom"));

                // Set service and typeAbonnement to abonnement
                abonnement.setService(service);
                abonnement.setTypeAbonnement(typeAbonnement);

                // Handle potential null values for dates
                Date dateDebut = rs.getDate("dateDebut");
                if (dateDebut != null) {
                    abonnement.setDateDebut(dateDebut.toLocalDate());
                }

                Date dateFin = rs.getDate("dateFin");
                if (dateFin != null) {
                    abonnement.setDateFin(dateFin.toLocalDate());
                }

                abonnement.setEstActif(rs.getBoolean("estActif"));
                abonnement.setPrixTotal(rs.getDouble("prixTotal"));
                // Set statut from String value
                abonnement.setStatut(Statut.valueOf(rs.getString("statut")));
                abonnement.setNombreSeancesRestantes(rs.getInt("nombreSeancesRestantes"));
                abonnement.setAutoRenouvellement(rs.getBoolean("autoRenouvellement"));
                abonnement.setModePaiement(rs.getString("modePaiement"));
                abonnement.setNumeroTransaction(rs.getString("numeroTransaction"));

                Date dernierePresentence = rs.getDate("dernierePresentence");
                if (dernierePresentence != null) {
                    abonnement.setDernierePresentence(dernierePresentence.toLocalDate());
                }

                abonnement.setTauxUtilisation(rs.getDouble("tauxUtilisation"));
                abonnements.add(abonnement);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des abonnements: " + e.getMessage(), e);
        }

        // Sort abonnements if needed
        if (tri != null) {
            switch (tri) {
                case "prix_asc" -> abonnements.sort(Comparator.comparing(Abonnement::getPrixTotal));
                case "prix_desc" -> abonnements.sort(Comparator.comparing(Abonnement::getPrixTotal).reversed());
            }
        }

        return abonnements;
    }

    // Update an existing abonnement
    public void updateAbonnement(Abonnement abonnement) {
        String sql = "UPDATE abonnement SET service_id = ?, typeAbonnement_id = ?, dateDebut = ?, " +
                "dateFin = ?, estActif = ?, prixTotal = ?, statut = ?, nombreSeancesRestantes = ?, " +
                "autoRenouvellement = ?, modePaiement = ?, numeroTransaction = ?, dernierePresentence = ?, " +
                "tauxUtilisation = ? WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, abonnement.getService().getId());
            pst.setLong(2, abonnement.getTypeAbonnement().getId());

            // Check for null values before setting the date
            if (abonnement.getDateDebut() != null) {
                pst.setDate(3, Date.valueOf(abonnement.getDateDebut()));
            } else {
                pst.setNull(3, Types.DATE);
            }

            if (abonnement.getDateFin() != null) {
                pst.setDate(4, Date.valueOf(abonnement.getDateFin()));
            } else {
                pst.setNull(4, Types.DATE);
            }

            pst.setBoolean(5, abonnement.isEstActif());
            pst.setDouble(6, abonnement.getPrixTotal());

            // Set the statut
            pst.setString(7, abonnement.getStatut().toString());

            pst.setInt(8, abonnement.getNombreSeancesRestantes());
            pst.setBoolean(9, abonnement.isAutoRenouvellement());
            pst.setString(10, abonnement.getModePaiement());
            pst.setString(11, abonnement.getNumeroTransaction());

            // Check for null value for dernierePresentence
            if (abonnement.getDernierePresentence() != null) {
                pst.setDate(12, Date.valueOf(abonnement.getDernierePresentence()));
            } else {
                pst.setNull(12, Types.DATE);
            }

            pst.setDouble(13, abonnement.getTauxUtilisation());
            pst.setLong(14, abonnement.getId());

            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'abonnement: " + e.getMessage(), e);
        }
    }

    // Delete an abonnement by ID
    public void deleteAbonnement(Long id) {
        String sql = "DELETE FROM abonnement WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'abonnement: " + e.getMessage(), e);
        }
    }

    // Search abonnements by keyword
    public List<Abonnement> searchAbonnements(String keyword) {
        List<Abonnement> abonnements = new ArrayList<>();
        String sql = "SELECT a.*, s.*, t.* FROM abonnement a " +
                "JOIN service s ON a.service_id = s.id " +
                "JOIN typeabonnement t ON a.typeAbonnement_id = t.id " +
                "WHERE s.nom LIKE ? OR t.nom LIKE ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            pst.setString(1, searchPattern);
            pst.setString(2, searchPattern);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Abonnement abonnement = new Abonnement();
                    abonnement.setId(rs.getLong("a.id"));

                    // Populate Service object
                    Service service = new Service();
                    service.setId(rs.getLong("s.id"));
                    service.setNom(rs.getString("s.nom"));

                    // Populate TypeAbonnement object
                    TypeAbonnement typeAbonnement = new TypeAbonnement();
                    typeAbonnement.setId(rs.getLong("t.id"));
                    typeAbonnement.setNom(rs.getString("t.nom"));

                    abonnement.setService(service);
                    abonnement.setTypeAbonnement(typeAbonnement);

                    // Handle potential null values for dates
                    Date dateDebut = rs.getDate("dateDebut");
                    if (dateDebut != null) {
                        abonnement.setDateDebut(dateDebut.toLocalDate());
                    }

                    Date dateFin = rs.getDate("dateFin");
                    if (dateFin != null) {
                        abonnement.setDateFin(dateFin.toLocalDate());
                    }

                    abonnement.setEstActif(rs.getBoolean("estActif"));
                    abonnement.setPrixTotal(rs.getDouble("prixTotal"));
                    // Set statut from String value
                    abonnement.setStatut(Statut.valueOf(rs.getString("statut")));
                    abonnement.setNombreSeancesRestantes(rs.getInt("nombreSeancesRestantes"));
                    abonnement.setAutoRenouvellement(rs.getBoolean("autoRenouvellement"));
                    abonnement.setModePaiement(rs.getString("modePaiement"));
                    abonnement.setNumeroTransaction(rs.getString("numeroTransaction"));

                    Date dernierePresentence = rs.getDate("dernierePresentence");
                    if (dernierePresentence != null) {
                        abonnement.setDernierePresentence(dernierePresentence.toLocalDate());
                    }

                    abonnement.setTauxUtilisation(rs.getDouble("tauxUtilisation"));
                    abonnements.add(abonnement);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche d'abonnements: " + e.getMessage(), e);
        }

        return abonnements;
    }
}