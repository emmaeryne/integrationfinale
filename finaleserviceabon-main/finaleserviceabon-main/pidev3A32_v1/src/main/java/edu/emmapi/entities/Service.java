package edu.emmapi.entities;

import java.time.LocalDateTime;

public class Service {
    private Long id;
    private String nom;
    private String description;
    private Double prix;
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
    private String salle;

    public Service() {
        this.estActif = true;
        this.capaciteMax = 1;
        this.dureeMinutes = 30;
        this.niveau = 1;
        this.nombreReservations = 0;
        this.salle = "OUVERT"; // Default to OUVERT
    }

    // Getters
    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public Double getPrix() { return prix; }
    public boolean isEstActif() { return estActif; }
    public int getCapaciteMax() { return capaciteMax; }
    public String getCategorie() { return categorie; }
    public int getDureeMinutes() { return dureeMinutes; }
    public int getNiveau() { return niveau; }
    public LocalDateTime getCreated_at() { return created_at; }
    public LocalDateTime getUpdated_at() { return updated_at; }
    public Double getNote() { return note; }
    public int getNombreReservations() { return nombreReservations; }
    public String getImage() { return image; }
    public String getSalle() { return salle; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setDescription(String description) { this.description = description; }
    public void setPrix(Double prix) { this.prix = prix; }
    public void setEstActif(boolean estActif) { this.estActif = estActif; }
    public void setCapaciteMax(int capaciteMax) { this.capaciteMax = capaciteMax; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public void setDureeMinutes(int dureeMinutes) { this.dureeMinutes = dureeMinutes; }
    public void setNiveau(int niveau) { this.niveau = niveau; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }
    public void setUpdated_at(LocalDateTime updated_at) { this.updated_at = updated_at; }
    public void setNote(Double note) { this.note = note; }
    public void setNombreReservations(int nombreReservations) { this.nombreReservations = nombreReservations; }
    public void setImage(String image) { this.image = image; }
    public void setSalle(String salle) {
        if (salle == null || (!salle.equals("OUVERT") && !salle.equals("FERME"))) {
            this.salle = "OUVERT"; // Default to OUVERT instead of throwing exception
        } else {
            this.salle = salle;
        }
    }

    public String getNiveauDifficulte() {
        return switch (niveau) {
            case 1 -> "Débutant";
            case 2 -> "Intermédiaire";
            case 3 -> "Avancé";
            default -> "Inconnu";
        };
    }

    public boolean estValide() {
        return validateNom() && validateDescription() && validatePrix() && validateCapacite() &&
                validateCategorie() && validateDuree() && validateNiveau() && validateSalle() && validateImage();
    }

    public boolean validateNom() {
        return nom != null && nom.matches("^[a-zA-ZÀ-ÿ0-9\\s-]{3,100}$");
    }

    public boolean validateDescription() {
        return description == null || description.length() <= 500;
    }

    public boolean validatePrix() {
        return prix != null && prix > 0 && prix <= 1000;
    }

    public boolean validateCapacite() {
        return capaciteMax > 0;
    }

    public boolean validateCategorie() {
        return categorie != null && categorie.length() <= 50;
    }

    public boolean validateDuree() {
        return dureeMinutes >= 15 && dureeMinutes <= 240;
    }

    public boolean validateNiveau() {
        return niveau >= 1 && niveau <= 3;
    }

    public boolean validateSalle() {
        return salle != null && (salle.equals("OUVERT") || salle.equals("FERME"));
    }

    public boolean validateImage() {
        return image == null || image.length() <= 255;
    }

    @Override
    public String toString() {
        return "Service{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", prix=" + prix +
                ", estActif=" + estActif +
                ", capaciteMax=" + capaciteMax +
                ", categorie='" + categorie + '\'' +
                ", dureeMinutes=" + dureeMinutes +
                ", niveau=" + niveau +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                ", note=" + note +
                ", nombreReservations=" + nombreReservations +
                ", image='" + image + '\'' +
                ", salle='" + salle + '\'' +
                '}';
    }
}