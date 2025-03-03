package edu.emmapi.entities;

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
}