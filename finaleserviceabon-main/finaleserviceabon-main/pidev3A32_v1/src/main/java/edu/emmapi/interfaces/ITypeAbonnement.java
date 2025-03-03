package edu.emmapi.interfaces;

import edu.emmapi.entities.TypeAbonnement;

import java.util.List;

public interface ITypeAbonnement {
    TypeAbonnement ajouterTypeAbonnement(TypeAbonnement typeAbonnement);
    List<TypeAbonnement> getAllTypeAbonnements();

    List<TypeAbonnement> getAllTypeAbonnements(String tri);

    void deleteTypeAbonnement(Long id);
    void updateTypeAbonnement(TypeAbonnement typeAbonnement);
}