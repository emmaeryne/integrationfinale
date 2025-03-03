package edu.emmapi.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Service {
    private Long id;
    private String nom;
    private String description;
    private double prix;
    private boolean estActif;
    private int capaciteMax;
    private String categorie;
    private int dureeMinutes;
    private int niveau; // 1: Débutant, 2: Intermédiaire, 3: Avancé
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private double note; // Note moyenne du service
    private int nombreReservations;

    // Constructeur par défaut
    public Service() {
        this.created_at = LocalDateTime.now();
        this.updated_at = LocalDateTime.now();
    }

    // Validation métier
    public boolean estValide() {
        return validateNom() &&
                // validatePrix() &&
                validateDuree() &&
                //validateCapacite() &&
                validateCategorie() &&
                validateNiveau() &&
                validateNote() &&
                validateNombreReservations();
    }

    private boolean validateNom() {
        return nom != null &&
                nom.length() >= 3 &&
                nom.length() <= 100 &&
                nom.matches("^[a-zA-Z0-9\\s-]+$");
    }

    /*private boolean validatePrix() {
        return prix > 0 && prix <= 1000;
    }*/

    private boolean validateDuree() {
        return dureeMinutes >= 15 && dureeMinutes <= 240;
    }

    /*private boolean validateCapacite() {
        return capaciteMax > 0 && capaciteMax <= 100;
    }*/

    private boolean validateCategorie() {
        return categorie != null && !categorie.trim().isEmpty();
    }

    private boolean validateNiveau() {
        return niveau >= 1 && niveau <= 3;
    }

    private boolean validateNote() {
        return note >= 0 && note <= 5;
    }

    private boolean validateNombreReservations() {
        return nombreReservations >= 0;
    }

    // Méthodes métier
    public void ajusterPrixSelonDemande(int nombreReservations) {
        if (nombreReservations > 50) {
            this.prix *= 1.1; // Augmentation de 10%
        } else if (nombreReservations < 10) {
            this.prix *= 0.9; // Réduction de 10%
        }
    }

    public String getNiveauDifficulte() {
        return switch (niveau) {
            case 1 -> "Débutant";
            case 2 -> "Intermédiaire";
            case 3 -> "Avancé";
            default -> "Non spécifié";
        };
    }

    public boolean estPopulaire() {
        return nombreReservations > 30 && note >= 4.0;
    }

    // Getters et Setters avec validation
    public long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID doit être un nombre positif");
        }
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        }
        this.nom = nom.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        //if (!validatePrix()) {
        //throw new IllegalArgumentException("Le prix doit être compris entre 0 et 1000");
        //  }
        this.prix = prix;
    }

    public boolean isEstActif() {
        return estActif;
    }

    public void setEstActif(boolean estActif) {
        this.estActif = estActif;
    }

    public int getcapaciteMax() {
        return capaciteMax;
    }

    public void setcapaciteMax(int capaciteMax) {
        // if (!validateCapacite()) {
        //throw new IllegalArgumentException("La capacité maximale doit être comprise entre 1 et 100");
        // }
        this.capaciteMax = capaciteMax;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        //if (!validateCategorie()) {
        // throw new IllegalArgumentException("La catégorie ne peut pas être vide");
        // }
        this.categorie = categorie;
    }

    public int getdureeMinutes() {
        return dureeMinutes;
    }

    public void setdureeMinutes(int dureeMinutes) {
        //if (!validateDuree()) {
        //throw new IllegalArgumentException("La durée doit être comprise entre 15 et 240 minutes");
        //}
        this.dureeMinutes = dureeMinutes;
    }

    public int getNiveau() {
        return niveau;
    }

    public void setNiveau(int niveau) {
        //if (!validateNiveau()) {
        // throw new IllegalArgumentException("Le niveau doit être compris entre 1 et 3");
        // }
        this.niveau = niveau;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public double getNote() {
        return note;
    }

    public void setNote(double note) {
        //if (!validateNote()) {
        //throw new IllegalArgumentException("La note doit être comprise entre 0 et 5");
        //}
        this.note = note;
    }

    public int getNombreReservations() {
        return nombreReservations;
    }

    public void setNombreReservations(int nombreReservations) {
        //if (!validateNombreReservations()) {
        // throw new IllegalArgumentException("Le nombre de réservations ne peut pas être négatif");
        // }
        this.nombreReservations = nombreReservations;
    }

    // Méthodes equals, hashCode et toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return Objects.equals(id, service.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Service{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prix=" + prix +
                ", niveau='" + getNiveauDifficulte() + '\'' +
                ", estActif=" + estActif +
                '}';
    }
}