package edu.emmapi.entities;

import java.time.LocalDateTime;

public class Reservation {
    private Long id;
    private int typeAbonnementId; // Remplace type
    private LocalDateTime dateReservation;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String statut;
    private int userId; // Inclus pour la table, mais fixé à 0

    public Reservation() {
        this.dateReservation = LocalDateTime.now();
        this.statut = "en attente";
        this.userId = 0; // Valeur par défaut pour compatibilité
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getTypeAbonnementId() { return typeAbonnementId; }
    public void setTypeAbonnementId(int typeAbonnementId) { this.typeAbonnementId = typeAbonnementId; }
    public LocalDateTime getDateReservation() { return dateReservation; }
    public void setDateReservation(LocalDateTime dateReservation) { this.dateReservation = dateReservation; }
    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }
    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}