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
    private int niveau;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private Double note;
    private int nombreReservations;
    private String image;

    // Constructeur par défaut
    public Service() {
        this.created_at = LocalDateTime.now();
        this.updated_at = LocalDateTime.now();
        this.estActif = true;
        this.nombreReservations = 0;
    }

    // Validation métier
    public boolean estValide() {
        return validateNom() &&
                validatePrix() &&
                validateDuree() &&
                validateCapacite() &&
                validateCategorie() &&
                validateNiveau() &&
                validateNote() &&
                validateNombreReservations();
    }

    private boolean validateNom() {
        return nom != null &&
                nom.length() >= 3 &&
                nom.length() <= 100 &&
                nom.matches("^[a-zA-ZÀ-ÿ0-9\\s-]+$");
    }

    private boolean validatePrix() {
        return prix > 0;
    }

    private boolean validateDuree() {
        return dureeMinutes >= 15 && dureeMinutes <= 240;
    }

    private boolean validateCapacite() {
        return capaciteMax > 0;
    }

    private boolean validateCategorie() {
        return categorie != null && !categorie.trim().isEmpty() && categorie.length() <= 50;
    }

    private boolean validateNiveau() {
        return niveau >= 1 && niveau <= 3;
    }

    private boolean validateNote() {
        return note == null || (note >= 0 && note <= 5);
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


    // Getters et Setters avec validation
    public Long getId() {
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
        if (prix <= 0) {
            throw new IllegalArgumentException("Le prix doit être positif");
        }
        this.prix = prix;
    }

    public boolean isEstActif() {
        return estActif;
    }

    public void setEstActif(boolean estActif) {
        this.estActif = estActif;
    }

    public int getCapaciteMax() {
        return capaciteMax;
    }

    public void setCapaciteMax(int capaciteMax) {
        if (capaciteMax <= 0) {
            throw new IllegalArgumentException("La capacité maximale doit être positive");
        }
        this.capaciteMax = capaciteMax;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        if (categorie == null || categorie.trim().isEmpty()) {
            throw new IllegalArgumentException("La catégorie ne peut pas être vide");
        }
        this.categorie = categorie;
    }

    public int getDureeMinutes() {
        return dureeMinutes;
    }

    public void setDureeMinutes(int dureeMinutes) {
        if (dureeMinutes < 15 || dureeMinutes > 240) {
            throw new IllegalArgumentException("La durée doit être comprise entre 15 et 240 minutes");
        }
        this.dureeMinutes = dureeMinutes;
    }

    public int getNiveau() {
        return niveau;
    }

    public void setNiveau(int niveau) {
        if (niveau < 1 || niveau > 3) {
            throw new IllegalArgumentException("Le niveau doit être compris entre 1 et 3");
        }
        this.niveau = niveau;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        if (created_at == null) {
            throw new IllegalArgumentException("La date de création ne peut pas être nulle");
        }
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        if (updated_at == null) {
            throw new IllegalArgumentException("La date de mise à jour ne peut pas être nulle");
        }
        this.updated_at = updated_at;
    }

    public Double getNote() {
        return note;
    }

    public void setNote(Double note) {
        if (note != null && (note < 0 || note > 5)) {
            throw new IllegalArgumentException("La note doit être comprise entre 0 et 5");
        }
        this.note = note;
    }

    public int getNombreReservations() {
        return nombreReservations;
    }

    public void setNombreReservations(int nombreReservations) {
        if (nombreReservations < 0) {
            throw new IllegalArgumentException("Le nombre de réservations ne peut pas être négatif");
        }
        this.nombreReservations = nombreReservations;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        if (image != null && image.length() > 255) {
            throw new IllegalArgumentException("L'image ne peut pas dépasser 255 caractères");
        }
        this.image = image;
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