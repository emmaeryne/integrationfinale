package edu.emmapi.interfaces;

import edu.emmapi.entities.Abonnement;
import edu.emmapi.entities.Service;
import edu.emmapi.entities.TypeAbonnement;

import java.sql.SQLException;
import java.util.List;

public interface IClientService {
    List<Service> getAllServices() throws SQLException;
    List<TypeAbonnement> getAllTypeAbonnements() throws SQLException;
    Abonnement createAbonnement(Long serviceId, Long typeAbonnementId) throws SQLException;
}