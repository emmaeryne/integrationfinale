/*package edu.emmapi.entities;

import java.sql.Timestamp;

public class TypeAbonnement {
    private Long id;
    private String nom;
    private String description;
    private Double prix;
    private Integer dureeEnMois;
    private Boolean isPremium; // Ensure this field is defined
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public TypeAbonnement() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public Integer getDureeEnMois() {
        return dureeEnMois;
    }

    public void setDureeEnMois(Integer dureeEnMois) {
        this.dureeEnMois = dureeEnMois;
    }

    public Boolean getIsPremium() { // Add this method
        return isPremium;
    }

    public void setIsPremium(Boolean isPremium) { // Add this method
        this.isPremium = isPremium;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}*/
package edu.emmapi.entities;

import java.time.LocalDateTime;

public class TypeAbonnement {
    private Long id;
    private String nom;
    private String description;
    private String prix;
    private Integer dureeEnMois;
    private Boolean isPremium;
    private LocalDateTime updatedAt;
    private Double reduction;
    private String prixReduit;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPrix() { return prix; }
    public void setPrix(String prix) { this.prix = prix; }
    public Integer getDureeEnMois() { return dureeEnMois; }
    public void setDureeEnMois(Integer dureeEnMois) { this.dureeEnMois = dureeEnMois; }
    public Boolean isPremium() { return isPremium; }
    public void setPremium(Boolean isPremium) { this.isPremium = isPremium; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Double getReduction() { return reduction; }
    public void setReduction(Double reduction) { this.reduction = reduction; }
    public String getPrixReduit() { return prixReduit; }
    public void setPrixReduit(String prixReduit) { this.prixReduit = prixReduit; }

    public Double getPrixAsDouble() {
        try {
            return prix != null ? Double.parseDouble(prix) : 0.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public Double getPrixReduitAsDouble() {
        try {
            return prixReduit != null ? Double.parseDouble(prixReduit) : getPrixAsDouble();
        } catch (NumberFormatException e) {
            return getPrixAsDouble();
        }
    }

    @Override
    public String toString() {
        return nom + " (" + prix + " €)";
    }
}