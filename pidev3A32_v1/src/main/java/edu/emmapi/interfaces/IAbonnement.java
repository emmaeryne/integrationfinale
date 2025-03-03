package edu.emmapi.interfaces;

import edu.emmapi.entities.Abonnement;

import java.sql.SQLException;
import java.util.List;

public interface IAbonnement {
    /**
     * Adds a new abonnement to the system.
     *
     * @param abonnement the abonnement to be added
     * @return the added abonnement with generated ID
     * @throws SQLException if there is an error during the operation
     */
    Abonnement ajouterAbonnement(Abonnement abonnement) throws SQLException;

    /**
     * Retrieves all abonnements from the system.
     *
     * @return a list of all abonnements
     * @throws SQLException if there is an error during the operation
     */
    List<Abonnement> getAllAbonnements() throws SQLException;

    /**
     * Deletes an abonnement by its ID.
     *
     * @param id the ID of the abonnement to be deleted
     * @throws SQLException if there is an error during the operation
     */
    void deleteAbonnement(Long id) throws SQLException;

    /**
     * Updates an existing abonnement.
     *
     * @param abonnement the abonnement with updated information
     * @throws SQLException if there is an error during the operation
     */
    void updateAbonnement(Abonnement abonnement) throws SQLException;

    /**
     * Retrieves an abonnement by its ID.
     *
     * @param id the ID of the abonnement to retrieve
     * @return the abonnement with the specified ID
     * @throws SQLException if there is an error during the operation
     */
    Abonnement getAbonnementById(Long id) throws SQLException;

    /**
     * Searches for abonnements based on a keyword.
     *
     * @param keyword the keyword to search for
     * @return a list of abonnements matching the keyword
     * @throws SQLException if there is an error during the operation
     */
    List<Abonnement> searchAbonnements(String keyword) throws SQLException;
}