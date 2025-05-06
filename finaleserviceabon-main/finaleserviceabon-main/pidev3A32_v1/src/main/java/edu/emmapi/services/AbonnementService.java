/*package edu.emmapi.services;

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
}*/
/*package edu.emmapi.services;

import edu.emmapi.entities.Abonnement;
import edu.emmapi.entities.Service;
import edu.emmapi.entities.Statut;
import edu.emmapi.entities.TypeAbonnement;
import edu.emmapi.tools.MyConnection;

import java.sql.*;
import java.time.LocalDate;
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
        String sql = "INSERT INTO abonnement (service_id, typeAbonnement_id, dateDebut, dateFin, prixTotal, statut, autoRenouvellement) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setLong(1, abonnement.getService().getId());
            pst.setLong(2, abonnement.getTypeAbonnement().getId());
            pst.setDate(3, Date.valueOf(abonnement.getDateDebut()));
            pst.setDate(4, Date.valueOf(abonnement.getDateFin()));
            pst.setDouble(5, abonnement.getPrixTotal());
            pst.setString(6, abonnement.getStatut().toString());
            pst.setBoolean(7, abonnement.isAutoRenouvellement());

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("L'insertion de l'abonnement a échoué, aucune ligne affectée.");
            }

            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    abonnement.setId((int) generatedKeys.getLong(1));
                } else {
                    throw new SQLException("L'insertion de l'abonnement a échoué, aucun ID généré.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout de l'abonnement: " + e.getMessage(), e);
        }
    }

    // Read all abonnements with optional sorting
    public List<Abonnement> getAllAbonnements(String tri) {
        List<Abonnement> abonnements = new ArrayList<>();
        String sql = "SELECT a.*, s.nom AS service_nom, t.nom AS type_nom " +
                "FROM abonnement a " +
                "JOIN service s ON a.service_id = s.id " +
                "JOIN typeabonnement t ON a.typeAbonnement_id = t.id";

        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Abonnement abonnement = new Abonnement();
                abonnement.setId((int) rs.getLong("id"));

                Service service = new Service();
                service.setId(rs.getLong("service_id"));
                service.setNom(rs.getString("service_nom"));
                abonnement.setService(service);

                TypeAbonnement typeAbonnement = new TypeAbonnement();
                typeAbonnement.setId(rs.getLong("typeAbonnement_id"));
                typeAbonnement.setNom(rs.getString("type_nom"));
                abonnement.setTypeAbonnement(typeAbonnement);

                abonnement.setDateDebut(rs.getDate("dateDebut").toLocalDate());
                abonnement.setDateFin(rs.getDate("dateFin").toLocalDate());
                abonnement.setPrixTotal(rs.getDouble("prixTotal"));
                abonnement.setStatut(Statut.valueOf(rs.getString("statut")));
                abonnement.setAutoRenouvellement(rs.getBoolean("autoRenouvellement"));

                abonnements.add(abonnement);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des abonnements: " + e.getMessage(), e);
        }

        // Sort abonnements if specified
        if (tri != null) {
            switch (tri) {
                case "prix_asc":
                    abonnements.sort(Comparator.comparing(Abonnement::getPrixTotal));
                    break;
                case "prix_desc":
                    abonnements.sort(Comparator.comparing(Abonnement::getPrixTotal).reversed());
                    break;
            }
        }

        return abonnements;
    }

    // Update an existing abonnement
    public void updateAbonnement(Abonnement abonnement) {
        String sql = "UPDATE abonnement SET service_id = ?, typeAbonnement_id = ?, dateDebut = ?, dateFin = ?, " +
                "prixTotal = ?, statut = ?, autoRenouvellement = ? WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, abonnement.getService().getId());
            pst.setLong(2, abonnement.getTypeAbonnement().getId());
            pst.setDate(3, Date.valueOf(abonnement.getDateDebut()));
            pst.setDate(4, Date.valueOf(abonnement.getDateFin()));
            pst.setDouble(5, abonnement.getPrixTotal());
            pst.setString(6, abonnement.getStatut().toString());
            pst.setBoolean(7, abonnement.isAutoRenouvellement());
            pst.setLong(8, abonnement.getId());

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("La mise à jour de l'abonnement a échoué, aucun abonnement trouvé avec l'ID: " + abonnement.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'abonnement: " + e.getMessage(), e);
        }
    }

    // Delete an abonnement by ID
    public void deleteAbonnement(Long id) {
        String sql = "DELETE FROM abonnement WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, id);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("La suppression de l'abonnement a échoué, aucun abonnement trouvé avec l'ID: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'abonnement: " + e.getMessage(), e);
        }
    }

    // Search abonnements by keyword
    public List<Abonnement> searchAbonnements(String keyword) {
        List<Abonnement> abonnements = new ArrayList<>();
        String sql = "SELECT a.*, s.nom AS service_nom, t.nom AS type_nom " +
                "FROM abonnement a " +
                "JOIN service s ON a.service_id = s.id " +
                "JOIN typeabonnement t ON a.typeAbonnement_id = t.id " +
                "WHERE s.nom LIKE ? OR t.nom LIKE ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, "%" + keyword + "%");
            pst.setString(2, "%" + keyword + "%");

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Abonnement abonnement = new Abonnement();
                    abonnement.setId((int) rs.getLong("id"));

                    Service service = new Service();
                    service.setId(rs.getLong("service_id"));
                    service.setNom(rs.getString("service_nom"));
                    abonnement.setService(service);

                    TypeAbonnement typeAbonnement = new TypeAbonnement();
                    typeAbonnement.setId(rs.getLong("typeAbonnement_id"));
                    typeAbonnement.setNom(rs.getString("type_nom"));
                    abonnement.setTypeAbonnement(typeAbonnement);

                    abonnement.setDateDebut(rs.getDate("dateDebut").toLocalDate());
                    abonnement.setDateFin(rs.getDate("dateFin").toLocalDate());
                    abonnement.setPrixTotal(rs.getDouble("prixTotal"));
                    abonnement.setStatut(Statut.valueOf(rs.getString("statut")));
                    abonnement.setAutoRenouvellement(rs.getBoolean("autoRenouvellement"));

                    abonnements.add(abonnement);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche d'abonnements: " + e.getMessage(), e);
        }

        return abonnements;
    }
}*/
package edu.emmapi.services;

import edu.emmapi.entities.Abonnement;
import edu.emmapi.entities.Service;
import edu.emmapi.entities.TypeAbonnement;
import edu.emmapi.entities.Statut;
import edu.emmapi.interfaces.IAbonnement;
import edu.emmapi.tools.MyConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AbonnementService implements IAbonnement {
    private final Connection conn;

    public AbonnementService() {
        this.conn = MyConnection.getInstance().getCnx();
    }

    @Override
    public Abonnement ajouterAbonnement(Abonnement abonnement) {
        validateAbonnement(abonnement);
        String sql = "INSERT INTO abonnement (service_id, type_abonnement_id, date_debut, date_fin, est_actif, prix_total, est_gratuit, statut, nombre_seances_restantes, auto_renouvellement, duree_mois, mode_paiement) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, Math.toIntExact(abonnement.getService().getId()));
            pst.setLong(2, abonnement.getTypeAbonnement().getId());
            pst.setDate(3, Date.valueOf(abonnement.getDateDebut()));
            pst.setDate(4, Date.valueOf(abonnement.getDateFin()));
            pst.setBoolean(5, abonnement.isEstActif());
            pst.setDouble(6, abonnement.getPrixTotal());
            pst.setBoolean(7, abonnement.isEstGratuit());
            pst.setString(8, abonnement.getStatut().name());
            pst.setInt(9, abonnement.getNombreSeancesRestantes());
            pst.setBoolean(10, abonnement.isAutoRenouvellement());
            pst.setObject(11, abonnement.getDureeMois(), Types.INTEGER);
            pst.setString(12, abonnement.getModePaiement());

            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Échec de l'ajout de l'abonnement.");
            }

            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    abonnement.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Échec de l'ajout, aucun ID obtenu.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout de l'abonnement: " + e.getMessage(), e);
        }
        return abonnement;
    }

    @Override
    public List<Abonnement> getAllAbonnements(String tri) {
        List<Abonnement> abonnements = new ArrayList<>();
        String sql = "SELECT a.*, s.nom as service_nom, t.nom as type_nom " +
                "FROM abonnement a " +
                "JOIN service s ON a.service_id = s.id " +
                "JOIN typeabonnement t ON a.type_abonnement_id = t.id";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Abonnement abonnement = new Abonnement();
                abonnement.setId(rs.getInt("id"));
                Service service = new Service();
                service.setId((long) rs.getInt("service_id"));
                service.setNom(rs.getString("service_nom"));
                abonnement.setService(service);
                TypeAbonnement typeAbonnement = new TypeAbonnement();
                typeAbonnement.setId(rs.getLong("type_abonnement_id"));
                typeAbonnement.setNom(rs.getString("type_nom"));
                abonnement.setTypeAbonnement(typeAbonnement);
                abonnement.setDateDebut(rs.getDate("date_debut").toLocalDate());
                abonnement.setDateFin(rs.getDate("date_fin").toLocalDate());
                abonnement.setEstActif(rs.getBoolean("est_actif"));
                abonnement.setPrixTotal(rs.getDouble("prix_total"));
                abonnement.setEstGratuit(rs.getBoolean("est_gratuit"));
                abonnement.setStatut(Statut.valueOf(rs.getString("statut")));
                abonnement.setNombreSeancesRestantes(rs.getInt("nombre_seances_restantes"));
                abonnement.setAutoRenouvellement(rs.getBoolean("auto_renouvellement"));
                abonnement.setDureeMois(rs.getInt("duree_mois") != 0 ? rs.getInt("duree_mois") : null);
                abonnement.setModePaiement(rs.getString("mode_paiement"));
                abonnements.add(abonnement);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des abonnements: " + e.getMessage(), e);
        }

        if (tri != null) {
            switch (tri) {
                case "prix_asc":
                    abonnements.sort(Comparator.comparing(Abonnement::getPrixTotal));
                    break;
                case "prix_desc":
                    abonnements.sort(Comparator.comparing(Abonnement::getPrixTotal).reversed());
                    break;
            }
        }

        return abonnements;
    }

    @Override
    public void updateAbonnement(Abonnement abonnement) {
        validateAbonnement(abonnement);
        String sql = "UPDATE abonnement SET service_id = ?, type_abonnement_id = ?, date_debut = ?, date_fin = ?, est_actif = ?, prix_total = ?, est_gratuit = ?, statut = ?, nombre_seances_restantes = ?, auto_renouvellement = ?, duree_mois = ?, mode_paiement = ? WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, Math.toIntExact(abonnement.getService().getId()));
            pst.setLong(2, abonnement.getTypeAbonnement().getId());
            pst.setDate(3, Date.valueOf(abonnement.getDateDebut()));
            pst.setDate(4, Date.valueOf(abonnement.getDateFin()));
            pst.setBoolean(5, abonnement.isEstActif());
            pst.setDouble(6, abonnement.getPrixTotal());
            pst.setBoolean(7, abonnement.isEstGratuit());
            pst.setString(8, abonnement.getStatut().name());
            pst.setInt(9, abonnement.getNombreSeancesRestantes());
            pst.setBoolean(10, abonnement.isAutoRenouvellement());
            pst.setObject(11, abonnement.getDureeMois(), Types.INTEGER);
            pst.setString(12, abonnement.getModePaiement());
            pst.setInt(13, abonnement.getId());

            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Échec de la mise à jour, aucun abonnement trouvé avec l'ID: " + abonnement.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'abonnement: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteAbonnement(Integer id) {
        String sql = "DELETE FROM abonnement WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Échec de la suppression, aucun abonnement trouvé avec l'ID: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'abonnement: " + e.getMessage(), e);
        }
    }

    private void validateAbonnement(Abonnement abonnement) {
        if (abonnement.getService() == null || abonnement.getService().getId() == null) {
            throw new IllegalArgumentException("Le service est obligatoire.");
        }
        if (abonnement.getTypeAbonnement() == null || abonnement.getTypeAbonnement().getId() == null) {
            throw new IllegalArgumentException("Le type d'abonnement est obligatoire.");
        }
        if (abonnement.getDateDebut() == null) {
            throw new IllegalArgumentException("La date de début est obligatoire.");
        }
        if (abonnement.getDateFin() == null) {
            throw new IllegalArgumentException("La date de fin est obligatoire.");
        }
        if (abonnement.getDateDebut().isAfter(abonnement.getDateFin())) {
            throw new IllegalArgumentException("La date de début doit être antérieure ou égale à la date de fin.");
        }
        if (abonnement.getStatut() == null) {
            throw new IllegalArgumentException("Le statut est obligatoire.");
        }
        if (!abonnement.isEstGratuit() && abonnement.getPrixTotal() <= 0) {
            throw new IllegalArgumentException("Le prix total est obligatoire et doit être positif si l'abonnement n'est pas gratuit.");
        }
        if (abonnement.isEstGratuit() && abonnement.getPrixTotal() != 0.0) {
            throw new IllegalArgumentException("Le prix total doit être 0.00 si l'abonnement est gratuit.");
        }
        if (abonnement.getNombreSeancesRestantes() < 0) {
            throw new IllegalArgumentException("Le nombre de séances restantes ne peut pas être négatif.");
        }
        if (!abonnement.isAutoRenouvellement() && abonnement.getDureeMois() == null) {
            throw new IllegalArgumentException("La durée en mois est obligatoire si l'auto-renouvellement est désactivé.");
        }
    }
}